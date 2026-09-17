<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getRescueOrders } from '../../api/rescueOrder'

const router = useRouter()
const loading = ref(false)
const orders = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await getRescueOrders(query)
    orders.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>救助工单</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 170px">
              <el-option v-for="s in ['WAIT_ASSIGN','ASSIGNED','PROCESSING','WAIT_CONFIRM','CLOSED','CANCELED']" :key="s" :label="s" :value="s" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadOrders">查询</el-button>
        </el-form>
        <el-table v-loading="loading" :data="orders" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="110" />
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column prop="handlerId" label="处理人ID" width="110" />
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/rescue-orders/${row.id}`)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadOrders" />
      </el-card>
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
