<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { createAnimal, deleteAnimal, getAnimals, updateAnimal, updateAnimalStatus } from '../../api/animal'
import { uploadFile } from '../../api/file'
import {ANIMAL_AGE, ANIMAL_GENDER, ANIMAL_STATUS, ANIMAL_TYPE, dict } from '../../constants/dict'

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
// 状态等选项显示中文
const statusOptionLabels = Object.fromEntries(statusOptions.map(code => [code, dict(ANIMAL_STATUS, code)]))
const typeOptionLabels = Object.fromEntries(typeOptions.map(code => [code, dict(ANIMAL_TYPE, code)]))
const genderOptionLabels = Object.fromEntries(genderOptions.map(code => [code, dict(ANIMAL_GENDER, code)]))
const ageOptionLabels = Object.fromEntries(ageOptions.map(code => [code, dict(ANIMAL_AGE, code)]))

// 封面上传
const coverUploading = ref(false)
const coverInputRef = ref()

const triggerCoverSelect = () => {
  coverInputRef.value?.click()
}

const onCoverChange = async e => {
  const file = e.target.files?.[0]
  e.target.value = ''
  if (!file) return
  if (!/^image\/(png|jpe?g|gif|webp)$/.test(file.type)) {
    ElMessage.warning('请选择图片文件（png/jpg/gif/webp）')
    return
  }
  coverUploading.value = true
  try {
    const res = await uploadFile(file, 'ANIMAL_IMAGE', editingId.value)
    form.coverImage = res.data.fileUrl
    ElMessage.success('封面上传成功')
  } catch (err) {
    ElMessage.error(err.message || '封面上传失败')
  } finally {
    coverUploading.value = false
  }
}

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
              <el-option v-for="item in typeOptions" :key="item" :label="typeOptionLabels[item]" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" clearable placeholder="全部" style="width: 170px">
              <el-option v-for="item in statusOptions" :key="item" :label="statusOptionLabels[item]" :value="item" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="search">查询</el-button>
          </el-form-item>
        </el-form>

        <el-table v-loading="loading" :data="animals" border>
          <el-table-column prop="animalNo" label="编号" min-width="130" />
          <el-table-column prop="name" label="名称" min-width="100" />
          <el-table-column prop="type" label="类型" width="90">
            <template #default="{ row }">{{ typeOptionLabels[row.type] || row.type }}</template>
          </el-table-column>
          <el-table-column prop="gender" label="性别" width="100">
            <template #default="{ row }">{{ genderOptionLabels[row.gender] || row.gender || '-' }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-width="130">
            <template #default="{ row }">{{ statusOptionLabels[row.status] || row.status }}</template>
          </el-table-column>
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
                  <el-option v-for="item in typeOptions" :key="item" :label="typeOptionLabels[item]" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别">
                <el-select v-model="form.gender">
                  <el-option v-for="item in genderOptions" :key="item" :label="genderOptionLabels[item]" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄">
                <el-select v-model="form.ageStage">
                  <el-option v-for="item in ageOptions" :key="item" :label="ageOptionLabels[item]" :value="item" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="状态" prop="status">
                <el-select v-model="form.status">
                  <el-option v-for="item in statusOptions" :key="item" :label="statusOptionLabels[item]" :value="item" />
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
                <div class="cover-upload" @click="triggerCoverSelect" :title="'点击上传封面图'">
                  <el-image v-if="form.coverImage" class="cover-preview" :src="form.coverImage" fit="cover">
                    <template #error>
                      <div class="cover-placeholder"><el-icon><Plus /></el-icon><span>图片加载失败</span></div>
                    </template>
                  </el-image>
                  <div v-else class="cover-placeholder">
                    <el-icon><Plus /></el-icon>
                    <span>点击选择图片</span>
                  </div>
                  <div class="cover-mask">
                    <span>{{ coverUploading ? '上传中...' : '点击更换' }}</span>
                  </div>
                  <input ref="coverInputRef" type="file" accept="image/png,image/jpeg,image/gif,image/webp" class="cover-input" @change="onCoverChange" />
                </div>
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
              <el-option v-for="item in statusOptions" :key="item" :label="statusOptionLabels[item]" :value="item" />
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

/* 封面上传 */
.cover-upload {
  position: relative;
  width: 120px;
  height: 90px;
  border: 1px dashed #c0c4cc;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  background: #f8fafc;
}

.cover-upload:hover {
  border-color: #0f766e;
}

.cover-preview,
.cover-placeholder {
  width: 100%;
  height: 100%;
}

.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}

.cover-mask {
  position: absolute;
  inset: 0;
  display: grid;
  place-items: center;
  background: rgba(0, 0, 0, 0.45);
  color: #fff;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}

.cover-upload:hover .cover-mask {
  opacity: 1;
}

.cover-input {
  display: none;
}
</style>
