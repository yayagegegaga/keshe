<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyRescueOrders } from '../../api/rescueOrder'
import { EMERGENCY_LEVEL, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  keyword: ''
})

const statusLabels = {
  WAIT_ASSIGN: '待分配',
  ASSIGNED: '已分配',
  PROCESSING: '处理中',
  WAIT_CONFIRM: '待确认',
  CLOSED: '已关闭',
  CANCELED: '已取消'
}

const emergencyOptions = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']
const emergencyLabels = Object.fromEntries(emergencyOptions.map(code => [code, dict(EMERGENCY_LEVEL, code)]))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyRescueOrders(query)
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

const resetSearch = () => {
  query.status = ''
  query.keyword = ''
  query.pageNum = 1
  loadData()
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>我的工单</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
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
          <el-button @click="resetSearch">重置</el-button>
        </div>

        <el-table v-loading="loading" :data="orders" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="location" label="地点" min-width="140" />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="100">
            <template #default="{ row }">{{ emergencyLabels[row.emergencyLevel] || row.emergencyLevel }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'CLOSED' ? 'success' : row.status === 'CANCELED' ? 'danger' : 'warning'">
                {{ statusLabels[row.status] || row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="170" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/rescue-orders/${row.id}`)">详情/处理</el-button>
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
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; flex-wrap: wrap; align-items: center; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>