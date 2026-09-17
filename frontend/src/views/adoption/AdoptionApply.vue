<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAnimalDetail } from '../../api/animal'
import { applyAdoption } from '../../api/adoption'
import { ANIMAL_STATUS, ANIMAL_TYPE, dict } from '../../constants/dict'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const animal = ref(null)
const form = reactive({
  animalId: Number(route.params.animalId),
  applicantName: '',
  phone: '',
  address: '',
  reason: '',
  experience: ''
})

const loadAnimal = async () => {
  loading.value = true
  try {
    const res = await getAnimalDetail(route.params.animalId)
    animal.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const submit = async () => {
  if (animal.value?.status !== 'ADOPTABLE') {
    ElMessage.warning('当前动物不是可领养状态')
    return
  }
  submitting.value = true
  try {
    const res = await applyAdoption(form)
    ElMessage.success('申请已提交')
    router.push(`/adoptions/${res.data.id}`)
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    submitting.value = false
  }
}

onMounted(loadAnimal)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>提交领养申请</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-card v-if="animal" shadow="never" class="block">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="动物编号">{{ animal.animalNo }}</el-descriptions-item>
          <el-descriptions-item label="名称">{{ animal.name }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ dict(ANIMAL_TYPE, animal.type) }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ dict(ANIMAL_STATUS, animal.status) }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
      <el-alert v-if="animal && animal.status !== 'ADOPTABLE'" class="block" type="warning" title="当前动物不是可领养状态，不能提交申请" show-icon :closable="false" />
      <el-card shadow="never" class="block">
        <el-form label-width="96px">
          <el-form-item label="申请人"><el-input v-model="form.applicantName" /></el-form-item>
          <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="居住地址"><el-input v-model="form.address" /></el-form-item>
          <el-form-item label="申请理由"><el-input v-model="form.reason" type="textarea" :rows="4" /></el-form-item>
          <el-form-item label="养宠经验"><el-input v-model="form.experience" type="textarea" :rows="3" /></el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="submitting" :disabled="animal?.status !== 'ADOPTABLE'" @click="submit">提交申请</el-button>
            <el-button @click="router.push('/animals')">返回动物列表</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(900px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.block { margin-bottom: 18px; }
</style>
