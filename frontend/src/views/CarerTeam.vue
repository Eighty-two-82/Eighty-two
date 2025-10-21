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
      
      <!-- Organization Management Section -->
      <div style="margin-bottom: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0; color: #1890ff;">
            <TeamOutlined style="margin-right: 8px;" />
            Organization Management
          </h3>
        </div>
        
        <!-- Current Organization Info -->
        <a-card v-if="currentOrganization" style="margin-bottom: 16px;">
          <template #title>
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <div style="display: flex; align-items: center; gap: 8px;">
                <span>Current Organization</span>
                <a-tag color="blue">{{ currentOrganization.name }}</a-tag>
              </div>
              <a-button 
                type="primary" 
                danger 
                @click="showRemoveOrgModal"
                :loading="removingOrg"
              >
                <DeleteOutlined />
                Remove Organization
              </a-button>
            </div>
          </template>
          
          <a-descriptions :column="2" bordered size="small">
            <a-descriptions-item label="Organization ID">
              {{ currentOrganization.id }}
            </a-descriptions-item>
            <a-descriptions-item label="Organization Name">
              {{ currentOrganization.name }}
            </a-descriptions-item>
            <a-descriptions-item label="Joined Date">
              {{ formatDate(currentOrganization.joinedDate) }}
            </a-descriptions-item>
            <a-descriptions-item label="Status">
              <a-tag :color="currentOrganization.status === 'Active' ? 'green' : 'red'">
                {{ currentOrganization.status }}
              </a-tag>
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
        
        <!-- No Organization Message -->
        <a-card v-else style="margin-bottom: 16px;">
          <a-empty description="No organization associated">
            <template #image>
              <TeamOutlined style="font-size: 48px; color: #d9d9d9;" />
            </template>
            <a-button type="primary" @click="showGenerateTokenModal">
              <UserAddOutlined />
              Invite Organization
            </a-button>
          </a-empty>
        </a-card>
      </div>

      <!-- Daily Workers Section -->
      <div style="margin-bottom: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0; color: #1890ff;">
            <UserOutlined style="margin-right: 8px;" />
            Today's Care Team - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
          </h3>
          <div style="display: flex; align-items: center; gap: 12px;">
            <a-date-picker
              v-model:value="selectedDate"
              @change="onDateChange"
              style="width: 200px;"
              placeholder="Select date"
            />
          </div>
        </div>
        
        <!-- Shift Selection -->
        <div style="margin-bottom: 20px;">
          <div style="display: flex; align-items: center; gap: 12px; margin-bottom: 12px;">
            <span style="font-weight: 500; color: #666;">Select Shift:</span>
            <a-radio-group v-model:value="selectedShift" @change="onShiftChange" button-style="solid">
              <a-radio-button value="morning">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Morning Shift
              </a-radio-button>
              <a-radio-button value="afternoon">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Afternoon Shift
              </a-radio-button>
              <a-radio-button value="night">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Night Shift
              </a-radio-button>
            </a-radio-group>
          </div>
          
          <!-- Shift Info -->
          <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 12px; margin-bottom: 16px;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <InfoCircleOutlined style="color: #52c41a;" />
              <span style="font-weight: 500; color: #52c41a;">{{ getShiftInfo().title }}</span>
            </div>
            <div style="margin-top: 4px; font-size: 12px; color: #666;">
              {{ getShiftInfo().description }}
            </div>
          </div>
        </div>
        
        <!-- Workers Grid -->
        <div v-if="filteredWorkers.length > 0">
          <div style="margin-bottom: 12px; color: #666; font-size: 14px;">
            Showing {{ filteredWorkers.length }} worker(s) for {{ getShiftInfo().title }}
          </div>
          <a-row :gutter="[16, 16]">
            <a-col
              v-for="worker in filteredWorkers"
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
                style="text-align: center; height: 200px;"
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
                  
                  
                  <!-- Name -->
                  <div style="font-size: 11px; color: #666; margin-bottom: 4px; line-height: 1.2;">
                    {{ worker.name }}
                  </div>
                  
                  <!-- Position -->
                  <div style="font-size: 10px; color: #999; margin-bottom: 4px; line-height: 1.2;">
                    {{ worker.position }}
                  </div>
                  
                  <!-- Role Badge -->
                  <div style="margin-bottom: 4px;">
                    <a-tag :color="getRoleColor(worker.role)" size="small">
                      {{ worker.role }}
                    </a-tag>
                  </div>
                  
                  <!-- Shift Badge -->
                  <div style="margin-bottom: 4px;">
                    <a-tag :color="getShiftColor(worker.shift)" size="small">
                      {{ getShiftDisplayName(worker.shift) }}
                    </a-tag>
                  </div>
                </div>
              </a-card>
            </a-col>
          </a-row>
        </div>
        
        <!-- No Workers Message -->
        <div v-else style="text-align: center; padding: 40px; color: #999;">
          <UserOutlined style="font-size: 48px; margin-bottom: 16px;" />
          <div style="font-size: 16px; margin-bottom: 8px;">No workers assigned</div>
          <div style="font-size: 12px;">No workers are scheduled for {{ getShiftInfo().title }} on {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}</div>
        </div>
      </div>

    </a-card>

    <!-- Generate Invite Token Modal -->
    <a-modal
      v-model:open="generateTokenModalVisible"
      title="Generate Invite Token"
      width="700px"
      @ok="confirmGenerateToken"
      @cancel="generateTokenModalVisible = false"
    >
      <!-- Usage Instructions -->
      <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
          <InfoCircleOutlined style="color: #52c41a; font-size: 16px;" />
          <span style="font-weight: 600; color: #52c41a; font-size: 14px;">How to Use Invite Tokens</span>
        </div>
        <div style="font-size: 13px; line-height: 1.6; color: #666;">
          <div style="margin-bottom: 8px;">
            <strong>Step 1:</strong> Fill out the form below to generate a unique invite token
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 2:</strong> Share the generated token with the organization or individual
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 3:</strong> They can use this token during registration to join your care team
          </div>
          <div style="margin-bottom: 8px;">
            <strong>Step 4:</strong> The token will automatically expire after the specified number of days
          </div>
          <div style="color: #ff4d4f; font-weight: 500;">
            <strong>Important:</strong> 
            <span v-if="tokenForm.tokenType === 'manager'">Manager tokens allow organizations to register new manager accounts</span>
            <span v-else-if="tokenForm.tokenType === 'worker'">Worker tokens allow organizations to register new worker accounts</span>
            <span v-else>Keep tokens secure and only share with trusted parties</span>
          </div>
        </div>
      </div>

      <a-form :model="tokenForm" layout="vertical">
        <a-form-item label="Organization Name" required>
          <a-input v-model:value="tokenForm.organizationName" placeholder="Enter organization name" />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            The name of the organization or individual who will receive this token
          </div>
        </a-form-item>
        
        <a-form-item label="Token Type" required>
          <a-select v-model:value="tokenForm.tokenType" placeholder="Select token type" style="width: 100%;">
            <a-select-option value="manager">Manager</a-select-option>
            <a-select-option value="worker">Worker</a-select-option>
          </a-select>
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            <span v-if="tokenForm.tokenType === 'manager'">Manager tokens allow organizations to register new manager accounts</span>
            <span v-else-if="tokenForm.tokenType === 'worker'">Worker tokens allow organizations to register new worker accounts</span>
            <span v-else>Select the type of account this token will create</span>
          </div>
        </a-form-item>
        
        <a-form-item label="Expiration Days" required>
          <a-input-number 
            v-model:value="tokenForm.expirationDays" 
            :min="1" 
            :max="30" 
            style="width: 100%;"
            placeholder="Enter expiration days (1-30)"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            Token will be valid for this many days after generation
          </div>
        </a-form-item>
        
        <a-form-item label="Notes">
          <a-textarea 
            v-model:value="tokenForm.notes" 
            placeholder="Enter any additional notes for the organization"
            :rows="3"
          />
          <div style="font-size: 12px; color: #999; margin-top: 4px;">
            Optional notes that will be included with the token (e.g., contact information, special instructions)
          </div>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Generated Token Display Modal -->
    <a-modal
      v-model:open="tokenDisplayModalVisible"
      :title="isShowingExistingToken ? 'Existing Token' : 'Invite Token Generated'"
      width="600px"
      :footer="null"
    >
      <div style="text-align: center; padding: 20px;">
        <div style="margin-bottom: 20px;">
          <a-typography-title :level="4" :style="{ color: isShowingExistingToken ? '#1890ff' : '#52c41a' }">
            {{ isShowingExistingToken ? 'Existing Valid Token' : 'Token Generated Successfully!' }}
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
        
        <!-- Detailed Usage Instructions -->
        <div style="text-align: left; background: #fff7e6; border: 1px solid #ffd591; border-radius: 6px; padding: 16px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 12px; color: #d46b08;">
            <InfoCircleOutlined style="margin-right: 6px;" />
            Instructions for {{ tokenForm.organizationName || 'the Organization' }}:
          </div>
          <div style="font-size: 13px; line-height: 1.6; color: #666;">
            <div style="margin-bottom: 8px;">
              <strong>1. Registration Process:</strong><br/>
              • Go to the registration page<br/>
              • Enter the invite token when prompted<br/>
              • Complete the registration form with your details
            </div>
            <div style="margin-bottom: 8px;">
              <strong>2. Token Details:</strong><br/>
              • Token Type: {{ tokenForm.tokenType === 'manager' ? 'Manager' : 'Worker' }}<br/>
              • Expires in: {{ tokenForm.expirationDays }} days<br/>
              • Generated: {{ new Date().toLocaleDateString() }}
            </div>
            <div style="margin-bottom: 8px;">
              <strong>3. After Registration:</strong><br/>
              <span v-if="tokenForm.tokenType === 'manager'">
                • You'll be automatically added to the care team as a Manager<br/>
                • You can start managing schedules and tasks<br/>
                • You can generate invite tokens for Workers<br/>
                • Contact the POA if you have any questions
              </span>
              <span v-else-if="tokenForm.tokenType === 'worker'">
                • You'll be automatically added to the care team as a Worker<br/>
                • You can view your assigned schedules and tasks<br/>
                • You can update task status and add notes<br/>
                • Contact your Manager if you have any questions
              </span>
              <span v-else>
                • You'll be automatically added to the care team<br/>
                • You can start managing schedules and tasks<br/>
                • Contact the team lead if you have any questions
              </span>
            </div>
            <div style="color: #ff4d4f; font-weight: 500;">
              <strong>⚠️ Important:</strong> 
              <span v-if="tokenForm.tokenType === 'manager'">This Manager token can be used multiple times within the expiration period and will expire automatically</span>
              <span v-else-if="tokenForm.tokenType === 'worker'">This Worker token can be used multiple times within the expiration period and will expire automatically</span>
              <span v-else>This token can only be used once and will expire automatically</span>
            </div>
          </div>
        </div>
        
        <!-- Contact Information -->
        <div v-if="tokenForm.notes" style="text-align: left; background: #f0f9ff; border: 1px solid #91d5ff; border-radius: 6px; padding: 12px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 8px; color: #1890ff;">Additional Notes:</div>
          <div style="font-size: 12px; line-height: 1.5; color: #666;">
            {{ tokenForm.notes }}
          </div>
        </div>
        
        <div style="margin-top: 20px;">
          <a-button type="primary" @click="tokenDisplayModalVisible = false">
            Close
          </a-button>
        </div>
      </div>
    </a-modal>

    <!-- Remove Organization Confirmation Modal -->
    <a-modal
      v-model:open="removeOrgModalVisible"
      title="Remove Organization"
      width="500px"
      @ok="confirmRemoveOrganization"
      @cancel="removeOrgModalVisible = false"
      :confirmLoading="removingOrg"
    >
      <div style="text-align: center; padding: 20px 0;">
        <div style="font-size: 48px; color: #ff4d4f; margin-bottom: 16px;">⚠️</div>
        <h3 style="color: #ff4d4f; margin-bottom: 16px;">Confirm Organization Removal</h3>
        <p style="margin-bottom: 16px; color: #666;">
          Are you sure you want to remove <strong>{{ currentOrganization?.name }}</strong> from your care team?
        </p>
        
        <div style="background: #fff2f0; border: 1px solid #ffccc7; border-radius: 6px; padding: 16px; margin-bottom: 20px; text-align: left;">
          <div style="font-weight: bold; margin-bottom: 8px; color: #ff4d4f;">
            <ExclamationCircleOutlined style="margin-right: 6px;" />
            This action will:
          </div>
          <ul style="margin: 0; padding-left: 20px; color: #666; font-size: 14px;">
            <li>Remove all workers from this organization</li>
            <li>Cancel all pending tasks assigned to these workers</li>
            <li>Clear all schedules for this organization</li>
            <li>Remove access to patient information</li>
            <li>This action cannot be undone</li>
          </ul>
        </div>
        
        <div style="background: #f6ffed; border: 1px solid #b7eb8f; border-radius: 6px; padding: 12px; margin-bottom: 20px;">
          <div style="font-weight: bold; margin-bottom: 4px; color: #52c41a;">
            <InfoCircleOutlined style="margin-right: 6px;" />
            Alternative Options:
          </div>
          <div style="font-size: 13px; color: #666;">
            • You can deactivate individual workers instead of removing the entire organization<br/>
            • You can temporarily suspend the organization's access<br/>
            • Contact the organization directly to resolve any issues
          </div>
        </div>
        
        <p style="color: #ff4d4f; font-weight: bold;">
          Type <strong>"REMOVE"</strong> to confirm this action:
        </p>
        <a-input 
          v-model:value="removeConfirmationText" 
          placeholder="Type REMOVE to confirm"
          style="margin-bottom: 16px;"
        />
      </div>
    </a-modal>

    <!-- Worker Detail Modal -->
    <a-modal
      v-model:open="workerDetailModalVisible"
      :title="`Care Team Member Details`"
      width="600px"
      :footer="null"
      @cancel="workerDetailModalVisible = false"
    >
      <div v-if="selectedWorker" style="padding: 20px;">
        <!-- Worker Photo and Basic Info -->
        <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 24px; padding: 16px; background: #f6ffed; border-radius: 8px;">
          <a-avatar
            :size="80"
            :src="selectedWorker.photo || undefined"
            :alt="selectedWorker.name || 'Worker'"
            style="border: 3px solid #52c41a;"
          >
            {{ (selectedWorker.name || 'W').charAt(0).toUpperCase() }}
          </a-avatar>
          <div style="flex: 1;">
            <h2 style="margin: 0 0 8px 0; color: #1890ff;">{{ selectedWorker.name }}</h2>
            <div style="font-size: 16px; color: #666; margin-bottom: 4px;">
              <strong>ID:</strong> {{ selectedWorker.workerId }}
            </div>
            <div style="font-size: 16px; color: #666;">
              <strong>Status:</strong> 
              <a-tag :color="getStatusColor(selectedWorker.status)">
                {{ selectedWorker.status }}
              </a-tag>
            </div>
          </div>
        </div>

        <!-- Detailed Information -->
        <a-row :gutter="[16, 16]">
          <a-col :span="12">
            <a-card size="small" title="Contact Information">
              <div>
                <strong>Email:</strong> {{ selectedWorker.email || 'N/A' }}
              </div>
            </a-card>
          </a-col>
          
          <a-col :span="12">
            <a-card size="small" title="Schedule Information">
              <div>
                <strong>Shift:</strong> 
                <a-tag :color="getShiftColor(selectedWorker.shift)">
                  {{ getShiftDisplayName(selectedWorker.shift) }}
                </a-tag>
              </div>
              <div v-if="selectedWorker.shiftStartTime && selectedWorker.shiftEndTime" style="margin-top: 8px;">
                <strong>Time:</strong> {{ selectedWorker.shiftStartTime }} - {{ selectedWorker.shiftEndTime }}
              </div>
            </a-card>
          </a-col>
        </a-row>

        <!-- Notes -->
        <div v-if="selectedWorker.notes" style="margin-top: 16px;">
          <a-card size="small" title="Notes">
            <div style="color: #666;">
              {{ selectedWorker.notes }}
            </div>
          </a-card>
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
  UserAddOutlined,
  ClockCircleOutlined,
  InfoCircleOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getMe, getUsersByRole, getShiftTimeSettings } from '@/services/userService'
import { getAllWorkers, getWorkersByOrganization, getDailySchedule } from '@/services/workerService'
import { generateInviteCode } from '@/services/inviteCodeService'

// Reactive data
const loading = ref(false)
const selectedWorker = ref(null)
const selectedDate = ref(null)
const selectedShift = ref('morning')
const generateTokenModalVisible = ref(false)
const tokenDisplayModalVisible = ref(false)
const workerDetailModalVisible = ref(false)
const generatedToken = ref('')
const isShowingExistingToken = ref(false)
const tokenExists = ref(false) // 标记Token是否存在
const tokenExpirationDate = ref(null) // 保存Token过期时间

// Organization management
const currentOrganization = ref(null)
const removeOrgModalVisible = ref(false)
const removingOrg = ref(false)
const removeConfirmationText = ref('')

// Token form data
const tokenForm = ref({
  organizationName: '',
  tokenType: 'manager',
  expirationDays: 7,
  notes: ''
})

// Workers data - loaded from API
const workers = ref([])


// Daily workers (subset of active workers)
const dailyWorkers = ref([])

// Daily schedules data - loaded from API
const dailySchedules = ref({})

// Computed property for filtered workers based on selected shift
const filteredWorkers = ref([])
const shiftTimeSettings = ref({})


// Methods
const selectWorker = (worker) => {
  selectedWorker.value = worker
  workerDetailModalVisible.value = true
}

const onDateChange = async (date) => {
  if (date) {
    const dateStr = date.format('YYYY-MM-DD')
    console.log('📅 CarerTeam - Date changed to:', dateStr)
    
    // Get current user info to get organizationId
    try {
      const userInfo = await getMe()
      const organizationId = userInfo?.data?.organizationId || 'default-org'
      
      // Load actual schedule data from API instead of using mock data
      await loadDailySchedule(organizationId, dateStr)
      updateFilteredWorkers()
    } catch (error) {
      console.error('❌ CarerTeam - Failed to load schedule for new date:', error)
      dailyWorkers.value = []
      updateFilteredWorkers()
    }
  }
}

const onShiftChange = () => {
  updateFilteredWorkers()
}

const updateDailyWorkers = (dateStr) => {
  // This function is no longer needed since we load actual schedule data from API
  // The dailyWorkers are now populated by loadDailySchedule function
  console.log('🔄 CarerTeam - updateDailyWorkers called for:', dateStr)
  console.log('📊 CarerTeam - Current dailyWorkers count:', dailyWorkers.value.length)
}

const updateFilteredWorkers = () => {
  // Filter workers based on selected shift
  filteredWorkers.value = dailyWorkers.value.filter(worker => {
    // Map frontend shift names to backend shift types
    const shiftTypeMap = {
      'morning': 'morning',
      'afternoon': 'afternoon', 
      'evening': 'evening'
    }
    
    const expectedShiftType = shiftTypeMap[selectedShift.value]
    return worker.shift === expectedShiftType
  })
}

// Shift-related methods
const getShiftInfo = () => {
  const settings = shiftTimeSettings.value
  const shiftInfo = {
    morning: {
      title: 'Morning Shift',
      description: `Covers morning care activities (${settings.morningShiftStart || '09:00'} - ${settings.morningShiftEnd || '14:00'}), breakfast assistance, and morning medications`
    },
    afternoon: {
      title: 'Afternoon Shift', 
      description: `Handles afternoon activities (${settings.afternoonShiftStart || '16:00'} - ${settings.afternoonShiftEnd || '22:00'}), lunch assistance, and afternoon care`
    },
    evening: {
      title: 'Evening Shift',
      description: `Provides evening care (${settings.eveningShiftStart || '22:00'} - ${settings.eveningShiftEnd || '06:00'}), evening medications, and night monitoring`
    }
  }
  return shiftInfo[selectedShift.value] || shiftInfo.morning
}

const getShiftColor = (shift) => {
  const colors = {
    morning: 'green',
    afternoon: 'blue', 
    evening: 'purple'
  }
  return colors[shift] || 'default'
}

const getShiftDisplayName = (shift) => {
  const names = {
    morning: 'Morning',
    afternoon: 'Afternoon',
    evening: 'Evening'
  }
  return names[shift] || 'Unknown'
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
const loadTokenFromBackend = async () => {
  try {
    console.log('🔍 Loading token from backend...')
    const userInfo = await getMe()
    console.log('🔍 CarerTeam.vue - User info received:', userInfo)
    console.log('🔍 CarerTeam.vue - PatientId:', userInfo?.data?.patientId)
    
    if (userInfo?.data?.patientId) {
      console.log('✅ CarerTeam.vue - Found patientId, loading patient data')
      const { getPatientById } = await import('@/services/patientService')
      const response = await getPatientById(userInfo.data.patientId)
      if (response?.data) {
        // 检查是否有Token信息
        if (response.data.inviteToken && response.data.tokenExpiration) {
          const expirationDate = new Date(response.data.tokenExpiration)
          const now = new Date()
          
          console.log('🔍 Token info from backend:', {
            token: response.data.inviteToken,
            expiration: response.data.tokenExpiration,
            isExpired: now >= expirationDate
          })
          
          // 如果Token未过期，设置本地状态
          if (now < expirationDate) {
            generatedToken.value = response.data.inviteToken
            tokenExists.value = true
            tokenExpirationDate.value = response.data.tokenExpiration
            console.log('✅ Valid token loaded from backend')
            return true
          } else {
            console.log('❌ Token expired, resetting state')
            tokenExists.value = false
            tokenExpirationDate.value = null
            generatedToken.value = ''
            return false
          }
        }
      }
    }
    
    console.log('❌ No valid token found in backend')
    tokenExists.value = false
    tokenExpirationDate.value = null
    generatedToken.value = ''
    return false
  } catch (error) {
    console.error('Failed to load token from backend:', error)
    tokenExists.value = false
    tokenExpirationDate.value = null
    generatedToken.value = ''
    return false
  }
}

const showGenerateTokenModal = async () => {
  try {
    console.log('🔍 Checking for existing token...')
    
    // 首先从后端加载Token状态
    const hasValidToken = await loadTokenFromBackend()
    
    if (hasValidToken) {
      console.log('✅ Token is valid, showing existing token')
      // Token仍然有效，直接显示现有Token，不显示表单
      isShowingExistingToken.value = true
      message.info('Showing existing valid token')
      generateTokenModalVisible.value = false
      tokenDisplayModalVisible.value = true
      return
    }
    
    console.log('📝 No valid token found, showing form to generate new token')
    // 没有有效Token，显示表单生成新Token
    const userInfo = await getMe()
    const defaultTokenType = userInfo?.data?.role === 'poa' ? 'manager' : 'worker'
    
    tokenForm.value = {
      organizationName: '',
      tokenType: defaultTokenType,
      expirationDays: 7,
      notes: ''
    }
    generateTokenModalVisible.value = true
  } catch (error) {
    console.error('❌ Failed to check existing token:', error)
    // 出错时显示表单
    const userInfo = await getMe()
    const defaultTokenType = userInfo?.data?.role === 'poa' ? 'manager' : 'worker'
    
    tokenForm.value = {
      organizationName: '',
      tokenType: defaultTokenType,
      expirationDays: 7,
      notes: ''
    }
    generateTokenModalVisible.value = true
  }
}

const confirmGenerateToken = async () => {
  if (!tokenForm.value.organizationName) {
    message.error('Please fill in organization name')
    return
  }

  try {
    message.loading('Generating invite token...', 0)
    
    // Get current user info
    const userInfo = await getMe()
    if (!userInfo?.data) {
      throw new Error('User not authenticated')
    }
    
    // Prepare invite data
    const inviteData = {
      createdBy: userInfo.data.id,
      createdByType: userInfo.data.role === 'poa' ? 'POA' : 'MANAGER',
      targetType: tokenForm.value.tokenType.toUpperCase(), // Convert 'manager' to 'MANAGER', 'worker' to 'WORKER'
      patientId: userInfo.data.patientId || 'default-patient',
      organizationId: userInfo.data.organizationId || 'default-org'
    }
    
    // Generate invite code via API
    const response = await generateInviteCode(inviteData)
    
    if (response.data) {
      generatedToken.value = response.data
      
      // Calculate token expiration (7 days from now)
      const expirationDate = new Date()
      expirationDate.setDate(expirationDate.getDate() + tokenForm.value.expirationDays)
      
      // Update patient record with token information
      try {
        const { getPatientById, updatePatient, createPatient } = await import('@/services/patientService')
        const { updateUserProfile } = await import('@/services/userService')
        
        // Prepare patient data with token information
        const patientData = {
          inviteToken: response.data,
          organizationName: tokenForm.value.organizationName,
          organizationId: userInfo.data.organizationId || 'default-org',
          tokenExpiration: expirationDate.toISOString(),
          createdBy: userInfo.data.id || 'poa',
          createdAt: new Date().toISOString(),
          poaId: userInfo.data.id || 'poa'
        }
        
        console.log('📤 Sending patient data to update:', patientData)
        
        if (userInfo?.data?.patientId) {
          // Update existing patient
          const updateResult = await updatePatient(userInfo.data.patientId, patientData)
          console.log('📥 Update result:', updateResult)
          console.log('📥 Update result data:', updateResult?.data)
          console.log('📥 Update result inviteToken:', updateResult?.data?.inviteToken)
        } else {
          // Create new patient with basic info
          const basicPatientData = {
            firstName: 'Client',
            lastName: 'Name',
            age: 0,
            medicalConditions: '',
            specialRequirements: '',
            ...patientData
          }
          await createPatient(basicPatientData)
        }
        
        // Update user record with organization name
        try {
          const userUpdateData = {
            organizationName: tokenForm.value.organizationName,
            organizationId: userInfo.data.organizationId || 'default-org'
          }
          
          console.log('📤 Updating user with organization info:', userUpdateData)
          await updateUserProfile(userInfo.data.id, userUpdateData)
          console.log('✅ User record updated with organization information')
        } catch (userError) {
          console.error('Failed to update user record:', userError)
          // Don't fail the whole operation if user update fails
        }
        
        console.log('✅ Patient record updated with token information')
      } catch (patientError) {
        console.error('Failed to update patient record:', patientError)
        // Don't fail the whole operation if patient update fails
      }
      
      message.destroy()
      message.success('Invite token generated successfully!')
      
      // 立即更新本地organization状态，不等待后端更新
      currentOrganization.value = {
        id: userInfo.data.organizationId || 'default-org',
        name: tokenForm.value.organizationName,
        joinedDate: new Date().toISOString().split('T')[0],
        status: 'Active'
      }
      console.log('✅ Organization updated locally:', currentOrganization.value)
      
      // 重新加载organization信息以显示新填写的organization name
      await loadCurrentOrganization()
      
      // 设置Token状态
      tokenExists.value = true
      tokenExpirationDate.value = expirationDate.toISOString()
      
      isShowingExistingToken.value = false
      generateTokenModalVisible.value = false
      tokenDisplayModalVisible.value = true
      
      // Reset form
      const defaultTokenType = userInfo.data.role === 'poa' ? 'manager' : 'worker'
      tokenForm.value = {
        organizationName: '',
        tokenType: defaultTokenType,
        expirationDays: 7,
        notes: ''
      }
    }
    
  } catch (error) {
    message.destroy()
    console.error('Failed to generate invite token:', error)
    message.error(error.message || 'Failed to generate invite token')
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

// Organization management methods
const loadCurrentOrganization = async () => {
  try {
    const userInfo = await getMe()
    console.log('🔍 Loading organization info for user:', userInfo?.data)
    
    // First try to get organization info from user record
    if (userInfo?.data?.organizationId || userInfo?.data?.organizationName) {
      currentOrganization.value = {
        id: userInfo.data.organizationId || 'default-org',
        name: userInfo.data.organizationName || 'Default Organization',
        joinedDate: '2024-01-01',
        status: 'Active'
      }
      console.log('✅ Organization loaded from user record:', currentOrganization.value)
    } else if (userInfo?.data?.patientId) {
      // If no organization info in user record, try to get it from patient record
      try {
        const { getPatientById } = await import('@/services/patientService')
        const patientResponse = await getPatientById(userInfo.data.patientId)
        
        if (patientResponse?.data?.organizationName) {
          currentOrganization.value = {
            id: patientResponse.data.organizationId || 'default-org',
            name: patientResponse.data.organizationName,
            joinedDate: '2024-01-01',
            status: 'Active'
          }
          console.log('✅ Organization loaded from patient record:', currentOrganization.value)
        } else {
          currentOrganization.value = null
          console.log('❌ No organization info found in patient record')
        }
      } catch (patientError) {
        console.error('Failed to load patient record:', patientError)
        currentOrganization.value = null
      }
    } else {
      currentOrganization.value = null
      console.log('❌ No organization info found')
    }
  } catch (error) {
    console.error('Failed to load current organization:', error)
    currentOrganization.value = null
  }
}

const showRemoveOrgModal = () => {
  removeOrgModalVisible.value = true
  removeConfirmationText.value = ''
}

const confirmRemoveOrganization = async () => {
  if (removeConfirmationText.value !== 'REMOVE') {
    message.error('Please type "REMOVE" to confirm this action')
    return
  }

  if (!currentOrganization.value) {
    message.error('No organization to remove')
    return
  }

  removingOrg.value = true
  
  try {
    message.loading('Removing organization...', 0)
    
    console.log('🔍 Frontend - Starting organization removal process')
    console.log('🔍 Frontend - Current organization:', currentOrganization.value)
    
    // Import the remove organization function
    const { removeOrganizationFromPatient } = await import('@/services/patientService')
    
    // Get current user info
    const userInfo = await getMe()
    console.log('🔍 Frontend - User info:', userInfo?.data)
    
    if (!userInfo?.data?.patientId) {
      console.error('❌ Frontend - Patient ID not found in user info')
      throw new Error('Patient ID not found')
    }
    
    console.log('🔍 Frontend - Patient ID:', userInfo.data.patientId)
    console.log('🔍 Frontend - Organization ID:', currentOrganization.value.id)
    
    // Call the API to remove organization
    console.log('🔍 Frontend - Calling removeOrganizationFromPatient API...')
    await removeOrganizationFromPatient(userInfo.data.patientId, {
      organizationId: currentOrganization.value.id,
      removedBy: userInfo.data.id,
      removedAt: new Date().toISOString(),
      reason: 'Manual removal by POA'
    })
    
    console.log('✅ Frontend - Organization removal API call successful')
    
    message.destroy()
    message.success('Organization removed successfully!')
    
    // Clear organization data
    currentOrganization.value = null
    
    // Clear token data as well since organization is removed
    generatedToken.value = ''
    tokenExists.value = false
    tokenExpirationDate.value = null
    
    // Reload workers to reflect changes
    await loadWorkers()
    
    // Close modal
    removeOrgModalVisible.value = false
    removeConfirmationText.value = ''
    
  } catch (error) {
    message.destroy()
    console.error('Failed to remove organization:', error)
    message.error(error.message || 'Failed to remove organization')
  } finally {
    removingOrg.value = false
  }
}

const formatDate = (dateString) => {
  if (!dateString) return 'N/A'
  return dayjs(dateString).format('YYYY-MM-DD')
}

onMounted(async () => {
  loading.value = true
  
  try {
    // Get current user info
    const userInfo = await getMe()
    const organizationId = userInfo?.data?.organizationId || 'default-org'
    
    console.log('👤 CarerTeam - Current user info:', userInfo?.data)
    console.log('🔍 CarerTeam - User organizationId:', userInfo?.data?.organizationId)
    console.log('🔍 CarerTeam - User patientId:', userInfo?.data?.patientId)
    console.log('🔍 CarerTeam - User role:', userInfo?.data?.role)
    
    // Debug: Check if POA has required data
    if (userInfo?.data?.role === 'poa') {
      console.log('🔍 CarerTeam - POA Debug Info:')
      console.log('  - Has patientId:', !!userInfo.data.patientId)
      console.log('  - Has organizationId:', !!userInfo.data.organizationId)
      console.log('  - PatientId value:', userInfo.data.patientId)
      console.log('  - OrganizationId value:', userInfo.data.organizationId)
      
      if (!userInfo.data.organizationId) {
        console.error('❌ CarerTeam - POA user missing organizationId - this will cause schedules not to display')
        message.warning('Your account is not properly linked to an organization. Please contact the manager.')
      }
    }
    
    // Load current organization info
    await loadCurrentOrganization()
    
    // Load workers from API
    await loadWorkers()
    
    // No need to add mock Manager and POA data - they should come from the API
    
    // Set today as default date
    selectedDate.value = dayjs()
    const todayStr = selectedDate.value.format('YYYY-MM-DD')
    
    // Load daily schedule for today
    await loadDailySchedule(organizationId, todayStr)
    
    // Load shift time settings from manager
    try {
      const managerId = '68f571d9bfbe063b0d64e3ac' // This should be dynamic in production
      const shiftTimeResponse = await getShiftTimeSettings(managerId)
      if (shiftTimeResponse?.data) {
        shiftTimeSettings.value = shiftTimeResponse.data
        console.log('Loaded shift time settings:', shiftTimeResponse.data)
      }
    } catch (error) {
      console.error('Failed to load shift time settings:', error)
      // Use default settings as fallback
      shiftTimeSettings.value = {
        morningShiftStart: '09:00',
        morningShiftEnd: '14:00',
        afternoonShiftStart: '16:00',
        afternoonShiftEnd: '22:00',
        eveningShiftStart: '22:00',
        eveningShiftEnd: '06:00'
      }
    }
    
    // Initialize filtered workers
    updateFilteredWorkers()
    
    // For managers, load client info automatically if bound to a patient
    if (userInfo?.data?.role === 'manager' && userInfo?.data?.patientId) {
      console.log('Manager is bound to patient:', userInfo.data.patientId)
      // The client info will be automatically loaded through the user's patientId
      // No need to manually enter Client ID
    }
    
  } catch (error) {
    console.error('Failed to load carer team data:', error)
    message.error('Failed to load carer team data')
    
    // No fallback mock data - show empty state
    selectedDate.value = dayjs()
    dailyWorkers.value = []
    filteredWorkers.value = []
  } finally {
    loading.value = false
  }
})

// Load existing token information
const loadExistingTokenInfo = async () => {
  try {
    const userInfo = await getMe()
    if (userInfo?.data?.patientId) {
      const { getPatientById } = await import('@/services/patientService')
      const existingPatient = await getPatientById(userInfo.data.patientId)
      
      // Check for token in different possible field names
      const tokenValue = existingPatient?.data?.inviteToken || 
                       existingPatient?.data?.clientId || 
                       existingPatient?.data?.token ||
                       existingPatient?.data?.inviteCode
      
      if (tokenValue && existingPatient?.data?.tokenExpiration) {
        const expirationDate = new Date(existingPatient.data.tokenExpiration)
        const now = new Date()
        const currentOrgId = userInfo.data.organizationId || 'default-org'
        
        // Check if token is still valid AND organization hasn't changed
        if (now < expirationDate && existingPatient.data.organizationId === currentOrgId) {
          // Token is still valid, load token information
          generatedToken.value = tokenValue
          tokenForm.value = {
            organizationName: existingPatient.data.organizationName || 'Default Organization',
            tokenType: 'manager',
            expirationDays: 7,
            notes: ''
          }
          console.log('✅ Loaded existing valid token:', generatedToken.value)
        }
      }
    }
  } catch (error) {
    console.error('Failed to load existing token info:', error)
  }
}

// Load workers from API
const loadWorkers = async () => {
  try {
    // Get current user info to get organizationId
    const userInfo = await getMe()
    const organizationId = userInfo?.data?.organizationId || 'default-org'
    
    // Use organization-specific API if organizationId is available
    let response
    try {
      if (organizationId && organizationId !== 'default-org') {
        response = await getWorkersByOrganization(organizationId)
      } else {
        // Fallback to all workers if no organization ID
        response = await getAllWorkers()
      }
    } catch (error) {
      console.error('Failed to load workers from organization API, trying role-based API:', error)
      // Fallback to role-based API
      response = await getUsersByRole('worker')
    }
    
    if (response?.data) {
      // Transform worker data to match expected formatme
      workers.value = response.data.map(worker => ({
        id: worker.id,
        workerId: worker.workerId || worker.id,
        name: worker.firstName && worker.lastName ? `${worker.firstName} ${worker.lastName}` : worker.name || 'Unknown Worker',
        firstName: worker.firstName || '',
        lastName: worker.lastName || '',
        email: worker.email || '',
        photo: worker.photo || worker.photoUrl || null,
        status: worker.status || 'Active',
        shift: 'morning' // Default shift, will be updated by daily schedule
      }))
      console.log('Loaded workers for CarerTeam:', workers.value.length, 'for organization:', organizationId)
    }
  } catch (error) {
    console.error('Failed to load workers:', error)
    // No fallback mock data - show empty state
    workers.value = []
  }
}

// No longer needed - shifts come from actual schedule data

// Load daily schedule from API - connects to Manager's schedule data
const loadDailySchedule = async (organizationId, dateStr) => {
  try {
    console.log('🔍 CarerTeam - Loading daily schedule for:', dateStr, 'organizationId:', organizationId)
    console.log('🔍 CarerTeam - API URL will be:', `/workers/organization/${organizationId}/daily-schedule/${dateStr}`)
    
    // Get current user info for debugging
    try {
      const userInfo = await getMe()
      console.log('🔍 CarerTeam - Current user role:', userInfo?.data?.role)
      console.log('🔍 CarerTeam - Current user patientId:', userInfo?.data?.patientId)
      console.log('🔍 CarerTeam - Current user organizationId:', userInfo?.data?.organizationId)
    } catch (error) {
      console.error('❌ CarerTeam - Failed to get user info:', error)
    }
    
    const response = await getDailySchedule(organizationId, dateStr)
    console.log('📅 CarerTeam - Raw schedule data from API:', response?.data)
    console.log('📅 CarerTeam - Response status:', response?.status)
    console.log('📅 CarerTeam - Response code:', response?.code)
    
    if (response?.data && response.data.length > 0) {
      // Convert Schedule objects to worker-like objects for display
      const scheduledWorkers = response.data.map(schedule => {
        console.log('🔄 CarerTeam - Processing schedule:', schedule)
        
        // Find the complete worker data from workers array
        // Try multiple matching strategies like in WorkerManagement
        let completeWorker = workers.value.find(w => w.id === schedule.workerId)
        if (!completeWorker) {
          // Try matching by workerId field
          completeWorker = workers.value.find(w => w.workerId === schedule.workerId)
        }
        if (!completeWorker) {
          // Try matching by name
          completeWorker = workers.value.find(w => w.name === schedule.workerName)
        }
        
        console.log('👤 CarerTeam - Found complete worker:', completeWorker)
        
        const shiftType = schedule.shiftType || 'morning'
        
        // Use shift time settings from manager instead of schedule data
        let shiftStartTime, shiftEndTime
        if (shiftType === 'morning') {
          shiftStartTime = shiftTimeSettings.value.morningShiftStart || '09:00'
          shiftEndTime = shiftTimeSettings.value.morningShiftEnd || '14:00'
        } else if (shiftType === 'afternoon') {
          shiftStartTime = shiftTimeSettings.value.afternoonShiftStart || '16:00'
          shiftEndTime = shiftTimeSettings.value.afternoonShiftEnd || '22:00'
        } else if (shiftType === 'evening') {
          shiftStartTime = shiftTimeSettings.value.eveningShiftStart || '22:00'
          shiftEndTime = shiftTimeSettings.value.eveningShiftEnd || '06:00'
        } else {
          // Fallback to schedule data if shift type is unknown
          shiftStartTime = schedule.shiftStartTime
          shiftEndTime = schedule.shiftEndTime
        }
        
        return {
          id: completeWorker?.id || schedule.workerId, // use DB id if available
          workerId: schedule.workerId, // keep business workerId separately
          name: schedule.workerName,
          firstName: schedule.workerName.split(' ')[0] || '',
          lastName: schedule.workerName.split(' ').slice(1).join(' ') || '',
          email: completeWorker?.email || '',
          photo: schedule.workerPhotoUrl || completeWorker?.photo || null,
          status: schedule.status || 'Active',
          shift: shiftType,
          shiftStartTime: shiftStartTime,
          shiftEndTime: shiftEndTime,
          notes: schedule.notes
        }
      })
      
      dailyWorkers.value = scheduledWorkers
      console.log('✅ CarerTeam - Loaded daily schedule for', dateStr, ':', scheduledWorkers.length, 'workers')
      console.log('📊 CarerTeam - Final dailyWorkers array:', dailyWorkers.value)
    } else {
      // If no schedule exists, show empty state
      dailyWorkers.value = []
      console.log('❌ CarerTeam - No schedule exists for', dateStr, '- showing empty state')
    }
  } catch (error) {
    console.error('❌ CarerTeam - Failed to load daily schedule:', error)
    // No fallback mock data - show empty state
    dailyWorkers.value = []
  }
}
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
