<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteFollowUp, getFollowUps, getMyFollowUps } from '../../api/followUp'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const records = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const isAdmin = () => {
  const roles = authStore.userInfo?.roles || []
  return roles.includes('ADMIN') || roles.includes('SUPER_ADMIN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = isAdmin() ? await getFollowUps(query) : await getMyFollowUps(query)
    records.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const remove = async row => {
  await ElMessageBox.confirm('确认删除该回访记录？', '删除确认', { type: 'warning' })
  try {
    await deleteFollowUp(row.id)
    ElMessage.success('已删除')
    loadData()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>回访记录</h1>
        <div>
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button v-if="isAdmin()" type="primary" @click="router.push('/follow-ups/create')">新增回访</el-button>
        </div>
      </div>
      <el-card shadow="never">
        <div class="filters">
          <el-input v-model="query.keyword" clearable placeholder="回访内容/动物情况" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData">查询</el-button>
        </div>
        <el-table v-loading="loading" :data="records" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalName" label="动物" min-width="120" />
          <el-table-column prop="adopterName" label="领养人" min-width="120" />
          <el-table-column prop="followTime" label="回访时间" min-width="170" />
          <el-table-column prop="animalCondition" label="动物情况" min-width="180" />
          <el-table-column prop="content" label="内容" min-width="240" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/follow-ups/${row.id}`)">详情</el-button>
              <el-button v-if="isAdmin()" link type="danger" @click="remove(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadData" />
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1180px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
.filters .el-input { max-width: 260px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
