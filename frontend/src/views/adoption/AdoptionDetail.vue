<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  cancelAdoption,
  firstApproveAdoption,
  getAdoptionDetail,
  interviewAdoption,
  rejectAdoption,
  successAdoption,
  trialAdoption,
  trialFailedAdoption
} from '../../api/adoption'
import { useAuthStore } from '../../stores/auth'
import { ADOPTION_STATUS, ANIMAL_STATUS, ANIMAL_TYPE, dict } from '../../constants/dict'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const detail = ref(null)
const isAdmin = computed(() => {
  const roles = authStore.userInfo?.roles || []
  return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')
})

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getAdoptionDetail(route.params.id)
    detail.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const askRemark = async (title, defaultValue = '') => {
  const { value } = await ElMessageBox.prompt('请输入备注', title, { inputValue: defaultValue })
  return value
}

const runReview = async (title, action, requiredRemark = false) => {
  try {
    const remark = await askRemark(title, title)
    if (requiredRemark && !remark) {
      ElMessage.warning('备注不能为空')
      return
    }
    await action(detail.value.id, { remark })
    ElMessage.success('处理成功')
    loadDetail()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '已取消')
  }
}

const cancel = async () => {
  try {
    const reason = await askRemark('取消申请', '用户取消申请')
    await cancelAdoption(detail.value.id, { reason })
    ElMessage.success('已取消')
    loadDetail()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '已取消')
  }
}

onMounted(loadDetail)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>领养申请详情</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="detail">
        <el-card shadow="never">
          <template #header>
            <div class="card-head">
              <span>申请信息</span>
              <div>
                <el-button v-if="isAdmin && detail.status === 'PENDING'" type="success" @click="runReview('初审通过', firstApproveAdoption)">初审通过</el-button>
                <el-button v-if="isAdmin && detail.status === 'FIRST_APPROVED'" type="success" @click="runReview('进入面谈', interviewAdoption)">进入面谈</el-button>
                <el-button v-if="isAdmin && detail.status === 'INTERVIEWING'" type="success" @click="runReview('进入试养', trialAdoption)">进入试养</el-button>
                <el-button v-if="isAdmin && detail.status === 'TRIAL'" type="success" @click="runReview('领养成功', successAdoption)">领养成功</el-button>
                <el-button v-if="isAdmin && ['PENDING','FIRST_APPROVED','INTERVIEWING'].includes(detail.status)" type="danger" @click="runReview('拒绝申请', rejectAdoption, true)">拒绝</el-button>
                <el-button v-if="isAdmin && detail.status === 'TRIAL'" type="warning" @click="runReview('试养失败', trialFailedAdoption, true)">试养失败</el-button>
                <el-button v-if="['PENDING','FIRST_APPROVED'].includes(detail.status)" type="danger" plain @click="cancel">取消申请</el-button>
              </div>
            </div>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="申请ID">{{ detail.id }}</el-descriptions-item>
            <el-descriptions-item label="申请状态">{{ dict(ADOPTION_STATUS, detail.status) }}</el-descriptions-item>
            <el-descriptions-item label="申请人">{{ detail.applicantName }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ detail.phone }}</el-descriptions-item>
            <el-descriptions-item label="地址" :span="2">{{ detail.address || '-' }}</el-descriptions-item>
            <el-descriptions-item label="申请理由" :span="2">{{ detail.reason }}</el-descriptions-item>
            <el-descriptions-item label="养宠经验" :span="2">{{ detail.experience || '-' }}</el-descriptions-item>
            <el-descriptions-item label="审核备注" :span="2">{{ detail.reviewRemark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>动物信息</template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="编号">{{ detail.animal?.animalNo }}</el-descriptions-item>
            <el-descriptions-item label="名称">{{ detail.animal?.name }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ dict(ANIMAL_TYPE, detail.animal?.type) }}</el-descriptions-item>
            <el-descriptions-item label="动物状态">{{ dict(ANIMAL_STATUS, detail.animal?.status) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>审核日志</template>
          <el-table :data="detail.logs" border>
            <el-table-column prop="createTime" label="时间" min-width="170" />
            <el-table-column prop="reviewerName" label="操作人" min-width="120" />
            <el-table-column prop="operationType" label="操作" min-width="130" />
            <el-table-column prop="oldStatus" label="原状态" min-width="130" />
            <el-table-column prop="newStatus" label="新状态" min-width="130" />
            <el-table-column prop="remark" label="备注" min-width="180" />
          </el-table>
        </el-card>
      </template>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1100px, calc(100% - 32px)); margin: 0 auto; }
.header, .card-head { display: flex; justify-content: space-between; align-items: center; gap: 16px; }
.header { margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.block { margin-top: 18px; }
</style>
