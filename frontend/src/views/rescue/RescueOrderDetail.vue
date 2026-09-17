<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { assignRescueOrder, cancelRescueOrder, closeRescueOrder, finishRescueOrder, getRescueOrderDetail, startRescueOrder } from '../../api/rescueOrder'
import { uploadFile } from '../../api/file'
import { getVolunteers } from '../../api/user'
import { EMERGENCY_LEVEL, RESCUE_ORDER_OPERATION, RESCUE_ORDER_STATUS, dict } from '../../constants/dict'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const order = ref(null)
const assignForm = reactive({ handlerId: null, remark: '' })
const finishForm = reactive({ processResult: '', processImage: '', remark: '' })
const imageUploading = ref(false)
const processImageInputRef = ref()
const volunteers = ref([])
const volunteersLoading = ref(false)

const loadVolunteers = async () => {
  volunteersLoading.value = true
  try {
    const res = await getVolunteers()
    volunteers.value = res.data || []
  } catch (e) {
    ElMessage.error('加载志愿者列表失败：' + (e.message || e))
  } finally {
    volunteersLoading.value = false
  }
}

const volunteerLabel = v => v ? `${v.nickname || v.username}（ID:${v.id}）` : ''

const triggerProcessImageSelect = () => processImageInputRef.value?.click()
const onProcessImageChange = async event => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'RESCUE_ORDER_IMAGE', Number(route.params.id))
    finishForm.processImage = res.data.fileUrl
    ElMessage.success('处理图片上传成功')
  } catch (e) {
    ElMessage.error(e.message || '处理图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

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

onMounted(() => {
  loadDetail()
  loadVolunteers()
})
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
            <el-descriptions-item label="状态">{{ dict(RESCUE_ORDER_STATUS, order.status) }}</el-descriptions-item>
            <el-descriptions-item label="标题">{{ order.title }}</el-descriptions-item>
            <el-descriptions-item label="紧急程度">{{ dict(EMERGENCY_LEVEL, order.emergencyLevel) }}</el-descriptions-item>
            <el-descriptions-item label="地点">{{ order.location }}</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ order.handler?.nickname || order.handlerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ order.description }}</el-descriptions-item>
            <el-descriptions-item label="处理结果" :span="2">{{ order.processResult || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>工单操作</template>
          <el-form :inline="true">
            <el-form-item label="处理人">
              <el-select
                v-model="assignForm.handlerId"
                placeholder="请选择志愿者"
                :loading="volunteersLoading"
                filterable
                style="width: 260px"
              >
                <el-option
                  v-for="v in volunteers"
                  :key="v.id"
                  :label="volunteerLabel(v)"
                  :value="v.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="备注">
              <el-input v-model="assignForm.remark" style="width: 180px" />
            </el-form-item>
            <el-button type="primary" @click="run(() => assignRescueOrder(order.id, { handlerId: assignForm.handlerId, remark: assignForm.remark }))">分配</el-button>
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
              <div class="process-image-upload" @click="triggerProcessImageSelect">
                <el-image v-if="finishForm.processImage" class="process-image-preview" :src="finishForm.processImage" fit="cover">
                  <template #error><div class="process-image-placeholder"><el-icon><Plus /></el-icon><span>图片加载失败</span></div></template>
                </el-image>
                <div v-else class="process-image-placeholder"><el-icon><Plus /></el-icon><span>点击选择处理图片</span></div>
                <div class="process-image-mask">{{ imageUploading ? '上传中...' : '点击更换' }}</div>
                <input ref="processImageInputRef" class="process-image-input" type="file" accept="image/png,image/jpeg,image/gif,image/webp" @change="onProcessImageChange" />
              </div>
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
            <el-table-column prop="oldStatus" label="原状态" min-width="130"><template #default="{ row }">{{ dict(RESCUE_ORDER_STATUS, row.oldStatus) }}</template></el-table-column>
            <el-table-column prop="newStatus" label="新状态" min-width="130"><template #default="{ row }">{{ dict(RESCUE_ORDER_STATUS, row.newStatus) }}</template></el-table-column>
            <el-table-column prop="operationType" label="操作" min-width="120"><template #default="{ row }">{{ dict(RESCUE_ORDER_OPERATION, row.operationType) }}</template></el-table-column>
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
.process-image-upload { position: relative; width: 220px; height: 150px; border: 1px dashed #c0c4cc; border-radius: 8px; overflow: hidden; cursor: pointer; background: #f8fafc; }
.process-image-preview, .process-image-placeholder { width: 100%; height: 100%; }
.process-image-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px; color: #909399; font-size: 13px; }
.process-image-mask { position: absolute; inset: 0; display: grid; place-items: center; color: #fff; background: rgba(0,0,0,.45); opacity: 0; transition: opacity .2s; }
.process-image-upload:hover .process-image-mask { opacity: 1; }
.process-image-input { display: none; }
</style>
