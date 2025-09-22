<template>
  <div class="upload-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>All Uploaded Files</h2>
          <a-tooltip title="File upload page for uploading and managing documents, images and other materials" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>

      <!-- 上传按钮 -->
      <div style="text-align: right; margin-bottom: 16px;">
        <a-upload
          :show-upload-list="false"
          :before-upload="handleBeforeUpload"
        >
          <a-button type="primary">+ Upload File</a-button>
        </a-upload>
      </div>

      <!-- 文件表格 -->
      <a-table :columns="columns" :data-source="files" row-key="id" bordered>
        <template #bodyCell="{ column, record }">
          <!-- 操作按钮 -->
          <template v-if="column.dataIndex === 'actions'">
            <a-space>
              <a-button size="small" @click="openCommentModal(record)">comment</a-button>
              <a-button size="small" @click="viewFile(record)">View</a-button>
            </a-space>
          </template>
          <!-- 评论 -->
          <template v-else-if="column.dataIndex === 'comment'">
            <span>{{ record.comment || '-' }}</span>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 评论弹窗 -->
    <a-modal
      v-model:open="isCommentModalOpen"
      title="Add Comment"
      @ok="saveComment"
      @cancel="cancelComment"
    >
      <a-textarea
        v-model:value="currentComment"
        placeholder="Write your comment here..."
        rows="4"
      />
    </a-modal>

    <!-- 文件查看弹窗 -->
    <a-modal
      v-model:open="isViewModalOpen"
      :title="currentViewFile?.name || 'View File'"
      width="80%"
      :footer="null"
    >
      <div v-if="currentViewFile" style="text-align: center;">
        <!-- 图片预览 -->
        <img 
          v-if="isImageFile(currentViewFile.name)" 
          :src="currentViewFile.fileUrl" 
          style="max-width: 100%; max-height: 500px; border-radius: 8px;"
          alt="Preview"
        />
        <!-- 其他文件类型 -->
        <div v-else style="padding: 40px;">
          <div style="font-size: 48px; color: #1890ff; margin-bottom: 16px;">📄</div>
          <h3>{{ currentViewFile.name }}</h3>
          <p style="color: #666; margin: 16px 0;">This file type cannot be previewed in the browser.</p>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { QuestionCircleOutlined } from '@ant-design/icons-vue'

const files = ref([
  { id: 1, name: 'Examination Report.pdf', category: 'Worker Upload', uploadedBy: 'C1', time: '2025-09-21', comment: '' },
  { id: 2, name: 'Budget Report.pdf', category: 'Admin Upload', uploadedBy: 'Name', time: '2025-09-21', comment: '' },
])

// 表格
const columns = [
  { title: 'File name', dataIndex: 'name' },
  { title: 'Category', dataIndex: 'category' },
  { title: 'Uploaded By', dataIndex: 'uploadedBy' },
  { title: 'Time', dataIndex: 'time' },
  { title: 'Comment', dataIndex: 'comment' },
  { title: 'Actions', dataIndex: 'actions' },
]

// 上传文件
const handleBeforeUpload = (file) => {
  // 创建文件URL用于预览
  const fileUrl = URL.createObjectURL(file)
  
  files.value.push({
    id: Date.now(),
    name: file.name,
    category: 'Worker Upload',
    uploadedBy: 'You',
    time: new Date().toLocaleDateString(),
    comment: '',
    file: file,
    fileUrl: fileUrl
  })
  return false // 阻止默认上传，改为本地存储
}

// 评论弹窗逻辑
const isCommentModalOpen = ref(false)
const currentFile = ref(null)
const currentComment = ref('')

const openCommentModal = (record) => {
  currentFile.value = record
  currentComment.value = record.comment || ''
  isCommentModalOpen.value = true
}

const saveComment = () => {
  if (currentFile.value) {
    currentFile.value.comment = currentComment.value
  }
  isCommentModalOpen.value = false
}

const cancelComment = () => {
  isCommentModalOpen.value = false
}

// 文件查看弹窗逻辑
const isViewModalOpen = ref(false)
const currentViewFile = ref(null)

// 查看文件
const viewFile = (record) => {
  currentViewFile.value = record
  isViewModalOpen.value = true
}

// 判断是否为图片文件
const isImageFile = (filename) => {
  const imageExtensions = ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.webp', '.svg']
  const extension = filename.toLowerCase().substring(filename.lastIndexOf('.'))
  return imageExtensions.includes(extension)
}
</script>

<style scoped>
.upload-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
