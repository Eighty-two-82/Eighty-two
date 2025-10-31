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
import { ref, onMounted } from 'vue'
import { QuestionCircleOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { getMe } from '@/services/userService'
import { uploadFile, getAllFiles, updateFileComment } from '@/services/fileService'

// Files list - loaded from API
const files = ref([])

const currentUser = ref(null)

// Load user info and files on mount
onMounted(async () => {
  try {
    const userInfo = await getMe()
    currentUser.value = userInfo?.data
    // Load files from API
    await loadFiles()
  } catch (error) {
    console.error('Failed to get user info:', error)
  }
})

// Helper function to get full file URL
const getFullFileUrl = (fileUrl) => {
  if (!fileUrl) return ''
  // If already a full URL, return as is
  if (fileUrl.startsWith('http://') || fileUrl.startsWith('https://')) {
    return fileUrl
  }
  // Otherwise, prepend backend base URL
  const API_BASE_URL = import.meta.env.MODE === "production"
    ? "https://care-scheduling-app-e8951cd9f9c6.herokuapp.com"
    : "http://localhost:8081"
  return `${API_BASE_URL}${fileUrl}`
}

// Load files from API
const loadFiles = async () => {
  try {
    const response = await getAllFiles()
    if (response?.data) {
      // Map backend FileRecord to frontend format
      files.value = response.data.map(file => ({
        id: file.id,
        name: file.name,
        category: file.category || 'General Upload',
        uploadedBy: file.uploadedBy || 'Unknown',
        time: file.createdAt ? new Date(file.createdAt).toLocaleDateString() : '',
        comment: file.comment || '',
        fileUrl: getFullFileUrl(file.fileUrl),
        contentType: file.contentType,
        size: file.size
      }))
    }
  } catch (error) {
    console.error('Failed to load files:', error)
  }
}

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
const handleBeforeUpload = async (file) => {
  try {
    // Get current user info
    const userInfo = await getMe()
    if (!userInfo?.data?.id) {
      message.error('User not authenticated')
      return false
    }
    
    // Determine category based on user role
    let category = 'General Upload'
    if (userInfo.data.role === 'worker') {
      category = 'Worker Upload'
    } else if (userInfo.data.role === 'manager') {
      category = 'Manager Upload'
    } else if (userInfo.data.role === 'poa') {
      category = 'POA Upload'
    }
    
    // Upload file via API
    const uploadResponse = await uploadFile(file, {
      category: category,
      uploadedBy: userInfo.data.name || userInfo.data.email || 'Unknown',
      comment: ''
    })
    
    if (uploadResponse.data) {
      message.success('File uploaded successfully')
      // Reload files list to show the new file
      await loadFiles()
    }
  } catch (error) {
    console.error('Failed to upload file:', error)
    message.error(error.message || 'Failed to upload file')
  }
  
  return false // 阻止默认上传
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

const saveComment = async () => {
  if (currentFile.value) {
    try {
      await updateFileComment(currentFile.value.id, currentComment.value)
      currentFile.value.comment = currentComment.value
      message.success('Comment updated successfully')
      // Reload files to get updated data
      await loadFiles()
    } catch (error) {
      console.error('Failed to update comment:', error)
      message.error(error.message || 'Failed to update comment')
    }
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
