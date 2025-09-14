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
              {{ userRole }}
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
            Home
          </a-menu-item>
          
          <!-- Manager and Worker Management -->
          <a-menu-item v-if="userRole === 'manager'" key="worker-management" style="color: #000; font-size: 14px;">
            <template #icon><AppstoreOutlined /></template>
            Worker Management
          </a-menu-item>
          
          <!-- Manager and POA/FM Budget -->
          <a-menu-item v-if="userRole === 'manager' || userRole === 'poa'" key="budget" style="color: #000; font-size: 14px;">
            <template #icon><BarChartOutlined /></template>
            Budget
          </a-menu-item>
          
          <!-- All roles Tasks -->
          <a-menu-item key="tasks" style="color: #000; font-size: 14px;">
            <template #icon><CheckSquareOutlined /></template>
            Tasks
          </a-menu-item>
          
          <!-- All roles Upload -->
          <a-menu-item key="upload" style="color: #000; font-size: 14px;">
            <template #icon><SettingOutlined /></template>
            Upload
          </a-menu-item>
          
          <!-- Manager Communication -->
          <a-menu-item v-if="userRole === 'manager'" key="communication" style="color: #000; font-size: 14px;">
            <template #icon><MessageOutlined /></template>
            Communication
          </a-menu-item>
          
          <!-- POA/FM Carer Team -->
          <a-menu-item v-if="userRole === 'poa'" key="carer-team" style="color: #000; font-size: 14px;">
            <template #icon><AppstoreOutlined /></template>
            Carer Team
          </a-menu-item>
          
          <!-- All roles Setting -->
          <a-menu-item key="setting" style="color: #000; font-size: 14px;">
            <template #icon><SettingOutlined /></template>
            Setting
          </a-menu-item>
        </a-menu>
      </a-layout-sider>
  
      <!-- right content -->
      <a-layout>
        <a-layout-content style="margin: 16px">
          <div style="padding: 24px; background: #fff; min-height: 360px">
            <router-view />
          </div>
        </a-layout-content>
      </a-layout>
    </a-layout>
  </template>
  
  <script setup>
  import { ref, onMounted, watchEffect } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import {
    HomeOutlined, CheckSquareOutlined, BarChartOutlined,
    SettingOutlined, MessageOutlined, AppstoreOutlined, UserOutlined
  } from '@ant-design/icons-vue'
  import { getMe } from '@/services/userService'
  
  const selectedKeys = ref(['home'])
  const userRole = ref('worker')
  const userName = ref('Test User')
  
  onMounted(async () => {
    try {
      console.log('Layout component mounted, fetching user info...')
      const userInfo = await getMe()
      console.log('User info received:', userInfo)
      userRole.value = userInfo?.data?.role || 'worker'
      userName.value = userInfo?.data?.name || 'Test User'
      console.log('Set userRole to:', userRole.value)
      console.log('Set userName to:', userName.value)
    } catch (e) {
      console.error('Failed to get user info:', e)
    }
  })
  
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
  