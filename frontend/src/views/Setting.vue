<template>
  <div class="setting-page">
    <a-row :gutter="[24, 24]">
      <!-- Patient Information Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <UserOutlined />
              <span>Patient Information</span>
              <a-tooltip title="Manage patient basic information and special requirements" placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <a-form
            :model="patientForm"
            :label-col="{ span: 4 }"
            :wrapper-col="{ span: 20 }"
            :disabled="!canEditPatient"
            @finish="onPatientFormFinish"
          >
            <a-form-item
              label="Patient Name"
              name="name"
              :rules="[{ required: true, message: 'Please input patient name!' }]"
            >
              <a-input v-model:value="patientForm.name" placeholder="Enter patient name" />
            </a-form-item>

            <a-form-item
              label="Age"
              name="age"
              :rules="[{ required: true, message: 'Please input patient age!' }]"
            >
              <a-input-number 
                v-model:value="patientForm.age" 
                placeholder="Enter age"
                :min="0"
                :max="150"
                style="width: 100%"
              />
            </a-form-item>

            <a-form-item
              label="Medical Conditions"
              name="medicalConditions"
            >
              <a-textarea
                v-model:value="patientForm.medicalConditions"
                placeholder="Enter medical conditions, allergies, medications, etc."
                :rows="3"
              />
            </a-form-item>

            <a-form-item
              label="Special Requirements"
              name="specialRequirements"
            >
              <a-textarea
                v-model:value="patientForm.specialRequirements"
                placeholder="Enter special requirements (e.g., daily coffee, specific meal times, etc.)"
                :rows="3"
              />
            </a-form-item>

            <a-form-item v-if="canEditPatient" :wrapper-col="{ offset: 4, span: 20 }">
              <a-button type="primary" html-type="submit" :loading="patientLoading">
                Save Patient Information
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <!-- Profile Settings Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <SettingOutlined />
              <span>Profile Settings</span>
              <a-tooltip title="Manage your personal information and account settings" placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <a-form
            :model="profileForm"
            :label-col="{ span: 3 }"
            :wrapper-col="{ span: 21 }"
            @finish="onProfileFormFinish"
          >
            <a-form-item
              label="Full Name"
              name="fullName"
            >
              <a-input v-model:value="profileForm.fullName" disabled />
            </a-form-item>

            <a-form-item
              label="Email"
              name="email"
              :rules="[
                { required: true, message: 'Please input your email!' },
                { type: 'email', message: 'Please enter a valid email!' }
              ]"
            >
              <a-input v-model:value="profileForm.email" placeholder="Enter your email" />
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 3, span: 21 }">
              <a-button type="primary" html-type="submit" :loading="profileLoading">
                Update Email
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>

      <!-- Notification Settings Section -->
      <a-col :span="24">
        <a-card>
          <template #title>
            <div style="display: flex; align-items: center; gap: 8px;">
              <BellOutlined />
              <span>Notification Settings</span>
              <a-tooltip title="Configure your notification preferences" placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
          </template>
          
          <a-form
            :model="notificationForm"
            :label-col="{ span: 6 }"
            :wrapper-col="{ span: 18 }"
            @finish="onNotificationFormFinish"
          >
            <a-form-item label="Task Reminders">
              <a-switch 
                v-model:checked="notificationForm.taskReminders"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications for task deadlines and updates
              </span>
            </a-form-item>

            <a-form-item label="Approval Notifications" v-if="userRole === 'poa' || userRole === 'manager'">
              <a-switch 
                v-model:checked="notificationForm.approvalNotifications"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications when approval is required
              </span>
            </a-form-item>

            <a-form-item label="Budget Warning" v-if="userRole === 'poa' || userRole === 'manager'">
              <a-switch 
                v-model:checked="notificationForm.budgetWarning"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications for budget alerts and warnings
              </span>
            </a-form-item>

            <a-form-item label="Email Notifications">
              <a-switch 
                v-model:checked="notificationForm.emailNotifications"
                checked-children="ON"
                un-checked-children="OFF"
              />
              <span style="margin-left: 8px; color: #666;">
                Receive notifications via email
              </span>
            </a-form-item>

            <a-form-item :wrapper-col="{ offset: 6, span: 18 }">
              <a-button type="primary" html-type="submit" :loading="notificationLoading">
                Save Notification Settings
              </a-button>
            </a-form-item>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import { 
  QuestionCircleOutlined, 
  UserOutlined, 
  SettingOutlined, 
  BellOutlined 
} from '@ant-design/icons-vue'
import { getMe } from '@/services/userService'

const userRole = ref('worker')
const patientLoading = ref(false)
const profileLoading = ref(false)
const notificationLoading = ref(false)

// Patient Information Form
const patientForm = reactive({
  name: '',
  age: null,
  medicalConditions: '',
  specialRequirements: ''
})

// Profile Form
const profileForm = reactive({
  fullName: '',
  email: ''
})

// Notification Form
const notificationForm = reactive({
  taskReminders: true,
  approvalNotifications: true,
  budgetWarning: true,
  emailNotifications: true
})

// Computed property to check if user can edit patient information
const canEditPatient = computed(() => {
  return userRole.value === 'poa'
})

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
    
    // Load user profile data
    profileForm.fullName = userInfo?.data?.name || 'Test User'
    profileForm.email = userInfo?.data?.email || 'test@example.com'
    
    // Load patient data (mock data for now)
    loadPatientData()
    loadNotificationSettings()
  } catch (e) {
    console.error('Failed to get user info:', e)
  }
})

// Mock function to load patient data
const loadPatientData = () => {
  // In real app, this would be an API call
  patientForm.name = 'P1'
  patientForm.age = 75
  patientForm.medicalConditions = 'High Blood Pressure'
  patientForm.specialRequirements = 'Daily morning coffee at 8 AM, Light lunch at 12 PM, Evening walk at 6 PM'
}

// Mock function to load notification settings
const loadNotificationSettings = () => {
  // In real app, this would be an API call
  notificationForm.taskReminders = true
  notificationForm.approvalNotifications = userRole.value === 'poa' || userRole.value === 'manager'
  notificationForm.budgetWarning = userRole.value === 'poa' || userRole.value === 'manager'
  notificationForm.emailNotifications = true
}

// Handle patient form submission
const onPatientFormFinish = async () => {
  if (!canEditPatient.value) {
    message.warning('You do not have permission to edit patient information')
    return
  }
  
  patientLoading.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    message.success('Patient information updated successfully')
  } catch (e) {
    message.error('Failed to update patient information')
  } finally {
    patientLoading.value = false
  }
}

// Handle profile form submission
const onProfileFormFinish = async () => {
  profileLoading.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    message.success('Email updated successfully')
  } catch (e) {
    message.error('Failed to update email')
  } finally {
    profileLoading.value = false
  }
}

// Handle notification form submission
const onNotificationFormFinish = async () => {
  notificationLoading.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    message.success('Notification settings updated successfully')
  } catch (e) {
    message.error('Failed to update notification settings')
  } finally {
    notificationLoading.value = false
  }
}
</script>

<style scoped>
.setting-page {
  max-width: 1200px;
  margin: 0 auto;
}

.ant-card {
  margin-bottom: 24px;
}

.ant-form-item {
  margin-bottom: 16px;
}
</style>
