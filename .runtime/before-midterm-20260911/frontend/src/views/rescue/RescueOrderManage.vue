<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createRescueOrder, getRescueOrders, deleteRescueOrder, closeRescueOrder } from '../../api/rescueOrder'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const orders = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  keyword: ''
})

const formRef = ref()
const form = reactive({
  title: '',
  description: '',
  location: '',
  emergencyLevel: 'MEDIUM'
})

const statusLabels = { WAIT_ASSIGN: '待分配', ASSIGNED: '已分配', PROCESSING: '处理中', WAIT_CONFIRM: '待确认', CLOSED: '已关闭', CANCELED: '已取消' }
const emergencyOptions = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']

const rules = {
  title: [{ required: true, message: '请输入工单标题', trigger: 'blur' }],
  description: [{ required: true, message: '请输入工单描述', trigger: 'blur' }],
  location: [{ required: true, message: '请输入地点', trigger: 'blur' }],
  emergencyLevel: [{ required: true, message: '请选择紧急程度', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRescueOrders(query)
    orders.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  loadData()
}

const resetForm = () => {
  Object.assign(form, {
    title: '',
    description: '',
    location: '',
    emergencyLevel: 'MEDIUM'
  })
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value.validate()
  try {
    await createRescueOrder(form)
    ElMessage.success('工单创建成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message)
  }
}

// 删除工单
const removeOrder = async (row) => {
  try {
    await ElMessageBox.confirm(
    `确认删除工单 "${row.title}"？删除后不可恢复。`,
    '删除确认',
    { type: 'warning' }
  )
    await deleteRescueOrder(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message)
  }
}

// 关闭工单
const closeOrder = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入关闭备注', '关闭工单', { inputValue: '工单已完成' })
    await closeRescueOrder(row.id, { remark: value })
    ElMessage.success('工单已关闭')
    loadData()
  } catch (e) {
    if (e !== 'cancel') if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message)
  }
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>工单管理</h1>
        <div class="header-actions">
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button type="primary" @click="openCreate">新建工单</el-button>
        </div>
      </div>

      <el-card shadow="never">
        <div class="filters">
          <el-select v-model="query.status" placeholder="状态" clearable @change="search" style="width: 140px">
            <el-option label="全部状态" value="" />
            <el-option label="待分配" value="WAIT_ASSIGN" />
            <el-option label="已分配" value="ASSIGNED" />
            <el-option label="处理中" value="PROCESSING" />
            <el-option label="待确认" value="WAIT_CONFIRM" />
            <el-option label="已关闭" value="CLOSED" />
            <el-option label="已取消" value="CANCELED" />
          </el-select>
          <el-input v-model="query.keyword" clearable placeholder="标题/地点" style="max-width: 260px" @keyup.enter="search" />
          <el-button type="primary" @click="search">查询</el-button>
        </div>

        <el-table v-loading="loading" :data="orders" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="location" label="地点" min-width="140" />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'CLOSED' ? 'success' : row.status === 'CANCELED' ? 'danger' : 'warning'">
                {{ statusLabels[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/rescue-orders/${row.id}`)">详情</el-button>
              <el-button
                v-if="['WAIT_ASSIGN', 'WAIT_CONFIRM'].includes(row.status)"
                link
                type="success"
                @click="closeOrder(row)"
              >
                关闭
              </el-button>
              <el-button
                v-if="row.status === 'WAIT_ASSIGN'"
                link
                type="danger"
                @click="removeOrder(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pager"
          layout="total, prev, pager, next"
          :total="total"
          :page-size="query.pageSize"
          v-model:current-page="query.pageNum"
          @current-change="loadData"
        />
      </el-card>

      <!-- 新建工单对话框 -->
      <el-dialog v-model="dialogVisible" title="新建工单" width="600px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
          <el-form-item label="标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入工单标题" />
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入工单描述" />
          </el-form-item>
          <el-form-item label="地点" prop="location">
            <el-input v-model="form.location" placeholder="请输入具体地点" />
          </el-form-item>
          <el-form-item label="紧急程度" prop="emergencyLevel">
            <el-select v-model="form.emergencyLevel" style="width: 100%">
              <el-option v-for="item in emergencyOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">创建</el-button>
        </template>
      </el-dialog>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
