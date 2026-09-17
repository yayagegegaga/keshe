<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelAdoption, getMyAdoptions } from '../../api/adoption'
import { ADOPTION_STATUS, ANIMAL_STATUS, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const applications = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const statuses = ['', 'PENDING', 'FIRST_APPROVED', 'INTERVIEWING', 'TRIAL', 'SUCCESS', 'REJECTED', 'CANCELED', 'TRIAL_FAILED']
const statusLabels = Object.fromEntries(statuses.filter(Boolean).map(code => [code, dict(ADOPTION_STATUS, code)]))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyAdoptions(query)
    applications.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const cancel = async row => {
  const { value } = await ElMessageBox.prompt('请输入取消原因', '取消申请', { inputValue: '用户取消申请' })
  try {
    await cancelAdoption(row.id, { reason: value })
    ElMessage.success('已取消')
    loadData()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>我的领养申请</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <div class="filters">
          <el-select v-model="query.status" placeholder="状态" clearable @change="loadData">
            <el-option v-for="item in statuses" :key="item || 'ALL'" :label="item ? statusLabels[item] : '全部状态'" :value="item" />
          </el-select>
        </div>
        <el-table v-loading="loading" :data="applications" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalName" label="动物" min-width="130" />
          <el-table-column prop="animalStatus" label="动物状态" width="120">
            <template #default="{ row }">{{ dict(ANIMAL_STATUS, row.animalStatus) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="申请状态" width="140">
            <template #default="{ row }">{{ statusLabels[row.status] || row.status }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="提交时间" min-width="170" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/adoptions/${row.id}`)">详情</el-button>
              <el-button v-if="['PENDING','FIRST_APPROVED'].includes(row.status)" link type="danger" @click="cancel(row)">取消</el-button>
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
.shell { width: min(1100px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
