<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getAnimals } from '../../api/animal'

const router = useRouter()
const loading = ref(false)
const animals = ref([])
const total = ref(0)
const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  type: '',
  status: ''
})

const typeOptions = ['CAT', 'DOG', 'OTHER']
const statusOptions = ['WAIT_RESCUE', 'OBSERVING', 'TREATING', 'ADOPTABLE', 'APPLYING', 'TRIAL', 'ADOPTED', 'NOT_ADOPTABLE', 'LOST']

const loadAnimals = async () => {
  loading.value = true
  try {
    const res = await getAnimals(query)
    animals.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  loadAnimals()
}

onMounted(loadAnimals)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="header">
        <h1>可查看动物档案</h1>
        <el-button @click="router.push('/')">返回首页</el-button>
      </div>
      <el-card shadow="never">
        <el-form class="filters" :inline="true" :model="query">
          <el-form-item label="搜索">
            <el-input v-model="query.keyword" placeholder="名称或编号" clearable />
          </el-form-item>
          <el-form-item label="类型">
            <el-select v-model="query.type" clearable placeholder="全部" style="width: 130px">
              <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 170px">
              <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="animals" border>
          <el-table-column label="主图" width="90">
            <template #default="{ row }">
              <el-image class="cover" :src="row.coverImage" fit="cover">
                <template #error>
                  <div class="image-slot">无图</div>
                </template>
              </el-image>
            </template>
          </el-table-column>
          <el-table-column prop="animalNo" label="编号" min-width="130" />
          <el-table-column prop="name" label="名称" min-width="100" />
          <el-table-column prop="type" label="类型" width="90" />
          <el-table-column prop="gender" label="性别" width="100" />
          <el-table-column prop="status" label="状态" min-width="130" />
          <el-table-column prop="area" label="区域" min-width="120" />
          <el-table-column label="操作" width="110">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/animals/${row.id}`)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          class="pager"
          layout="total, prev, pager, next"
          :total="total"
          :page-size="query.pageSize"
          v-model:current-page="query.pageNum"
          @current-change="loadAnimals"
        />
      </el-card>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
}

.shell {
  width: min(1120px, calc(100% - 32px));
  margin: 0 auto;
  padding: 32px 0;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}

h1 {
  margin: 0;
  font-size: 26px;
}

.filters {
  margin-bottom: 12px;
}

.cover {
  width: 56px;
  height: 44px;
  border-radius: 6px;
  background: #e2e8f0;
}

.image-slot {
  display: grid;
  place-items: center;
  width: 100%;
  height: 100%;
  font-size: 12px;
  color: #64748b;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
