<template>
  <div class="home-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Welcome to Care App</h2>
          <a-tooltip title="This is the system main page, displaying system overview and quick action entries" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
// Home page component
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
