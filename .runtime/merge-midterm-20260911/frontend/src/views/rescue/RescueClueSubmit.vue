<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createRescueClue } from '../../api/rescueClue'
import { uploadFile } from '../../api/file'
import { ANIMAL_TYPE, EMERGENCY_LEVEL, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const formRef = ref()
const form = reactive({
  animalType: 'CAT',
  location: '',
  description: '',
  emergencyLevel: 'MEDIUM',
  imageUrl: '',
  contact: ''
})
const rules = {
  animalType: [{ required: true, message: '请选择动物类型', trigger: 'change' }],
  location: [{ required: true, message: '请输入发现地点', trigger: 'blur' }],
  description: [{ required: true, message: '请输入线索描述', trigger: 'blur' }],
  emergencyLevel: [{ required: true, message: '请选择紧急程度', trigger: 'change' }]
}

const animalTypeOptions = ['CAT', 'DOG', 'OTHER']
const emergencyOptions = ['LOW', 'MEDIUM', 'HIGH', 'URGENT']
const animalTypeLabels = Object.fromEntries(animalTypeOptions.map(code => [code, dict(ANIMAL_TYPE, code)]))
const emergencyLabels = Object.fromEntries(emergencyOptions.map(code => [code, dict(EMERGENCY_LEVEL, code)]))

// 图片上传
const imageUploading = ref(false)
const imageInputRef = ref()

const triggerImageSelect = () => {
  imageInputRef.value?.click()
}

const onImageChange = async e => {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  imageUploading.value = true
  try {
    const res = await uploadFile(file, 'RESCUE_CLUE_IMAGE')
    form.imageUrl = res.data.fileUrl
    ElMessage.success('图片上传成功')
  } catch (err) {
    ElMessage.error(err.message || '图片上传失败')
  } finally {
    imageUploading.value = false
  }
}

const removeImage = () => {
  form.imageUrl = ''
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await createRescueClue(form)
    ElMessage.success('线索已提交')
    router.push('/')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="page">
    <section class="panel">
      <div class="header">
        <h1>提交救助线索</h1>
        <el-button @click="router.push('/')">返回</el-button>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <el-form-item label="动物类型" prop="animalType">
          <el-select v-model="form.animalType">
            <el-option v-for="code in animalTypeOptions" :key="code" :label="animalTypeLabels[code]" :value="code" />
          </el-select>
        </el-form-item>
        <el-form-item label="发现地点" prop="location">
          <el-input v-model="form.location" placeholder="如：一食堂门口、图书馆后门、3号宿舍楼下" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="紧急程度" prop="emergencyLevel">
          <el-select v-model="form.emergencyLevel">
            <el-option v-for="code in emergencyOptions" :key="code" :label="emergencyLabels[code]" :value="code" />
          </el-select>
        </el-form-item>
        <el-form-item label="现场图片">
          <div class="image-upload" @click="triggerImageSelect" :title="'点击选择图片'">
            <el-image v-if="form.imageUrl" class="image-preview" :src="form.imageUrl" fit="cover">
              <template #error>
                <div class="image-placeholder"><el-icon><Plus /></el-icon><span>图片加载失败</span></div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <el-icon><Plus /></el-icon>
              <span>选择图片</span>
            </div>
            <div class="image-mask">
              <span>{{ imageUploading ? '上传中...' : '点击更换' }}</span>
            </div>
            <input ref="imageInputRef" type="file" accept="image/png,image/jpeg,image/gif,image/webp" class="image-input" @change="onImageChange" />
          </div>
          <el-button v-if="form.imageUrl" class="remove-btn" link type="danger" @click="removeImage">移除图片</el-button>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.contact" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="submit">提交</el-button>
        </el-form-item>
      </el-form>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.panel { width: min(720px, calc(100% - 32px)); margin: 0 auto; padding: 24px; background: #fff; border: 1px solid #dbe4e0; border-radius: 8px; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }

.image-upload {
  position: relative;
  width: 200px;
  height: 150px;
  border: 1px dashed #c0c4cc;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  background: #f8fafc;
}

.image-upload:hover {
  border-color: #0f766e;
}

.image-preview,
.image-placeholder {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: #909399;
  font-size: 13px;
}

.image-mask {
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

.image-upload:hover .image-mask {
  opacity: 1;
}

.image-input {
  display: none;
}

.remove-btn {
  margin-top: 8px;
}
</style>
