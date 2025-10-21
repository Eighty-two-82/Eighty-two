<template>
    <a-layout style="min-height: 100vh">
      <!-- left menu -->
      <a-layout-sider :width="256" theme="light">
        <!-- User Info Section -->
        <div style="padding: 16px; border-bottom: 1px solid #f0f0f0; background: #fafafa;">
          <div style="text-align: center;">
            <a-avatar :size="48" style="margin-bottom: 8px;">
              <template #icon><UserOutlined /></template>
            </a-avatar>
            <div style="font-size: 16px; font-weight: 600; color: #1890ff; margin-bottom: 4px;">
              {{ userName }}
            </div>
            <div style="font-size: 12px; color: #666; text-transform: uppercase;">
              {{ getRoleDisplayName() }}
            </div>
          </div>
        </div>
        
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="inline"
          @click="onMenuClick"
          style="border: none; background: white;"
        >
          <a-menu-item key="home" style="color: #000; font-size: 14px;">
            <template #icon><HomeOutlined /></template>
            <span>Home</span>
            <a-tooltip title="Return to main page, view system overview and quick actions" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Worker Management -->
          <a-menu-item v-if="userRole === 'manager'" key="worker-management" style="color: #000; font-size: 14px;">
            <template #icon><AppstoreOutlined /></template>
            <span>Worker Management</span>
            <a-tooltip title="Manage care staff, including inviting Workers to join the team" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Power of Attorney/Family Member Budget -->
          <a-menu-item v-if="userRole === 'manager' || userRole === 'poa'" key="budget" style="color: #000; font-size: 14px;">
            <template #icon><BarChartOutlined /></template>
            <span>Budget</span>
            <a-tooltip :title="getBudgetTooltip()" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Tasks -->
          <a-menu-item key="tasks" style="color: #000; font-size: 14px;">
            <template #icon><CheckSquareOutlined /></template>
            <span>Tasks</span>
            <a-tooltip :title="getTasksTooltip()" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Upload -->
          <a-menu-item key="upload" style="color: #000; font-size: 14px;">
            <template #icon><SettingOutlined /></template>
            <span>Upload</span>
            <a-tooltip title="Upload and manage files, including documents, images and other materials" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Manager and Power of Attorney/Family Member Communication -->
          <a-menu-item v-if="userRole === 'manager' || userRole === 'poa'" key="communication" style="color: #000; font-size: 14px;">
            <template #icon><MessageOutlined /></template>
            <span>Communication</span>
            <a-tooltip :title="getCommunicationTooltip()" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Power of Attorney/Family Member Carer Team -->
          <a-menu-item v-if="userRole === 'poa'" key="carer-team" style="color: #000; font-size: 14px;">
            <template #icon><AppstoreOutlined /></template>
            <span>Carer Team</span>
            <a-tooltip title="View and manage care team information, invite organizations to join" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- All roles Setting -->
          <a-menu-item key="setting" style="color: #000; font-size: 14px;">
            <template #icon><SettingOutlined /></template>
            <span>Setting</span>
            <a-tooltip :title="getSettingTooltip()" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
          
          <!-- Logout -->
          <a-menu-item key="logout" style="color: #ff4d4f; font-size: 14px;">
            <template #icon><LogoutOutlined /></template>
            <span>Logout</span>
            <a-tooltip title="Logout from the system" placement="right">
              <QuestionCircleOutlined style="margin-left: 8px; color: #999; cursor: help;" />
            </a-tooltip>
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
  
      <!-- right content -->
      <a-layout>
        <!-- Header with help button, notifications and logout -->
        <a-layout-header style="background: #fff; padding: 0 24px; display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #f0f0f0;">
          <!-- Left side - User info -->
          <div style="display: flex; align-items: center; gap: 12px;">
            <div style="font-size: 16px; font-weight: 600; color: #1890ff;">
              Welcome, {{ userName }}
            </div>
            <a-tag :color="getRoleColor()" style="margin: 0;">
              {{ getRoleDisplayName() }}
            </a-tag>
          </div>
          
          <!-- Right side - Actions -->
          <div style="display: flex; align-items: center; gap: 16px;">
            <!-- Notification Bell -->
            <a-badge 
              :count="unreadNotificationsCount" 
              :offset="[10, 0]"
              :show-zero="false"
            >
              <a-tooltip title="View notifications and alerts">
                <a-button type="text" @click="showNotificationModal" style="color: #1890ff; position: relative;">
                  <template #icon><BellOutlined /></template>
                </a-button>
              </a-tooltip>
            </a-badge>
            
            <!-- Help Button -->
            <a-tooltip title="Click to get help information">
              <a-button type="text" @click="showHelpModal" style="color: #1890ff;">
                <template #icon><QuestionCircleOutlined /></template>
                Help
              </a-button>
            </a-tooltip>
            
            <!-- Logout Button -->
            <a-tooltip title="Logout from the system">
              <a-button type="text" @click="handleLogout" style="color: #ff4d4f;">
                <template #icon><LogoutOutlined /></template>
                Logout
              </a-button>
            </a-tooltip>
          </div>
        </a-layout-header>
        
        <a-layout-content style="margin: 16px">
          <div style="padding: 24px; background: #fff; min-height: 360px">
            <router-view />
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>

    <!-- Help Modal -->
    <a-modal
      v-model:open="helpModalVisible"
      title="Help Information"
      :footer="null"
      width="600px"
    >
      <div style="line-height: 1.6;">
        <h4>System Feature Introduction:</h4>
        <ul>
          <li><strong>Home:</strong> Return to main page, view system overview and quick actions</li>
          <li v-if="userRole === 'manager'"><strong>Worker Management:</strong> Manage care staff, including inviting Workers to join the team</li>
          <li v-if="userRole === 'manager' || userRole === 'poa'"><strong>Budget:</strong> {{ getBudgetTooltip() }}</li>
          <li><strong>Tasks:</strong> {{ getTasksTooltip() }}</li>
          <li><strong>Upload:</strong> Upload and manage files, including documents, images and other materials</li>
          <li v-if="userRole === 'manager' || userRole === 'poa'"><strong>Communication:</strong> {{ getCommunicationTooltip() }}</li>
          <li v-if="userRole === 'poa'"><strong>Carer Team:</strong> View and manage care team information, invite organizations to join</li>
          <li><strong>Setting:</strong> {{ getSettingTooltip() }}</li>
          <li><strong>Logout:</strong> Logout from the system and return to login page</li>
        </ul>
        <p style="margin-top: 16px; color: #666;">
          <strong>Tip:</strong> Hover over the question mark icon on menu items to view detailed descriptions.
        </p>
      </div>
    </a-modal>

    <!-- First Visit Modal -->
    <a-modal
      v-model:open="firstVisitModalVisible"
      title="Welcome to Care App"
      :footer="null"
      :closable="false"
      :maskClosable="false"
      width="500px"
    >
      <div style="text-align: center; line-height: 1.6;">
        <div style="font-size: 48px; color: #1890ff; margin-bottom: 16px;">👋</div>
        <h3>Welcome to Care App!</h3>
        <p style="margin: 16px 0; color: #666;">
          <strong>💡 Tip:</strong> If you encounter any problems while using the system, please click the <strong>Help</strong> button in the top right corner to get help information.
        </p>
        <a-button type="primary" @click="closeFirstVisitModal" style="margin-top: 16px;">
          Get Started
        </a-button>
      </div>
    </a-modal>

    <!-- Notification Modal -->
    <a-modal
      v-model:open="notificationModalVisible"
      title="Notifications & Alerts"
      width="600px"
      :footer="null"
    >
      <div style="max-height: 500px; overflow-y: auto;">
        <div v-if="notifications.length === 0" style="text-align: center; padding: 40px; color: #999;">
          <BellOutlined style="font-size: 48px; margin-bottom: 16px; color: #d9d9d9;" />
          <p>No notifications</p>
        </div>
        
        <div v-else>
          <div v-for="notification in notifications" :key="notification.id" 
               style="margin-bottom: 16px; padding: 12px; border-radius: 6px; border-left: 4px solid;"
               :style="{ 
                 backgroundColor: getNotificationBgColor(notification.type),
                 borderLeftColor: getNotificationColor(notification.type)
               }">
            <div style="display: flex; justify-content: space-between; align-items: flex-start;">
              <div style="flex: 1;">
                <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 4px;">
                  <span :style="{ color: getNotificationColor(notification.type) }">
                    {{ getNotificationIcon(notification.type) }}
                  </span>
                  <span style="font-weight: 600; font-size: 14px;">
                    {{ notification.title }}
                  </span>
                  <a-tag :color="getNotificationTagColor(notification.type)" size="small">
                    {{ notification.type?.toUpperCase() || 'NOTIFICATION' }}
                  </a-tag>
                </div>
                <p style="margin: 0; color: #666; font-size: 13px; line-height: 1.4;">
                  {{ notification.message }}
                </p>
                <div style="margin-top: 8px; font-size: 12px; color: #999;">
                  {{ formatNotificationTime(notification.createdAt) }}
                </div>
              </div>
              <a-button 
                v-if="!notification.isRead" 
                type="text" 
                size="small" 
                @click="markAsRead(notification.id)"
                style="color: #1890ff;"
              >
                Mark as read
              </a-button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="notifications.length > 0" style="margin-top: 16px; text-align: center; border-top: 1px solid #f0f0f0; padding-top: 16px;">
        <a-button type="primary" @click="markAllAsRead" :disabled="unreadNotificationsCount === 0">
          Mark All as Read
        </a-button>
      </div>
    </a-modal>
  </template>
  
  <script setup>
  import { ref, onMounted, watchEffect, computed } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { message } from 'ant-design-vue'
  import {
    HomeOutlined, CheckSquareOutlined, BarChartOutlined,
    SettingOutlined, MessageOutlined, AppstoreOutlined, UserOutlined,
    QuestionCircleOutlined, BellOutlined, LogoutOutlined
  } from '@ant-design/icons-vue'
  import { getMe, logout } from '@/services/userService'
  import { getMyNotifications, markNotificationAsRead, markAllNotificationsAsRead } from '@/services/notificationService'
  import { getBudgetByPatient } from '@/services/budgetService'
  
  const selectedKeys = ref(['home'])
  const userRole = ref('worker')
  const userName = ref('Test User')
  const helpModalVisible = ref(false)
  const firstVisitModalVisible = ref(false)
  const notificationModalVisible = ref(false)
  
  // User notification settings
  const userNotificationSettings = ref({
    taskReminders: true,
    approvalNotifications: true,
    budgetWarning: true,
    emailNotifications: true
  })
  
  // Real notification data from backend
  const allNotifications = ref([])
  
  onMounted(async () => {
    try {
      console.log('Layout component mounted, fetching user info...')
      const userInfo = await getMe()
      console.log('User info received:', userInfo)
      userRole.value = userInfo?.data?.role || 'worker'
      userName.value = userInfo?.data?.name || 'Test User'
      
      // Load user notification settings
      if (userInfo?.data) {
        userNotificationSettings.value = {
          taskReminders: userInfo.data.taskReminders !== undefined ? userInfo.data.taskReminders : true,
          approvalNotifications: userInfo.data.approvalNotifications !== undefined ? userInfo.data.approvalNotifications : true,
          budgetWarning: userInfo.data.budgetWarning !== undefined ? userInfo.data.budgetWarning : true,
          emailNotifications: userInfo.data.emailNotifications !== undefined ? userInfo.data.emailNotifications : true
        }
        console.log('Loaded notification settings:', userNotificationSettings.value)
      }
      
      console.log('Set userRole to:', userRole.value)
      console.log('Set userName to:', userName.value)
      
      // Load notifications from backend
      await loadNotifications()
      
      // Check if this is the first visit
      checkFirstVisit()
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
  // Load notifications from backend
  const loadNotifications = async () => {
    try {
      console.log('Loading notifications from backend...')
      const result = await getMyNotifications()
      allNotifications.value = result.data || []
      console.log('Loaded notifications:', allNotifications.value)
    } catch (error) {
      console.error('Failed to load notifications:', error)
      allNotifications.value = []
    }
  }
  
  
  // Check if this is the user's first visit
  const checkFirstVisit = async () => {
    try {
      const userInfo = await getMe()
      const userId = userInfo?.data?.id || 'anonymous'
      const appFirstVisitKey = `app_first_visit_${userId}`
      
      // Check if user has visited the app before
      const hasVisitedBefore = localStorage.getItem(appFirstVisitKey)
      
      if (!hasVisitedBefore) {
        // First time visiting the app
        firstVisitModalVisible.value = true
      }
    } catch (error) {
      console.error('Failed to check first visit:', error)
      // On error, don't show the modal to avoid annoying users
      firstVisitModalVisible.value = false
    }
  }

  // Close first visit modal and mark as visited
  const closeFirstVisitModal = () => {
    firstVisitModalVisible.value = false
    try {
      const userInfo = getMe()
      const userId = userInfo?.data?.id || 'anonymous'
      const appFirstVisitKey = `app_first_visit_${userId}`
      localStorage.setItem(appFirstVisitKey, 'true')
    } catch (error) {
      console.error('Failed to save first visit status:', error)
    }
  }

  // Show help modal
  const showHelpModal = () => {
    helpModalVisible.value = true
  }

  // Show notification modal
  const showNotificationModal = () => {
    notificationModalVisible.value = true
  }

  // Get notifications based on user role and notification settings
  const notifications = computed(() => {
    // Filter notifications based on user settings
    return allNotifications.value.filter(notification => {
      const category = notification.category
      
      // Map notification categories to user settings
      switch (category) {
        case 'task':
          return userNotificationSettings.value.taskReminders
        case 'approval':
          return userNotificationSettings.value.approvalNotifications
        case 'budget':
          return userNotificationSettings.value.budgetWarning
        case 'system':
          // System notifications are always shown (important for maintenance, etc.)
          return true
        default:
          // Default to showing notification if category is not recognized
          return true
      }
    })
  })

  // Computed property for unread notifications count
  const unreadNotificationsCount = computed(() => {
    const count = notifications.value.filter(n => !n.isRead).length
    console.log('Unread notifications count:', count)
    return count
  })

  // Notification functions
  const markAsRead = async (notificationId) => {
    try {
      await markNotificationAsRead(notificationId)
      
      // Update local state
      const notification = allNotifications.value.find(n => n.id === notificationId)
      if (notification) {
        notification.isRead = true
        console.log('Marked notification as read:', notificationId)
        console.log('Unread count after mark as read:', unreadNotificationsCount.value)
        
        // Show success message
        message.success('Notification marked as read')
      }
    } catch (error) {
      console.error('Failed to mark notification as read:', error)
      message.error('Failed to mark notification as read')
    }
  }

  const markAllAsRead = async () => {
    try {
      // Mark all backend notifications as read
      const result = await markAllNotificationsAsRead()
      
      // Mark all local notifications as read
      allNotifications.value.forEach(notification => {
        notification.isRead = true
      })
      
      console.log('Marked all notifications as read')
      console.log('Unread count after mark all as read:', unreadNotificationsCount.value)
      
      // Show success message
      message.success('All notifications marked as read')
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error)
      message.error('Failed to mark all notifications as read')
    }
  }

  // Format notification time
  const formatNotificationTime = (timestamp) => {
    const now = new Date()
    const time = new Date(timestamp)
    const diffMs = now - time
    const diffMins = Math.floor(diffMs / (1000 * 60))
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))

    if (diffMins < 1) return 'Just now'
    if (diffMins < 60) return `${diffMins} minutes ago`
    if (diffHours < 24) return `${diffHours} hours ago`
    if (diffDays < 7) return `${diffDays} days ago`
    return time.toLocaleDateString()
  }

  // Get notification color based on type
  const getNotificationColor = (type) => {
    switch (type) {
      case 'error': return '#ff4d4f'
      case 'warning': return '#faad14'
      case 'success': return '#52c41a'
      case 'info': 
      default: return '#1890ff'
    }
  }

  // Get notification background color
  const getNotificationBgColor = (type) => {
    switch (type) {
      case 'error': return '#fff2f0'
      case 'warning': return '#fffbe6'
      case 'success': return '#f6ffed'
      case 'info': 
      default: return '#f0f9ff'
    }
  }

  // Get notification tag color
  const getNotificationTagColor = (type) => {
    switch (type) {
      case 'error': return 'red'
      case 'warning': return 'orange'
      case 'success': return 'green'
      case 'info': 
      default: return 'blue'
    }
  }

  // Get notification icon
  const getNotificationIcon = (type) => {
    switch (type) {
      case 'error': return '❌'
      case 'warning': return '⚠️'
      case 'success': return '✅'
      case 'info': 
      default: return 'ℹ️'
    }
  }

  // Role display functions
  const getRoleDisplayName = () => {
    switch (userRole.value) {
      case 'manager': return 'Manager'
      case 'poa': return 'Power of Attorney'
      case 'worker': return 'Worker'
      default: return 'User'
    }
  }

  const getRoleColor = () => {
    switch (userRole.value) {
      case 'manager': return 'blue'
      case 'poa': return 'green'
      case 'worker': return 'orange'
      default: return 'default'
    }
  }

  // Tooltip functions
  const getBudgetTooltip = () => {
    if (userRole.value === 'manager') {
      return 'Manage and monitor budget allocations, track expenses and financial reports'
    } else if (userRole.value === 'poa') {
      return 'View budget information and financial reports for care services'
    }
    return ''
  }

  const getTasksTooltip = () => {
    switch (userRole.value) {
      case 'manager': return 'Create, assign and manage care tasks, monitor completion status'
      case 'poa': return 'View task progress, approve completed tasks and provide feedback'
      case 'worker': return 'View assigned tasks, update completion status and submit for approval'
      default: return 'Manage and track care-related tasks'
    }
  }

  const getCommunicationTooltip = () => {
    if (userRole.value === 'manager') {
      return 'Communicate with care team members, send announcements and coordinate care activities'
    } else if (userRole.value === 'poa') {
      return 'Communicate with care team, receive updates and provide feedback on care services'
    }
    return ''
  }

  const getSettingTooltip = () => {
    switch (userRole.value) {
      case 'manager': return 'Configure system settings, manage user permissions and system preferences'
      case 'poa': return 'Update personal information, notification preferences and account settings'
      case 'worker': return 'Update personal profile, notification settings and account preferences'
      default: return 'Manage account settings and preferences'
    }
  }

  // Menu click handler
  const onMenuClick = ({ key }) => {
    console.log('Menu clicked:', key)
    selectedKeys.value = [key]
    
    if (key === 'logout') {
      handleLogout()
      return
    }
    
    const path = getPathByKey(key)
    if (path) router.push(path)
  }

  // Get path by menu key
  const getPathByKey = (key) => {
    const pathMap = {
      'home': '/app/menu',
      'worker-management': '/app/worker-management',
      'budget': '/app/budget',
      'tasks': '/app/tasks',
      'upload': '/app/upload',
      'communication': '/app/communication',
      'carer-team': '/app/carer-team',
      'setting': '/app/setting'
    }
    return pathMap[key]
  }

  // Router and route
  const router = useRouter()
  const route = useRoute()

  // Get menu key by path
  const getKeyByPath = (path) => {
    const keyMap = {
      '/app/menu': 'home',
      '/app/worker-management': 'worker-management',
      '/app/budget': 'budget',
      '/app/tasks': 'tasks',
      '/app/upload': 'upload',
      '/app/communication': 'communication',
      '/app/carer-team': 'carer-team',
      '/app/setting': 'setting'
    }
    return keyMap[path]
  }

  // Watch route changes to update selected keys
  watchEffect(() => {
    const currentPath = route.path
    const key = getKeyByPath(currentPath)
    if (key) {
      selectedKeys.value = [key]
    }
  })

  // Logout handler
  const handleLogout = async () => {
    try {
      await logout()
      router.push('/login')
    } catch (error) {
      console.error('Logout failed:', error)
      // Still redirect to login even if logout fails
      router.push('/login')
    }
  }
  </script>

  <style scoped>
  .ant-layout-sider {
    box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  }
  
  .ant-menu-item {
    margin: 0 !important;
    border-radius: 6px !important;
  }
  
  .ant-menu-item:hover {
    background-color: #f0f9ff !important;
  }
  
  .ant-menu-item-selected {
    background-color: #e6f7ff !important;
    color: #1890ff !important;
  }
  
  .ant-menu-item-selected .anticon {
    color: #1890ff !important;
  }
  </style>