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
      <div style="display:flex; justify-content: space-between; align-items:center; margin-bottom: 16px;">
        <div style="color:#666">Supported: Images, PDF, Word, Excel</div>
        <a-upload
          :show-upload-list="false"
          :before-upload="handleBeforeUpload"
          :accept="acceptTypes"
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
import api from '@/services/api'

// Files list - loaded from API
const files = ref([])

const currentUser = ref(null)

// Load user info and files on mount
onMounted(async () => {
  try {
    const userInfo = await getMe()
    currentUser.value = userInfo?.data

    // Load existing files so POA can see uploads
    const listRes = await api.get('/files')
    const result = listRes.data
    if (result && result.code === '0' && Array.isArray(result.data)) {
      files.value = result.data.map(r => ({
        id: r.id,
        name: r.name,
        category: r.category,
        uploadedBy: r.uploadedBy,
        time: r.createdAt ? new Date(r.createdAt).toLocaleString() : '',
        comment: r.comment || '',
        fileUrl: r.fileUrl
      }))
    }
  } catch (error) {
    console.error('Failed to initialize upload page:', error)
  }
})

// 表格
const columns = [
  { title: 'File name', dataIndex: 'name' },
  { title: 'Category', dataIndex: 'category' },
  { title: 'Uploaded By', dataIndex: 'uploadedBy' },
  { title: 'Time', dataIndex: 'time' },
  { title: 'Comment', dataIndex: 'comment' },
  { title: 'Actions', dataIndex: 'actions' },
]

// 允许的类型
const acceptTypes = '.jpg,.jpeg,.png,.gif,.bmp,.webp,.svg,.pdf,.doc,.docx,.xls,.xlsx'

// 上传文件（通用：二进制直传后端并保存记录；头像请在员工编辑上传）
const handleBeforeUpload = async (file) => {
  try {
    // Get current user info
    const userInfo = await getMe()
    if (!userInfo?.data?.id) {
      message.error('User not authenticated')
      return false
    }
    
    const formData = new FormData()
    formData.append('file', file)
    formData.append('category', detectCategory(file.name))
    formData.append('uploadedBy', userInfo.data.name || 'You')

    const response = await api.post('/files/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    const result = response.data
    if (result && result.code === '0' && result.data) {
      const r = result.data
      files.value.unshift({
        id: r.id,
        name: r.name,
        category: r.category,
        uploadedBy: r.uploadedBy,
        time: r.createdAt ? new Date(r.createdAt).toLocaleString() : '',
        comment: r.comment || '',
        fileUrl: r.fileUrl
      })
      message.success('File uploaded successfully')
    } else {
      message.error(result?.msg || 'Failed to upload file')
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

// 根据扩展名简单识别类别
const detectCategory = (filename) => {
  const ext = filename.toLowerCase().substring(filename.lastIndexOf('.'))
  if (['.jpg','.jpeg','.png','.gif','.bmp','.webp','.svg'].includes(ext)) return 'Image'
  if (['.pdf'].includes(ext)) return 'PDF'
  if (['.doc','.docx'].includes(ext)) return 'Word'
  if (['.xls','.xlsx'].includes(ext)) return 'Excel'
  return 'Document'
}
</script>

<style scoped>
.upload-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
