<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { assignRescueOrder, cancelRescueOrder, closeRescueOrder, finishRescueOrder, getRescueOrderDetail, startRescueOrder } from '../../api/rescueOrder'
import { uploadFile } from '../../api/file'
import { useAuthStore } from '../../stores/auth'
import { EMERGENCY_LEVEL, RESCUE_ORDER_STATUS, RESCUE_ORDER_OPERATION, dict } from '../../constants/dict'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const order = ref(null)
const assignForm = reactive({ handlerId: '', remark: '' })
const finishForm = reactive({ processResult: '', processImage: '', remark: '' })

// ===== 角色判断 =====
const userRoles = computed(() => authStore.userInfo?.roles || [])
const isAdmin = computed(() => userRoles.value.includes('ADMIN') || userRoles.value.includes('SUPER_ADMIN'))
const isVolunteer = computed(() => userRoles.value.includes('VOLUNTEER') && !isAdmin.value)
const currentUserId = computed(() => authStore.userInfo?.id)

// ===== 权限判断 =====
// 是否是该工单的处理人
const isHandler = computed(() => order.value?.handlerId && order.value.handlerId === currentUserId.value)

// 管理员：可分配、关闭、取消
const canAssign = computed(() => isAdmin.value && order.value?.status === 'WAIT_ASSIGN')
const canClose = computed(() => isAdmin.value && ['WAIT_ASSIGN', 'WAIT_CONFIRM'].includes(order.value?.status))
const canCancel = computed(() => isAdmin.value && ['WAIT_ASSIGN', 'ASSIGNED'].includes(order.value?.status))

// 志愿者（当前处理人）：可开始处理、提交完成
const canStart = computed(() => isHandler.value && order.value?.status === 'ASSIGNED')
const canFinish = computed(() => isHandler.value && order.value?.status === 'PROCESSING')

// 结果图片上传
const imageUploading = ref(false)
const processImageInputRef = ref()

const triggerProcessImageSelect = () => {
  processImageInputRef.value?.click()
}

const onProcessImageChange = async e => {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'RESCUE_ORDER_IMAGE')
    finishForm.processImage = res.data.fileUrl
    ElMessage.success('图片上传成功')
  } catch (err) {
    ElMessage.error(err.message || '图片上传失败')
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
            <el-descriptions-item label="状态">{{ dict(RESCUE_ORDER_STATUS, order.status) }}</el-descriptions-item>
            <el-descriptions-item label="标题">{{ order.title }}</el-descriptions-item>
            <el-descriptions-item label="紧急程度">{{ dict(EMERGENCY_LEVEL, order.emergencyLevel) }}</el-descriptions-item>
            <el-descriptions-item label="地点">{{ order.location }}</el-descriptions-item>
            <el-descriptions-item label="处理人">{{ order.handler?.nickname || order.handlerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ order.description }}</el-descriptions-item>
            <el-descriptions-item label="处理结果" :span="2">{{ order.processResult || '-' }}</el-descriptions-item>
            <el-descriptions-item v-if="order.processImage" label="结果图片" :span="2">
              <el-image :src="order.processImage" fit="cover" style="width: 200px; height: 150px; border-radius: 6px;" />
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- ===== 管理员操作区 ===== -->
        <el-card v-if="isAdmin && (canAssign || canClose || canCancel)" class="block" shadow="never">
          <template #header>工单操作（管理员）</template>
          <el-form :inline="true">
            <template v-if="canAssign">
              <el-form-item label="处理人ID">
                <el-input v-model="assignForm.handlerId" placeholder="志愿者用户ID" style="width: 140px" />
              </el-form-item>
              <el-form-item label="备注">
                <el-input v-model="assignForm.remark" style="width: 180px" />
              </el-form-item>
              <el-button type="primary" @click="run(() => assignRescueOrder(order.id, { handlerId: Number(assignForm.handlerId), remark: assignForm.remark }))">分配</el-button>
            </template>
            <el-button v-if="canClose" type="success" @click="run(() => closeRescueOrder(order.id, { remark: '确认关闭' }))">关闭</el-button>
            <el-button v-if="canCancel" type="danger" @click="run(() => cancelRescueOrder(order.id, { reason: '管理员取消' }))">取消</el-button>
          </el-form>
        </el-card>

        <!-- ===== 志愿者操作区 ===== -->
        <el-card v-if="isHandler && (canStart || canFinish)" class="block" shadow="never">
          <template #header>工单处理（志愿者）</template>

          <!-- 开始处理 -->
          <div v-if="canStart">
            <el-button type="primary" @click="run(() => startRescueOrder(order.id))">开始处理</el-button>
          </div>

          <!-- 提交完成 -->
          <el-form v-if="canFinish" label-width="90px">
            <el-form-item label="处理结果">
              <el-input v-model="finishForm.processResult" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="结果图片">
              <div class="finish-upload" @click="triggerProcessImageSelect" :title="'点击选择图片'">
                <el-image v-if="finishForm.processImage" class="finish-preview" :src="finishForm.processImage" fit="cover">
                  <template #error>
                    <div class="finish-placeholder"><el-icon><Plus /></el-icon><span>图片加载失败</span></div>
                  </template>
                </el-image>
                <div v-else class="finish-placeholder">
                  <el-icon><Plus /></el-icon>
                  <span>选择图片</span>
                </div>
                <div class="finish-mask">
                  <span>{{ imageUploading ? '上传中...' : '点击更换' }}</span>
                </div>
                <input ref="processImageInputRef" type="file" accept="image/png,image/jpeg,image/gif,image/webp" class="finish-input" @change="onProcessImageChange" />
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

        <!-- ===== 提示（志愿者但非处理人） ===== -->
        <el-card v-if="isVolunteer && !isHandler" class="block" shadow="never">
          <el-alert type="info" title="你不是该工单的处理人，无法操作。" :closable="false" show-icon />
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>流转日志</template>
          <el-table :data="order.logs" border>
            <el-table-column prop="createTime" label="时间" min-width="170" />
            <el-table-column prop="operatorName" label="操作人" min-width="120" />
            <el-table-column prop="oldStatus" label="原状态" min-width="130">
              <template #default="{ row }">{{ dict(RESCUE_ORDER_STATUS, row.oldStatus) }}</template>
            </el-table-column>
            <el-table-column prop="newStatus" label="新状态" min-width="130">
              <template #default="{ row }">{{ dict(RESCUE_ORDER_STATUS, row.newStatus) }}</template>
            </el-table-column>
            <el-table-column prop="operationType" label="操作" min-width="120">
              <template #default="{ row }">{{ dict(RESCUE_ORDER_OPERATION, row.operationType) }}</template>
            </el-table-column>
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
/* 结果图片上传 */
.finish-upload {
  position: relative;
  width: 200px;
  height: 150px;
  border: 1px dashed #c0c4cc;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  background: #f8fafc;
}
.finish-upload:hover { border-color: #0f766e; }
.finish-preview, .finish-placeholder { width: 100%; height: 100%; }
.finish-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px; color: #909399; font-size: 13px; }
.finish-mask { position: absolute; inset: 0; display: grid; place-items: center; background: rgba(0,0,0,.45); color: #fff; font-size: 13px; opacity: 0; transition: opacity .2s; }
.finish-upload:hover .finish-mask { opacity: 1; }
.finish-input { display: none; }
</style>