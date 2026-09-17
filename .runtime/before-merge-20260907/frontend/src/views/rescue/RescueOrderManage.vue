<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createRescueOrder, getRescueOrders } from '../../api/rescueOrder'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const orders = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const form = reactive({ title: '', description: '', location: '', emergencyLevel: 'MEDIUM' })

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

const submit = async () => {
  try {
    await createRescueOrder(form)
    ElMessage.success('工单已创建')
    dialogVisible.value = false
    Object.assign(form, { title: '', description: '', location: '', emergencyLevel: 'MEDIUM' })
    loadOrders()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadOrders)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>救助工单管理</h1>
        <div>
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button type="primary" @click="dialogVisible = true">新建工单</el-button>
        </div>
      </div>
      <el-card shadow="never">
        <el-table v-loading="loading" :data="orders" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="110" />
          <el-table-column prop="status" label="状态" width="140" />
          <el-table-column prop="handlerId" label="处理人ID" width="110" />
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/rescue-orders/${row.id}`)">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadOrders" />
      </el-card>

      <el-dialog v-model="dialogVisible" title="新建救助工单" width="560px">
        <el-form label-width="90px">
          <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
          <el-form-item label="紧急程度">
            <el-select v-model="form.emergencyLevel">
              <el-option label="LOW" value="LOW" />
              <el-option label="MEDIUM" value="MEDIUM" />
              <el-option label="HIGH" value="HIGH" />
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
