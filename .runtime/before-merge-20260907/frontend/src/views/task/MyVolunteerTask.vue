<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { finishVolunteerTask, getMyVolunteerTasks } from '../../api/volunteerTask'

const router = useRouter()
const loading = ref(false)
const tasks = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const dialogVisible = ref(false)
const current = ref(null)
const finishForm = reactive({ content: '', imageUrl: '' })

const loadTasks = async () => {
  loading.value = true
  try {
    const res = await getMyVolunteerTasks(query)
    tasks.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openFinish = row => {
  current.value = row
  finishForm.content = ''
  finishForm.imageUrl = ''
  dialogVisible.value = true
}

const submitFinish = async () => {
  try {
    await finishVolunteerTask(current.value.id, finishForm)
    ElMessage.success('已提交完成记录')
    dialogVisible.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadTasks)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>我的志愿者任务</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
              <el-option v-for="item in ['CLAIMED','FINISHED','REVIEWED','CANCELED']" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadTasks">查询</el-button>
        </el-form>
        <el-table v-loading="loading" :data="tasks" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="taskType" label="类型" width="130" />
          <el-table-column prop="status" label="状态" width="130" />
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column label="操作" width="170">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/volunteer-tasks/${row.id}`)">详情</el-button>
              <el-button link type="success" @click="openFinish(row)">提交完成</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadTasks" />
      </el-card>
      <el-dialog v-model="dialogVisible" title="提交完成记录" width="520px">
        <el-form label-width="90px">
          <el-form-item label="完成内容"><el-input v-model="finishForm.content" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="图片URL"><el-input v-model="finishForm.imageUrl" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitFinish">提交</el-button>
        </template>
      </el-dialog>
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
