<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { cancelVolunteerTask, claimVolunteerTask, finishVolunteerTask, getVolunteerTaskDetail, reviewVolunteerTask } from '../../api/volunteerTask'
import { uploadFile } from '../../api/file'
import { TASK_STATUS, TASK_TYPE, dict } from '../../constants/dict'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const task = ref(null)
const finishForm = reactive({ content: '', imageUrl: '' })
const reviewForm = reactive({ approved: true, reviewRemark: '' })

// 完成图片上传
const imageUploading = ref(false)
const finishImageInputRef = ref()

const triggerFinishImageSelect = () => {
  finishImageInputRef.value?.click()
}

const onFinishImageChange = async e => {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'TASK_IMAGE')
    finishForm.imageUrl = res.data.fileUrl
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
    const res = await getVolunteerTaskDetail(route.params.id)
    task.value = res.data
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
        <h1>任务详情</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="task">
        <el-card shadow="never">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="标题">{{ task.title }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ dict(TASK_STATUS, task.status) }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ dict(TASK_TYPE, task.taskType) }}</el-descriptions-item>
            <el-descriptions-item label="地点">{{ task.location }}</el-descriptions-item>
            <el-descriptions-item label="发布人">{{ task.publisher?.nickname || task.publisherId }}</el-descriptions-item>
            <el-descriptions-item label="志愿者">{{ task.volunteer?.nickname || task.volunteerId || '-' }}</el-descriptions-item>
            <el-descriptions-item label="描述" :span="2">{{ task.description || '-' }}</el-descriptions-item>
            <el-descriptions-item label="审核备注" :span="2">{{ task.reviewRemark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        <el-card class="block" shadow="never">
          <template #header>操作</template>
          <el-button type="primary" @click="run(() => claimVolunteerTask(task.id))">领取任务</el-button>
          <el-button type="danger" @click="run(() => cancelVolunteerTask(task.id, { reason: '管理员取消' }))">取消任务</el-button>
          <el-divider />
          <el-form label-width="90px">
            <el-form-item label="完成内容"><el-input v-model="finishForm.content" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="完成图片">
              <div class="finish-upload" @click="triggerFinishImageSelect" :title="'点击选择图片'">
                <el-image v-if="finishForm.imageUrl" class="finish-preview" :src="finishForm.imageUrl" fit="cover">
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
                <input ref="finishImageInputRef" type="file" accept="image/png,image/jpeg,image/gif,image/webp" class="finish-input" @change="onFinishImageChange" />
              </div>
            </el-form-item>
            <el-form-item><el-button type="primary" @click="run(() => finishVolunteerTask(task.id, finishForm))">提交完成</el-button></el-form-item>
          </el-form>
          <el-divider />
          <el-form :inline="true">
            <el-form-item label="审核">
              <el-radio-group v-model="reviewForm.approved">
                <el-radio :label="true">通过</el-radio>
                <el-radio :label="false">退回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="备注"><el-input v-model="reviewForm.reviewRemark" /></el-form-item>
            <el-button type="success" @click="run(() => reviewVolunteerTask(task.id, reviewForm))">提交审核</el-button>
          </el-form>
        </el-card>
        <el-card class="block" shadow="never">
          <template #header>完成记录</template>
          <el-table :data="task.records" border>
            <el-table-column prop="createTime" label="时间" min-width="170" />
            <el-table-column prop="volunteerId" label="志愿者ID" width="110" />
            <el-table-column prop="content" label="内容" min-width="240" />
            <el-table-column prop="imageUrl" label="图片" min-width="180" />
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

/* 完成图片上传 */
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

.finish-upload:hover {
  border-color: #0f766e;
}

.finish-preview,
.finish-placeholder {
  width: 100%;
  height: 100%;
}

.finish-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
}

.finish-mask {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 13px;
  opacity: 0;
  transition: opacity 0.2s;
}

.finish-upload:hover .finish-mask {
  opacity: 1;
}

.finish-input {
  display: none;
}
</style>
