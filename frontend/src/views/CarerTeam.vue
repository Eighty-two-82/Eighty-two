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
                Morning Shift (06:00-14:00)
              </a-radio-button>
              <a-radio-button value="afternoon">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Afternoon Shift (14:00-22:00)
              </a-radio-button>
              <a-radio-button value="night">
                <ClockCircleOutlined style="margin-right: 4px;" />
                Night Shift (22:00-06:00)
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
                  
                  <!-- Worker ID -->
                  <div style="font-weight: bold; color: #1890ff; font-size: 12px;">
                    {{ worker.workerId }}
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
  InfoCircleOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { getMe } from '@/services/userService'
import { getAllWorkers, getDailySchedule } from '@/services/workerService'
import { generateInviteCode } from '@/services/inviteCodeService'

// Reactive data
const loading = ref(false)
const selectedWorker = ref(null)
const selectedDate = ref(null)
const selectedShift = ref('morning')
const generateTokenModalVisible = ref(false)
const tokenDisplayModalVisible = ref(false)
const generatedToken = ref('')
const isShowingExistingToken = ref(false)
const tokenExists = ref(false) // 标记Token是否存在
const tokenExpirationDate = ref(null) // 保存Token过期时间

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


// Methods
const selectWorker = (worker) => {
  selectedWorker.value = worker
  message.info(`Selected care team member: ${worker.name} (${worker.workerId})`)
}

const onDateChange = (date) => {
  if (date) {
    const dateStr = date.format('YYYY-MM-DD')
    updateDailyWorkers(dateStr)
    updateFilteredWorkers()
  }
}

const onShiftChange = () => {
  updateFilteredWorkers()
}

const updateDailyWorkers = (dateStr) => {
  // For now, show all active team members for the selected date
  // In a real implementation, this would filter based on actual schedule data
  dailyWorkers.value = workers.value.filter(worker => worker.status === 'Active')
}

const updateFilteredWorkers = () => {
  // Filter workers based on selected shift
  filteredWorkers.value = dailyWorkers.value.filter(worker => {
    return worker.shift === selectedShift.value
  })
}

// Shift-related methods
const getShiftInfo = () => {
  const shiftInfo = {
    morning: {
      title: 'Morning Shift',
      description: 'Covers morning care activities, breakfast assistance, and morning medications (06:00 - 14:00)'
    },
    afternoon: {
      title: 'Afternoon Shift', 
      description: 'Handles afternoon activities, lunch assistance, and afternoon care (14:00 - 22:00)'
    },
    night: {
      title: 'Night Shift',
      description: 'Provides overnight care, evening medications, and night monitoring (22:00 - 06:00)'
    }
  }
  return shiftInfo[selectedShift.value] || shiftInfo.morning
}

const getShiftColor = (shift) => {
  const colors = {
    morning: 'green',
    afternoon: 'blue', 
    night: 'purple'
  }
  return colors[shift] || 'default'
}

const getShiftDisplayName = (shift) => {
  const names = {
    morning: 'Morning',
    afternoon: 'Afternoon',
    night: 'Night'
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
        
        console.log('✅ Patient record updated with token information')
      } catch (patientError) {
        console.error('Failed to update patient record:', patientError)
        // Don't fail the whole operation if patient update fails
      }
      
      message.destroy()
      message.success('Invite token generated successfully!')
      
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

onMounted(async () => {
  loading.value = true
  
  try {
    // Get current user info
    const userInfo = await getMe()
    const organizationId = userInfo?.data?.organizationId || 'default-org'
    
    // Load workers from API
    await loadWorkers()
    
    // Add Manager and POA to workers list with shift assignments
    const managerAndPOA = [
      {
        id: 99,
        workerId: 'M001',
        name: 'Manager Smith',
        photo: null,
        position: 'Care Manager',
        status: 'Active',
        joinDate: '2022-01-01',
        email: 'manager@careapp.com',
        role: 'Manager',
        shift: 'morning' // Manager typically works morning shift
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
        role: 'POA',
        shift: 'morning' // POA typically available during morning shift
      }
    ]
    
    // Combine all team members
    workers.value = [...workers.value, ...managerAndPOA]
    
    // Set today as default date
    selectedDate.value = dayjs()
    const todayStr = selectedDate.value.format('YYYY-MM-DD')
    
    // Load daily schedule for today
    await loadDailySchedule(organizationId, todayStr)
    
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
    
    // Fallback to mock data
    selectedDate.value = dayjs()
    const todayStr = selectedDate.value.format('YYYY-MM-DD')
    updateDailyWorkers(todayStr)
    updateFilteredWorkers()
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
    const response = await getAllWorkers()
    if (response?.data) {
      // Add shift information to workers
      workers.value = response.data.map(worker => ({
        ...worker,
        shift: getRandomShift() // Assign random shift for demo purposes
      }))
    }
  } catch (error) {
    console.error('Failed to load workers:', error)
    // Keep using mock data with shift information
    workers.value = [
      {
        id: 1,
        workerId: 'W001',
        name: 'Alice Johnson',
        photo: null,
        position: 'Senior Care Worker',
        status: 'Active',
        joinDate: '2022-01-15',
        email: 'alice@careapp.com',
        role: 'Worker',
        shift: 'morning'
      },
      {
        id: 2,
        workerId: 'W002',
        name: 'Bob Smith',
        photo: null,
        position: 'Care Worker',
        status: 'Active',
        joinDate: '2022-03-20',
        email: 'bob@careapp.com',
        role: 'Worker',
        shift: 'afternoon'
      },
      {
        id: 3,
        workerId: 'W003',
        name: 'Carol Davis',
        photo: null,
        position: 'Night Care Worker',
        status: 'Active',
        joinDate: '2022-05-10',
        email: 'carol@careapp.com',
        role: 'Worker',
        shift: 'night'
      },
      {
        id: 4,
        workerId: 'W004',
        name: 'David Wilson',
        photo: null,
        position: 'Care Worker',
        status: 'Active',
        joinDate: '2022-07-05',
        email: 'david@careapp.com',
        role: 'Worker',
        shift: 'morning'
      },
      {
        id: 5,
        workerId: 'W005',
        name: 'Eva Brown',
        photo: null,
        position: 'Senior Care Worker',
        status: 'Active',
        joinDate: '2022-09-12',
        email: 'eva@careapp.com',
        role: 'Worker',
        shift: 'afternoon'
      },
      {
        id: 6,
        workerId: 'W006',
        name: 'Frank Miller',
        photo: null,
        position: 'Care Worker',
        status: 'Active',
        joinDate: '2022-11-08',
        email: 'frank@careapp.com',
        role: 'Worker',
        shift: 'night'
      },
      {
        id: 7,
        workerId: 'W007',
        name: 'Grace Lee',
        photo: null,
        position: 'Senior Care Worker',
        status: 'Active',
        joinDate: '2023-01-15',
        email: 'grace@careapp.com',
        role: 'Worker',
        shift: 'morning'
      }
    ]
  }
}

// Helper function to assign random shifts for demo
const getRandomShift = () => {
  const shifts = ['morning', 'afternoon', 'night']
  return shifts[Math.floor(Math.random() * shifts.length)]
}

// Load daily schedule from API
const loadDailySchedule = async (organizationId, dateStr) => {
  try {
    const response = await getDailySchedule(organizationId, dateStr)
    if (response?.data) {
      dailyWorkers.value = response.data
    } else {
      // If no schedule exists, show all active workers
      dailyWorkers.value = workers.value.filter(worker => worker.status === 'Active')
    }
  } catch (error) {
    console.error('Failed to load daily schedule:', error)
    // Fallback to mock data
    updateDailyWorkers(dateStr)
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
