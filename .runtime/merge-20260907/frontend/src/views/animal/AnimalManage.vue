<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createAnimal, deleteAnimal, getAnimals, updateAnimal, updateAnimalStatus } from '../../api/animal'

const router = useRouter()
const loading = ref(false)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const editingId = ref(null)
const animals = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '', type: '', status: '' })
const formRef = ref()
const form = reactive({
  animalNo: '',
  name: '',
  type: 'CAT',
  gender: 'UNKNOWN',
  ageStage: 'UNKNOWN',
  color: '',
  healthStatus: '',
  sterilizationStatus: 'UNKNOWN',
  vaccineStatus: 'UNKNOWN',
  area: '',
  status: 'WAIT_RESCUE',
  description: '',
  coverImage: ''
})
const statusForm = reactive({ id: null, status: '', remark: '' })

const typeOptions = ['CAT', 'DOG', 'OTHER']
const genderOptions = ['MALE', 'FEMALE', 'UNKNOWN']
const ageOptions = ['BABY', 'YOUNG', 'ADULT', 'OLD', 'UNKNOWN']
const statusOptions = ['WAIT_RESCUE', 'OBSERVING', 'TREATING', 'ADOPTABLE', 'APPLYING', 'TRIAL', 'ADOPTED', 'NOT_ADOPTABLE', 'LOST']

const rules = {
  animalNo: [{ required: true, message: '请输入动物编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入动物名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const resetForm = () => {
  editingId.value = null
  Object.assign(form, {
    animalNo: '',
    name: '',
    type: 'CAT',
    gender: 'UNKNOWN',
    ageStage: 'UNKNOWN',
    color: '',
    healthStatus: '',
    sterilizationStatus: 'UNKNOWN',
    vaccineStatus: 'UNKNOWN',
    area: '',
    status: 'WAIT_RESCUE',
    description: '',
    coverImage: ''
  })
}

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

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = row => {
  editingId.value = row.id
  Object.assign(form, {
    animalNo: row.animalNo,
    name: row.name,
    type: row.type,
    gender: row.gender || 'UNKNOWN',
    ageStage: row.ageStage || 'UNKNOWN',
    color: row.color || '',
    healthStatus: row.healthStatus || '',
    sterilizationStatus: row.sterilizationStatus || 'UNKNOWN',
    vaccineStatus: row.vaccineStatus || 'UNKNOWN',
    area: row.area || '',
    status: row.status,
    description: row.description || '',
    coverImage: row.coverImage || ''
  })
  dialogVisible.value = true
}

const submit = async () => {
  await formRef.value.validate()
  const payload = { ...form }
  try {
    if (editingId.value) {
      delete payload.animalNo
      await updateAnimal(editingId.value, payload)
      ElMessage.success('修改成功')
    } else {
      await createAnimal(payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadAnimals()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const openStatus = row => {
  statusForm.id = row.id
  statusForm.status = row.status
  statusForm.remark = ''
  statusDialogVisible.value = true
}

const submitStatus = async () => {
  try {
    await updateAnimalStatus(statusForm.id, { status: statusForm.status, remark: statusForm.remark })
    ElMessage.success('状态已更新')
    statusDialogVisible.value = false
    loadAnimals()
  } catch (e) {
    ElMessage.error(e.message)
  }
}

const removeAnimal = async row => {
  await ElMessageBox.confirm(`确认删除动物档案 ${row.name}？`, '删除确认', { type: 'warning' })
  try {
    await deleteAnimal(row.id)
    ElMessage.success('删除成功')
    loadAnimals()
  } catch (e) {
    ElMessage.error(e.message)
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
        <h1>动物档案管理</h1>
        <div>
          <el-button @click="router.push('/')">返回首页</el-button>
          <el-button type="primary" @click="openCreate">新增动物</el-button>
        </div>
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
          <el-table-column prop="animalNo" label="编号" min-width="130" />
          <el-table-column prop="name" label="名称" min-width="100" />
          <el-table-column prop="type" label="类型" width="90" />
          <el-table-column prop="gender" label="性别" width="100" />
          <el-table-column prop="status" label="状态" min-width="130" />
          <el-table-column prop="area" label="区域" min-width="120" />
          <el-table-column label="操作" width="250" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" @click="router.push(`/animals/${row.id}`)">详情</el-button>
              <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button link type="warning" @click="openStatus(row)">状态</el-button>
              <el-button link type="danger" @click="removeAnimal(row)">删除</el-button>
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

      <el-dialog v-model="dialogVisible" :title="editingId ? '编辑动物' : '新增动物'" width="680px">
        <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
          <el-row :gutter="12">
            <el-col :span="12">
              <el-form-item label="编号" prop="animalNo">
                <el-input v-model="form.animalNo" :disabled="Boolean(editingId)" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="名称" prop="name">
                <el-input v-model="form.name" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="类型" prop="type">
                <el-select v-model="form.type">
                  <el-option v-for="item in typeOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="form.gender">
                  <el-option v-for="item in genderOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-select v-model="form.ageStage">
                  <el-option v-for="item in ageOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-select v-model="form.status">
                  <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="毛色">
                <el-input v-model="form.color" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="区域">
                <el-input v-model="form.area" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="健康状态">
                <el-input v-model="form.healthStatus" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="封面图">
                <el-input v-model="form.coverImage" />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="描述">
                <el-input v-model="form.description" type="textarea" :rows="3" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">保存</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="statusDialogVisible" title="修改动物状态" width="420px">
        <el-form label-width="80px">
          <el-form-item label="状态">
            <el-select v-model="statusForm.status">
              <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="statusForm.remark" type="textarea" :rows="3" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatus">保存</el-button>
        </template>
      </el-dialog>
    </section>
  </main>
</template>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f7fb;
}

.shell {
  width: min(1180px, calc(100% - 32px));
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

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
