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
  </div>
</template>

<script setup lang="ts">
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
const handleBeforeUpload = (file: File) => {
  files.value.push({
    id: Date.now(),
    name: file.name,
    category: 'Worker Upload',
    uploadedBy: 'You',
    time: new Date().toLocaleDateString(),
    comment: ''
  })
  return false // 阻止默认上传，改为本地存储
}

// 评论弹窗逻辑
const isCommentModalOpen = ref(false)
const currentFile = ref<any>(null)
const currentComment = ref('')

const openCommentModal = (record: any) => {
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

// 查看文件
const viewFile = (record: any) => {
  console.log('View:', record.name)
}
</script>

<style scoped>
.upload-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
