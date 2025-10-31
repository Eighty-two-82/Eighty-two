<template>
  <div class="communication-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <MessageOutlined />
            <span>Message Inbox</span>
            <a-tooltip :title="getPageTooltip()" placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <a-button type="primary" @click="showComposeModal">
            <template #icon><EditOutlined /></template>
            Compose Message
          </a-button>
        </div>
      </template>

      <!-- 消息列表 -->
      <a-table 
        :columns="columns" 
        :data-source="messages" 
        row-key="id" 
        bordered
        :pagination="{ pageSize: 10 }"
      >
        <template #bodyCell="{ column, record }">
          <!-- 状态列 -->
          <template v-if="column.dataIndex === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ record.status }}
            </a-tag>
          </template>
          
          <!-- 操作按钮 -->
          <template v-else-if="column.dataIndex === 'actions'">
            <a-space>
              <a-button size="small" @click="viewMessage(record)">View</a-button>
              <a-button size="small" type="primary" @click="replyMessage(record)">Reply</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 撰写消息弹窗 -->
    <a-modal
      v-model:open="isComposeModalOpen"
      title="Compose Message"
      width="600px"
      @ok="sendMessage"
      @cancel="cancelCompose"
    >
      <a-form :model="composeForm" layout="vertical">
        <a-form-item label="To" required>
          <a-select v-model:value="composeForm.to" placeholder="Select recipient">
            <a-select-option 
              v-for="recipient in availableRecipients" 
              :key="recipient.value" 
              :value="recipient.value"
            >
              {{ recipient.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="Subject" required>
          <a-input v-model:value="composeForm.subject" placeholder="Enter message subject" />
        </a-form-item>
        
        <a-form-item label="Message" required>
          <a-textarea 
            v-model:value="composeForm.message" 
            placeholder="Enter your message..."
            :rows="6"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 查看消息弹窗 -->
    <a-modal
      v-model:open="isViewModalOpen"
      :title="currentMessage?.subject || 'View Message'"
      width="600px"
      :footer="null"
    >
      <div v-if="currentMessage" style="line-height: 1.6;">
        <div style="margin-bottom: 16px; padding: 12px; background: #f5f5f5; border-radius: 6px;">
          <p><strong>From:</strong> {{ currentMessage.from }}</p>
          <p><strong>Date:</strong> {{ currentMessage.date }}</p>
          <p><strong>Status:</strong> 
            <a-tag :color="getStatusColor(currentMessage.status)">
              {{ currentMessage.status }}
            </a-tag>
          </p>
        </div>
        <div style="white-space: pre-wrap;">{{ currentMessage.content }}</div>
      </div>
    </a-modal>

    <!-- 回复消息弹窗 -->
    <a-modal
      v-model:open="isReplyModalOpen"
      title="Reply Message"
      width="600px"
      @ok="sendReply"
      @cancel="cancelReply"
    >
      <a-form :model="replyForm" layout="vertical">
        <a-form-item label="To">
          <a-input v-model:value="replyForm.to" disabled />
        </a-form-item>
        
        <a-form-item label="Subject">
          <a-input v-model:value="replyForm.subject" />
        </a-form-item>
        
        <a-form-item label="Message" required>
          <a-textarea 
            v-model:value="replyForm.message" 
            placeholder="Enter your reply..."
            :rows="6"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { 
  QuestionCircleOutlined, 
  MessageOutlined, 
  EditOutlined 
} from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'
import api from '@/services/api'
import { 
  getAllMessages, 
  sendMessage as sendMessageAPI, 
  replyToMessage, 
  markMessageAsRead, 
  deleteMessage 
} from '@/services/communicationService'

const userRole = ref('worker')
const currentUserId = ref('')
const currentOrganizationId = ref('')
const recipientUserId = ref('')
const recipientUserName = ref('')
const isComposeModalOpen = ref(false)
const isViewModalOpen = ref(false)
const isReplyModalOpen = ref(false)
const currentMessage = ref(null)

const messages = ref([])

// 表格列定义
const columns = [
  { title: 'Subject', dataIndex: 'subject', width: '30%' },
  { title: 'From', dataIndex: 'from', width: '15%' },
  { title: 'Date', dataIndex: 'date', width: '20%' },
  { title: 'Status', dataIndex: 'status', width: '15%' },
  { title: 'Actions', dataIndex: 'actions', width: '20%' }
]

// 撰写表单
const composeForm = reactive({
  to: '',
  subject: '',
  message: ''
})

// 回复表单
const replyForm = reactive({
  to: '',
  subject: '',
  message: ''
})

// 可用的收件人
const availableRecipients = computed(() => {
  if (recipientUserId.value && recipientUserName.value) {
    if (userRole.value === 'poa') {
      return [{ value: recipientUserId.value, label: recipientUserName.value }]
    } else if (userRole.value === 'manager') {
      return [{ value: recipientUserId.value, label: recipientUserName.value }]
    }
  }
  return []
})

// 获取收件人用户信息
const loadRecipient = async () => {
  try {
    if (!currentOrganizationId.value) {
      console.warn('No organization ID available')
      return
    }
    
    // 根据当前用户角色确定收件人类型
    let targetUserType = ''
    if (userRole.value === 'poa') {
      targetUserType = 'MANAGER'
    } else if (userRole.value === 'manager') {
      targetUserType = 'POA'
    } else {
      return
    }
    
    // 获取收件人用户信息
    const response = await api.get(`/auth/organization/${currentOrganizationId.value}/userType/${targetUserType}`)
    const result = response.data
    
    if (result.code === '0' && result.data) {
      const user = result.data
      recipientUserId.value = user.id
      recipientUserName.value = `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email || 'User'
      console.log('Loaded recipient:', recipientUserId.value, recipientUserName.value)
    }
  } catch (error) {
    console.error('Failed to load recipient:', error)
  }
}

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
    currentUserId.value = userInfo?.data?.id || ''
    currentOrganizationId.value = userInfo?.data?.organizationId || ''
    
    // Load recipient user info
    await loadRecipient()
    
    // Load messages from API
    await loadMessages()
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

// Load messages from API
const loadMessages = async () => {
  try {
    const response = await getAllMessages()
    if (response?.data) {
      messages.value = response.data
      console.log('Loaded messages:', messages.value.length)
    }
  } catch (error) {
    console.error('Failed to load messages:', error)
    message.error('Failed to load messages')
  }
}

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'unread': return 'red'
    case 'read': return 'blue'
    case 'replied': return 'green'
    default: return 'default'
  }
}

// 获取页面提示
const getPageTooltip = () => {
  switch (userRole.value) {
    case 'manager':
      return 'Communication with patient agents/family members, send messages and notifications'
    case 'poa':
      return 'Communication with managers, receive messages and notifications'
    default:
      return 'Communication page'
  }
}

// 显示撰写弹窗
const showComposeModal = () => {
  composeForm.to = ''
  composeForm.subject = ''
  composeForm.message = ''
  isComposeModalOpen.value = true
}

// 发送消息
const sendMessage = async () => {
  if (!composeForm.to || !composeForm.subject || !composeForm.message) {
    message.error('Please fill in all required fields')
    return
  }
  
  if (!recipientUserId.value) {
    message.error('Recipient not found. Please try again.')
    return
  }
  
  try {
    const userInfo = await getMe()
    const fromUserName = userInfo?.data?.name || userInfo?.data?.email || 'Current User'
    
    const messageData = {
      to: recipientUserId.value, // 使用实际的 userId
      toUserName: recipientUserName.value,
      subject: composeForm.subject,
      content: composeForm.message,
      from: fromUserName,
      category: 'general'
    }
    
    const response = await sendMessageAPI(messageData)
    
    if (response.data) {
      // 重新加载消息列表以确保显示最新消息
      await loadMessages()
      message.success('Message sent successfully')
      isComposeModalOpen.value = false
      
      // Reset form
      composeForm.to = ''
      composeForm.subject = ''
      composeForm.message = ''
    }
  } catch (error) {
    console.error('Failed to send message:', error)
    message.error(error.message || 'Failed to send message')
  }
}

// 取消撰写
const cancelCompose = () => {
  isComposeModalOpen.value = false
}

// 查看消息
const viewMessage = async (record) => {
  currentMessage.value = record
  
  // 标记为已读（如果状态是 unread 或 sent）
  if (record.status === 'unread' || record.status === 'sent') {
    try {
      await markMessageAsRead(record.id)
      record.status = 'read'
      // 更新消息列表
      await loadMessages()
    } catch (error) {
      console.error('Failed to mark message as read:', error)
    }
  }
  
  isViewModalOpen.value = true
}

// 回复消息
const replyMessage = (record) => {
  replyForm.subject = `Re: ${record.subject}`
  replyForm.message = ''
  replyForm.to = record.from // 显示收件人名称
  currentMessage.value = record
  isReplyModalOpen.value = true
}

// 发送回复
const sendReply = async () => {
  if (!replyForm.message || !currentMessage.value) {
    message.error('Please enter your reply message')
    return
  }
  
  try {
    const userInfo = await getMe()
    const fromUserName = userInfo?.data?.name || userInfo?.data?.email || 'Current User'
    
    // 确定收件人（原消息的发送者）
    // 需要从原消息中获取发送者的 userId
    // 由于前端只显示了 fromUserName，我们需要通过 API 获取完整的消息信息
    const originalMessageId = currentMessage.value.id
    
    const replyData = {
      content: replyForm.message,
      subject: replyForm.subject,
      originalSubject: currentMessage.value.subject,
      from: fromUserName
    }
    
    const response = await replyToMessage(originalMessageId, replyData)
    
    if (response.data) {
      // 重新加载消息列表以确保显示最新回复
      await loadMessages()
      message.success('Reply sent successfully')
      isReplyModalOpen.value = false
      
      // Reset form
      replyForm.message = ''
      replyForm.subject = ''
      replyForm.to = ''
    }
  } catch (error) {
    console.error('Failed to send reply:', error)
    message.error(error.message || 'Failed to send reply')
  }
}

// 取消回复
const cancelReply = () => {
  isReplyModalOpen.value = false
}
</script>

<style scoped>
.communication-page {
  max-width: 1200px;
  margin: 0 auto;
}

.ant-table-tbody > tr > td {
  vertical-align: top;
}
</style>
