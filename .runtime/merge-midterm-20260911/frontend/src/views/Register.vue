<script setup>
import { reactive, ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

// 注册类型：'user' 或 'volunteer'
const registerType = ref(route.query.type === 'volunteer' ? 'volunteer' : 'user')
watch(() => route.query.type, type => { registerType.value = type === 'volunteer' ? 'volunteer' : 'user' })
watch(registerType, () => formRef.value?.clearValidate())

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: '',
  // 志愿者专属字段
  studentId: '',
  realName: '',
  major: ''
})

// 动态校验规则
const rules = computed(() => {
  const baseRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 4, max: 30, message: '用户名长度需为4到30位', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 30, message: '密码长度需为6到30位', trigger: 'blur' }
    ]
  }

  // 如果是志愿者注册，增加额外校验
  if (registerType.value === 'volunteer') {
    return {
      ...baseRules,
      studentId: [
        { required: true, message: '请输入学号', trigger: 'blur' }
      ],
      realName: [
        { required: true, message: '请输入真实姓名', trigger: 'blur' }
      ],
      major: [
        { required: true, message: '请输入专业', trigger: 'blur' }
      ]
    }
  }

  return baseRules
})

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    if (registerType.value === 'volunteer') {
      // 志愿者注册
      await authStore.registerVolunteer(form)
      ElMessage.success('志愿者注册成功，请登录')
    } else {
      // 普通用户注册
      await authStore.register(form)
      ElMessage.success('注册成功，请登录')
    }
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page register-page">
    <section class="panel">
      <h1>{{ registerType === 'volunteer' ? '注册志愿者' : '注册普通用户' }}</h1>
      <p>
        {{ registerType === 'volunteer' 
          ? '填写校园信息，加入救助队伍并领取志愿任务。' 
          : '注册后即可提交救助线索、申请领养并接收通知。' 
        }}
      </p>

      <!-- 切换标签 -->
      <div class="tab-switch">
        <el-button 
          :type="registerType === 'user' ? 'primary' : ''"
          plain
          @click="registerType = 'user'"
        >
          普通用户注册
        </el-button>
        <el-button 
          :type="registerType === 'volunteer' ? 'primary' : ''"
          plain
          @click="registerType = 'volunteer'"
        >
          志愿者注册
        </el-button>
      </div>

      <el-form 
        ref="formRef" 
        :model="form" 
        :rules="rules" 
        label-position="top" 
        @keyup.enter="submit"
      >
        <!-- 公共字段 -->
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入4-30位用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="6-30位" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="选填" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="选填" />
        </el-form-item>

        <!-- 志愿者专属字段 -->
        <template v-if="registerType === 'volunteer'">
          <el-divider content-position="left">志愿者信息</el-divider>
          <el-form-item label="学号" prop="studentId">
            <el-input v-model="form.studentId" placeholder="请输入学号" />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="form.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="专业" prop="major">
            <el-input v-model="form.major" placeholder="请输入专业" />
          </el-form-item>
        </template>

        <el-button 
          type="primary" 
          class="submit" 
          :loading="loading" 
          @click="submit"
        >
          {{ registerType === 'volunteer' ? '注册志愿者' : '注册普通用户' }}
        </el-button>
      </el-form>

      <div class="links">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </section>
  </main>
</template>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  background: #eef4f2;
  padding: 28px 0;
}

.panel {
  width: min(460px, calc(100% - 32px));
  padding: 28px;
  background: #fff;
  border: 1px solid #dbe4e0;
  border-radius: 8px;
}

h1 {
  margin: 0 0 8px;
  font-size: 28px;
}

p {
  margin: 0 0 24px;
  color: #64748b;
}

.submit {
  width: 100%;
}

.links {
  margin-top: 18px;
  text-align: center;
}
</style>
