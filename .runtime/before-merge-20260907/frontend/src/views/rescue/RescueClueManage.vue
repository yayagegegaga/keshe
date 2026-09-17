<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getRescueClues, reviewRescueClue } from '../../api/rescueClue'

const loading = ref(false)
const clues = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, status: '' })
const dialogVisible = ref(false)
const current = ref(null)
const reviewForm = reactive({ approved: true, reviewRemark: '', createOrder: true, orderTitle: '', orderDescription: '' })

const loadClues = async () => {
  loading.value = true
  try {
    const res = await getRescueClues(query)
    clues.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const openReview = row => {
  current.value = row
  reviewForm.approved = true
  reviewForm.reviewRemark = ''
  reviewForm.createOrder = true
  reviewForm.orderTitle = `救助工单-${row.location}`
  reviewForm.orderDescription = row.description
  dialogVisible.value = true
}

const submitReview = async () => {
  try {
    await reviewRescueClue(current.value.id, reviewForm)
    ElMessage.success('审核完成')
    dialogVisible.value = false
    loadClues()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

onMounted(loadClues)
</script>

<template>
  <main class="page">
    <section class="shell">
      <h1>救助线索审核</h1>
      <el-card shadow="never">
        <el-form :inline="true">
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 160px">
              <el-option label="PENDING" value="PENDING" />
              <el-option label="CONVERTED" value="CONVERTED" />
              <el-option label="INVALID" value="INVALID" />
              <el-option label="CANCELED" value="CANCELED" />
            </el-select>
          </el-form-item>
          <el-button type="primary" @click="loadClues">查询</el-button>
        </el-form>
        <el-table v-loading="loading" :data="clues" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalType" label="类型" width="100" />
          <el-table-column prop="location" label="地点" min-width="180" />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="110" />
          <el-table-column prop="status" label="状态" width="130" />
          <el-table-column prop="description" label="描述" min-width="220" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button link type="primary" :disabled="row.status !== 'PENDING'" @click="openReview(row)">审核</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination class="pager" layout="total, prev, pager, next" :total="total" :page-size="query.pageSize" v-model:current-page="query.pageNum" @current-change="loadClues" />
      </el-card>
      <el-dialog v-model="dialogVisible" title="审核线索" width="560px">
        <el-form label-width="110px">
          <el-form-item label="审核结果">
            <el-radio-group v-model="reviewForm.approved">
              <el-radio :label="true">通过并生成工单</el-radio>
              <el-radio :label="false">无效线索</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="审核备注"><el-input v-model="reviewForm.reviewRemark" /></el-form-item>
          <template v-if="reviewForm.approved">
            <el-form-item label="工单标题"><el-input v-model="reviewForm.orderTitle" /></el-form-item>
            <el-form-item label="工单描述"><el-input v-model="reviewForm.orderDescription" type="textarea" :rows="3" /></el-form-item>
          </template>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交</el-button>
        </template>
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
