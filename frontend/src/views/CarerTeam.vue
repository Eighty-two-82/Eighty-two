<template>
  <div class="carer-team-page">
    <a-card>
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <h2>Carer Team</h2>
            <a-tooltip title="View all care team members and their daily schedules" placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <div style="display: flex; align-items: center; gap: 8px;">
            <a-button type="primary" @click="showGenerateTokenModal">
              <UserAddOutlined />
              Generate Invite Token
            </a-button>
            <a-tooltip title="Generate an invite token to share with organizations for adding new care team members" placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
        </div>
      </template>
      
      <!-- Daily Workers Section -->
      <div style="margin-bottom: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0; color: #1890ff;">
            <UserOutlined style="margin-right: 8px;" />
            Today's Care Team - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
          </h3>
          <a-date-picker
            v-model:value="selectedDate"
            @change="onDateChange"
            style="width: 200px;"
            placeholder="Select date"
          />
        </div>
        <a-row :gutter="[16, 16]">
          <a-col
            v-for="worker in dailyWorkers"
            :key="worker.id"
            :xs="12"
            :sm="8"
            :md="6"
            :lg="4"
            :xl="3"
          >
            <a-card
              hoverable
              size="small"
              style="text-align: center; height: 140px;"
              @click="selectWorker(worker)"
              :class="{ 'selected-worker': selectedWorker?.id === worker.id }"
            >
              <div style="display: flex; flex-direction: column; align-items: center; height: 100%; justify-content: space-between;">
                <!-- Photo -->
                <div style="margin-top: 8px;">
                  <a-avatar
                    :size="50"
                    :src="worker.photo"
                    :alt="worker.name"
                    style="border: 2px solid #f0f0f0;"
                  >
                    {{ worker.name.charAt(0).toUpperCase() }}
                  </a-avatar>
                </div>
                
                <!-- Worker ID -->
                <div style="font-weight: bold; color: #1890ff; font-size: 12px;">
                  {{ worker.workerId }}
                </div>
                
                <!-- Name -->
                <div style="font-size: 11px; color: #666; margin-bottom: 8px; line-height: 1.2;">
                  {{ worker.name }}
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
      </div>

      <!-- All Care Team Members -->
      <div style="margin-bottom: 20px;">
        <h3 style="margin-bottom: 16px; color: #52c41a;">
          <TeamOutlined style="margin-right: 8px;" />
          All Care Team Members
        </h3>
        <a-table
          :columns="carerColumns"
          :data-source="allCarers"
          :pagination="{ pageSize: 10 }"
          row-key="id"
          :loading="loading"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'photo'">
              <a-avatar
                :size="40"
                :src="record.photo"
                :alt="record.name"
              >
                {{ record.name.charAt(0).toUpperCase() }}
              </a-avatar>
            </template>
            
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            
            <template v-if="column.key === 'role'">
              <a-tag :color="getRoleColor(record.role)">
                {{ record.role }}
              </a-tag>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- Generate Invite Token Modal -->
    <a-modal
      v-model:open="generateTokenModalVisible"
      title="Generate Invite Token"
      width="600px"
      @ok="confirmGenerateToken"
      @cancel="generateTokenModalVisible = false"
    >
      <a-form :model="tokenForm" layout="vertical">
        <a-form-item label="Organization Name" required>
          <a-input v-model:value="tokenForm.organizationName" placeholder="Enter organization name" />
        </a-form-item>
        
        <a-form-item label="Token Type" required>
          <a-select v-model:value="tokenForm.tokenType" placeholder="Select token type" style="width: 100%;">
            <a-select-option value="manager">Manager</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="Expiration Days" required>
          <a-input-number 
            v-model:value="tokenForm.expirationDays" 
            :min="1" 
            :max="30" 
            style="width: 100%;"
            placeholder="Enter expiration days (1-30)"
          />
        </a-form-item>
        
        <a-form-item label="Notes">
          <a-textarea 
            v-model:value="tokenForm.notes" 
            placeholder="Enter any additional notes for the organization"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Generated Token Display Modal -->
    <a-modal
      v-model:open="tokenDisplayModalVisible"
      title="Invite Token Generated"
      width="500px"
      :footer="null"
    >
      <div style="text-align: center; padding: 20px;">
        <div style="margin-bottom: 20px;">
          <a-typography-title :level="4" style="color: #52c41a;">
            Token Generated Successfully!
          </a-typography-title>
        </div>
        
        <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px;">Invite Token:</div>
          <div style="font-family: monospace; font-size: 16px; color: #1890ff; word-break: break-all;">
            {{ generatedToken }}
          </div>
        </div>
        
        <div style="margin-bottom: 20px;">
          <a-button type="primary" @click="copyToken" style="margin-right: 8px;">
            Copy Token
          </a-button>
          <a-button @click="downloadToken">
            Download as Text
          </a-button>
        </div>
        
        <div style="text-align: left; background: #fff7e6; border: 1px solid #ffd591; border-radius: 6px; padding: 12px;">
          <div style="font-weight: bold; margin-bottom: 8px;">Instructions for Organization:</div>
          <div style="font-size: 12px; line-height: 1.5;">
            1. Share this token with {{ tokenForm.organizationName }}<br/>
            2. The organization can use this token to register new {{ tokenForm.tokenType }} accounts<br/>
            3. Token expires in {{ tokenForm.expirationDays }} days<br/>
            4. Keep this token secure and do not share publicly
          </div>
        </div>
        
        <div style="margin-top: 20px;">
          <a-button type="primary" @click="tokenDisplayModalVisible = false">
            Close
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { 
  QuestionCircleOutlined, 
  UserOutlined,
  TeamOutlined,
  UserAddOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

// Reactive data
const loading = ref(false)
const selectedWorker = ref(null)
const selectedDate = ref(null)
const generateTokenModalVisible = ref(false)
const tokenDisplayModalVisible = ref(false)
const generatedToken = ref('')

// Token form data
const tokenForm = ref({
  organizationName: '',
  tokenType: 'manager',
  expirationDays: 7,
  notes: ''
})

// Mock worker data (same as WorkerManagement)
const workers = ref([
  {
    id: 1,
    workerId: 'W001',
    name: 'A',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-01-15',
    email: 'a@careapp.com',
    role: 'Worker'
  },
  {
    id: 2,
    workerId: 'W002',
    name: 'B',
    photo: null,
    position: 'Senior Care Assistant',
    status: 'Active',
    joinDate: '2022-11-20',
    email: 'b@careapp.com',
    role: 'Worker'
  },
  {
    id: 3,
    workerId: 'W003',
    name: 'C',
    photo: null,
    position: 'Care Assistant',
    status: 'On Leave',
    joinDate: '2023-03-10',
    email: 'c@careapp.com',
    role: 'Worker'
  },
  {
    id: 4,
    workerId: 'W004',
    name: 'D',
    photo: null,
    position: 'Care Coordinator',
    status: 'Active',
    joinDate: '2022-08-05',
    email: 'd@careapp.com',
    role: 'Worker'
  },
  {
    id: 5,
    workerId: 'W005',
    name: 'E',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-02-14',
    email: 'e@careapp.com',
    role: 'Worker'
  },
  {
    id: 6,
    workerId: 'W006',
    name: 'F',
    photo: null,
    position: 'Senior Care Assistant',
    status: 'Active',
    joinDate: '2022-09-30',
    email: 'f@careapp.com',
    role: 'Worker'
  },
  {
    id: 7,
    workerId: 'W007',
    name: 'G',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-04-22',
    email: 'g@careapp.com',
    role: 'Worker'
  },
  {
    id: 8,
    workerId: 'W008',
    name: 'H',
    photo: null,
    position: 'Care Coordinator',
    status: 'Active',
    joinDate: '2022-12-08',
    email: 'h@careapp.com',
    role: 'Worker'
  }
])

// Add Manager and POA to the team
const allCarers = ref([])

// Daily workers (subset of active workers)
const dailyWorkers = ref([])

// Daily schedules data (same as WorkerManagement)
const dailySchedules = ref({
  // Mock data for different dates
  '2024-01-15': [1, 2, 4, 5, 6], // John, Sarah, Emily, David, Lisa
  '2024-01-16': [1, 3, 4, 6, 7], // John, Michael, Emily, Robert, Jennifer
  '2024-01-17': [2, 4, 5, 7, 8], // Sarah, Emily, David, Jennifer
  '2024-01-18': [1, 2, 5, 6, 7], // John, Sarah, David, Lisa, Robert
  '2024-01-19': [3, 4, 6, 7, 8]  // Michael, Emily, Lisa, Robert, Jennifer
})

// Table columns for all carers
const carerColumns = [
  {
    title: 'Photo',
    key: 'photo',
    width: 80,
    align: 'center'
  },
  {
    title: 'ID',
    dataIndex: 'workerId',
    key: 'workerId',
    width: 100
  },
  {
    title: 'Name',
    dataIndex: 'name',
    key: 'name',
    width: 150
  },
  {
    title: 'Position',
    dataIndex: 'position',
    key: 'position',
    width: 150
  },
  {
    title: 'Role',
    key: 'role',
    width: 100,
    align: 'center'
  },
  {
    title: 'Status',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: 'Join Date',
    dataIndex: 'joinDate',
    key: 'joinDate',
    width: 120
  },
  {
    title: 'Email',
    dataIndex: 'email',
    key: 'email',
    width: 200
  }
]

// Methods
const selectWorker = (worker) => {
  selectedWorker.value = worker
  message.info(`Selected care team member: ${worker.name} (${worker.workerId})`)
}

const onDateChange = (date) => {
  if (date) {
    const dateStr = date.format('YYYY-MM-DD')
    updateDailyWorkers(dateStr)
  }
}

const updateDailyWorkers = (dateStr) => {
  const workerIds = dailySchedules.value[dateStr] || []
  dailyWorkers.value = workers.value.filter(worker => workerIds.includes(worker.id))
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Active': return 'green'
    case 'On Leave': return 'orange'
    case 'Inactive': return 'red'
    default: return 'default'
  }
}

const getRoleColor = (role) => {
  switch (role) {
    case 'Manager': return 'blue'
    case 'POA': return 'purple'
    case 'Worker': return 'green'
    default: return 'default'
  }
}

// Token generation methods
const showGenerateTokenModal = () => {
  tokenForm.value = {
    organizationName: '',
    tokenType: 'manager',
    expirationDays: 7,
    notes: ''
  }
  generateTokenModalVisible.value = true
}

const confirmGenerateToken = async () => {
  if (!tokenForm.value.organizationName) {
    message.error('Please fill in organization name')
    return
  }

  try {
    message.loading('Generating invite token...', 0)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    // Generate a mock token (in real app, this would come from backend)
    const tokenId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
    const timestamp = Date.now()
    generatedToken.value = `CARE_INVITE_${tokenForm.value.tokenType.toUpperCase()}_${tokenId}_${timestamp}`
    
    message.destroy()
    message.success('Invite token generated successfully!')
    
    generateTokenModalVisible.value = false
    tokenDisplayModalVisible.value = true
    
  } catch (error) {
    message.destroy()
    message.error('Failed to generate invite token')
  }
}

const copyToken = async () => {
  try {
    await navigator.clipboard.writeText(generatedToken.value)
    message.success('Token copied to clipboard!')
  } catch (error) {
    message.error('Failed to copy token')
  }
}

const downloadToken = () => {
  const tokenData = {
    token: generatedToken.value,
    organization: tokenForm.value.organizationName,
    tokenType: tokenForm.value.tokenType,
    expirationDays: tokenForm.value.expirationDays,
    generatedDate: new Date().toISOString(),
    notes: tokenForm.value.notes
  }
  
  const blob = new Blob([JSON.stringify(tokenData, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = `care-invite-token-${tokenForm.value.organizationName.replace(/\s+/g, '-')}.json`
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
  URL.revokeObjectURL(url)
  
  message.success('Token data downloaded successfully!')
}

onMounted(() => {
  // Load carer data
  loading.value = true
  
  // Initialize all carers (workers + manager + POA)
  allCarers.value = [
    ...workers.value,
    {
      id: 99,
      workerId: 'M001',
      name: 'Manager Smith',
      photo: null,
      position: 'Care Manager',
      status: 'Active',
      joinDate: '2022-01-01',
      email: 'manager@careapp.com',
      role: 'Manager'
    },
    {
      id: 100,
      workerId: 'P001',
      name: 'Family Member',
      photo: null,
      position: 'Power of Attorney',
      status: 'Active',
      joinDate: '2022-01-01',
      email: 'family@careapp.com',
      role: 'POA'
    }
  ]
  
  // Set today as default date
  selectedDate.value = dayjs()
  const todayStr = selectedDate.value.format('YYYY-MM-DD')
  
  // Initialize daily workers for today
  updateDailyWorkers(todayStr)
  
  setTimeout(() => {
    loading.value = false
  }, 500)
})
</script>

<style scoped>
.carer-team-page {
  max-width: 1200px;
  margin: 0 auto;
}

.selected-worker {
  border: 2px solid #1890ff !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3) !important;
}

.selected-worker .ant-card-body {
  background-color: #f6ffed;
}
</style>
