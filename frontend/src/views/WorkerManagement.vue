<template>
  <div class="worker-management-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
          <h2>Worker Management</h2>
          <a-tooltip title="Care staff management page, Managers can invite Workers to join the team" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
      
      <!-- Action Buttons -->
      <div style="margin-bottom: 20px; display: flex; gap: 12px; flex-wrap: wrap;">
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="primary" @click="generateInviteToken" :loading="generatingToken">
            <template #icon>
              <UserAddOutlined />
            </template>
            Generate Invite Token
          </a-button>
          <a-tooltip title="Generate a unique invitation code for workers to join the care team. The token will expire after the specified number of days." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
        
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="default" @click="showDailyManagementModal">
            <template #icon>
              <CalendarOutlined />
            </template>
            Daily Management
          </a-button>
          <a-tooltip title="Manage daily work schedules and upload attendance photos. Set which workers are assigned for each day and upload their daily photos for verification." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
        
        <div style="display: flex; align-items: center; gap: 4px;">
          <a-button type="default" @click="showTransferModal">
            <template #icon>
              <SwapOutlined />
            </template>
            Transfer Worker
          </a-button>
          <a-tooltip title="Transfer a worker to another company or organization. This will move the worker's information and records to the target company." placement="top">
            <span style="color: #999; cursor: help; font-size: 12px; width: 16px; height: 16px; border-radius: 50%; border: 1px solid #999; display: inline-flex; align-items: center; justify-content: center; line-height: 1;">?</span>
          </a-tooltip>
        </div>
      </div>

      <!-- Invite Token Display -->
      <a-alert
        v-if="generatedToken"
        :message="`Generated Invite Token: ${generatedToken}`"
        type="success"
        show-icon
        closable
        @close="generatedToken = null"
        style="margin-bottom: 20px;"
      />

      <!-- Daily Workers Section -->
      <div style="margin-bottom: 30px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0; color: #1890ff;">
            <UserOutlined style="margin-right: 8px;" />
            Daily Workers - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
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
              style="text-align: center; height: 140px; cursor: pointer;"
              @click="showWorkerDetail(worker)"
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

      <!-- All Workers Table -->
      <div style="margin-bottom: 20px;">
        <h3 style="margin-bottom: 16px; color: #52c41a;">
          <TeamOutlined style="margin-right: 8px;" />
          All Workers
        </h3>
        <a-table
          :columns="workerColumns"
          :data-source="workers"
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
            
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editWorker(record)">
                  Edit
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- Generate Token Modal -->
    <a-modal
      v-model:open="tokenModalVisible"
      title="Generate Invite Token"
      @ok="confirmGenerateToken"
      @cancel="tokenModalVisible = false"
    >
      <a-form :model="tokenForm" layout="vertical">
        <a-form-item label="Token Type">
          <a-input
            v-model:value="tokenForm.type"
            value="Worker"
            disabled
            style="background-color: #f5f5f5;"
          />
          <div style="font-size: 12px; color: #666; margin-top: 4px;">
            This token is specifically for inviting workers to join the team
          </div>
        </a-form-item>
        <a-form-item label="Expiration (days)">
          <a-input-number
            v-model:value="tokenForm.expirationDays"
            :min="1"
            :max="30"
            style="width: 100%"
          />
        </a-form-item>
        <a-form-item label="Notes (optional)">
          <a-textarea
            v-model:value="tokenForm.notes"
            placeholder="Add any notes for this invite token"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Daily Management Modal -->
    <a-modal
      v-model:open="dailyManagementModalVisible"
      title="Daily Management"
      width="1200px"
      @ok="confirmDailyManagement"
      @cancel="dailyManagementModalVisible = false"
    >
      <a-form :model="scheduleForm" layout="vertical">
        <a-form-item label="Select Date">
          <a-date-picker
            v-model:value="scheduleForm.date"
            style="width: 200px;"
            placeholder="Select date for schedule"
          />
        </a-form-item>
        
        <a-row :gutter="24">
          <!-- Left Column: Worker Selection -->
          <a-col :span="12">
            <a-form-item label="Select Workers for Today">
              <div style="max-height: 300px; overflow-y: auto; border: 1px solid #d9d9d9; border-radius: 6px; padding: 12px;">
                <a-checkbox-group v-model:value="scheduleForm.selectedWorkers" style="width: 100%;">
                  <div
                    v-for="worker in availableWorkers"
                    :key="worker.id"
                    style="margin-bottom: 12px; padding: 8px; border: 1px solid #f0f0f0; border-radius: 4px;"
                    :style="{ backgroundColor: scheduleForm.selectedWorkers.includes(worker.id) ? '#f6ffed' : '#fff' }"
                  >
                    <a-checkbox :value="worker.id" style="width: 100%;">
                      <div style="display: flex; align-items: center; gap: 12px;">
                        <a-avatar :size="32" :src="worker.photo">
                          {{ worker.name.charAt(0).toUpperCase() }}
                        </a-avatar>
                        <div style="flex: 1;">
                          <div style="font-weight: bold;">{{ worker.name }}</div>
                          <div style="font-size: 12px; color: #666;">{{ worker.workerId }} - {{ worker.position }}</div>
                        </div>
                        <a-button 
                          v-if="scheduleForm.selectedWorkers.includes(worker.id)"
                          size="small" 
                          type="primary"
                          @click.stop="uploadWorkerPhoto(worker)"
                        >
                          Upload Photo
                        </a-button>
                      </div>
                    </a-checkbox>
                  </div>
                </a-checkbox-group>
              </div>
            </a-form-item>
          </a-col>
          
          <!-- Right Column: Photo Upload for Selected Worker -->
          <a-col :span="12">
            <a-form-item label="Upload Photo for Selected Worker">
              <div v-if="selectedWorkerForPhoto" style="margin-bottom: 16px; padding: 12px; background: #f6ffed; border-radius: 6px;">
                <div style="display: flex; align-items: center; gap: 12px;">
                  <a-avatar :size="40" :src="selectedWorkerForPhoto.photo">
                    {{ selectedWorkerForPhoto.name.charAt(0).toUpperCase() }}
                  </a-avatar>
                  <div>
                    <div style="font-weight: bold;">{{ selectedWorkerForPhoto.name }}</div>
                    <div style="font-size: 12px; color: #666;">{{ selectedWorkerForPhoto.workerId }} - {{ selectedWorkerForPhoto.position }}</div>
                  </div>
                </div>
              </div>
              
              <a-upload-dragger
                v-model:file-list="fileList"
                name="file"
                :before-upload="beforeUpload"
                @change="handleUploadChange"
                :disabled="!selectedWorkerForPhoto"
                list-type="picture-card"
                :show-upload-list="true"
                :max-count="1"
              >
                <div v-if="fileList.length === 0">
                  <p class="ant-upload-drag-icon">
                    <InboxOutlined />
                  </p>
                  <p class="ant-upload-text">
                    {{ selectedWorkerForPhoto ? `Upload photo for ${selectedWorkerForPhoto.name}` : 'Select a worker first' }}
                  </p>
                  <p class="ant-upload-hint">
                    {{ selectedWorkerForPhoto ? 'Click or drag photo to upload for this worker' : 'Please select a worker from the left to upload their photo' }}
                  </p>
                </div>
              </a-upload-dragger>
              
              <!-- Photo Preview -->
              <div v-if="fileList.length > 0" style="margin-top: 16px;">
                <h4 style="margin-bottom: 8px;">Photo Preview:</h4>
                <div style="display: flex; gap: 12px; flex-wrap: wrap;">
                  <div
                    v-for="file in fileList"
                    :key="file.uid"
                    style="position: relative; border: 1px solid #d9d9d9; border-radius: 6px; padding: 8px;"
                  >
                    <img
                      :src="file.thumbUrl || file.url"
                      :alt="file.name"
                      style="width: 120px; height: 120px; object-fit: cover; border-radius: 4px;"
                    />
                    <div style="margin-top: 4px; font-size: 12px; text-align: center; color: #666;">
                      {{ file.name }}
                    </div>
                    <a-button
                      size="small"
                      danger
                      style="position: absolute; top: 4px; right: 4px;"
                      @click="removeFile(file.uid)"
                    >
                      ×
                    </a-button>
                  </div>
                </div>
              </div>
              
              <div style="margin-top: 16px;">
                <a-alert
                  message="Upload Guidelines"
                  description="Please ensure photos are clear and include worker's face and ID badge. Supported formats: JPG, PNG. Max file size: 5MB per photo."
                  type="info"
                  show-icon
                />
              </div>
            </a-form-item>
          </a-col>
        </a-row>

        <a-form-item label="Shift Information">
          <a-row :gutter="16">
            <a-col :span="12">
              <a-form-item label="Morning Shift (8:00-16:00)">
                <a-select
                  v-model:value="scheduleForm.morningShift"
                  mode="multiple"
                  placeholder="Select morning shift workers"
                  style="width: 100%;"
                >
                  <a-select-option
                    v-for="worker in availableWorkers"
                    :key="worker.id"
                    :value="worker.id"
                  >
                    {{ worker.name }} ({{ worker.workerId }})
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="Evening Shift (16:00-24:00)">
                <a-select
                  v-model:value="scheduleForm.eveningShift"
                  mode="multiple"
                  placeholder="Select evening shift workers"
                  style="width: 100%;"
                >
                  <a-select-option
                    v-for="worker in availableWorkers"
                    :key="worker.id"
                    :value="worker.id"
                  >
                    {{ worker.name }} ({{ worker.workerId }})
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form-item>

        <a-form-item label="Schedule Notes">
          <a-textarea
            v-model:value="scheduleForm.notes"
            placeholder="Add any notes for this schedule"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Transfer Worker Modal -->
    <a-modal
      v-model:open="transferModalVisible"
      title="Transfer Worker to Another Company"
      @ok="confirmTransferWorker"
      @cancel="transferModalVisible = false"
    >
      <a-form :model="transferForm" layout="vertical">
        <a-form-item label="Select Worker">
          <a-select
            v-model:value="transferForm.workerId"
            placeholder="Choose a worker to transfer"
            show-search
            :filter-option="filterOption"
          >
            <a-select-option
              v-for="worker in workers"
              :key="worker.id"
              :value="worker.id"
            >
              {{ worker.name }} (ID: {{ worker.workerId }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="Target Patient/Team" required>
          <a-select
            v-model:value="transferForm.targetPatient"
            placeholder="Select target patient or team"
            style="width: 100%;"
            show-search
            :filter-option="filterOption"
          >
            <a-select-option value="patient1">Patient A - A (Room 101)</a-select-option>
            <a-select-option value="patient2">Patient B - B (Room 102)</a-select-option>
            <a-select-option value="patient3">Patient C - C (Room 103)</a-select-option>
            <a-select-option value="patient4">Patient D - D (Room 104)</a-select-option>
            <a-select-option value="team1">General Care Team (Return to headquarters for reassignment)</a-select-option>
            <a-select-option value="team2">Other Organization</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="Transfer Reason">
          <a-textarea
            v-model:value="transferForm.reason"
            placeholder="Explain the reason for transfer"
            :rows="3"
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Worker Detail Modal -->
    <a-modal
      v-model:open="workerDetailModalVisible"
      title="Worker Details"
      width="600px"
      :footer="null"
      @cancel="workerDetailModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="padding: 20px;">
        <!-- Worker Photo and Basic Info -->
        <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 24px; padding: 16px; background: #f6ffed; border-radius: 8px;">
          <a-avatar
            :size="80"
            :src="selectedWorkerDetail.photo"
            :alt="selectedWorkerDetail.name"
            style="border: 3px solid #52c41a;"
          >
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div style="flex: 1;">
            <h2 style="margin: 0 0 8px 0; color: #1890ff;">{{ selectedWorkerDetail.name }}</h2>
            <div style="font-size: 16px; color: #666; margin-bottom: 4px;">
              <strong>ID:</strong> {{ selectedWorkerDetail.workerId }}
            </div>
            <div style="font-size: 16px; color: #666; margin-bottom: 4px;">
              <strong>Position:</strong> {{ selectedWorkerDetail.position }}
            </div>
            <div style="font-size: 16px; color: #666;">
              <strong>Status:</strong> 
              <a-tag :color="getStatusColor(selectedWorkerDetail.status)">
                {{ selectedWorkerDetail.status }}
              </a-tag>
            </div>
          </div>
        </div>

        <!-- Detailed Information -->
        <a-row :gutter="[16, 16]">
          <a-col :span="12">
            <a-card size="small" title="Contact Information">
              <div style="margin-bottom: 8px;">
                <strong>Email:</strong> {{ selectedWorkerDetail.email }}
              </div>
              <div>
                <strong>Join Date:</strong> {{ selectedWorkerDetail.joinDate }}
              </div>
            </a-card>
          </a-col>
          
          <a-col :span="12">
            <a-card size="small" title="Work Schedule">
              <div style="margin-bottom: 8px;">
                <strong>Today's Status:</strong> 
                <a-tag color="green">Scheduled</a-tag>
              </div>
              <div style="margin-bottom: 8px;">
                <strong>Shift:</strong> Morning (8:00-16:00)
              </div>
              <div>
                <strong>Last Photo:</strong> 
                <a-tag color="blue">Uploaded Today</a-tag>
              </div>
            </a-card>
          </a-col>
        </a-row>

        <!-- Photo Gallery -->
        <div style="margin-top: 20px;">
          <h3 style="margin-bottom: 12px;">Recent Photos</h3>
          <div style="display: flex; gap: 12px; flex-wrap: wrap;">
            <div
              v-if="selectedWorkerDetail.photo"
              style="position: relative; border: 1px solid #d9d9d9; border-radius: 6px; padding: 8px;"
            >
              <img
                :src="selectedWorkerDetail.photo"
                :alt="selectedWorkerDetail.name"
                style="width: 100px; height: 100px; object-fit: cover; border-radius: 4px;"
              />
              <div style="margin-top: 4px; font-size: 12px; text-align: center; color: #666;">
                Today
              </div>
            </div>
            <div style="border: 2px dashed #d9d9d9; border-radius: 6px; padding: 8px; width: 100px; height: 100px; display: flex; align-items: center; justify-content: center; color: #999;">
              <div style="text-align: center;">
                <div style="font-size: 24px; margin-bottom: 4px;">📷</div>
                <div style="font-size: 10px;">No more photos</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Action Buttons -->
        <div style="margin-top: 24px;">
          <h4 style="margin-bottom: 12px; color: #1890ff;">Worker Management Actions</h4>
          <div style="display: flex; gap: 12px; justify-content: center; flex-wrap: wrap;">
            <a-button type="primary" @click="editWorkerStatusFromDetail">
              Edit Status
            </a-button>
            <a-button @click="uploadPhotoFromDetail">
              Upload Photo
            </a-button>
            <a-button @click="transferWorkerFromDetail">
              Transfer Worker
            </a-button>
            <a-button danger @click="removeWorkerFromTeam">
              Remove from Team
            </a-button>
          </div>
          <div style="margin-top: 12px; padding: 12px; background: #f6ffed; border-radius: 6px; font-size: 12px; color: #666;">
            <div><strong>Edit Status:</strong> Change worker status (Active/On leave/Resigned) - keeps historical data</div>
            <div><strong>Transfer Worker:</strong> Move worker to serve a different patient or return to headquarters for reassignment</div>
            <div><strong>Remove from Team:</strong> Remove worker from current patient team but keep in organization</div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- Edit Worker Status Modal -->
    <a-modal
      v-model:open="editStatusModalVisible"
      title="Edit Worker Status"
      width="500px"
      @ok="confirmEditStatus"
      @cancel="editStatusModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; background: #f6ffed; border-radius: 6px;">
          <a-avatar :size="40" :src="selectedWorkerDetail.photo">
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div>
            <div style="font-weight: bold;">{{ selectedWorkerDetail.name }}</div>
            <div style="font-size: 12px; color: #666;">{{ selectedWorkerDetail.workerId }} - {{ selectedWorkerDetail.position }}</div>
          </div>
        </div>
      </div>

      <a-form :model="editStatusForm" layout="vertical">
        <a-form-item label="Current Status">
          <a-tag :color="getStatusColor(selectedWorkerDetail?.status)">
            {{ selectedWorkerDetail?.status }}
          </a-tag>
        </a-form-item>

        <a-form-item label="New Status" required>
          <a-select
            v-model:value="editStatusForm.newStatus"
            placeholder="Select new status"
            style="width: 100%;"
          >
            <a-select-option value="Active">
              <a-tag color="green">Active</a-tag>
            </a-select-option>
            <a-select-option value="On leave">
              <a-tag color="orange">On leave</a-tag>
            </a-select-option>
            <a-select-option value="Transferred out">
              <a-tag color="blue">Transferred out</a-tag>
            </a-select-option>
            <a-select-option value="Resigned">
              <a-tag color="red">Resigned</a-tag>
            </a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="Reason for Status Change">
          <a-textarea
            v-model:value="editStatusForm.reason"
            placeholder="Explain the reason for status change (optional)"
            :rows="3"
          />
        </a-form-item>

        <a-alert
          v-if="editStatusForm.newStatus === 'Resigned'"
          message="Important Notice"
          description="Setting status to 'Resigned' will mark the worker as no longer employed by the organization. Historical data will be preserved but the worker will be removed from active assignments."
          type="warning"
          show-icon
          style="margin-bottom: 16px;"
        />
        
        <a-alert
          v-if="editStatusForm.newStatus === 'Transferred out'"
          message="Transfer Notice"
          description="Setting status to 'Transferred out' indicates the worker has been moved to another organization or department. Historical data will be preserved."
          type="info"
          show-icon
          style="margin-bottom: 16px;"
        />
      </a-form>
    </a-modal>

    <!-- Remove Worker from Team Modal -->
    <a-modal
      v-model:open="removeFromTeamModalVisible"
      title="Remove Worker from Team"
      width="500px"
      @ok="confirmRemoveFromTeam"
      @cancel="removeFromTeamModalVisible = false"
    >
      <div v-if="selectedWorkerDetail" style="margin-bottom: 20px;">
        <div style="display: flex; align-items: center; gap: 12px; padding: 12px; background: #fff2e8; border-radius: 6px;">
          <a-avatar :size="40" :src="selectedWorkerDetail.photo">
            {{ selectedWorkerDetail.name.charAt(0).toUpperCase() }}
          </a-avatar>
          <div>
            <div style="font-weight: bold;">{{ selectedWorkerDetail.name }}</div>
            <div style="font-size: 12px; color: #666;">{{ selectedWorkerDetail.workerId }} - {{ selectedWorkerDetail.position }}</div>
          </div>
        </div>
      </div>

      <a-form :model="removeFromTeamForm" layout="vertical">
        <a-form-item label="Reason for Removal">
          <a-select
            v-model:value="removeFromTeamForm.reason"
            placeholder="Select reason"
            style="width: 100%;"
          >
            <a-select-option value="Service completed">Service completed</a-select-option>
            <a-select-option value="Patient request">Patient request</a-select-option>
            <a-select-option value="Schedule conflict">Schedule conflict</a-select-option>
            <a-select-option value="Performance issue">Performance issue</a-select-option>
            <a-select-option value="Other">Other</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item label="Additional Notes">
          <a-textarea
            v-model:value="removeFromTeamForm.notes"
            placeholder="Add any additional notes (optional)"
            :rows="3"
          />
        </a-form-item>

        <a-form-item label="Effective Date">
          <a-date-picker
            v-model:value="removeFromTeamForm.effectiveDate"
            style="width: 100%;"
            placeholder="Select effective date"
          />
        </a-form-item>

        <a-alert
          message="Important Notice"
          description="This action will remove the worker from the current patient team but keep them in the organization. They can be reassigned to other patients or teams."
          type="info"
          show-icon
          style="margin-bottom: 16px;"
        />
      </a-form>
    </a-modal>

  </div>
</template>

<script setup>
import { ref, onMounted, h, resolveComponent } from 'vue'
import dayjs from 'dayjs'
import { 
  QuestionCircleOutlined, 
  UserAddOutlined, 
  UploadOutlined, 
  SwapOutlined,
  InboxOutlined,
  UserOutlined,
  TeamOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

// Reactive data
const loading = ref(false)
const generatingToken = ref(false)
const generatedToken = ref(null)
const tokenModalVisible = ref(false)
const uploadModalVisible = ref(false)
const transferModalVisible = ref(false)
const dailyManagementModalVisible = ref(false)
const fileList = ref([])
const selectedWorker = ref(null)
const selectedDate = ref(null)
const activeTab = ref('schedule')
const selectedWorkerForPhoto = ref(null)
const workerDetailModalVisible = ref(false)
const selectedWorkerDetail = ref(null)
const editStatusModalVisible = ref(false)
const removeFromTeamModalVisible = ref(false)

// Forms
const tokenForm = ref({
  type: 'Worker',
  expirationDays: 7,
  notes: ''
})

const transferForm = ref({
  workerId: null,
  targetPatient: '',
  reason: ''
})

const editStatusForm = ref({
  newStatus: '',
  reason: ''
})

const removeFromTeamForm = ref({
  reason: '',
  notes: '',
  effectiveDate: null
})

const scheduleForm = ref({
  date: null,
  selectedWorkers: [],
  morningShift: [],
  eveningShift: [],
  notes: ''
})

const photoForm = ref({
  date: null
})

// Mock worker data
const workers = ref([
  {
    id: 1,
    workerId: 'W001',
    name: 'A',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-01-15',
    phone: '+1-555-0101',
    email: 'a@careapp.com'
  },
  {
    id: 2,
    workerId: 'W002',
    name: 'B',
    photo: null,
    position: 'Senior Care Assistant',
    status: 'Active',
    joinDate: '2022-11-20',
    phone: '+1-555-0102',
    email: 'b@careapp.com'
  },
  {
    id: 3,
    workerId: 'W003',
    name: 'C',
    photo: null,
    position: 'Care Assistant',
    status: 'On Leave',
    joinDate: '2023-03-10',
    phone: '+1-555-0103',
    email: 'c@careapp.com'
  },
  {
    id: 4,
    workerId: 'W004',
    name: 'D',
    photo: null,
    position: 'Care Coordinator',
    status: 'Active',
    joinDate: '2022-08-05',
    phone: '+1-555-0104',
    email: 'd@careapp.com'
  },
  {
    id: 5,
    workerId: 'W005',
    name: 'E',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-02-14',
    phone: '+1-555-0105',
    email: 'e@careapp.com'
  },
  {
    id: 6,
    workerId: 'W006',
    name: 'F',
    photo: null,
    position: 'Senior Care Assistant',
    status: 'Active',
    joinDate: '2022-09-30',
    phone: '+1-555-0106',
    email: 'f@careapp.com'
  },
  {
    id: 7,
    workerId: 'W007',
    name: 'G',
    photo: null,
    position: 'Care Assistant',
    status: 'Active',
    joinDate: '2023-04-22',
    phone: '+1-555-0107',
    email: 'g@careapp.com'
  },
  {
    id: 8,
    workerId: 'W008',
    name: 'H',
    photo: null,
    position: 'Care Coordinator',
    status: 'Active',
    joinDate: '2022-12-08',
    phone: '+1-555-0108',
    email: 'h@careapp.com'
  }
])

// Daily workers (subset of active workers)
const dailyWorkers = ref([])

// Available workers (all active workers)
const availableWorkers = ref([])

// Daily schedules data
const dailySchedules = ref({
  // Mock data for different dates
  '2024-01-15': [1, 2, 4, 5, 6], // John, Sarah, Emily, David, Lisa
  '2024-01-16': [1, 3, 4, 6, 7], // John, Michael, Emily, Robert, Jennifer
  '2024-01-17': [2, 4, 5, 7, 8], // Sarah, Emily, David, Jennifer, (假设有第8个员工)
  '2024-01-18': [1, 2, 5, 6, 7], // John, Sarah, David, Lisa, Robert
  '2024-01-19': [3, 4, 6, 7, 8]  // Michael, Emily, Lisa, Robert, Jennifer
})

// Table columns
const workerColumns = [
  {
    title: 'Photo',
    key: 'photo',
    width: 80,
    align: 'center'
  },
  {
    title: 'Worker ID',
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
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 120,
    align: 'center'
  }
]

// Methods
const generateInviteToken = () => {
  tokenModalVisible.value = true
}

const confirmGenerateToken = async () => {
  generatingToken.value = true
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Generate a mock token
    const token = `INV-${Date.now().toString(36).toUpperCase()}-${Math.random().toString(36).substr(2, 8).toUpperCase()}`
    generatedToken.value = token
    
    message.success('Invite token generated successfully!')
    tokenModalVisible.value = false
    
    // Reset form
    tokenForm.value = {
      type: 'Worker',
      expirationDays: 7,
      notes: ''
    }
  } catch (error) {
    message.error('Failed to generate invite token')
  } finally {
    generatingToken.value = false
  }
}

const showUploadModal = () => {
  uploadModalVisible.value = true
}

const showDailyManagementModal = () => {
  dailyManagementModalVisible.value = true
  activeTab.value = 'schedule'
  // Set default date to today
  scheduleForm.value.date = selectedDate.value || dayjs()
  photoForm.value.date = selectedDate.value || dayjs()
  // Reset selected worker for photo
  selectedWorkerForPhoto.value = null
  fileList.value = []
}

const uploadWorkerPhoto = (worker) => {
  selectedWorkerForPhoto.value = worker
  fileList.value = [] // Clear previous files
  message.info(`Ready to upload photo for ${worker.name}`)
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('You can only upload image files!')
    return false
  }
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isLt5M) {
    message.error('Image must be smaller than 5MB!')
    return false
  }
  return false // Prevent auto upload
}

const handleUploadChange = (info) => {
  let fileList = [...info.fileList]
  
  // Limit to 1 file
  fileList = fileList.slice(-1)
  
  // Generate preview URL for images
  fileList = fileList.map(file => {
    if (file.originFileObj) {
      file.thumbUrl = URL.createObjectURL(file.originFileObj)
    }
    return file
  })
  
  // Update the file list
  fileList.value = fileList
}

const removeFile = (uid) => {
  fileList.value = fileList.value.filter(file => file.uid !== uid)
}

const confirmUploadPhotos = async () => {
  if (fileList.value.length === 0) {
    message.warning('Please select photos to upload')
    return
  }
  
  try {
    // Simulate upload process
    message.loading('Uploading photos...', 0)
    await new Promise(resolve => setTimeout(resolve, 2000))
    message.destroy()
    
    message.success(`Successfully uploaded ${fileList.value.length} photos`)
    uploadModalVisible.value = false
    fileList.value = []
  } catch (error) {
    message.destroy()
    message.error('Failed to upload photos')
  }
}

const showTransferModal = () => {
  transferModalVisible.value = true
}

const confirmTransferWorker = async () => {
  if (!transferForm.value.workerId || !transferForm.value.targetPatient) {
    message.warning('Please fill in all required fields')
    return
  }
  
  try {
    const worker = workers.value.find(w => w.id === transferForm.value.workerId)
    if (worker) {
      // Simulate transfer process
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // Remove worker from current daily schedule
      const today = selectedDate.value.format('YYYY-MM-DD')
      if (dailySchedules.value[today]) {
        dailySchedules.value[today] = dailySchedules.value[today].filter(id => id !== worker.id)
      }
      
      // Remove from dailyWorkers
      const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === worker.id)
      if (dailyWorkerIndex !== -1) {
        dailyWorkers.value.splice(dailyWorkerIndex, 1)
      }
      
      const targetName = transferForm.value.targetPatient === 'team1' 
        ? 'General Care Team (headquarters for reassignment)'
        : transferForm.value.targetPatient === 'team2'
        ? 'Other Organization'
        : transferForm.value.targetPatient
      message.success(`Worker ${worker.name} has been transferred to ${targetName}`)
      transferModalVisible.value = false
      
      // Reset form
      transferForm.value = {
        workerId: null,
        targetPatient: '',
        reason: ''
      }
    }
  } catch (error) {
    message.error('Failed to transfer worker')
  }
}

const getStatusColor = (status) => {
  switch (status) {
    case 'Active': return 'green'
    case 'On leave': return 'orange'
    case 'Transferred out': return 'blue'
    case 'Resigned': return 'red'
    case 'Inactive': return 'red'
    default: return 'default'
  }
}

const editWorker = (worker) => {
  selectedWorkerDetail.value = worker
  editStatusForm.value = {
    newStatus: worker.status || 'Active',
    reason: ''
  }
  editStatusModalVisible.value = true
}


const filterOption = (input, option) => {
  return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
}

const selectWorker = (worker) => {
  selectedWorker.value = worker
  message.info(`Selected worker: ${worker.name} (${worker.workerId})`)
}

const showWorkerDetail = (worker) => {
  selectedWorkerDetail.value = worker
  workerDetailModalVisible.value = true
}

const editWorkerStatusFromDetail = () => {
  editStatusForm.value = {
    newStatus: selectedWorkerDetail.value.status || 'Active',
    reason: ''
  }
  editStatusModalVisible.value = true
}

const uploadPhotoFromDetail = () => {
  workerDetailModalVisible.value = false
  // Set the worker for photo upload
  selectedWorkerForPhoto.value = selectedWorkerDetail.value
  // Open daily management modal
  dailyManagementModalVisible.value = true
  message.info(`Ready to upload photo for ${selectedWorkerDetail.value.name}`)
}

const transferWorkerFromDetail = () => {
  workerDetailModalVisible.value = false
  transferForm.value = {
    workerId: selectedWorkerDetail.value.id,
    targetPatient: '',
    reason: ''
  }
  transferModalVisible.value = true
}

const removeWorkerFromTeam = () => {
  removeFromTeamForm.value = {
    reason: '',
    notes: '',
    effectiveDate: dayjs()
  }
  removeFromTeamModalVisible.value = true
}

const confirmEditStatus = async () => {
  if (!editStatusForm.value.newStatus) {
    message.error('Please select a new status')
    return
  }

  try {
    message.loading('Updating worker status...', 0)
    await new Promise(resolve => setTimeout(resolve, 1500))

    // Update worker status in the workers array
    const workerIndex = workers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
    if (workerIndex !== -1) {
      workers.value[workerIndex].status = editStatusForm.value.newStatus
    }

    // Update in dailyWorkers if present
    const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
    if (dailyWorkerIndex !== -1) {
      dailyWorkers.value[dailyWorkerIndex].status = editStatusForm.value.newStatus
    }

    // Update selectedWorkerDetail
    selectedWorkerDetail.value.status = editStatusForm.value.newStatus

    message.destroy()
    message.success(`Successfully updated ${selectedWorkerDetail.value.name}'s status to ${editStatusForm.value.newStatus}`)
    editStatusModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to update worker status')
  }
}

const confirmRemoveFromTeam = async () => {
  if (!removeFromTeamForm.value.reason) {
    message.error('Please select a reason for removal')
    return
  }

  try {
    message.loading('Removing worker from team...', 0)
    await new Promise(resolve => setTimeout(resolve, 1500))

    // Remove worker from dailyWorkers (current team)
    const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerDetail.value.id)
    if (dailyWorkerIndex !== -1) {
      dailyWorkers.value.splice(dailyWorkerIndex, 1)
    }

    // Update daily schedules to remove this worker
    const today = selectedDate.value.format('YYYY-MM-DD')
    if (dailySchedules.value[today]) {
      dailySchedules.value[today] = dailySchedules.value[today].filter(id => id !== selectedWorkerDetail.value.id)
    }

    message.destroy()
    message.success(`Successfully removed ${selectedWorkerDetail.value.name} from the current patient team`)
    removeFromTeamModalVisible.value = false
    workerDetailModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to remove worker from team')
  }
}

const showScheduleModal = () => {
  scheduleModalVisible.value = true
  // Set default date to today
  scheduleForm.value.date = selectedDate.value || dayjs()
}

const confirmDailyManagement = async () => {
  // Handle schedule update
  if (!scheduleForm.value.date || scheduleForm.value.selectedWorkers.length === 0) {
    message.warning('Please select a date and at least one worker')
    return
  }
  
  try {
    const dateStr = scheduleForm.value.date.format('YYYY-MM-DD')
    dailySchedules.value[dateStr] = scheduleForm.value.selectedWorkers
    
    // Update daily workers if it's the selected date
    if (dateStr === selectedDate.value?.format('YYYY-MM-DD')) {
      updateDailyWorkers(dateStr)
    }
    
    // Handle photo upload if there are files
    if (fileList.value.length > 0 && selectedWorkerForPhoto.value) {
      message.loading(`Uploading photo for ${selectedWorkerForPhoto.value.name}...`, 0)
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // Save the photo URL to the worker's data
      const uploadedFile = fileList.value[0]
      if (uploadedFile && uploadedFile.thumbUrl) {
        // Update the worker's photo in the workers array
        const workerIndex = workers.value.findIndex(w => w.id === selectedWorkerForPhoto.value.id)
        if (workerIndex !== -1) {
          workers.value[workerIndex].photo = uploadedFile.thumbUrl
        }
        
        // Update the worker's photo in the dailyWorkers array if they are currently displayed
        const dailyWorkerIndex = dailyWorkers.value.findIndex(w => w.id === selectedWorkerForPhoto.value.id)
        if (dailyWorkerIndex !== -1) {
          dailyWorkers.value[dailyWorkerIndex].photo = uploadedFile.thumbUrl
        }
      }
      
      message.destroy()
      message.success(`Successfully uploaded photo for ${selectedWorkerForPhoto.value.name}`)
    }
    
    message.success('Daily schedule updated successfully!')
  } catch (error) {
    message.error('Failed to update schedule')
  }
  
  // Reset forms
  scheduleForm.value = {
    date: null,
    selectedWorkers: [],
    morningShift: [],
    eveningShift: [],
    notes: ''
  }
  photoForm.value = {
    date: null
  }
  selectedWorkerForPhoto.value = null
  fileList.value = []
  
  dailyManagementModalVisible.value = false
}

const confirmScheduleUpdate = async () => {
  if (!scheduleForm.value.date || scheduleForm.value.selectedWorkers.length === 0) {
    message.warning('Please select a date and at least one worker')
    return
  }
  
  try {
    const dateStr = scheduleForm.value.date.format('YYYY-MM-DD')
    dailySchedules.value[dateStr] = scheduleForm.value.selectedWorkers
    
    // Update daily workers if it's the selected date
    if (dateStr === selectedDate.value?.format('YYYY-MM-DD')) {
      updateDailyWorkers(dateStr)
    }
    
    message.success('Daily schedule updated successfully!')
    scheduleModalVisible.value = false
    
    // Reset form
    scheduleForm.value = {
      date: null,
      selectedWorkers: [],
      morningShift: [],
      eveningShift: [],
      notes: ''
    }
  } catch (error) {
    message.error('Failed to update schedule')
  }
}

const onDateChange = (date) => {
  if (date) {
    const dateStr = date.format('YYYY-MM-DD')
    updateDailyWorkers(dateStr)
  }
}

const updateDailyWorkers = (dateStr) => {
  const workerIds = dailySchedules.value[dateStr] || []
  // Filter workers and maintain their photo data
  dailyWorkers.value = workers.value.filter(worker => workerIds.includes(worker.id))
}

const formatDate = (date) => {
  if (!date) return 'Today'
  return date.format('YYYY-MM-DD')
}

onMounted(() => {
  // Load worker data
  loading.value = true
  
  // Initialize available workers (all active workers)
  availableWorkers.value = workers.value.filter(worker => worker.status === 'Active')
  
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
.worker-management-page {
  max-width: 1200px;
  margin: 0 auto;
}

.ant-upload-drag-icon {
  font-size: 48px;
  color: #1890ff;
}

.ant-upload-text {
  font-size: 16px;
  color: #666;
}

.ant-upload-hint {
  font-size: 14px;
  color: #999;
}

.selected-worker {
  border: 2px solid #1890ff !important;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3) !important;
}

.selected-worker .ant-card-body {
  background-color: #f6ffed;
}
</style>
