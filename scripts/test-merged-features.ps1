param([string]$BaseUrl = 'http://127.0.0.1:8081')
$ErrorActionPreference = 'Stop'
if ($BaseUrl -ne 'http://127.0.0.1:8081') { throw 'This test writes fixtures and must target the isolated test server on 8081.' }
function Call-Api($Method, $Path, $Body = $null, $Token = '') {
    $args = @{ Method = $Method; Uri = "$BaseUrl/api$Path"; ContentType = 'application/json'; SkipHttpErrorCheck = $true }
    if ($Token) { $args.Headers = @{ Authorization = "Bearer $Token" } }
    if ($null -ne $Body) { $args.Body = ConvertTo-Json -Depth 6 -InputObject $Body }
    Invoke-RestMethod @args
}
function Assert-True($Condition, $Label) {
    if (-not $Condition) { throw "FAIL: $Label" }
    Write-Output "PASS: $Label"
}
$stamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$admin = Call-Api POST /auth/login @{username='admin';password='Admin@123'}
Assert-True ($admin.code -eq 200) 'Existing administrator login'
$a = $admin.data.token
$invalid = Call-Api POST /auth/register-volunteer @{username="invalid$stamp";password='Test@123'}
Assert-True ($invalid.code -ne 200) 'Volunteer required fields validated'
$profile = @{username="mergev$stamp";password='Test@123';nickname='合并测试志愿者';studentId="T$stamp";realName='测试同学';major='软件工程';phone='';email=''}
$registered = Call-Api POST /auth/register-volunteer $profile
Assert-True ($registered.code -eq 200 -and $registered.data.roles -contains 'VOLUNTEER') 'Volunteer registration assigns VOLUNTEER'
$duplicate = Call-Api POST /auth/register-volunteer $profile
Assert-True ($duplicate.code -ne 200) 'Duplicate username rejected'
$vlogin = Call-Api POST /auth/login @{username=$profile.username;password=$profile.password}
Assert-True ($vlogin.code -eq 200 -and $vlogin.data.roles -contains 'VOLUNTEER') 'New volunteer login'
$v = $vlogin.data.token
$ordinary = Call-Api POST /auth/register @{username="mergeu$stamp";password='Test@123';nickname='测试用户'}
Assert-True ($ordinary.code -eq 200 -and $ordinary.data.roles -contains 'USER') 'Ordinary registration still assigns USER'
$ulogin = Call-Api POST /auth/login @{username="mergeu$stamp";password='Test@123'}
$u = $ulogin.data.token
$all = Call-Api GET '/volunteer-tasks?pageSize=100' $null $a
$visible = Call-Api GET '/volunteer-tasks?pageSize=100' $null $v
Assert-True ($visible.code -eq 200 -and $visible.data.total -eq $all.data.total) 'Volunteer can see all tasks'
$other = $all.data.records | Where-Object { $_.volunteerId -and $_.status -eq 'CLAIMED' } | Select-Object -First 1
Assert-True ($null -ne $other) 'Assigned task fixture exists'
$detail = Call-Api GET "/volunteer-tasks/$($other.id)" $null $v
Assert-True ($detail.code -eq 200) 'Volunteer can read another volunteer task'
$finish = Call-Api PUT "/volunteer-tasks/$($other.id)/finish" @{content='不能替他人提交'} $v
Assert-True ($finish.code -ne 200) 'Volunteer cannot finish another volunteer task'
$denied = Call-Api GET '/volunteer-tasks' $null $u
Assert-True ($denied.code -eq 403) 'Ordinary user cannot access volunteer tasks'
$orderBody = @{title="merge-$stamp";description='合并功能验证';location='测试地点';emergencyLevel='URGENT'}
$order = Call-Api POST /rescue-orders $orderBody $a
Assert-True ($order.code -eq 200) 'Create urgent rescue order'
$id = $order.data.id
$query = Call-Api GET "/rescue-orders?keyword=merge-$stamp&status=WAIT_ASSIGN" $null $a
Assert-True ($query.code -eq 200 -and $query.data.total -eq 1) 'Order keyword and status filters'
$denied = Call-Api DELETE "/rescue-orders/$id" $null $v
Assert-True ($denied.code -eq 403) 'Volunteer cannot delete orders'
$closed = Call-Api PUT "/rescue-orders/$id/close" @{remark='管理员直接关闭测试'} $a
Assert-True ($closed.code -eq 200 -and $closed.data.status -eq 'CLOSED') 'Administrator closes waiting order'
$logs = Call-Api GET "/rescue-orders/$id/logs" $null $a
Assert-True (@($logs.data).Count -eq 2) 'Close operation recorded in log'
$denied = Call-Api DELETE "/rescue-orders/$id" $null $a
Assert-True ($denied.code -ne 200) 'Closed order cannot be deleted'
$new = Call-Api POST /rescue-orders $orderBody $a
$deleted = Call-Api DELETE "/rescue-orders/$($new.data.id)" $null $a
Assert-True ($deleted.code -eq 200) 'Administrator deletes waiting order with dependent logs'
$missing = Call-Api GET "/rescue-orders/$($new.data.id)" $null $a
Assert-True ($missing.code -eq 404) 'Deleted order no longer accessible'
$clue = Call-Api POST /rescue-clues @{animalType='CAT';location="merge-$stamp";description='线索测试';emergencyLevel='MEDIUM';contact='测试联系人'} $u
Assert-True ($clue.code -eq 200) 'Create test clue'
$review = Call-Api PUT "/rescue-clues/$($clue.data.id)/review" @{approved=$true;createOrder=$true;reviewRemark='审核通过'} $a
Assert-True ($review.code -eq 200 -and $review.data.status -eq 'CONVERTED') 'Quick clue approval converts to order'
$linked = Call-Api GET "/rescue-orders?keyword=merge-$stamp&pageSize=100" $null $a
$linkedOrder = $linked.data.records | Where-Object { $_.clueId -eq $clue.data.id } | Select-Object -First 1
Assert-True ($null -ne $linkedOrder) 'Converted clue has an order'
$deleted = Call-Api DELETE "/rescue-orders/$($linkedOrder.id)" $null $a
Assert-True ($deleted.code -eq 200) 'Delete clue-linked waiting order'
$clueDetail = Call-Api GET "/rescue-clues/$($clue.data.id)" $null $a
Assert-True ($clueDetail.data.status -eq 'PENDING') 'Deleted order returns source clue to pending review'
$animals = Call-Api GET '/animals?status=ADOPTABLE' $null $u
Assert-True ($animals.code -eq 200 -and @($animals.data.records | Where-Object status -ne 'ADOPTABLE').Count -eq 0) 'Adoptable animal filter'
Write-Output 'ALL MERGED FEATURE CHECKS PASSED'
