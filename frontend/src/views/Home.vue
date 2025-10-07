<template>
  <div class="home-page">
    <a-card style="margin-bottom: 16px;">
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px; justify-content: space-between;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <h2 style="margin: 0;">Welcome to Care App</h2>
            <a-tooltip title="This is the system main page, displaying system overview and quick action entries" placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <a-tag color="blue" style="text-transform: uppercase;">{{ roleDisplay }}</a-tag>
        </div>
      </template>
      <div style="color:#666;">Choose a shortcut below to jump directly to a feature page.</div>
    </a-card>

    <!-- Worker: Tasks + Upload -->
    <a-row v-if="userRole === 'worker'" :gutter="16" style="margin-bottom: 16px;">
      <!-- Tasks Preview -->
      <a-col :xs="24" :md="12">
        <a-card>
          <template #title>
            <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
              <div style="display:flex;align-items:center;gap:8px;">
                <CheckSquareOutlined />
                <router-link to="/app/tasks" style="color:#1890ff;">Tasks Preview</router-link>
              </div>
              <router-link to="/app/tasks">
                <a-button type="link">View all</a-button>
              </router-link>
            </div>
          </template>

          <a-table :columns="taskPreviewColumns" :data-source="taskPreview" :pagination="false" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'priority'">
                <a-tag :color="getPriorityColor(record.priority)">{{ getPriorityText(record.priority) }}</a-tag>
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">{{ record.status }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>

      <!-- Upload Preview -->
      <a-col :xs="24" :md="12">
        <a-card>
          <template #title>
            <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
              <div style="display:flex;align-items:center;gap:8px;">
                <UploadOutlined />
                <router-link to="/app/upload" style="color:#1890ff;">Upload Preview</router-link>
              </div>
              <router-link to="/app/upload">
                <a-button type="link">Open</a-button>
              </router-link>
            </div>
          </template>

          <a-list :data-source="uploadPreview" bordered size="small">
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta :title="item.name" :description="item.size" />
                <span style="color:#999; font-size:12px;">{{ item.time }}</span>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- Non-worker: Budget + Tasks -->
    <a-row v-else :gutter="16" style="margin-bottom: 16px;">
      <!-- Budget Preview -->
      <a-col :xs="24" :md="12">
        <a-card>
          <template #title>
            <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
              <div style="display:flex;align-items:center;gap:8px;">
                <BarChartOutlined />
                <router-link to="/app/budget" style="color:#1890ff;">Budget Preview</router-link>
              </div>
              <a-tag :color="budgetUsageColor">{{ totalUsagePct }}% used</a-tag>
            </div>
          </template>

          <a-row :gutter="12">
            <a-col :span="8">
              <a-card size="small">
                <div style="text-align:center;">
                  <div style="font-size:12px;color:#666;margin-bottom:4px;">Total Budget</div>
                  <div style="font-size:20px;font-weight:700;color:#1890ff;">${{ totalBudget.toLocaleString() }}</div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card size="small">
                <div style="text-align:center;">
                  <div style="font-size:12px;color:#666;margin-bottom:4px;">Used</div>
                  <div style="font-size:20px;font-weight:700;color:#fa8c16;">${{ totalUsed.toLocaleString() }}</div>
                </div>
              </a-card>
            </a-col>
            <a-col :span="8">
              <a-card size="small">
                <div style="text-align:center;">
                  <div style="font-size:12px;color:#666;margin-bottom:4px;">Money Left</div>
                  <div :style="{fontSize:'20px',fontWeight:'700',color: totalLeft < 0 ? '#ff4d4f' : '#52c41a'}">${{ totalLeft.toLocaleString() }}</div>
                </div>
              </a-card>
            </a-col>
          </a-row>

          <!-- Top categories (table) -->
          <div style="margin-top:12px;">
            <div style="display:flex;align-items:center;gap:8px;margin-bottom:8px;">
              <span style="font-weight:600;">Top Categories Near/Over Budget</span>
            </div>
            <a-table
              :columns="topCategoryColumns"
              :data-source="topCategoryWarnings"
              :pagination="false"
              size="small"
              rowKey="category"
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'usage'">
                  <span style="white-space: nowrap;">${{ record.used.toLocaleString() }} / ${{ record.budget.toLocaleString() }}</span>
                </template>
                <template v-else-if="column.key === 'progress'">
                  <div style="display:flex;align-items:center;gap:8px;">
                    <a-progress :percent="record.pct" :stroke-color="record.pct >= 100 ? '#ff4d4f' : (record.pct >= 80 ? '#fa8c16' : '#52c41a')" :show-info="false" style="flex:1;" />
                    <a-tag :color="record.pct >= 100 ? 'red' : (record.pct >= 80 ? 'orange' : 'green')">{{ record.pct }}%</a-tag>
                  </div>
                </template>
              </template>
            </a-table>
          </div>
        </a-card>
      </a-col>

      <!-- Tasks Preview -->
      <a-col :xs="24" :md="12">
        <a-card>
          <template #title>
            <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
              <div style="display:flex;align-items:center;gap:8px;">
                <CheckSquareOutlined />
                <router-link to="/app/tasks" style="color:#1890ff;">Tasks Preview</router-link>
              </div>
              <router-link to="/app/tasks">
                <a-button type="link">View all</a-button>
              </router-link>
            </div>
          </template>

          <a-table :columns="taskPreviewColumns" :data-source="taskPreview" :pagination="false" size="small">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'priority'">
                <a-tag :color="getPriorityColor(record.priority)">{{ getPriorityText(record.priority) }}</a-tag>
              </template>
              <template v-else-if="column.key === 'status'">
                <a-tag :color="getStatusColor(record.status)">{{ record.status }}</a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
    </a-row>

    <!-- Communication Preview (only for non-worker) -->
    <a-card v-if="userRole !== 'worker'">
      <template #title>
        <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
          <div style="display:flex;align-items:center;gap:8px;">
            <MessageOutlined />
            <router-link to="/app/communication" style="color:#1890ff;">Communication Preview</router-link>
          </div>
          <router-link to="/app/communication">
            <a-button type="link">Open</a-button>
          </router-link>
        </div>
      </template>

      <a-list :data-source="communicationPreview" bordered size="small">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta :title="item.subject" :description="item.preview" />
            <div style="display:flex;align-items:center;gap:12px;">
              <a-tag :color="item.unread ? 'blue' : 'default'">{{ item.unread ? 'Unread' : 'Read' }}</a-tag>
              <span style="color:#999; font-size:12px;">{{ item.time }}</span>
            </div>
          </a-list-item>
        </template>
      </a-list>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import { QuestionCircleOutlined, CheckSquareOutlined, BarChartOutlined, SettingOutlined, AppstoreOutlined, MessageOutlined, HomeOutlined, TeamOutlined, UploadOutlined } from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'
import { getBudgetByPatient, getBudgetCalculations } from '@/services/budgetService'
import { getTasksByPatient, getTasksByWorker, getAllTasks } from '@/services/taskService'

const userRole = ref('worker')

onMounted(async () => {
  try {
    const me = await getMe()
    userRole.value = me?.data?.role || 'worker'
    
    // Load real data based on user role
    await loadHomeData()
  } catch (e) {
    // keep default
  }
})

// Load home page data from API
const loadHomeData = async () => {
  try {
    const userInfo = await getMe()
    if (!userInfo?.data) return
    
    // Load budget data for non-worker roles
    if (userRole.value !== 'worker' && userInfo.data.patientId) {
      try {
        const budgetResponse = await getBudgetByPatient(userInfo.data.patientId)
        if (budgetResponse?.data) {
          budgetCategories.value = budgetResponse.data.categories || []
        }
      } catch (error) {
        console.error('Failed to load budget data:', error)
        budgetCategories.value = []
      }
    }
    
    // Load task data
    try {
      let taskResponse
      if (userRole.value === 'worker') {
        taskResponse = await getTasksByWorker(userInfo.data.id)
      } else if (userRole.value === 'poa') {
        taskResponse = await getTasksByPatient(userInfo.data.patientId)
      } else if (userRole.value === 'manager') {
        taskResponse = await getAllTasks()
      }
      
      if (taskResponse?.data) {
        taskPreview.value = taskResponse.data.slice(0, 3).map(task => ({
          id: task.id,
          title: task.title,
          priority: task.priority,
          dueDate: task.dueDate,
          status: task.status
        }))
      } else {
        taskPreview.value = []
      }
    } catch (error) {
      console.error('Failed to load task data:', error)
      taskPreview.value = []
    }
    
  } catch (error) {
    console.error('Failed to load home data:', error)
    budgetCategories.value = []
    taskPreview.value = []
  }
}

const roleDisplay = computed(() => {
  switch (userRole.value) {
    case 'poa': return 'Power of Attorney'
    case 'manager': return 'Manager'
    case 'worker': return 'Worker'
    default: return userRole.value
  }
})

// --- Budget Preview - loaded from API ---
const budgetCategories = ref([])

const totalBudget = computed(() => budgetCategories.value.reduce((s, c) => s + c.budget, 0))
const totalUsed = computed(() => budgetCategories.value.reduce((s, c) => s + c.used, 0))
const totalLeft = computed(() => totalBudget.value - totalUsed.value)
const totalUsagePct = computed(() => Math.round((totalUsed.value / totalBudget.value) * 100))
const budgetUsageColor = computed(() => totalUsagePct.value >= 100 ? 'red' : totalUsagePct.value >= 80 ? 'orange' : 'green')

const topCategoryWarnings = computed(() => {
  const arr = budgetCategories.value
    .map(c => ({
      category: c.name,
      pct: Math.round((c.used / c.budget) * 100),
      used: c.used,
      budget: c.budget
    }))
    .sort((a, b) => b.pct - a.pct)
    .slice(0, 3)
  return arr
})

const topCategoryColumns = [
  { title: 'Category', dataIndex: 'category', key: 'category' },
  { title: 'Usage', key: 'usage' },
  { title: 'Progress', key: 'progress', width: 160 }
]

// --- Tasks Preview - loaded from API ---
const taskPreview = ref([])

const taskPreviewColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' }
]

const getPriorityColor = (priority) => {
  switch (priority) {
    case 'normal': return 'green'
    case 'urgent': return 'orange'
    case 'very-urgent': return 'red'
    default: return 'default'
  }
}

const getPriorityText = (priority) => {
  switch (priority) {
    case 'normal': return 'Normal'
    case 'urgent': return 'Urgent'
    case 'very-urgent': return 'Very Urgent'
    default: return priority
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Pending': return 'orange'
    case 'In Progress': return 'blue'
    case 'Completed': return 'green'
    case 'Worker Completed': return 'gold'
    case 'Rejected': return 'red'
    case 'Cancelled': return 'red'
    default: return 'default'
  }
}

// --- Upload Preview (mock) ---
const uploadPreview = ref([
  { id: 'f1', name: 'receipt_2025-09-21.pdf', size: '234 KB', time: '1h ago' },
  { id: 'f2', name: 'care_plan_update.docx', size: '88 KB', time: 'Yesterday' },
  { id: 'f3', name: 'medication_photo.jpg', size: '1.2 MB', time: '2 days ago' }
])

// --- Communication Preview (mock) ---
const communicationPreview = ref([
  { id: 101, subject: 'Budget alert acknowledged', preview: 'Thanks, we will adjust the category next week.', unread: true, time: '2h ago' },
  { id: 102, subject: 'New task request', preview: 'POA requested: Add Physical Therapy Session.', unread: true, time: '5h ago' },
  { id: 103, subject: 'System maintenance notice', preview: 'System maintenance tonight from 2:00 AM to 4:00 AM.', unread: false, time: 'Yesterday' }
])
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
