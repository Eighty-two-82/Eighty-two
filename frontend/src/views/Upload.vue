<template>
  <div class="upload-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Upload</h2>
          <a-tooltip title="File upload page for uploading and managing documents, images and other materials" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup>
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
// Upload page component
</script>

<style scoped>
.upload-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
