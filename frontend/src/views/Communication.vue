<template>
  <div class="communication-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Communication</h2>
          <a-tooltip :title="getPageTooltip()" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'

const userRole = ref('worker')

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

const getPageTooltip = () => {
  switch (userRole.value) {
    case 'manager':
      return 'Communication with patient agents/family members, send messages and notifications'
    case 'poa':
      return 'Communication with managers, receive messages and notifications'
    default:
      return 'Communication page'
  }
}
</script>

<style scoped>
.communication-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
