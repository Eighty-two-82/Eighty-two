<template>
    <div class="auth-container">
      <a-card class="max-w-4xl mx-auto mt-16" style="padding: 40px; position: relative;">
        <a-button 
          type="text" 
          @click="goToLogin" 
          style="position: absolute; top: 20px; right: 20px; font-size: 18px; color: #666;"
          title="Back to Login"
        >
          ✕
        </a-button>
        <h1 class="text-3xl font-semibold mb-6 text-center">Enter Invite Code</h1>
        
        <div class="text-center mb-6 text-gray-600">
          <p>Please enter your organization's invite code to continue</p>
        </div>
  
        <a-alert
          v-if="reason === 'expired'"
          type="warning"
          message="Your previous invite code has expired. Please enter a new one."
          show-icon
          class="mb-4"
        />
  
        <a-form :model="form" @finish="onSubmit" :disabled="submitting">
          <a-form-item
            name="inviteCode"
            :rules="[{ required: true, message: 'Please input invite code' }]"
          >
            <a-input
              v-model:value="form.inviteCode"
              placeholder="Enter your invite code here..."
              allow-clear
              size="large"
              style="font-size: 16px; text-align: center;"
            />
          </a-form-item>
  
          <div class="text-center" style="margin-top: 30px;">
            <a-button type="primary" html-type="submit" :loading="submitting" size="large" style="min-width: 120px;">
              Submit
            </a-button>
          </div>
        </a-form>
      </a-card>
    </div>
  </template>
  
  <script setup>
  import { reactive, ref, onMounted } from 'vue'
  import { message } from 'ant-design-vue'
  import { submitInviteCode, getInviteStatus, getMe } from '@/services/userService'
  import { useRoute, useRouter } from 'vue-router'
  
  const route = useRoute()
  const router = useRouter()
  
  const form = reactive({ inviteCode: '' })
  const submitting = ref(false)
  const reason = ref('')
  

  onMounted(async () => {
    try {
      const me = await getMe()
      if (!needsInvite(me?.data?.role)) {
        goBack()
        return
      }
      const status = await getInviteStatus()
      if (status?.data?.valid) {
        goBack()
      } else {
        reason.value = status?.data?.reason || 'missing'
      }
    } catch (e) {
    }
  })
  
  function needsInvite(role) {
    return role === 'worker' || role === 'manager'
  }
  
  async function onSubmit() {
    try {
      submitting.value = true
      await submitInviteCode(form.inviteCode.trim())
      message.success('Invite code verified successfully')
      goBack(true)
    } catch (e) {
      message.error(e?.message || 'Invalid invite code, please try again')
    } finally {
      submitting.value = false
    }
  }
  
  function goBack(forceRefresh = false) {
    const redirect = route.query.redirect || '/app/menu'
    // Clear router cache to ensure fresh data
    if (window.clearRouterCache) {
      window.clearRouterCache()
    }
    router.replace(String(redirect))
  }
  
  function goToLogin() {
    // Clear any stored tokens
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
    // Navigate to login page
    router.replace('/login')
  }
  </script>
  
  <style scoped>
  .auth-container {
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f6f7fb;
    padding: 20px;
  }
  </style>
  