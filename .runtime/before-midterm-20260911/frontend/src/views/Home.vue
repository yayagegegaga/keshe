<script setup>
import { onMounted, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Connection, Refresh, SwitchButton, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '../api/request'
import { useAuthStore } from '../stores/auth'
import { getStatisticsOverview } from '../api/statistics'

const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)
const health = ref('')
const error = ref('')
const statistics = ref(null)

// 判断当前用户角色
const userRoles = computed(() => authStore.userInfo?.roles || [])
const isAdmin = computed(() => userRoles.value.includes('ADMIN') || userRoles.value.includes('SUPER_ADMIN'))
const isVolunteer = computed(() => userRoles.value.includes('VOLUNTEER') && !isAdmin.value)

const loadHealth = async () => {
  loading.value = true
  error.value = ''
  try {
    const res = await request.get('/health')
    health.value = res.data
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

const loadMe = async () => {
  try {
    await authStore.fetchMe()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const loadStatistics = async () => {
  try {
    const res = await getStatisticsOverview()
    statistics.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const logout = async () => {
  await authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}

// ========== 统计卡片跳转方法 ==========
const goToAnimalList = () => {
  router.push('/animals')
}

const goToAdoptableAnimals = () => {
  router.push({
    path: '/animals',
    query: { status: 'ADOPTABLE' }
  })
}

const goToNotifications = () => {
  router.push('/notifications')
}

const goToClueManage = () => {
  router.push('/rescue-clues/manage')
}

const goToTaskManage = () => {
  router.push('/volunteer-task-manage')
}

const goToAdoptionManage = () => {
  router.push('/adoptions/manage')
}

onMounted(async () => {
  await Promise.all([loadHealth(), loadMe(), loadStatistics()])
})
</script>

<template>
  <main class="page home-page">
    <section class="shell">
      <div class="header">
        <div>
          <p class="eyebrow">Stray Animal Platform</p>
          <h1>流浪动物救助与领养协同管理平台</h1>
        </div>
        <div class="hero-visual" aria-hidden="true"><span>🐾</span><small>CARE<br />TOGETHER</small></div>
        <el-button type="danger" plain @click="logout">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>

      <!-- ========== 操作按钮区域 ========== -->
      <div class="actions">
        <!-- 所有用户都显示：动物列表 -->
        <el-button type="primary" @click="router.push('/animals')">动物列表</el-button>
        <el-button @click="router.push('/rescue-clues/submit')">提交救助线索</el-button>

        <!-- ===== 志愿者专属：志愿者任务 ===== -->
        <el-button v-if="isVolunteer" @click="router.push('/volunteer-tasks')">志愿者任务</el-button>

        <!-- ===== 管理员专属：工单管理（保留管理后台，删除前台"救助工单"） ===== -->
        <el-button v-if="isAdmin" @click="router.push('/rescue-orders-manage')">工单管理</el-button>

        <!-- 所有用户都显示 -->
        <el-button @click="router.push('/adoptions/my')">我的领养申请</el-button>
        <el-button @click="router.push('/follow-ups')">回访记录</el-button>
        <el-button @click="router.push('/notifications')">我的通知</el-button>
        <el-button @click="router.push('/files/upload')">文件上传</el-button>

        <!-- ===== 志愿者专属：我的任务 ===== -->
        <el-button v-if="isVolunteer" @click="router.push('/volunteer-tasks/my')">我的任务</el-button>

        <!-- ===== 管理员专属按钮 ===== -->
        <template v-if="isAdmin">
          <el-button @click="router.push('/animal-manage')">动物管理</el-button>
          <el-button @click="router.push('/rescue-clues/manage')">线索审核</el-button>
          <el-button @click="router.push('/volunteer-task-manage')">任务管理</el-button>
          <el-button @click="router.push('/adoptions/manage')">领养审核</el-button>
        </template>
      </div>

      <div class="grid">
        <el-card shadow="never" class="stats-card">
          <template #header>
            <div class="card-title">
              首页统计
            </div>
          </template>
          <div class="stats-grid" v-if="statistics">
            <!-- ===== 所有用户可见 ===== -->
            <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToAnimalList" title="点击查看全部动物列表">
              <strong>{{ statistics.animalCount }}</strong>
              <span>动物总数</span>
            </div>

            <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToAdoptableAnimals" title="点击查看可领养动物列表">
              <strong>{{ statistics.adoptableAnimalCount }}</strong>
              <span>可领养动物</span>
            </div>

            <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToNotifications" title="点击查看我的通知">
              <strong>{{ statistics.unreadNotificationCount }}</strong>
              <span>未读通知</span>
            </div>

            <!-- ===== 管理员专属统计（删除所有工单相关统计） ===== -->
            <template v-if="isAdmin">
              <!-- 待审核线索 → 线索审核 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToClueManage" title="点击进入线索审核">
                <strong>{{ statistics.pendingClueCount }}</strong>
                <span>待审核线索</span>
              </div>

              <!-- 任务总数 → 任务管理 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToTaskManage" title="点击进入任务管理">
                <strong>{{ statistics.volunteerTaskCount }}</strong>
                <span>任务总数</span>
              </div>

              <!-- 待审核任务 → 任务管理 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToTaskManage" title="点击进入任务管理">
                <strong>{{ statistics.pendingReviewTaskCount }}</strong>
                <span>待审核任务</span>
              </div>

              <!-- 待领取任务 → 任务管理 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToTaskManage" title="点击进入任务管理">
                <strong>{{ statistics.waitClaimTaskCount }}</strong>
                <span>待领取任务</span>
              </div>

              <!-- 申请总数 → 领养审核 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToAdoptionManage" title="点击进入领养审核">
                <strong>{{ statistics.adoptionApplicationCount }}</strong>
                <span>申请总数</span>
              </div>

              <!-- 待审核申请 → 领养审核 -->
              <div class="clickable-item" role="link" tabindex="0" @keydown.enter="$event.currentTarget.click()" @click="goToAdoptionManage" title="点击进入领养审核">
                <strong>{{ statistics.pendingAdoptionCount }}</strong>
                <span>待审核申请</span>
              </div>
            </template>
          </div>
          <el-alert v-else type="warning" title="统计数据暂不可用" :closable="false" />
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><User /></el-icon>
              当前用户
            </div>
          </template>
          <el-descriptions v-if="authStore.userInfo" :column="1" border>
            <el-descriptions-item label="用户ID">{{ authStore.userInfo.id }}</el-descriptions-item>
            <el-descriptions-item label="用户名">{{ authStore.userInfo.username }}</el-descriptions-item>
            <el-descriptions-item label="昵称">{{ authStore.userInfo.nickname || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">
              <el-tag
                v-for="role in authStore.userInfo.roles"
                :key="role"
                class="role-tag"
                type="success"
              >
                {{ role }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <el-card shadow="never">
          <template #header>
            <div class="card-title">
              <el-icon><Connection /></el-icon>
              后端健康检查
            </div>
          </template>
          <el-alert
            v-if="error"
            type="error"
            :title="error"
            show-icon
            :closable="false"
          />
          <el-result
            v-else
            icon="success"
            title="服务运行中"
            :sub-title="health || '等待后端响应'"
          >
            <template #extra>
              <el-button type="primary" :loading="loading" @click="loadHealth">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </template>
          </el-result>
        </el-card>
      </div>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
  color: #1f2937;
}

.shell {
  width: min(1100px, calc(100% - 32px));
  margin: 0 auto;
  padding: 40px 0;
}

.header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #0f766e;
  font-size: 14px;
  font-weight: 700;
}

h1 {
  margin: 0;
  font-size: 30px;
  line-height: 1.25;
}

.subtitle {
  margin: 12px 0 0;
  color: #64748b;
}

.grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 1fr);
  gap: 18px;
}

.stats-card {
  grid-column: 1 / -1;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px;
}

.stats-grid div {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #f8fafc;
}

.stats-grid strong {
  display: block;
  font-size: 24px;
  color: #0f766e;
}

.stats-grid span {
  color: #64748b;
}

.actions {
  display: flex;
  gap: 12px;
  margin-bottom: 18px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 700;
}

.role-tag {
  margin-right: 8px;
}

@media (max-width: 760px) {
  .header,
  .grid {
    grid-template-columns: 1fr;
  }

  .header {
    flex-direction: column;
  }
}
</style>
