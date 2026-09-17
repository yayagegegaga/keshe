<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createFollowUp } from '../../api/followUp'
import { getAdoptions } from '../../api/adoption'

const router = useRouter()
const submitting = ref(false)
const applications = ref([])
const form = reactive({
  applicationId: '',
  followTime: '',
  content: '',
  animalCondition: '',
  imageUrl: '',
  remark: ''
})

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
          <el-form-item label="图片URL"><el-input v-model="form.imageUrl" /></el-form-item>
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
</style>
