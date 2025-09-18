<template>
  <div class="tasks-page">
    <a-card>
      <!-- 标题 -->
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Task Management</h2>
          <a-tooltip :title="getPageTooltip()" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>

      <a-row :gutter="24">
        <!-- 左边：日历 -->
        <a-col :span="10">
          <div class="calendar-wrapper">
            <a-calendar 
              v-model:value="value" 
              :fullscreen="false" 
              @panelChange="onPanelChange" 
              @select="onDateSelect"
            />
          </div>
        </a-col>

        <!-- 右边：任务说明 -->
        <a-col :span="14">
          <div class="tasks-info">
            <h3>Tasks for {{ value?.format('MMMM D, YYYY') || 'Select a date' }}</h3>

            <!-- Tabs for filtering -->
            <a-tabs v-model:activeKey="activeTab">
              <a-tab-pane key="all" tab="All" />
              <a-tab-pane key="pending" tab="Pending" />
              <a-tab-pane key="done" tab="Done" />
            </a-tabs>

            <!-- 按 Category 分组 -->
            <a-collapse accordion>
              <a-collapse-panel 
                v-for="cat in categories" 
                :key="cat" 
                :header="cat"
              >
                <a-list
                  :data-source="filteredTasksByCategory(cat)"
                  :renderItem="renderTaskItem"
                  bordered
                />
              </a-collapse-panel>
            </a-collapse>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, h } from 'vue'
import { QuestionCircleOutlined, CheckCircleOutlined } from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'
import { Dayjs } from 'dayjs'
import { List, Tag, Button, Popconfirm } from 'ant-design-vue'

const userRole = ref('worker')
const value = ref<Dayjs>()
const activeTab = ref('all')

// 示例任务数据
const tasks = ref([
  { id: 1, date: '2025-09-18', name: 'Measure blood pressure before 9:00 AM', carer: 'Alice', status: 'pending', priority: 'urgent', category: 'Health' },
  { id: 2, date: '2025-09-18', name: 'Buy toothpaste', carer: 'Bob', status: 'pending', priority: 'normal', category: 'Shopping' },
  { id: 3, date: '2025-09-18', name: 'Purchase prescription medicine (budget ¥500)', carer: 'Charlie', status: 'done', priority: 'very-urgent', category: 'Shopping' },
  { id: 4, date: '2025-09-19', name: 'Daily Walk (30 min)', carer: 'Alice', status: 'pending', priority: 'normal', category: 'Exercise' },
  { id: 5, date: '2025-09-19', name: 'Accompany to Medical Check-up', carer: 'David', status: 'pending', priority: 'urgent', category: 'Appointment' },
  { id: 6, date: '2025-09-20', name: 'Remind to take blood pressure medicine', carer: 'Bob', status: 'done', priority: 'normal', category: 'Health' },
  { id: 7, date: '2025-09-20', name: 'Organize medical report files', carer: 'Charlie', status: 'pending', priority: 'normal', category: 'Health' }
])

// 动态获取所有类别
const categories = computed(() => {
  if (!value.value) return []
  const taskList = tasksForSelectedDate.value
  return [...new Set(taskList.map(t => t.category))]
})

// 根据日期过滤任务
const tasksForSelectedDate = computed(() => {
  if (!value.value) return []
  return tasks.value.filter(task => task.date === value.value.format('YYYY-MM-DD'))
})

// 根据 tab 再过滤
const filteredTasks = computed(() => {
  switch (activeTab.value) {
    case 'pending':
      return tasksForSelectedDate.value.filter(t => t.status === 'pending')
    case 'done':
      return tasksForSelectedDate.value.filter(t => t.status === 'done')
    default:
      return tasksForSelectedDate.value
  }
})

// 优先级顺序
const priorityOrder = { 'very-urgent': 3, 'urgent': 2, 'normal': 1 }

// 根据类别再过滤并排序
const filteredTasksByCategory = (cat: string) => {
  return filteredTasks.value
    .filter(t => t.category === cat)
    .sort((a, b) => priorityOrder[b.priority] - priorityOrder[a.priority])
}

// 点击完成任务
const markAsDone = (id: number) => {
  const task = tasks.value.find(t => t.id === id)
  if (task) task.status = 'done'
}

// 渲染优先级 Tag
const getPriorityTag = (priority: string) => {
  switch (priority) {
    case 'very-urgent':
      return h(Tag, { color: 'red' }, { default: () => 'Very Urgent' })
    case 'urgent':
      return h(Tag, { color: 'orange' }, { default: () => 'Urgent' })
    default:
      return h(Tag, { color: 'blue' }, { default: () => 'Normal' })
  }
}

// 渲染任务卡片
const renderTaskItem: List['$props']['renderItem'] = (task: any) => {
  return h(
    'a-card',
    {
      size: 'small',
      style: { 
        marginBottom: '12px', 
        borderLeft: task.status === 'done' ? '4px solid green' : '4px solid orange' 
      }
    },
    {
      default: () => [
        h('div', { style: 'display:flex; justify-content:space-between; align-items:center;' }, [
          // 左边：任务信息
          h('div', {}, [
            h('strong', {}, task.name),
            h('div', { style: 'font-size: 12px; color: #666;' }, `Carer: ${task.carer}`),
            h('div', { style: 'font-size: 12px; color: #1890ff;' }, `Category: ${task.category}`),
            getPriorityTag(task.priority)
          ]),
          // 右边：状态或按钮
          task.status === 'done'
            ? h(CheckCircleOutlined, { style: 'color: green; font-size: 18px;' })
            : h(
                Popconfirm,
                { 
                  title: 'Are you sure to mark this task as done?', 
                  okText: 'Yes', 
                  cancelText: 'No',
                  onConfirm: () => markAsDone(task.id)
                },
                {
                  default: () => h(Button, { type: 'primary', size: 'small' }, { default: () => 'Mark as Done' })
                }
              )
        ])
      ]
    }
  )
}

const onPanelChange = (val: Dayjs, mode: string) => {
  console.log('Panel changed:', val.format('YYYY-MM-DD'), mode)
}
const onDateSelect = (val: Dayjs) => {
  console.log('Date selected:', val.format('YYYY-MM-DD'))
}

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

const getPageTooltip = () => {
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
</script>

<style scoped>
.tasks-page {
  max-width: 1200px;
  margin: 0 auto;
}
.calendar-wrapper {
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  padding: 8px;
}
.tasks-info {
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  background: #fafafa;
}
</style>
