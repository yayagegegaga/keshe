<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
  nickname: '',
  phone: '',
  email: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 30, message: '用户名长度需为4到30位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 30, message: '密码长度需为6到30位', trigger: 'blur' }
  ]
}

const submit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await authStore.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="auth-page">
    <section class="panel">
      <h1>注册普通用户</h1>
      <p>注册后默认绑定 USER 角色。</p>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="submit">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-button type="primary" class="submit" :loading="loading" @click="submit">注册</el-button>
      </el-form>
      <div class="links">
        <router-link to="/login">返回登录</router-link>
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
