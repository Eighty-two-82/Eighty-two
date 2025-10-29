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

    <!-- Worker: Schedule + Tasks (First Row) -->
    <a-row v-if="userRole === 'worker'" :gutter="16" style="margin-bottom: 16px;">
      <!-- Schedule Preview -->
      <a-col :xs="24" :md="12">
        <a-card>
          <template #title>
            <div style="display:flex;align-items:center;gap:8px;justify-content:space-between;">
              <div style="display:flex;align-items:center;gap:8px;">
                <ClockCircleOutlined />
                <span style="color:#1890ff;">My Schedule</span>
              </div>
              <a-button type="link" @click="showScheduleModal">View all</a-button>
            </div>
          </template>

          <div v-if="workerSchedule.length > 0">
            <a-list :data-source="workerSchedule" bordered size="small">
              <template #renderItem="{ item }">
                <a-list-item>
                  <a-list-item-meta>
                    <template #title>
                      <div style="display: flex; align-items: center; gap: 8px;">
                        <span>{{ item.date }}</span>
                        <a-tag :color="getShiftColor(item.shift)">{{ item.shift }}</a-tag>
                      </div>
                    </template>
                    <template #description>
                      <div style="font-size: 12px; color: #666;">
                        {{ item.time }}
                      </div>
                    </template>
                  </a-list-item-meta>
                </a-list-item>
              </template>
            </a-list>
          </div>
          <div v-else style="text-align: center; padding: 20px; color: #999;">
            <ClockCircleOutlined style="font-size: 24px; margin-bottom: 8px;" />
            <div>No schedule assigned</div>
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

    <!-- Worker: Upload (Second Row) -->
    <a-row v-if="userRole === 'worker'" :gutter="16" style="margin-bottom: 16px;">
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

    <!-- Worker Schedule Modal -->
    <a-modal
      v-model:open="scheduleModalVisible"
      title="My Schedule"
      width="800px"
      :footer="null"
      @cancel="scheduleModalVisible = false"
    >
      <div v-if="userRole === 'worker'">
        <div style="margin-bottom: 16px;">
          <a-date-picker
            v-model:value="selectedScheduleDate"
            @change="onScheduleDateChange"
            style="width: 200px;"
            placeholder="Select date to view schedule"
          />
        </div>
        
        <div v-if="selectedScheduleDate">
          <a-card :title="`Schedule for ${selectedScheduleDate.format('YYYY-MM-DD')}`" size="small">
            <div v-if="getScheduleForDate(selectedScheduleDate).length > 0">
              <a-list :data-source="getScheduleForDate(selectedScheduleDate)" bordered>
                <template #renderItem="{ item }">
                  <a-list-item>
                    <a-list-item-meta>
                      <template #title>
                        <div style="display: flex; align-items: center; gap: 12px;">
                          <a-tag :color="getShiftColor(item.shift)" style="font-size: 14px; padding: 4px 8px;">
                            {{ item.shift }}
                          </a-tag>
                          <span style="font-weight: 500;">{{ item.time }}</span>
                        </div>
                      </template>
                      <template #description>
                        <div style="color: #666;">
                          <div>Date: {{ item.date }}</div>
                          <div>Duration: {{ item.duration }}</div>
                          <div v-if="item.notes">Notes: {{ item.notes }}</div>
                        </div>
                      </template>
                    </a-list-item-meta>
                  </a-list-item>
                </template>
              </a-list>
            </div>
            <div v-else style="text-align: center; padding: 40px; color: #999;">
              <ClockCircleOutlined style="font-size: 48px; margin-bottom: 16px;" />
              <div style="font-size: 16px; margin-bottom: 8px;">No schedule for this date</div>
              <div style="font-size: 12px;">You are not scheduled to work on {{ selectedScheduleDate.format('YYYY-MM-DD') }}</div>
            </div>
          </a-card>
        </div>
        
        <div v-else style="text-align: center; padding: 40px; color: #999;">
          <CalendarOutlined style="font-size: 48px; margin-bottom: 16px;" />
          <div style="font-size: 16px; margin-bottom: 8px;">Select a date to view your schedule</div>
          <div style="font-size: 12px;">Choose a date from the date picker above to see your work schedule</div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, h } from 'vue'
import dayjs from 'dayjs'
import { QuestionCircleOutlined, CheckSquareOutlined, BarChartOutlined, SettingOutlined, AppstoreOutlined, MessageOutlined, HomeOutlined, TeamOutlined, UploadOutlined, ClockCircleOutlined, CalendarOutlined } from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'
import { getBudgetByPatient, getBudgetCalculations } from '@/services/budgetService'
import { getTasksByPatient, getTasksByWorker, getAllTasks } from '@/services/taskService'

const userRole = ref('worker')

// Schedule related data
const scheduleModalVisible = ref(false)
const selectedScheduleDate = ref(null)
const workerSchedule = ref([])
const allWorkerSchedules = ref([])

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
    
    // Load worker schedule data
    if (userRole.value === 'worker') {
      try {
        await loadWorkerSchedule(userInfo.data.id)
      } catch (error) {
        console.error('Failed to load worker schedule:', error)
        workerSchedule.value = []
        allWorkerSchedules.value = []
      }
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

const totalBudget = computed(() => budgetCategories.value.reduce((s, c) => s + (c.categoryBudget || 0), 0))
const totalUsed = computed(() => budgetCategories.value.reduce((s, c) => {
  // Calculate used from subElements if available
  if (c.subElements && c.subElements.length > 0) {
    const categoryUsed = c.subElements.reduce((sum, se) => sum + (se.totalUtilised || 0), 0)
    return s + categoryUsed
  }
  return s + (c.used || 0)
}, 0))
const totalLeft = computed(() => totalBudget.value - totalUsed.value)
const totalUsagePct = computed(() => {
  if (totalBudget.value === 0) {
    return 0
  }
  return Math.round((totalUsed.value / totalBudget.value) * 100)
})
const budgetUsageColor = computed(() => totalUsagePct.value >= 100 ? 'red' : totalUsagePct.value >= 80 ? 'orange' : 'green')

const topCategoryWarnings = computed(() => {
  const arr = budgetCategories.value
    .map(c => {
      // Calculate used from subElements
      const used = c.subElements && c.subElements.length > 0 
        ? c.subElements.reduce((sum, se) => sum + (se.totalUtilised || 0), 0)
        : (c.used || 0)
      const budget = c.categoryBudget || c.budget || 0
      return {
        category: c.name,
        pct: budget > 0 ? Math.round((used / budget) * 100) : 0,
        used,
        budget
      }
    })
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

// --- Worker Schedule Functions ---
const loadWorkerSchedule = async (workerId) => {
  // Mock data for now - in real implementation, this would call an API
  const mockSchedule = [
    {
      id: 1,
      date: '2024-01-15',
      shift: 'Morning Shift',
      time: '06:00 - 14:00',
      duration: '8 hours',
      notes: 'Regular morning care duties'
    },
    {
      id: 2,
      date: '2024-01-16',
      shift: 'Afternoon Shift',
      time: '14:00 - 22:00',
      duration: '8 hours',
      notes: 'Afternoon care and medication assistance'
    },
    {
      id: 3,
      date: '2024-01-17',
      shift: 'Night Shift',
      time: '22:00 - 06:00',
      duration: '8 hours',
      notes: 'Overnight monitoring and care'
    },
    {
      id: 4,
      date: '2024-01-18',
      shift: 'Morning Shift',
      time: '06:00 - 14:00',
      duration: '8 hours',
      notes: 'Morning care and breakfast assistance'
    },
    {
      id: 5,
      date: '2024-01-19',
      shift: 'Afternoon Shift',
      time: '14:00 - 22:00',
      duration: '8 hours',
      notes: 'Afternoon activities and dinner assistance'
    }
  ]
  
  allWorkerSchedules.value = mockSchedule
  // Show next 3 upcoming schedules in preview
  workerSchedule.value = mockSchedule.slice(0, 3)
}

const showScheduleModal = () => {
  scheduleModalVisible.value = true
  selectedScheduleDate.value = dayjs()
}

const onScheduleDateChange = (date) => {
  selectedScheduleDate.value = date
}

const getScheduleForDate = (date) => {
  if (!date || !allWorkerSchedules.value.length) return []
  
  const dateStr = date.format('YYYY-MM-DD')
  return allWorkerSchedules.value.filter(schedule => schedule.date === dateStr)
}

const getShiftColor = (shift) => {
  switch (shift) {
    case 'Morning Shift': return 'green'
    case 'Afternoon Shift': return 'blue'
    case 'Night Shift': return 'purple'
    default: return 'default'
  }
}
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
