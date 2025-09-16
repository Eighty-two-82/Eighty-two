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
        </a-menu>
      </a-layout-sider>
  
      <!-- right content -->
      <a-layout>
        <!-- Header with help button -->
        <a-layout-header style="background: #fff; padding: 0 24px; display: flex; justify-content: flex-end; align-items: center; border-bottom: 1px solid #f0f0f0;">
          <a-tooltip title="Click to get help information">
            <a-button type="text" @click="showHelpModal" style="color: #1890ff;">
              <template #icon><QuestionCircleOutlined /></template>
              Help
            </a-button>
          </a-tooltip>
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
  </template>
  
  <script setup>
  import { ref, onMounted, watchEffect } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import {
    HomeOutlined, CheckSquareOutlined, BarChartOutlined,
    SettingOutlined, MessageOutlined, AppstoreOutlined, UserOutlined,
    QuestionCircleOutlined
  } from '@ant-design/icons-vue'
  import { getMe } from '@/services/userService'
  
  const selectedKeys = ref(['home'])
  const userRole = ref('worker')
  const userName = ref('Test User')
  const helpModalVisible = ref(false)
  const firstVisitModalVisible = ref(false)
  
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
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
  // Check if this is the user's first visit
  const checkFirstVisit = () => {
    const hasVisited = localStorage.getItem('careApp_hasVisited')
    if (!hasVisited) {
      firstVisitModalVisible.value = true
    }
  }
  
  // Close first visit modal and mark as visited
  const closeFirstVisitModal = () => {
    firstVisitModalVisible.value = false
    localStorage.setItem('careApp_hasVisited', 'true')
  }
  
  // Show help modal
  const showHelpModal = () => {
    helpModalVisible.value = true
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
  </script>
  