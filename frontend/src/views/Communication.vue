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
import { getMe, getUsersByRole } from '@/services/userService'
import { 
  getAllMessages, 
  sendMessage as sendMessageAPI, 
  replyToMessage, 
  markMessageAsRead, 
  deleteMessage 
} from '@/services/communicationService'

const userRole = ref('worker')
const currentUserName = ref('')
const currentUserId = ref('')
const currentUserPatientId = ref('')
const availableUsers = ref([])
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
  if (userRole.value === 'poa') {
    // POA can message managers who are bound to the same patient
    return availableUsers.value
      .filter(user => user.role === 'manager' && user.patientId === currentUserPatientId.value)
      .map(user => {
        const fullName = user.name || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email
        const displayName = fullName === user.email ? user.email : `${fullName} (${user.email})`
        return {
          value: user.id,
          label: displayName,
          toUserId: user.id,
          toUserName: fullName
        }
      })
  } else if (userRole.value === 'manager') {
    // Manager can message POA and family members who are bound to the same patient
    return availableUsers.value
      .filter(user => (user.role === 'poa' || user.userType === 'FM') && user.patientId === currentUserPatientId.value)
      .map(user => {
        const fullName = user.name || `${user.firstName || ''} ${user.lastName || ''}`.trim() || user.email
        const displayName = fullName === user.email ? user.email : `${fullName} (${user.email})`
        return {
          value: user.id,
          label: displayName,
          toUserId: user.id,
          toUserName: fullName
        }
      })
  }
  return []
})

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
    currentUserName.value = userInfo?.data?.name || 'User'
    currentUserId.value = userInfo?.data?.id || ''
    currentUserPatientId.value = userInfo?.data?.patientId || ''
    
    // Load available users for communication
    await loadAvailableUsers()
    
    // Load messages from API
    await loadMessages()
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

// Load available users for communication
const loadAvailableUsers = async () => {
  try {
    // Load managers and POA/family members
    const [managersResponse, poaResponse] = await Promise.all([
      getUsersByRole('manager').catch(() => ({ data: [] })),
      getUsersByRole('poa').catch(() => ({ data: [] }))
    ])
    
    // Combine all users
    const allUsers = [
      ...(managersResponse.data || []),
      ...(poaResponse.data || [])
    ]
    
    // Add name field for easier access
    availableUsers.value = allUsers.map(user => ({
      ...user,
      name: `${user.firstName || ''} ${user.middleName || ''} ${user.lastName || ''}`.trim() || user.email
    }))
  } catch (error) {
    console.error('Failed to load available users:', error)
    // Fallback to empty array
    availableUsers.value = []
  }
}

// Load messages from API
const loadMessages = async () => {
  try {
    const response = await getAllMessages()
    if (response?.data && Array.isArray(response.data)) {
      // Transform backend data to frontend format
      messages.value = response.data
        .filter(msg => msg && msg.id) // Filter out invalid messages
        .map(msg => {
          // Determine status based on whether current user is sender or receiver
          let status = 'unread'
          if (msg.fromUserId === currentUserId.value) {
            // Current user is the sender - show as sent
            status = 'sent'
          } else {
            // Current user is the receiver - show as read/unread
            status = msg.readAt ? 'read' : 'unread'
          }
          
          return {
            id: msg.id,
            subject: msg.subject || 'No Subject',
            from: msg.fromUserName || 'Unknown',
            date: msg.createdAt ? new Date(msg.createdAt).toLocaleString() : 'Unknown Date',
            status: status,
            content: msg.content || ''
          }
        })
    }
  } catch (error) {
    console.error('Failed to load messages:', error)
    // Keep using mock data if API fails
  }
}

// 获取状态颜色
const getStatusColor = (status) => {
  switch (status) {
    case 'unread': return 'red'
    case 'read': return 'blue'
    case 'sent': return 'green'
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
  
  try {
    // Find the selected recipient
    const selectedRecipient = availableRecipients.value.find(r => r.value === composeForm.to)
    
    const messageData = {
      to: composeForm.to,
      toUserId: selectedRecipient?.toUserId || composeForm.to,
      toUserName: selectedRecipient?.toUserName || composeForm.to,
      subject: composeForm.subject,
      content: composeForm.message,
      fromUserName: currentUserName.value,
      category: 'general'
    }
    
    const response = await sendMessageAPI(messageData)
    
    // service 已返回后端的 data 对象: { code, msg, data }
    if (response && response.data) {
      const resData = response.data
      const newMessage = {
        id: resData.id,
        subject: resData.subject,
        from: resData.fromUserName,
        date: new Date(resData.createdAt).toLocaleString(),
        status: 'sent',
        content: resData.content
      }
      messages.value.unshift(newMessage)
      message.success('Message sent successfully')
    } else {
      console.error('Invalid response format:', response)
      message.error('Invalid response from server')
    }
    
    // Always close modal and reset form after sending (success or failure)
    isComposeModalOpen.value = false
    composeForm.to = ''
    composeForm.subject = ''
    composeForm.message = ''
    
  } catch (error) {
    console.error('Failed to send message:', error)
    message.error(error.message || 'Failed to send message')
    
    // Close modal and reset form even on error
    isComposeModalOpen.value = false
    composeForm.to = ''
    composeForm.subject = ''
    composeForm.message = ''
  }
}

// 取消撰写
const cancelCompose = () => {
  isComposeModalOpen.value = false
}

// 查看消息
const viewMessage = async (record) => {
  if (!record || !record.id) {
    message.error('Invalid message')
    return
  }
  
  currentMessage.value = record
  
  // 标记为已读
  if (record.status === 'unread') {
    try {
      await markMessageAsRead(record.id)
      record.status = 'read'
    } catch (error) {
      console.error('Failed to mark message as read:', error)
    }
  }
  
  isViewModalOpen.value = true
}

// 回复消息
const replyMessage = (record) => {
  if (!record || !record.id) {
    message.error('Invalid message')
    return
  }
  
  // 根据发送者确定收件人
  if (record.from === 'Manager') {
    replyForm.to = 'Family Member'
  } else {
    replyForm.to = 'Manager'
  }
  replyForm.subject = `Re: ${record.subject}`
  replyForm.message = ''
  currentMessage.value = record
  isReplyModalOpen.value = true
}

// 发送回复
const sendReply = async () => {
  if (!replyForm.message) {
    message.error('Please enter your reply message')
    return
  }
  
  if (!currentMessage.value || !currentMessage.value.id) {
    message.error('No message selected for reply')
    return
  }
  
  try {
    const replyData = {
      content: replyForm.message,
      subject: replyForm.subject,
      fromUserName: currentUserName.value
    }
    
    const response = await replyToMessage(currentMessage.value.id, replyData)
    
    // service 已返回后端的 data 对象: { code, msg, data }
    if (response && response.data) {
      const resData = response.data
      const newReply = {
        id: resData.id,
        subject: resData.subject,
        from: resData.fromUserName,
        date: new Date(resData.createdAt).toLocaleString(),
        status: 'sent',
        content: resData.content
      }
      
      messages.value.unshift(newReply)
      
      if (currentMessage.value) {
        currentMessage.value.status = 'replied'
      }
      
      message.success('Reply sent successfully')
    } else {
      console.error('Invalid reply response format:', response)
      message.error('Invalid response from server')
    }
    
    // Always close modal and reset form after sending (success or failure)
    isReplyModalOpen.value = false
    replyForm.to = ''
    replyForm.subject = ''
    replyForm.message = ''
    
  } catch (error) {
    console.error('Failed to send reply:', error)
    message.error(error.message || 'Failed to send reply')
    
    // Close modal and reset form even on error
    isReplyModalOpen.value = false
    replyForm.to = ''
    replyForm.subject = ''
    replyForm.message = ''
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
