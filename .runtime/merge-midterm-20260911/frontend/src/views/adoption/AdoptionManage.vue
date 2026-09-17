<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  firstApproveAdoption,
  getAdoptions,
  interviewAdoption,
  rejectAdoption,
  successAdoption,
  trialAdoption,
  trialFailedAdoption
} from '../../api/adoption'
import { ADOPTION_STATUS, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const applications = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '', keyword: '' })
const statuses = ['', 'PENDING', 'FIRST_APPROVED', 'INTERVIEWING', 'TRIAL', 'SUCCESS', 'REJECTED', 'CANCELED', 'TRIAL_FAILED']
const statusLabels = Object.fromEntries(statuses.filter(Boolean).map(code => [code, dict(ADOPTION_STATUS, code)]))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getAdoptions(query)
    applications.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const askRemark = async (title, defaultValue = '') => {
  const { value } = await ElMessageBox.prompt('请输入处理备注', title, { inputValue: defaultValue })
  return value
}

const runAction = async (row, title, action, requiredRemark = false) => {
  try {
    const remark = await askRemark(title, title)
    if (requiredRemark && !remark) {
      ElMessage.warning('备注不能为空')
      return
    }
    await action(row.id, { remark })
    ElMessage.success('处理成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '已取消')
  }
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>领养审核管理</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <div class="filters">
          <el-select v-model="query.status" placeholder="状态" clearable @change="loadData">
            <el-option v-for="item in statuses" :key="item || 'ALL'" :label="item ? statusLabels[item] : '全部状态'" :value="item" />
          </el-select>
          <el-input v-model="query.keyword" clearable placeholder="申请人/电话/理由" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData">查询</el-button>
        </div>
        <el-table v-loading="loading" :data="applications" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalName" label="动物" min-width="120" />
          <el-table-column prop="applicantName" label="申请人" min-width="120" />
          <el-table-column prop="phone" label="电话" min-width="130" />
          <el-table-column prop="status" label="状态" width="140">
            <template #default="{ row }">{{ statusLabels[row.status] || row.status }}</template>
          </el-table-column>
          <el-table-column label="操作" width="360">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/adoptions/${row.id}`)">详情</el-button>
              <el-button v-if="row.status === 'PENDING'" link type="success" @click="runAction(row, '初审通过', firstApproveAdoption)">初审</el-button>
              <el-button v-if="row.status === 'FIRST_APPROVED'" link type="success" @click="runAction(row, '进入面谈', interviewAdoption)">面谈</el-button>
              <el-button v-if="row.status === 'INTERVIEWING'" link type="success" @click="runAction(row, '进入试养', trialAdoption)">试养</el-button>
              <el-button v-if="row.status === 'TRIAL'" link type="success" @click="runAction(row, '领养成功', successAdoption)">成功</el-button>
              <el-button v-if="['PENDING','FIRST_APPROVED','INTERVIEWING'].includes(row.status)" link type="danger" @click="runAction(row, '拒绝申请', rejectAdoption, true)">拒绝</el-button>
              <el-button v-if="row.status === 'TRIAL'" link type="warning" @click="runAction(row, '试养失败', trialFailedAdoption, true)">试养失败</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadData" />
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1200px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
.filters .el-input { max-width: 260px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
