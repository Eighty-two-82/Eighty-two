<template>
  <div class="carer-team-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Carer Team</h2>
          <a-tooltip title="Power of Attorney can view and manage care team information, invite organizations to join" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
// Carer Team page component
</script>

<style scoped>
.carer-team-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
