<template>
  <div class="login-container">
    <a-form
      :model="formState"
      name="Login"
      :label-col="{ span: 6 }"
      :wrapper-col="{ span: 18 }"
      autocomplete="off"
      @finish="onFinish"
      @finishFailed="onFinishFailed"
      :disabled="loading"
      class="login-form"
    >
      <h1 class="login-title">Sign in</h1>

      <!-- Email -->
      <a-form-item
        name="email"
        :rules="[
          { required: true, message: 'Please input your Email!' },
          { type: 'email', message: 'Email format is invalid' }
        ]"
        :wrapper-col="{ span: 24 }"
      >
        <a-input v-model:value="formState.email" placeholder="Enter Email">
          <template #prefix><UserOutlined /></template>
        </a-input>
      </a-form-item>

      <!-- Password -->
      <a-form-item
        name="password"
        :rules="[{ required: true, message: 'Please input your password!' }]"
        :wrapper-col="{ span: 24 }"
      >
        <a-input-password v-model:value="formState.password" placeholder="Enter password">
          <template #prefix><LockOutlined /></template>
        </a-input-password>
      </a-form-item>

      <!-- Remember + Forgot -->
      <a-form-item :wrapper-col="{ span: 24 }">
        <div class="row-between">
          <a-checkbox v-model:checked="formState.remember">Remember me</a-checkbox>
          <a class="login-form-forgot" @click="onForgot">Forgot password?</a>
        </div>
      </a-form-item>

      <!-- Submit -->
      <a-form-item :wrapper-col="{ span: 24 }">
        <a-button type="primary" html-type="submit" :loading="loading" block>Sign in</a-button>
      </a-form-item>

      <!-- Sign up -->
      <div class="muted">
        Don't have an account?
        <a-typography-link @click="onRegister">Register</a-typography-link>
      </div>
    </a-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined } from '@ant-design/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { login } from '@/services/userService'

const router = useRouter()
const route = useRoute()

const formState = reactive({
  email: '',
  password: '',
  remember: true,
})

const loading = ref(false)

const onFinish = async () => {
  loading.value = true
  try {
    const res = await login({
      email: formState.email,
      password: formState.password,
    })

    const token = res?.data?.token
    if (token) {
      // Always use sessionStorage for security (no persistent storage)
      sessionStorage.setItem('token', token)
    }
    message.success('Signed in')

    // Clear router cache to ensure fresh data
    if (window.clearRouterCache) {
      window.clearRouterCache()
    }

    //check user role and invite code status
    try {
      const userInfo = res?.data?.user || { role: 'user' }
      const role = userInfo.role
      
      if (role === 'worker' || role === 'manager') {
        const inviteStatus = res?.data?.inviteStatus || { valid: false }
        console.log('🔍 Login - User role:', role)
        console.log('🔍 Login - Invite status:', inviteStatus)
        console.log('🔍 Login - User ID:', userInfo.id)
        
        // Only redirect to invite code page if user hasn't used any invite code yet
        if (!inviteStatus.valid && inviteStatus.reason === 'missing') {
          console.log('🔄 Login - User needs to submit invite code, redirecting...')
          router.replace('/invitecode')
          return
        } else if (inviteStatus.valid || inviteStatus.reason === 'already_used') {
          console.log('✅ Login - User has already used invite code, proceeding to app...')
        } else {
          console.log('⚠️ Login - Unknown invite status, proceeding to app...')
        }
      }
      
      // regular users or users with valid invite codes, redirect to target page
      const redirect = route.query.redirect || '/app/menu'
      router.replace(String(redirect))
    } catch (e) {
      console.error('❌ Login - Error checking invite status:', e)
      // if getting user info fails, redirect to default page
      const redirect = route.query.redirect || '/app/menu'
      router.replace(String(redirect))
    }
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || 'Login failed'
    message.error(msg)
  } finally {
    loading.value = false
  }
}

const onFinishFailed = () => {
  message.error('Please check the form')
}

const onForgot = () => {
  router.push('/forgot-password')
}

const onRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: #f0f2f5;
}
.login-form {
  width: 450px;
  padding: 36px 32px;
  background: white;
  border-radius: 14px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.12);
}
.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}
.row-between {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}
.muted {
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin: 8px 0 6px;
}
</style>
