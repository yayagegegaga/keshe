<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getVolunteerTasks } from '../../api/volunteerTask'
import { TASK_STATUS, TASK_TYPE, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const tasks = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '', taskType: '' })
const statuses = ['WAIT_CLAIM', 'CLAIMED', 'FINISHED', 'REVIEWED', 'CANCELED']
const types = ['FEEDING', 'CLEANING', 'MEDICAL', 'FOLLOW_UP', 'TEMP_RESCUE']

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getVolunteerTasks(query)
    tasks.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

onMounted(loadTasks)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>志愿者任务</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
              <el-option v-for="item in statuses" :key="item" :label="dict(TASK_STATUS, item)" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="query.taskType" clearable placeholder="全部" style="width: 160px">
              <el-option v-for="item in types" :key="item" :label="dict(TASK_TYPE, item)" :value="item" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadTasks">查询</el-button>
        </el-form>
        <el-table v-loading="loading" :data="tasks" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="taskType" label="类型" width="130"><template #default="{ row }">{{ dict(TASK_TYPE, row.taskType) }}</template></el-table-column>
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column prop="status" label="状态" width="130"><template #default="{ row }">{{ dict(TASK_STATUS, row.status) }}</template></el-table-column>
          <el-table-column prop="volunteerId" label="志愿者ID" width="110" />
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/volunteer-tasks/${row.id}`)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadTasks" />
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
