<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { uploadFile } from '../../api/file'

const router = useRouter()
const selectedFile = ref(null)
const bizType = ref('ANIMAL_IMAGE')
const bizId = ref('')
const uploading = ref(false)
const result = ref(null)
const types = ['ANIMAL_IMAGE', 'RESCUE_CLUE_IMAGE', 'RESCUE_ORDER_IMAGE', 'TASK_IMAGE', 'ADOPTION_IMAGE', 'FOLLOW_UP_IMAGE', 'OTHER']

const onFileChange = file => {
  selectedFile.value = file.raw
}

const submit = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请先选择文件')
    return
  }
  uploading.value = true
  try {
    const res = await uploadFile(selectedFile.value, bizType.value, bizId.value ? Number(bizId.value) : null)
    result.value = res.data
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    uploading.value = false
  }
}
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>文件上传</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form label-width="100px">
          <el-form-item label="业务类型">
            <el-select v-model="bizType">
              <el-option v-for="item in types" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="业务ID">
            <el-input v-model="bizId" placeholder="可选，例如动物ID、任务ID、回访记录ID" />
          </el-form-item>
          <el-form-item label="选择文件">
            <el-upload :auto-upload="false" :limit="1" :on-change="onFileChange">
              <el-button>选择文件</el-button>
            </el-upload>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="uploading" @click="submit">上传</el-button>
          </el-form-item>
        </el-form>
        <el-alert v-if="result" class="result" type="success" :title="`上传成功：${result.fileUrl}`" :closable="false" />
        <el-descriptions v-if="result" class="result" :column="1" border>
          <el-descriptions-item label="文件ID">{{ result.fileId }}</el-descriptions-item>
          <el-descriptions-item label="原始文件名">{{ result.originalName }}</el-descriptions-item>
          <el-descriptions-item label="业务类型">{{ result.fileType }}</el-descriptions-item>
          <el-descriptions-item label="业务ID">{{ result.bizId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="访问URL">{{ result.fileUrl }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(820px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.result { margin-top: 18px; }
</style>
