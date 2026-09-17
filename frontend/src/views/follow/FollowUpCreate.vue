<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createFollowUp } from '../../api/followUp'
import { getAdoptions } from '../../api/adoption'
import { uploadFile } from '../../api/file'

const router = useRouter()
const submitting = ref(false)
const imageUploading = ref(false)
const imageInputRef = ref()
const applications = ref([])
const form = reactive({
  applicationId: '',
  followTime: '',
  content: '',
  animalCondition: '',
  imageUrl: '',
  remark: ''
})

const triggerImageSelect = () => imageInputRef.value?.click()

const onImageChange = async event => {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'FOLLOW_UP_IMAGE')
    form.imageUrl = res.data.fileUrl
    ElMessage.success('回访图片上传成功')
  } catch (e) {
    ElMessage.error(e.message || '图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

const removeImage = () => { form.imageUrl = '' }

const submit = async () => {
  if (!form.applicationId) {
    ElMessage.warning('请选择领养成功的申请')
    return
  }
  if (!form.content) {
    ElMessage.warning('请填写回访内容')
    return
  }
  submitting.value = true
  try {
    await createFollowUp({
      ...form,
      applicationId: Number(form.applicationId),
      followTime: form.followTime || null
    })
    ElMessage.success('回访记录已保存')
    router.push('/follow-ups')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

const loadSuccessApplications = async () => {
  try {
    const res = await getAdoptions({ pageNum: 1, pageSize: 100, status: 'SUCCESS' })
    applications.value = res.data.records
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadSuccessApplications)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>新增回访记录</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-card shadow="never">
        <el-form label-width="110px">
          <el-form-item label="领养申请">
            <el-select v-model="form.applicationId" filterable placeholder="选择 SUCCESS 申请">
              <el-option
                v-for="item in applications"
                :key="item.id"
                :label="`#${item.id} ${item.animalName || item.animalId} - ${item.applicantName}`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="回访时间"><el-date-picker v-model="form.followTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
          <el-form-item label="回访内容"><el-input v-model="form.content" type="textarea" :rows="4" /></el-form-item>
          <el-form-item label="动物情况"><el-input v-model="form.animalCondition" /></el-form-item>
          <el-form-item label="回访图片">
            <div class="image-upload" @click="triggerImageSelect">
              <el-image v-if="form.imageUrl" class="image-preview" :src="form.imageUrl" fit="cover">
                <template #error>
                  <div class="image-placeholder"><el-icon><Plus /></el-icon><span>图片加载失败，点击重选</span></div>
                </template>
              </el-image>
              <div v-else class="image-placeholder">
                <el-icon><Plus /></el-icon>
                <span>{{ imageUploading ? '上传中...' : '点击选择图片' }}</span>
              </div>
              <div v-if="form.imageUrl" class="image-mask">点击更换</div>
              <input ref="imageInputRef" class="image-input" type="file" accept="image/png,image/jpeg,image/gif,image/webp" @change="onImageChange" />
            </div>
            <el-button v-if="form.imageUrl" link type="danger" @click.stop="removeImage">移除图片</el-button>
          </el-form-item>
          <el-form-item label="备注"><el-input v-model="form.remark" /></el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" @click="submit">保存</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(880px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.image-upload { position: relative; width: 220px; height: 150px; border: 1px dashed #c0c4cc; border-radius: 8px; overflow: hidden; cursor: pointer; background: #f8fafc; }
.image-preview, .image-placeholder { width: 100%; height: 100%; }
.image-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px; color: #909399; font-size: 13px; }
.image-mask { position: absolute; inset: 0; display: grid; place-items: center; color: #fff; background: rgba(0, 0, 0, .45); opacity: 0; transition: opacity .2s; }
.image-upload:hover .image-mask { opacity: 1; }
.image-input { display: none; }
</style>
