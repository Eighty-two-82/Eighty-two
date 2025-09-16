<template>
  <div class="worker-management-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Worker Management</h2>
          <a-tooltip title="Care staff management page, Managers can invite Workers to join the team" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
// Worker Management page component
</script>

<style scoped>
.worker-management-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
