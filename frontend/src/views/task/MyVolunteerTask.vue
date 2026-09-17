<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { finishVolunteerTask, getMyVolunteerTasks } from '../../api/volunteerTask'
import { uploadFile } from '../../api/file'
import { TASK_STATUS, TASK_TYPE, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const tasks = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const dialogVisible = ref(false)
const current = ref(null)
const finishForm = reactive({ content: '', imageUrl: '' })
const statuses = ['CLAIMED', 'FINISHED', 'REVIEWED', 'CANCELED']
const imageUploading = ref(false)
const finishImageInputRef = ref()
const triggerFinishImageSelect = () => finishImageInputRef.value?.click()
const onFinishImageChange = async event => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) { ElMessage.warning('请选择图片文件（png/jpg/gif/webp）'); return }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'TASK_IMAGE')
    finishForm.imageUrl = res.data.fileUrl
    ElMessage.success('图片上传成功')
  } catch (e) { ElMessage.error(e.message || '图片上传失败') }
  finally { imageUploading.value = false }
}

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getMyVolunteerTasks(query)
    tasks.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openFinish = row => {
  current.value = row
  finishForm.content = ''
  finishForm.imageUrl = ''
  dialogVisible.value = true
}

const submitFinish = async () => {
  try {
    await finishVolunteerTask(current.value.id, finishForm)
    ElMessage.success('已提交完成记录')
    dialogVisible.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadTasks)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>我的志愿者任务</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
              <el-option v-for="item in statuses" :key="item" :label="dict(TASK_STATUS, item)" :value="item" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadTasks">查询</el-button>
        </el-form>
        <el-table v-loading="loading" :data="tasks" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="taskType" label="类型" width="130"><template #default="{ row }">{{ dict(TASK_TYPE, row.taskType) }}</template></el-table-column>
          <el-table-column prop="status" label="状态" width="130"><template #default="{ row }">{{ dict(TASK_STATUS, row.status) }}</template></el-table-column>
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column label="操作" width="170">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/volunteer-tasks/${row.id}`)">详情</el-button>
              <el-button link type="success" @click="openFinish(row)">提交完成</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadTasks" />
      </el-card>
      <el-dialog v-model="dialogVisible" title="提交完成记录" width="520px">
        <el-form label-width="90px">
          <el-form-item label="完成内容"><el-input v-model="finishForm.content" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="完成图片">
            <div class="finish-upload" @click="triggerFinishImageSelect">
              <el-image v-if="finishForm.imageUrl" class="finish-preview" :src="finishForm.imageUrl" fit="cover" />
              <div v-else class="finish-placeholder"><el-icon><Plus /></el-icon><span>点击选择图片</span></div>
              <div class="finish-mask">{{ imageUploading ? '上传中...' : '点击更换' }}</div>
              <input ref="finishImageInputRef" class="finish-input" type="file" accept="image/png,image/jpeg,image/gif,image/webp" @change="onFinishImageChange" />
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFinish">提交</el-button>
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
.finish-upload { position: relative; width: 200px; height: 150px; border: 1px dashed #c0c4cc; border-radius: 8px; overflow: hidden; cursor: pointer; background: #f8fafc; }
.finish-preview, .finish-placeholder { width: 100%; height: 100%; }
.finish-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px; color: #909399; font-size: 13px; }
.finish-mask { position: absolute; inset: 0; display: grid; place-items: center; color: #fff; background: rgba(0,0,0,.45); opacity: 0; transition: opacity .2s; }
.finish-upload:hover .finish-mask { opacity: 1; }
.finish-input { display: none; }
</style>
