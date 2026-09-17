<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAnimalDetail } from '../../api/animal'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const animal = ref(null)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getAnimalDetail(route.params.id)
    animal.value = res.data
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}

onMounted(loadDetail)
</script>

<template>
  <main class="page">
    <section class="shell">
      <div class="toolbar">
        <el-button @click="router.back()">返回</el-button>
        <h1>动物详情</h1>
        <el-button
          v-if="animal?.status === 'ADOPTABLE'"
          type="primary"
          @click="router.push(`/adoptions/apply/${animal.id}`)"
        >
          申请领养
        </el-button>
      </div>

      <el-skeleton v-if="loading" :rows="6" animated />
      <template v-else-if="animal">
        <el-card shadow="never">
          <div class="detail">
            <el-image class="cover" :src="animal.coverImage" fit="cover">
              <template #error>
                <div class="image-slot">暂无图片</div>
              </template>
            </el-image>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="编号">{{ animal.animalNo }}</el-descriptions-item>
              <el-descriptions-item label="名称">{{ animal.name }}</el-descriptions-item>
              <el-descriptions-item label="类型">{{ animal.type }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ animal.gender || '-' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ animal.ageStage || '-' }}</el-descriptions-item>
              <el-descriptions-item label="状态">{{ animal.status }}</el-descriptions-item>
              <el-descriptions-item label="区域">{{ animal.area || '-' }}</el-descriptions-item>
              <el-descriptions-item label="健康">{{ animal.healthStatus || '-' }}</el-descriptions-item>
              <el-descriptions-item label="描述" :span="2">{{ animal.description || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>图片列表</template>
          <div class="images">
            <el-image
              v-for="image in animal.images"
              :key="image.id"
              class="thumb"
              :src="image.imageUrl"
              fit="cover"
            >
              <template #error>
                <div class="image-slot">{{ image.imageType }}</div>
              </template>
            </el-image>
          </div>
        </el-card>

        <el-card class="block" shadow="never">
          <template #header>健康记录</template>
          <el-table :data="animal.healthRecords" border>
            <el-table-column prop="recordTime" label="记录时间" min-width="170" />
            <el-table-column prop="healthStatus" label="健康状态" min-width="120" />
            <el-table-column prop="hospital" label="医院" min-width="160" />
            <el-table-column prop="treatmentContent" label="处理内容" min-width="220" />
            <el-table-column prop="remark" label="备注" min-width="160" />
          </el-table>
        </el-card>
      </template>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
}

.shell {
  width: min(1100px, calc(100% - 32px));
  margin: 0 auto;
  padding: 32px 0;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

h1 {
  margin: 0;
  font-size: 26px;
}

.detail {
  display: grid;
  grid-template-columns: 220px minmax(0, 1fr);
  gap: 18px;
}

.cover {
  width: 220px;
  height: 220px;
  border-radius: 8px;
  background: #e2e8f0;
}

.image-slot {
  display: grid;
  place-items: center;
  width: 100%;
  height: 100%;
  color: #64748b;
  background: #e2e8f0;
}

.block {
  margin-top: 18px;
}

.images {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.thumb {
  width: 140px;
  height: 100px;
  border-radius: 6px;
  background: #e2e8f0;
}

@media (max-width: 720px) {
  .detail {
    grid-template-columns: 1fr;
  }
}
</style>
