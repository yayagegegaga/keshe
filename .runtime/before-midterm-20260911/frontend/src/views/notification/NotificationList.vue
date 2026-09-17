<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getMyNotifications, getUnreadNotificationCount, markAllNotificationsRead, markNotificationRead } from '../../api/notification'

const router = useRouter()
const loading = ref(false)
const notifications = ref([])
const total = ref(0)
const unread = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, isRead: '', type: '' })
const types = ['', 'ADOPTION', 'TASK', 'RESCUE_ORDER', 'SYSTEM']

const loadData = async () => {
  loading.value = true
  try {
    const params = { ...query, isRead: query.isRead === '' ? null : query.isRead }
    const [listRes, countRes] = await Promise.all([getMyNotifications(params), getUnreadNotificationCount()])
    notifications.value = listRes.data.records
    total.value = listRes.data.total
    unread.value = countRes.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const read = async row => {
  try {
    await markNotificationRead(row.id)
    ElMessage.success('已标记已读')
    loadData()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const readAll = async () => {
  try {
    await markAllNotificationsRead()
    ElMessage.success('已全部标记已读')
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
        <h1>我的通知</h1>
        <div>
          <el-tag type="danger">未读 {{ unread }}</el-tag>
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button type="primary" @click="readAll">全部已读</el-button>
        </div>
      </div>
      <el-card shadow="never">
        <div class="filters">
          <el-select v-model="query.type" clearable placeholder="类型" @change="loadData">
            <el-option v-for="item in types" :key="item || 'ALL'" :label="item || '全部类型'" :value="item" />
          </el-select>
          <el-select v-model="query.isRead" clearable placeholder="状态" @change="loadData">
            <el-option label="全部" value="" />
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </div>
        <el-table v-loading="loading" :data="notifications" border>
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="type" label="类型" width="130" />
          <el-table-column prop="content" label="内容" min-width="260" />
          <el-table-column prop="createTime" label="时间" min-width="170" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.isRead ? 'info' : 'danger'">{{ row.isRead ? '已读' : '未读' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button v-if="!row.isRead" link type="primary" @click="read(row)">标记已读</el-button>
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
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
.header > div { display: flex; align-items: center; gap: 12px; }
h1 { margin: 0; font-size: 26px; }
.filters { display: flex; gap: 12px; margin-bottom: 16px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
