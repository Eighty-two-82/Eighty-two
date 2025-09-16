<template>
  <div class="tasks-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Tasks</h2>
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
      return 'Task management: Assign tasks to care staff, monitor task execution'
    case 'worker':
      return 'Task management: View assigned tasks and execute them'
    case 'poa':
      return 'Task management: View all task execution status, approve important tasks'
    default:
      return 'Task management page'
  }
}
</script>

<style scoped>
.tasks-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>