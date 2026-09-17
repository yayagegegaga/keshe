<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelVolunteerTask, createVolunteerTask, deleteVolunteerTask, getVolunteerTasks, reviewVolunteerTask, updateVolunteerTask } from '../../api/volunteerTask'
import { TASK_STATUS, TASK_TYPE, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const editingId = ref(null)
const tasks = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const form = reactive({ title: '', taskType: 'FEEDING', description: '', location: '' })
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

const openCreate = () => {
  editingId.value = null
  Object.assign(form, { title: '', taskType: 'FEEDING', description: '', location: '' })
  dialogVisible.value = true
}

const openEdit = row => {
  editingId.value = row.id
  Object.assign(form, { title: row.title, taskType: row.taskType, description: row.description || '', location: row.location })
  dialogVisible.value = true
}

const submit = async () => {
  try {
    if (editingId.value) {
      await updateVolunteerTask(editingId.value, form)
      ElMessage.success('修改成功')
    } else {
      await createVolunteerTask(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadTasks()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const review = async (row, approved) => {
  try {
    await reviewVolunteerTask(row.id, { approved, reviewRemark: approved ? '审核通过' : '退回重新提交' })
    ElMessage.success('审核完成')
    loadTasks()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const cancel = async row => {
  try {
    await cancelVolunteerTask(row.id, { reason: '管理员取消' })
    ElMessage.success('已取消')
    loadTasks()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const remove = async row => {
  await ElMessageBox.confirm(`确认删除任务 ${row.title}？`, '删除确认', { type: 'warning' })
  try {
    await deleteVolunteerTask(row.id)
    ElMessage.success('已删除')
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
        <h1>志愿者任务管理</h1>
        <div>
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button type="primary" @click="openCreate">发布任务</el-button>
        </div>
      </div>
      <el-card shadow="never">
        <el-table v-loading="loading" :data="tasks" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="taskType" label="类型" width="130"><template #default="{ row }">{{ dict(TASK_TYPE, row.taskType) }}</template></el-table-column>
          <el-table-column prop="status" label="状态" width="130"><template #default="{ row }">{{ dict(TASK_STATUS, row.status) }}</template></el-table-column>
          <el-table-column prop="volunteerId" label="志愿者ID" width="110" />
          <el-table-column label="操作" width="310">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/volunteer-tasks/${row.id}`)">详情</el-button>
              <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button link type="success" @click="review(row, true)">通过</el-button>
              <el-button link type="warning" @click="review(row, false)">退回</el-button>
              <el-button link type="danger" @click="cancel(row)">取消</el-button>
              <el-button link type="danger" @click="remove(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadTasks" />
      </el-card>
      <el-dialog v-model="dialogVisible" :title="editingId ? '编辑任务' : '发布任务'" width="560px">
        <el-form label-width="90px">
          <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="类型">
            <el-select v-model="form.taskType">
              <el-option v-for="item in types" :key="item" :label="dict(TASK_TYPE, item)" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
          <el-form-item label="地点"><el-input v-model="form.location" /></el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">保存</el-button>
        </template>
      </el-dialog>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1180px, calc(100% - 32px)); margin: 0 auto; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
h1 { margin: 0; font-size: 26px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
