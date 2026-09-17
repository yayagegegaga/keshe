<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assignRescueOrder, cancelRescueOrder, closeRescueOrder, finishRescueOrder, getRescueOrderDetail, startRescueOrder } from '../../api/rescueOrder'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)
const assignForm = reactive({ handlerId: '', remark: '' })
const finishForm = reactive({ processResult: '', processImage: '', remark: '' })

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getRescueOrderDetail(route.params.id)
    order.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const run = async action => {
  try {
    await action()
    ElMessage.success('操作成功')
    await loadDetail()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadDetail)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>工单详情</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="order">
        <el-card shadow="never">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="ID">{{ order.id }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ order.status }}</el-descriptions-item>
            <el-descriptions-item label="标题">{{ order.title }}</el-descriptions-item>
            <el-descriptions-item label="紧急程度">{{ order.emergencyLevel }}</el-descriptions-item>
            <el-descriptions-item label="地点">{{ order.location }}</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ order.handler?.nickname || order.handlerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ order.description }}</el-descriptions-item>
            <el-descriptions-item label="处理结果" :span="2">{{ order.processResult || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>工单操作</template>
          <el-form :inline="true">
            <el-form-item label="处理人ID">
              <el-input v-model="assignForm.handlerId" placeholder="志愿者用户ID" style="width: 140px" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="assignForm.remark" style="width: 180px" />
            </el-form-item>
            <el-button type="primary" @click="run(() => assignRescueOrder(order.id, { handlerId: Number(assignForm.handlerId), remark: assignForm.remark }))">分配</el-button>
            <el-button @click="run(() => startRescueOrder(order.id))">开始处理</el-button>
            <el-button type="success" @click="run(() => closeRescueOrder(order.id, { remark: '确认关闭' }))">关闭</el-button>
            <el-button type="danger" @click="run(() => cancelRescueOrder(order.id, { reason: '管理员取消' }))">取消</el-button>
          </el-form>
          <el-divider />
          <el-form label-width="90px">
            <el-form-item label="处理结果">
              <el-input v-model="finishForm.processResult" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="结果图片">
              <el-input v-model="finishForm.processImage" />
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="finishForm.remark" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="run(() => finishRescueOrder(order.id, finishForm))">提交完成</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>流转日志</template>
          <el-table :data="order.logs" border>
            <el-table-column prop="createTime" label="时间" min-width="170" />
            <el-table-column prop="operatorName" label="操作人" min-width="120" />
            <el-table-column prop="oldStatus" label="原状态" min-width="130" />
            <el-table-column prop="newStatus" label="新状态" min-width="130" />
            <el-table-column prop="operationType" label="操作" min-width="120" />
            <el-table-column prop="remark" label="备注" min-width="180" />
          </el-table>
        </el-card>
      </template>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.block { margin-top: 18px; }
</style>
