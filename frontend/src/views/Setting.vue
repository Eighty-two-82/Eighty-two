<template>
  <div class="setting-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Settings</h2>
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
    case 'poa':
      return 'Settings page: Fill in patient information and special requirements, manage personal basic information and preferences'
    case 'worker':
    case 'manager':
      return 'Settings page: View patient information and special requirements, manage personal basic information and preferences'
    default:
      return 'Settings page: Manage personal basic information and preferences'
  }
}
</script>

<style scoped>
.setting-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
