<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getFollowUpDetail } from '../../api/followUp'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getFollowUpDetail(route.params.id)
    detail.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>回访详情</h1>
        <el-button @click="router.back()">返回</el-button>
      </div>
      <el-skeleton v-if="loading" :rows="5" animated />
      <el-card v-else-if="detail" shadow="never">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="记录ID">{{ detail.id }}</el-descriptions-item>
          <el-descriptions-item label="申请ID">{{ detail.applicationId }}</el-descriptions-item>
          <el-descriptions-item label="动物">{{ detail.animalName }}</el-descriptions-item>
          <el-descriptions-item label="领养人">{{ detail.adopterName }}</el-descriptions-item>
          <el-descriptions-item label="回访时间">{{ detail.followTime }}</el-descriptions-item>
          <el-descriptions-item label="动物情况">{{ detail.animalCondition || '-' }}</el-descriptions-item>
          <el-descriptions-item label="回访内容" :span="2">{{ detail.content }}</el-descriptions-item>
          <el-descriptions-item label="图片URL" :span="2">{{ detail.imageUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(960px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
</style>
