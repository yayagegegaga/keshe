<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createRescueClue } from '../../api/rescueClue'

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
            <el-option label="CAT" value="CAT" />
            <el-option label="DOG" value="DOG" />
            <el-option label="OTHER" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="发现地点" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="紧急程度" prop="emergencyLevel">
          <el-select v-model="form.emergencyLevel">
            <el-option label="LOW" value="LOW" />
            <el-option label="MEDIUM" value="MEDIUM" />
            <el-option label="HIGH" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片 URL">
          <el-input v-model="form.imageUrl" />
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
</style>
