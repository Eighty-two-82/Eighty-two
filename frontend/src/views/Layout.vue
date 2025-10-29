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
                    {{ notification.type.toUpperCase() }}
                  </a-tag>
                </div>
                <p style="margin: 0; color: #666; font-size: 13px; line-height: 1.4;">
                  {{ notification.message }}
                </p>
                <div style="margin-top: 8px; font-size: 12px; color: #999;">
                  {{ formatNotificationTime(notification.createdAt || notification.timestamp) }}
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
  import {
    HomeOutlined, CheckSquareOutlined, BarChartOutlined,
    SettingOutlined, MessageOutlined, AppstoreOutlined, UserOutlined,
    QuestionCircleOutlined, BellOutlined, LogoutOutlined
  } from '@ant-design/icons-vue'
  import { message } from 'ant-design-vue'
  import { getMe, logout } from '@/services/userService'
  import { 
    getMyNotifications, 
    markAsRead as markNotificationAsRead, 
    markAllAsRead as markAllNotificationsAsRead 
  } from '@/services/notificationService'
  
  const selectedKeys = ref(['home'])
  const userRole = ref('worker')
  const userName = ref('Test User')
  const helpModalVisible = ref(false)
  const firstVisitModalVisible = ref(false)
  const notificationModalVisible = ref(false)
  
  onMounted(async () => {
    try {
      console.log('Layout component mounted, fetching user info...')
      const userInfo = await getMe()
      console.log('User info received:', userInfo)
      userRole.value = userInfo?.data?.role || 'worker'
      userName.value = userInfo?.data?.name || 'Test User'
      console.log('Set userRole to:', userRole.value)
      console.log('Set userName to:', userName.value)
      
      // Check if this is the first visit
      checkFirstVisit()
      
      // Load notifications from backend API
      await loadNotifications()
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
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
  const closeFirstVisitModal = async () => {
    firstVisitModalVisible.value = false
    
    try {
      const userInfo = await getMe()
      const userId = userInfo?.data?.id || 'anonymous'
      const appFirstVisitKey = `app_first_visit_${userId}`
      
      // Mark that user has visited the app
      localStorage.setItem(appFirstVisitKey, 'true')
      console.log('App first visit marked as completed for user:', userId)
    } catch (error) {
      console.error('Failed to save first visit status:', error)
    }
  }
  
  // Show help modal
  const showHelpModal = () => {
    helpModalVisible.value = true
  }
  
  // Handle logout
  const handleLogout = () => {
    console.log('Logout button clicked');
    logout()
  }
  
  // Get role display name
  const getRoleDisplayName = () => {
    switch (userRole.value) {
      case 'poa':
        return 'Power of Attorney / Family Member'
      case 'manager':
        return 'Manager'
      case 'worker':
        return 'Worker'
      default:
        return userRole.value
    }
  }
  
  // Get role color for tag
  const getRoleColor = () => {
    switch (userRole.value) {
      case 'poa':
        return 'purple'
      case 'manager':
        return 'blue'
      case 'worker':
        return 'green'
      default:
        return 'default'
    }
  }
  
  // Get dynamic tooltips based on user role
  const getTasksTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'Task management: Assign tasks to care staff, monitor task execution'
      case 'worker':
        return 'Task management: View assigned tasks and execute them'
      case 'poa':
        return 'Task management: View all task execution status, approve important tasks'
      default:
        return 'Task management page'
    }
  }
  
  const getSettingTooltip = () => {
    switch (userRole.value) {
      case 'poa':
        return 'Settings page: Fill in patient information and special requirements, manage personal basic information and preferences'
      case 'worker':
      case 'manager':
        return 'Settings page: View patient information and special requirements, manage personal basic information and preferences'
      default:
        return 'Settings page: Manage personal basic information and preferences'
    }
  }
  
  const getCommunicationTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'Communication with patient agents/family members, send messages and notifications'
      case 'poa':
        return 'Communication with managers, receive messages and notifications'
      default:
        return 'Communication page'
    }
  }
  
  const getBudgetTooltip = () => {
    switch (userRole.value) {
      case 'manager':
        return 'View and manage budget information, including expense statistics and financial reports'
      case 'poa':
        return 'Power of Attorney/Family Members can modify and manage budgets'
      default:
        return 'Budget management page'
    }
  }
  
  const router = useRouter()
  const route = useRoute()
  
  // Route mapping
  const keyToPath = {
    home: '/app/menu',
    tasks: '/app/tasks',
    'carer-team': '/app/carer-team',
    budget: '/app/budget',
    upload: '/app/upload',
    setting: '/app/setting',
    logout: '/app/logout',
    'worker-management': '/app/worker-management',
    communication: '/app/communication',
  }
  
  // Auto highlight when route changes
  watchEffect(() => {
    const pathToKey = Object.fromEntries(Object.entries(keyToPath).map(([k, v]) => [v, k]))
    const k = pathToKey[route.path] || 'home'
    selectedKeys.value = [k]
  })
  
  const onMenuClick = ({ key }) => {
    const path = keyToPath[key]
    if (path) router.push(path)
  }

  // Notification data loaded from backend API
  const allNotifications = ref([])

  // Get notifications - now loaded from API
  const notifications = computed(() => {
    return allNotifications.value || []
  })

  // Computed property for unread notifications count
  const unreadNotificationsCount = computed(() => {
    const count = notifications.value.filter(n => !n.isRead).length
    console.log('Unread notifications count:', count)
    return count
  })

  // Notification functions
  const showNotificationModal = () => {
    notificationModalVisible.value = true
  }

  // Load notifications from backend API
  const loadNotifications = async () => {
    try {
      const response = await getMyNotifications()
      if (response?.data) {
        allNotifications.value = response.data
        console.log('Loaded notifications from backend:', allNotifications.value.length)
      }
    } catch (error) {
      console.error('Failed to load notifications:', error)
      // Set empty array on error
      allNotifications.value = []
    }
  }

  const markAsRead = async (notificationId) => {
    try {
      const response = await markNotificationAsRead(notificationId)
      if (response?.data) {
        // Update local notification state
        const notification = allNotifications.value.find(n => n.id === notificationId)
        if (notification) {
          notification.isRead = true
        }
        console.log('Marked notification as read:', notificationId)
        message.success('Notification marked as read')
      }
    } catch (error) {
      console.error('Failed to mark notification as read:', error)
      message.error('Failed to mark notification as read')
    }
  }

  const markAllAsRead = async () => {
    try {
      const response = await markAllNotificationsAsRead()
      if (response?.data !== undefined) {
        const markedCount = response.data
        console.log('Marked all notifications as read, count:', markedCount)
        
        // Update all notifications to read
        allNotifications.value.forEach(n => n.isRead = true)
        
        // Show success message
        if (markedCount > 0) {
          message.success(`All ${markedCount} notifications marked as read`)
        } else {
          message.info('No unread notifications to mark')
        }
      }
    } catch (error) {
      console.error('Failed to mark all notifications as read:', error)
      message.error('Failed to mark all notifications as read')
    }
  }

  const getNotificationColor = (type) => {
    switch (type) {
      case 'error': return '#ff4d4f'
      case 'warning': return '#fa8c16'
      case 'info': return '#1890ff'
      case 'success': return '#52c41a'
      default: return '#666'
    }
  }

  const getNotificationBgColor = (type) => {
    switch (type) {
      case 'error': return '#fff2f0'
      case 'warning': return '#fff7e6'
      case 'info': return '#e6f7ff'
      case 'success': return '#f6ffed'
      default: return '#fafafa'
    }
  }

  const getNotificationTagColor = (type) => {
    switch (type) {
      case 'error': return 'red'
      case 'warning': return 'orange'
      case 'info': return 'blue'
      case 'success': return 'green'
      default: return 'default'
    }
  }

  const getNotificationIcon = (type) => {
    switch (type) {
      case 'error': return '🚨'
      case 'warning': return '⚠️'
      case 'info': return 'ℹ️'
      case 'success': return '✅'
      default: return '📢'
    }
  }

  const formatNotificationTime = (timestamp) => {
    if (!timestamp) return 'Unknown'
    
    const now = new Date()
    // Handle both Date objects and string timestamps
    const notificationDate = timestamp instanceof Date ? timestamp : new Date(timestamp)
    const diff = now - notificationDate
    const hours = Math.floor(diff / (1000 * 60 * 60))
    const minutes = Math.floor(diff / (1000 * 60))
    
    if (hours > 24) {
      return `${Math.floor(hours / 24)} days ago`
    } else if (hours > 0) {
      return `${hours} hours ago`
    } else if (minutes > 0) {
      return `${minutes} minutes ago`
    } else {
      return 'Just now'
    }
  }
  </script>
  