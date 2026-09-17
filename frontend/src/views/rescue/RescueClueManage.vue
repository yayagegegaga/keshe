<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRescueClues, getRescueClueDetail, reviewRescueClue } from '../../api/rescueClue'
import { ANIMAL_TYPE, EMERGENCY_LEVEL, RESCUE_CLUE_STATUS, dict } from '../../constants/dict'

const router = useRouter()
const loading = ref(false)
const clues = ref([])
const total = ref(0)
const detail = ref(null)
const detailVisible = ref(false)
const showDetail = async row => {
  try {
    const res = await getRescueClueDetail(row.id)
    detail.value = res.data
    detailVisible.value = true
  } catch (e) { ElMessage.error(e.message) }
}
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  status: 'PENDING',
  keyword: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRescueClues(query)
    clues.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

const review = async (row, approved) => {
  try {
    const { value } = await ElMessageBox.prompt(
      approved ? '请输入审核备注（可选）' : '请输入拒绝原因',
      approved ? '审核通过' : '拒绝线索',
      { inputValue: approved ? '审核通过，已转工单' : '线索无效' }
    )
    await reviewRescueClue(row.id, {
      approved,
      reviewRemark: value,
      createOrder: approved
    })
    ElMessage.success(approved ? '审核通过' : '已拒绝')
    loadData()
  } catch (e) {
    if (e !== 'cancel' && e !== 'close') ElMessage.error(e.message)
  }
}

const search = () => {
  query.pageNum = 1
  loadData()
}

onMounted(loadData)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>线索审核管理</h1>
        <div class="header-actions">
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button @click="router.push('/rescue-clues/submit')">提交线索</el-button>
        </div>
      </div>

      <el-card shadow="never">
        <div class="filters">
          <el-select v-model="query.status" placeholder="状态" clearable @change="search">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已转工单" value="CONVERTED" />
            <el-option label="无效线索" value="INVALID" />
            <el-option label="已取消" value="CANCELED" />
            <el-option label="全部" value="" />
          </el-select>
          <el-input v-model="query.keyword" clearable placeholder="地点/描述" @keyup.enter="search" />
          <el-button type="primary" @click="search">查询</el-button>
        </div>

        <el-table v-loading="loading" :data="clues" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalType" label="动物类型" width="100"><template #default="{ row }">{{ dict(ANIMAL_TYPE, row.animalType) }}</template></el-table-column>
          <el-table-column prop="location" label="地点" min-width="160" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="100"><template #default="{ row }">{{ dict(EMERGENCY_LEVEL, row.emergencyLevel) }}</template></el-table-column>
          <el-table-column prop="status" label="状态" width="110"><template #default="{ row }">{{ dict(RESCUE_CLUE_STATUS, row.status) }}</template></el-table-column>
          <el-table-column prop="createTime" label="提交时间" min-width="170" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="showDetail(row)">详情</el-button>
              <el-button v-if="row.status === 'PENDING'" link type="success" @click="review(row, true)">通过</el-button>
              <el-button v-if="row.status === 'PENDING'" link type="danger" @click="review(row, false)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pager"
          layout="total, prev, pager, next"
          :total="total"
          :page-size="query.pageSize"
          v-model:current-page="query.pageNum"
          @current-change="loadData"
        />
      </el-card>
      <el-dialog v-model="detailVisible" title="救助线索详情" width="min(640px, 92vw)">
        <el-descriptions v-if="detail" :column="1" border>
          <el-descriptions-item label="动物类型">{{ dict(ANIMAL_TYPE, detail.animalType) }}</el-descriptions-item>
          <el-descriptions-item label="地点">{{ detail.location }}</el-descriptions-item>
          <el-descriptions-item label="描述">{{ detail.description }}</el-descriptions-item>
          <el-descriptions-item label="紧急程度">{{ dict(EMERGENCY_LEVEL, detail.emergencyLevel) }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ detail.contact || '未填写' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注">{{ detail.reviewRemark || '暂无' }}</el-descriptions-item>
          <el-descriptions-item v-if="detail.imageUrl" label="现场图片">
            <el-image :src="detail.imageUrl" :preview-src-list="[detail.imageUrl]" fit="contain" style="max-width: 100%; max-height: 280px" />
          </el-descriptions-item>
        </el-descriptions>
      </el-dialog>
    </section>
  </main>
</template>

<style scoped>
.page { min-height: 100vh; background: #f5f7fb; padding: 32px 0; }
.shell { width: min(1120px, calc(100% - 32px)); margin: 0 auto; }
h1 { margin: 0 0 18px; font-size: 26px; }
.pager { margin-top: 16px; justify-content: flex-end; }
</style>
