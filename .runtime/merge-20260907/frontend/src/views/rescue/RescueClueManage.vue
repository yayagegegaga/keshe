<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRescueClues, reviewRescueClue } from '../../api/rescueClue'

const router = useRouter()
const loading = ref(false)
const clues = ref([])
const total = ref(0)
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
    if (e !== 'cancel') ElMessage.error(e.message)
  }
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
          <el-select v-model="query.status" placeholder="状态" clearable @change="loadData">
            <el-option label="待审核" value="PENDING" />
            <el-option label="已转工单" value="CONVERTED" />
            <el-option label="无效线索" value="INVALID" />
            <el-option label="已取消" value="CANCELED" />
            <el-option label="全部" value="" />
          </el-select>
          <el-input v-model="query.keyword" clearable placeholder="地点/描述" @keyup.enter="loadData" />
          <el-button type="primary" @click="loadData">查询</el-button>
        </div>

        <el-table v-loading="loading" :data="clues" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="animalType" label="动物类型" width="100" />
          <el-table-column prop="location" label="地点" min-width="160" />
          <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
          <el-table-column prop="emergencyLevel" label="紧急程度" width="100" />
          <el-table-column prop="status" label="状态" width="110" />
          <el-table-column prop="createTime" label="提交时间" min-width="170" />
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/rescue-clues/${row.id}`)">详情</el-button>
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
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
  padding: 32px 0;
}

.shell {
  width: min(1200px, calc(100% - 32px));
  margin: 0 auto;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

h1 {
  margin: 0;
  font-size: 26px;
}

.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filters .el-input {
  max-width: 260px;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>