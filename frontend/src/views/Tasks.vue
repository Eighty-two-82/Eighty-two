<template>
  <div class="tasks-page">
    <!-- Date Selection Section -->
    <div style="margin-bottom: 30px;">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <h3 style="margin: 0; color: #1890ff;">
          <CheckSquareOutlined style="margin-right: 8px;" />
          Today's Tasks - {{ selectedDate ? selectedDate.format('YYYY-MM-DD') : '' }}
        </h3>
        <a-date-picker v-model:value="selectedDate" @change="onDateChange" style="width: 200px;" placeholder="Select date" />
      </div>
    </div>

    <!-- POA/Worker Simple Task View -->
    <div v-if="!isManager">
    <a-card>
      <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>{{ isPOA ? 'Task Review' : 'My Tasks' }}</span>
              <a-tooltip :title="isPOA ? 'Review tasks completed by workers and confirm completion. You can also request task adjustments from the manager.' : 'View your assigned tasks for today. You can mark tasks as completed.'">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
          </div>
            <div v-if="isPOA" style="display: flex; align-items: center; gap: 8px;">
              <a-button type="primary" @click="showRequestTaskModal">
                <PlusOutlined />
                Request Task
              </a-button>
              <a-tooltip title="Request task adjustments or new tasks from the manager">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
          </div>
        </template>
        
        <a-table
          :columns="simpleTaskColumns"
          :data-source="getMyTasks()"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'priority'">
              <a-tag :color="getPriorityColor(record.priority)">
                {{ getPriorityText(record.priority) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            
            <template v-if="column.key === 'actions'">
              <!-- POA: Can confirm worker completion or mark as completed -->
              <div v-if="isPOA">
                <div style="display: flex; align-items: center; gap: 8px;">
                  <a-button 
                    v-if="record.status === 'Worker Completed'"
                    size="small" 
                    type="primary" 
                    @click="showConfirmModal(record, 'approve')"
                    style="background-color: #52c41a; border-color: #52c41a;"
                  >
                    Approve
                  </a-button>
                  <a-button 
                    v-if="record.status === 'Worker Completed'"
                    size="small" 
                    danger
                    @click="showConfirmModal(record, 'reject')"
                  >
                    Reject
                  </a-button>
                  <span v-if="record.status === 'In Progress'" style="color: #999; font-size: 12px; margin-left: 8px;">
                    Wait for worker to finish
                  </span>
                  <span v-if="record.status === 'Pending'" style="color: #999; font-size: 12px; margin-left: 8px;">
                    Task not started yet
                  </span>
                  <span v-if="record.status === 'Completed'" style="color: #52c41a; font-weight: bold;">✓ Completed</span>
                  <span v-else-if="record.status === 'Rejected'" style="color: #ff4d4f; font-weight: bold;">✗ Rejected</span>
                </div>
              </div>
              <!-- Worker: Can only mark their own tasks as completed -->
              <div v-else>
                <div style="display: flex; align-items: center; gap: 8px;">
                <a-button 
                    v-if="record.status !== 'Completed' && record.status !== 'Worker Completed'"
                    size="small" 
                  type="primary" 
                    @click="workerCompleteTask(record)"
                    style="background-color: #52c41a; border-color: #52c41a;"
                  >
                    Complete
                  </a-button>
                  <a-tooltip v-if="record.status !== 'Completed' && record.status !== 'Worker Completed'" title="Click when task is done">
                    <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
                  </a-tooltip>
                  <span v-if="record.status === 'Worker Completed'" style="color: #faad14; font-weight: bold;">⏳ Pending Review</span>
                  <span v-else-if="record.status === 'Completed'" style="color: #52c41a; font-weight: bold;">✓ Completed</span>
                </div>
              </div>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- My Requests Section (Only for POA) -->
      <a-card v-if="isPOA" style="margin-top: 24px;">
        <template #title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>My Requests</span>
            <a-tooltip title="View the status of your submitted requests and manager responses. You can see if your requests were approved, rejected, or are still pending.">
              <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="myRequestsColumns"
          :data-source="myRequests"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getRequestStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'managerResponse'">
              <div v-if="record.status === 'Approved'" style="color: #52c41a; font-weight: bold;">
                ✓ Approved
                <div v-if="record.approvalReason" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ record.approvalReason }}
                </div>
              </div>
              <div v-else-if="record.status === 'Rejected'" style="color: #ff4d4f; font-weight: bold;">
                ✗ Rejected
                <div v-if="record.rejectionReason" style="font-size: 12px; color: #666; margin-top: 4px;">
                  {{ record.rejectionReason }}
                </div>
              </div>
              <div v-else style="color: #999;">
                Waiting for manager response
              </div>
            </template>
          </template>
        </a-table>
      </a-card>
    </div>

    <!-- Manager Full Task Management View -->
    <div v-if="isManager">
      <!-- Today's Tasks Section -->
      <a-card style="margin-bottom: 24px;">
        <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>All Tasks</span>
              <a-tooltip title="View and manage all tasks for today. You can create new tasks, edit existing ones, complete tasks, or delete them.">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
            <a-tooltip title="Create a new task and assign it to a worker. Set priority level, due date, and description.">
              <a-button type="primary" @click="showCreateTaskModal">
                <PlusOutlined />
                Create Task
              </a-button>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="taskColumns"
          :data-source="todayTasks"
          :pagination="false"
                  size="small" 
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'priority'">
              <a-tag :color="getPriorityColor(record.priority)">
                {{ getPriorityText(record.priority) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editTask(record)">
                  Edit
                </a-button>
                <a-button 
                  size="small" 
                  type="primary" 
                  @click="completeTask(record)"
                  :disabled="record.status === 'Completed'"
                  style="background-color: #52c41a; border-color: #52c41a;"
                >
                  Complete
                </a-button>
                <a-button size="small" danger @click="deleteTask(record)">
                  Delete
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Recurring Tasks Section -->
      <a-card style="margin-bottom: 24px;">
        <template #title>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="display: flex; align-items: center; gap: 8px;">
              <span>Recurring Tasks</span>
              <a-tooltip title="Manage recurring task templates. Create templates for tasks that repeat daily, weekly, or monthly. The system will automatically generate tasks based on these templates.">
                <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
              </a-tooltip>
            </div>
            <a-tooltip title="Create a new recurring task template. Set the frequency (daily, weekly, monthly) and the system will automatically generate tasks.">
              <a-button type="primary" @click="showCreateRecurringTaskModal">
                <PlusOutlined />
                Create Recurring Task
              </a-button>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="recurringTaskColumns"
          :data-source="recurringTasks"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'frequency'">
              <a-tag :color="getFrequencyColor(record.frequency)">
                {{ getFrequencyText(record.frequency, record.frequencyNumber) }}
              </a-tag>
            </template>
            <template v-if="column.key === 'status'">
              <a-tag :color="record.isActive ? 'green' : 'red'">
                {{ record.isActive ? 'Active' : 'Inactive' }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="editRecurringTask(record)">
                  Edit
                </a-button>
                <a-button 
                  size="small" 
                  :type="record.isActive ? 'default' : 'primary'"
                  @click="toggleRecurringTask(record)"
                >
                  {{ record.isActive ? 'Deactivate' : 'Activate' }}
                </a-button>
                <a-button size="small" danger @click="deleteRecurringTask(record)">
                  Delete
                </a-button>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Work Assignment Section -->
      <a-card style="margin-bottom: 24px;">
        <template #title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>Work Assignment</span>
            <a-tooltip title="View work assignments for all workers. Shows how many tasks each worker has been assigned and their current status. Click 'View' to see detailed task information.">
              <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="assignmentColumns"
          :data-source="workAssignments"
          :pagination="false"
          size="small"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-button size="small" @click="viewAssignment(record)">
                View
              </a-button>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Request Change Section -->
      <a-card>
        <template #title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <span>Request Change</span>
            <a-tooltip title="Review and manage change requests from family members. You can approve or reject requests for task modifications, worker changes, or other adjustments.">
              <span style="color: #999; cursor: help; font-size: 12px; border: 1px solid #999; border-radius: 50%; width: 16px; height: 16px; display: inline-flex; align-items: center; justify-content: center;">?</span>
            </a-tooltip>
          </div>
        </template>
        
        <a-table
          :columns="requestColumns"
          :data-source="changeRequests"
          :pagination="false"
                  size="small" 
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <a-tag :color="getRequestStatusColor(record.status)">
                {{ record.status }}
              </a-tag>
            </template>
            <template v-if="column.key === 'actions'">
              <a-space>
                <a-button size="small" @click="viewRequestDetails(record)">
                  View Details
                </a-button>
                <a-space v-if="record.status === 'Pending'">
                  <a-button size="small" type="primary" @click="showRequestConfirmModal(record, 'approve')">
                    Approve
                  </a-button>
                  <a-button size="small" danger @click="showRequestConfirmModal(record, 'reject')">
                    Reject
                  </a-button>
                </a-space>
                <span v-else style="color: #999;">{{ record.status }}</span>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <!-- Create Recurring Task Modal -->
      <a-modal
        v-model:open="createRecurringTaskModalVisible"
        title="Create Recurring Task Template"
        width="700px"
        @ok="confirmCreateRecurringTask"
        @cancel="createRecurringTaskModalVisible = false"
      >
        <a-form :model="createRecurringTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="createRecurringTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="createRecurringTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To" required>
            <a-select v-model:value="createRecurringTaskForm.assignedTo" placeholder="Select worker" style="width: 100%;">
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="createRecurringTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="createRecurringTaskForm.frequency" placeholder="Select frequency" style="width: 100%;" @change="onFrequencyChange">
                  <a-select-option value="daily">Day(s)</a-select-option>
                  <a-select-option value="weekly">Week(s)</a-select-option>
                  <a-select-option value="monthly">Month(s)</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="createRecurringTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="createRecurringTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="createRecurringTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="createRecurringTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="createRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="createRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="createRecurringTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="createRecurringTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Edit Recurring Task Modal -->
      <a-modal
        v-model:open="editRecurringTaskModalVisible"
        title="Edit Recurring Task Template"
        width="700px"
        @ok="confirmEditRecurringTask"
        @cancel="editRecurringTaskModalVisible = false"
      >
        <a-form :model="editRecurringTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="editRecurringTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="editRecurringTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To" required>
            <a-select v-model:value="editRecurringTaskForm.assignedTo" placeholder="Select worker" style="width: 100%;">
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="editRecurringTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="editRecurringTaskForm.frequency" placeholder="Select frequency" style="width: 100%;" @change="onEditFrequencyChange">
                  <a-select-option value="daily">Day(s)</a-select-option>
                  <a-select-option value="weekly">Week(s)</a-select-option>
                  <a-select-option value="monthly">Month(s)</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="editRecurringTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="editRecurringTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="editRecurringTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="editRecurringTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="editRecurringTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="editRecurringTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="editRecurringTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="editRecurringTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
          <a-form-item label="Status">
            <a-switch v-model:checked="editRecurringTaskForm.isActive" checked-children="Active" un-checked-children="Inactive" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Create Task Modal -->
      <a-modal
        v-model:open="createTaskModalVisible"
        title="Create New Task"
        width="600px"
        @ok="confirmCreateTask"
        @cancel="createTaskModalVisible = false"
      >
        <a-form :model="createTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="createTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="createTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To" required>
            <a-select v-model:value="createTaskForm.assignedTo" placeholder="Select worker" style="width: 100%;">
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Priority" required>
            <a-select v-model:value="createTaskForm.priority" placeholder="Select priority" style="width: 100%;">
              <a-select-option value="normal">
                <a-tag color="green">Normal</a-tag>
              </a-select-option>
              <a-select-option value="urgent">
                <a-tag color="orange">Urgent</a-tag>
              </a-select-option>
              <a-select-option value="very-urgent">
                <a-tag color="red">Very Urgent</a-tag>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Due Date" required>
            <a-date-picker v-model:value="createTaskForm.dueDate" style="width: 100%;" placeholder="Select due date" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- Edit Task Modal -->
      <a-modal
        v-model:open="editTaskModalVisible"
        title="Edit Task"
        width="600px"
        @ok="confirmEditTask"
        @cancel="editTaskModalVisible = false"
      >
        <a-form :model="editTaskForm" layout="vertical">
          <a-form-item label="Task Title" required>
            <a-input v-model:value="editTaskForm.title" placeholder="Enter task title" />
          </a-form-item>
          <a-form-item label="Description">
            <a-textarea v-model:value="editTaskForm.description" placeholder="Enter task description" :rows="3" />
          </a-form-item>
          <a-form-item label="Assign To" required>
            <a-select v-model:value="editTaskForm.assignedTo" placeholder="Select worker" style="width: 100%;">
              <a-select-option v-for="worker in availableWorkers" :key="worker.id" :value="worker.id">
                {{ worker.name }} ({{ worker.workerId }})
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Priority" required>
            <a-select v-model:value="editTaskForm.priority" placeholder="Select priority" style="width: 100%;">
              <a-select-option value="normal">
                <a-tag color="green">Normal</a-tag>
              </a-select-option>
              <a-select-option value="urgent">
                <a-tag color="orange">Urgent</a-tag>
              </a-select-option>
              <a-select-option value="very-urgent">
                <a-tag color="red">Very Urgent</a-tag>
              </a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Status" required>
            <a-select v-model:value="editTaskForm.status" placeholder="Select status" style="width: 100%;">
              <a-select-option value="Pending">Pending</a-select-option>
              <a-select-option value="In Progress">In Progress</a-select-option>
              <a-select-option value="Completed">Completed</a-select-option>
              <a-select-option value="Cancelled">Cancelled</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="Due Date" required>
            <a-date-picker v-model:value="editTaskForm.dueDate" style="width: 100%;" placeholder="Select due date" />
          </a-form-item>
        </a-form>
      </a-modal>

      <!-- View Assignment Modal -->
      <a-modal
        v-model:open="viewAssignmentModalVisible"
        title="Work Assignment Details"
        width="700px"
        :footer="null"
      >
        <div v-if="selectedAssignment">
          <a-descriptions :column="1" bordered>
            <a-descriptions-item label="Worker">
              {{ selectedAssignment.worker }}
            </a-descriptions-item>
            <a-descriptions-item label="Total Tasks Assigned">
              {{ selectedAssignment.tasks }}
            </a-descriptions-item>
            <a-descriptions-item label="Status">
              <a-tag :color="getStatusColor(selectedAssignment.status)">
                {{ selectedAssignment.status }}
              </a-tag>
            </a-descriptions-item>
          </a-descriptions>
          
          <div style="margin-top: 20px;">
            <h4>Assigned Tasks:</h4>
            <a-table
              :columns="taskDetailColumns"
              :data-source="getWorkerTasks(selectedAssignment.worker)"
              :pagination="false"
              size="small"
              bordered
            >
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'priority'">
                  <a-tag :color="getPriorityColor(record.priority)">
                    {{ getPriorityText(record.priority) }}
                  </a-tag>
                </template>
                <template v-if="column.key === 'status'">
                  <a-tag :color="getStatusColor(record.status)">
                    {{ record.status }}
                  </a-tag>
                </template>
              </template>
            </a-table>
            </div>

          <div style="margin-top: 16px; text-align: right;">
            <a-button @click="viewAssignmentModalVisible = false">
              Close
            </a-button>
          </div>
        </div>
      </a-modal>

    </div>
    <!-- End Manager View -->

    <!-- POA Request Task Modal (Outside Manager View) -->
    <a-modal
      v-if="isPOA"
      v-model:open="requestTaskModalVisible"
      title="Request Task Adjustment"
      width="600px"
      @ok="confirmRequestTask"
      @cancel="requestTaskModalVisible = false"
    >
      <a-form :model="requestTaskForm" layout="vertical">
        <a-form-item label="Request Type" required>
          <a-select v-model:value="requestTaskForm.requestType" placeholder="Select request type" style="width: 100%;" @change="onRequestTypeChange">
            <a-select-option value="new">Add New Task</a-select-option>
            <a-select-option value="recurring">Add Recurring Task</a-select-option>
            <a-select-option value="modify">Edit Task</a-select-option>
            <a-select-option value="remove">Remove Task</a-select-option>
            <a-select-option value="reschedule">Reschedule Task</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="Task Title" required>
          <a-input v-model:value="requestTaskForm.title" placeholder="Enter task title" />
        </a-form-item>
        <a-form-item label="Description">
          <a-textarea v-model:value="requestTaskForm.description" placeholder="Enter task description or modification details" :rows="3" />
        </a-form-item>
        <a-form-item label="Priority">
          <a-select v-model:value="requestTaskForm.priority" placeholder="Select priority" style="width: 100%;">
            <a-select-option value="normal">Normal</a-select-option>
            <a-select-option value="urgent">Urgent</a-select-option>
            <a-select-option value="very-urgent">Very Urgent</a-select-option>
          </a-select>
        </a-form-item>
        
        <!-- Recurring Task Frequency (only show when request type is recurring) -->
        <div v-if="requestTaskForm.requestType === 'recurring'">
          <a-form-item label="Recurrence Frequency" required>
            <a-row :gutter="8">
              <a-col :span="8">
                <a-input-number 
                  v-model:value="requestTaskForm.frequencyNumber" 
                  :min="1" 
                  :max="12" 
                  style="width: 100%;" 
                  placeholder="Number"
                />
              </a-col>
              <a-col :span="16">
                <a-select v-model:value="requestTaskForm.frequency" placeholder="Select frequency" style="width: 100%;">
                  <a-select-option value="daily">Day(s)</a-select-option>
                  <a-select-option value="weekly">Week(s)</a-select-option>
                  <a-select-option value="monthly">Month(s)</a-select-option>
                </a-select>
              </a-col>
            </a-row>
          </a-form-item>
          
          <!-- Daily options -->
          <div v-if="requestTaskForm.frequency === 'daily'">
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Weekly options -->
          <div v-if="requestTaskForm.frequency === 'weekly'">
            <a-form-item label="Day of Week" required>
              <a-select v-model:value="requestTaskForm.dayOfWeek" placeholder="Select day" style="width: 100%;">
                <a-select-option value="monday">Monday</a-select-option>
                <a-select-option value="tuesday">Tuesday</a-select-option>
                <a-select-option value="wednesday">Wednesday</a-select-option>
                <a-select-option value="thursday">Thursday</a-select-option>
                <a-select-option value="friday">Friday</a-select-option>
                <a-select-option value="saturday">Saturday</a-select-option>
                <a-select-option value="sunday">Sunday</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <!-- Monthly options -->
          <div v-if="requestTaskForm.frequency === 'monthly'">
            <a-form-item label="Day of Month" required>
              <a-input-number v-model:value="requestTaskForm.dayOfMonth" :min="1" :max="31" style="width: 100%;" placeholder="Enter day (1-31)" />
            </a-form-item>
            <a-form-item label="Time of Day" required>
              <a-time-picker v-model:value="requestTaskForm.timeOfDay" format="HH:mm" style="width: 100%;" placeholder="Select time" />
            </a-form-item>
          </div>
          
          <a-form-item label="Start Date" required>
            <a-date-picker v-model:value="requestTaskForm.startDate" style="width: 100%;" placeholder="Select start date" />
          </a-form-item>
          <a-form-item label="End Date (Optional)">
            <a-date-picker v-model:value="requestTaskForm.endDate" style="width: 100%;" placeholder="Select end date (leave empty for no end)" />
          </a-form-item>
        </div>
        
        <a-form-item label="Reason for Request" required>
          <a-textarea v-model:value="requestTaskForm.reason" placeholder="Explain why this task adjustment is needed" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- POA Task Confirmation Modal（仅 POA 渲染） -->
    <a-modal
      v-if="isPOA"
      v-model:open="taskConfirmModalVisible"
      :title="confirmModalTitle"
      width="400px"
      @ok="handleTaskConfirmation"
      @cancel="taskConfirmModalVisible = false"
    >
      <p>{{ confirmModalMessage }}</p>
      <a-form-item label="Reason (Optional)">
        <a-textarea v-model:value="confirmReason" placeholder="Enter reason for approval/rejection" :rows="2" />
      </a-form-item>
    </a-modal>

    <!-- Request Details Modal -->
    <a-modal
      v-model:open="requestDetailsModalVisible"
      title="Request Details"
      width="700px"
      :footer="null"
    >
      <div v-if="selectedRequest">
        <a-descriptions :column="1" bordered>
          <a-descriptions-item label="Requester">
            {{ selectedRequest.requester }}
          </a-descriptions-item>
          <a-descriptions-item label="Task Title">
            {{ selectedRequest.taskTitle }}
          </a-descriptions-item>
          <a-descriptions-item label="Request Type">
            <a-tag :color="getRequestTypeColor(selectedRequest.requestType)">
              {{ getRequestTypeText(selectedRequest.requestType) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring'" label="Frequency">
            <a-tag :color="getFrequencyColor(selectedRequest.frequency)">
              {{ getFrequencyText(selectedRequest.frequency, selectedRequest.frequencyNumber) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.timeOfDay" label="Time">
            {{ selectedRequest.timeOfDay }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.dayOfWeek" label="Day of Week">
            {{ selectedRequest.dayOfWeek }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.dayOfMonth" label="Day of Month">
            {{ selectedRequest.dayOfMonth }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.startDate" label="Start Date">
            {{ selectedRequest.startDate }}
          </a-descriptions-item>
          <a-descriptions-item v-if="selectedRequest.requestType === 'recurring' && selectedRequest.endDate" label="End Date">
            {{ selectedRequest.endDate }}
          </a-descriptions-item>
          <a-descriptions-item label="Reason">
            {{ selectedRequest.reason }}
          </a-descriptions-item>
          <a-descriptions-item label="Status">
            <a-tag :color="getRequestStatusColor(selectedRequest.status)">
              {{ selectedRequest.status }}
            </a-tag>
          </a-descriptions-item>
        </a-descriptions>
        
        <div style="margin-top: 20px; text-align: right;">
          <a-space>
            <a-button @click="requestDetailsModalVisible = false">
              Close
            </a-button>
            <a-button 
              v-if="selectedRequest.status === 'Pending'"
              type="primary" 
              @click="approveFromDetails"
            >
              Approve
            </a-button>
            <a-button 
              v-if="selectedRequest.status === 'Pending'"
              danger 
              @click="rejectFromDetails"
            >
              Reject
            </a-button>
          </a-space>
        </div>
      </div>
    </a-modal>

    <!-- Request Confirmation Modal -->
    <a-modal
      v-model:open="requestConfirmModalVisible"
      :title="requestConfirmModalTitle"
      width="400px"
      @ok="handleRequestConfirmation"
      @cancel="requestConfirmModalVisible = false"
    >
      <p>{{ requestConfirmModalMessage }}</p>
      <a-form-item label="Reason (Optional)">
        <a-textarea v-model:value="requestConfirmReason" placeholder="Enter reason for approval/rejection" :rows="2" />
      </a-form-item>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import { CheckSquareOutlined, PlusOutlined } from '@ant-design/icons-vue'
import { getMe } from '../services/userService'

// Reactive data
const selectedDate = ref(null)
const createTaskModalVisible = ref(false)
const editTaskModalVisible = ref(false)
const viewAssignmentModalVisible = ref(false)
const selectedAssignment = ref(null)
const currentUser = ref(null)
const requestTaskModalVisible = ref(false)
const taskConfirmModalVisible = ref(false)
const confirmModalTitle = ref('')
const confirmModalMessage = ref('')
const confirmReason = ref('')
const pendingTaskAction = ref(null)
const createRecurringTaskModalVisible = ref(false)
const editRecurringTaskModalVisible = ref(false)

// Request confirmation modal
const requestConfirmModalVisible = ref(false)
const requestConfirmModalTitle = ref('')
const requestConfirmModalMessage = ref('')
const requestConfirmReason = ref('')
const pendingRequestAction = ref(null)

// Request details modal
const requestDetailsModalVisible = ref(false)
const selectedRequest = ref(null)

// Computed properties
const isManager = computed(() => currentUser.value?.role === 'manager')
const isPOA = computed(() => currentUser.value?.role === 'poa')
const isWorker = computed(() => currentUser.value?.role === 'worker')

// Forms
const createTaskForm = ref({
  title: '',
  description: '',
  assignedTo: '',
  priority: '',
  dueDate: null
})

const editTaskForm = ref({
  id: null,
  title: '',
  description: '',
  assignedTo: '',
  priority: '',
  status: '',
  dueDate: null
})

const requestTaskForm = ref({
  requestType: '',
  title: '',
  description: '',
  priority: '',
  reason: '',
  // Recurring task fields
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  startDate: null,
  endDate: null
})

const createRecurringTaskForm = ref({
  title: '',
  description: '',
  assignedTo: '',
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  startDate: null,
  endDate: null
})

const editRecurringTaskForm = ref({
  id: null,
  title: '',
  description: '',
  assignedTo: '',
  frequency: '',
  frequencyNumber: 1,
  timeOfDay: null,
  dayOfWeek: '',
  dayOfMonth: null,
  startDate: null,
  endDate: null,
  isActive: true
})

// Mock data
const availableWorkers = ref([
  { id: 1, name: 'A', workerId: 'W001' },
  { id: 2, name: 'B', workerId: 'W002' },
  { id: 3, name: 'C', workerId: 'W003' },
  { id: 4, name: 'D', workerId: 'W004' },
  { id: 5, name: 'E', workerId: 'W005' },
  { id: 6, name: 'F', workerId: 'W006' },
  { id: 7, name: 'G', workerId: 'W007' },
  { id: 8, name: 'H', workerId: 'W008' }
])

const todayTasks = ref([
  {
    id: 1,
    title: 'Morning Medication',
    description: 'Administer morning medication to patient',
    assignedTo: 'A',
    priority: 'normal',
    dueDate: '2025-09-21',
    status: 'Worker Completed'
  },
  {
    id: 2,
    title: 'Physical Therapy',
    description: 'Assist with physical therapy exercises',
    assignedTo: 'B',
    priority: 'urgent',
    dueDate: '2025-09-21',
    status: 'In Progress'
  },
  {
    id: 3,
    title: 'Emergency Response',
    description: 'Monitor patient condition closely',
    assignedTo: 'A',
    priority: 'very-urgent',
    dueDate: '2025-09-21',
    status: 'Worker Completed'
  },
  {
    id: 4,
    title: 'Evening Care',
    description: 'Prepare patient for evening routine',
    assignedTo: 'C',
    priority: 'normal',
    dueDate: '2025-09-21',
    status: 'Worker Completed'
  }
])

const workAssignments = ref([
  {
    id: 1,
    worker: 'A',
    tasks: 2,
    status: 'Assigned'
  },
  {
    id: 2,
    worker: 'B',
    tasks: 1,
    status: 'Assigned'
  },
  {
    id: 3,
    worker: 'C',
    tasks: 0,
    status: 'Available'
  }
])

const changeRequests = ref([
  {
    id: 1,
    requester: 'POA',
    taskTitle: 'Monthly Health Check',
    requestType: 'recurring',
    frequency: 'monthly',
    frequencyNumber: 3,
    timeOfDay: '10:00',
    dayOfMonth: 15,
    startDate: '2025-02-01',
    endDate: null,
    reason: 'Patient needs quarterly health assessments',
    status: 'Pending'
  },
  {
    id: 2,
    requester: 'Family Member',
    taskTitle: 'Morning Medication',
    requestType: 'Time Change',
    reason: 'Patient prefers medication at 9 AM instead of 8 AM',
    status: 'Pending'
  }
])

// POA's own requests (for viewing status and manager responses)
const myRequests = ref([
  {
    id: 1,
    taskTitle: 'Morning Medication',
    requestType: 'Time Change',
    reason: 'Patient prefers medication at 9 AM instead of 8 AM',
    status: 'Pending',
    submittedDate: '2025-01-20',
    approvalReason: null,
    rejectionReason: null
  },
  {
    id: 2,
    taskTitle: 'Physical Therapy',
    requestType: 'Add New Task',
    reason: 'Patient needs additional physical therapy sessions',
    status: 'Approved',
    submittedDate: '2025-01-18',
    approvalReason: 'Approved. Will add 2 additional sessions per week.',
    rejectionReason: null
  },
  {
    id: 3,
    taskTitle: 'Monthly Health Check',
    requestType: 'recurring',
    frequency: 'monthly',
    frequencyNumber: 3,
    reason: 'Patient needs quarterly health assessments',
    status: 'Pending',
    submittedDate: '2025-01-19',
    approvalReason: null,
    rejectionReason: null
  },
  {
    id: 4,
    taskTitle: 'Evening Care',
    requestType: 'Remove Task',
    reason: 'Patient no longer needs evening care assistance',
    status: 'Rejected',
    submittedDate: '2025-01-15',
    approvalReason: null,
    rejectionReason: 'Cannot remove evening care as patient still requires assistance with bedtime routine.'
  }
])

const recurringTasks = ref([
  {
    id: 1,
    title: 'Morning Medication',
    description: 'Administer morning medication to patient',
    assignedTo: 'A',
    frequency: 'daily',
    frequencyNumber: 1,
    timeOfDay: '08:00',
    startDate: '2025-01-01',
    endDate: null,
    isActive: true,
    createdAt: '2025-01-01'
  },
  {
    id: 2,
    title: 'Physical Therapy',
    description: 'Assist with physical therapy exercises',
    assignedTo: 'B',
    frequency: 'weekly',
    frequencyNumber: 2,
    dayOfWeek: 'monday',
    timeOfDay: '14:00',
    startDate: '2025-01-01',
    endDate: null,
    isActive: true,
    createdAt: '2025-01-01'
  },
  {
    id: 3,
    title: 'Monthly Health Check',
    description: 'Conduct monthly health assessment',
    assignedTo: 'A',
    frequency: 'monthly',
    frequencyNumber: 6,
    dayOfMonth: 15,
    timeOfDay: '10:00',
    startDate: '2025-01-01',
    endDate: null,
    isActive: true,
    createdAt: '2025-01-01'
  }
])

// Table columns
const taskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Assigned To', dataIndex: 'assignedTo', key: 'assignedTo' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const simpleTaskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Description', dataIndex: 'description', key: 'description' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const assignmentColumns = [
  { title: 'Worker', dataIndex: 'worker', key: 'worker' },
  { title: 'Tasks', dataIndex: 'tasks', key: 'tasks' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const requestColumns = [
  { title: 'Requester', dataIndex: 'requester', key: 'requester' },
  { title: 'Task', dataIndex: 'taskTitle', key: 'taskTitle' },
  { title: 'Request Type', dataIndex: 'requestType', key: 'requestType' },
  { title: 'Frequency', key: 'frequency', customRender: ({ record }) => {
    if (record.requestType === 'recurring' && record.frequency) {
      return getFrequencyText(record.frequency, record.frequencyNumber)
    }
    return '-'
  }},
  { title: 'Reason', dataIndex: 'reason', key: 'reason' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 120 }
]

const myRequestsColumns = [
  { title: 'Task', dataIndex: 'taskTitle', key: 'taskTitle' },
  { title: 'Request Type', dataIndex: 'requestType', key: 'requestType' },
  { title: 'Frequency', key: 'frequency', customRender: ({ record }) => {
    if (record.requestType === 'recurring' && record.frequency) {
      return getFrequencyText(record.frequency, record.frequencyNumber)
    }
    return '-'
  }},
  { title: 'Reason', dataIndex: 'reason', key: 'reason' },
  { title: 'Submitted Date', dataIndex: 'submittedDate', key: 'submittedDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Manager Response', key: 'managerResponse', width: 200 }
]

const taskDetailColumns = [
  { title: 'Task Title', dataIndex: 'title', key: 'title' },
  { title: 'Description', dataIndex: 'description', key: 'description' },
  { title: 'Priority', dataIndex: 'priority', key: 'priority' },
  { title: 'Due Date', dataIndex: 'dueDate', key: 'dueDate' },
  { title: 'Status', dataIndex: 'status', key: 'status' }
]

const recurringTaskColumns = [
  { title: 'Title', dataIndex: 'title', key: 'title' },
  { title: 'Assigned To', dataIndex: 'assignedTo', key: 'assignedTo' },
  { title: 'Frequency', dataIndex: 'frequency', key: 'frequency' },
  { title: 'Schedule', key: 'schedule', customRender: ({ record }) => {
    if (record.frequency === 'daily') {
      return `Daily at ${record.timeOfDay}`
    } else if (record.frequency === 'weekly') {
      return `${record.dayOfWeek} at ${record.timeOfDay}`
    } else if (record.frequency === 'monthly') {
      return `Day ${record.dayOfMonth} at ${record.timeOfDay}`
    }
    return '-'
  }},
  { title: 'Status', dataIndex: 'status', key: 'status' },
  { title: 'Actions', key: 'actions', width: 200 }
]

// Methods
const onDateChange = (date) => {
  selectedDate.value = date
  // Update tasks based on selected date
  updateTasksForDate(date)
}

const updateTasksForDate = (date) => {
  // Filter tasks for selected date
  const dateStr = date.format('YYYY-MM-DD')
  // In real app, this would fetch from API
  console.log('Loading tasks for date:', dateStr)
  
  // Generate tasks from recurring templates for the selected date
  generateTasksFromTemplates(date)
}

const showCreateTaskModal = () => {
  createTaskForm.value = {
    title: '',
    description: '',
    assignedTo: '',
    priority: '',
    dueDate: selectedDate.value || dayjs()
  }
  createTaskModalVisible.value = true
}

const confirmCreateTask = async () => {
  if (!createTaskForm.value.title || !createTaskForm.value.assignedTo || !createTaskForm.value.priority) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Creating task...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newTask = {
      id: Date.now(),
      title: createTaskForm.value.title,
      description: createTaskForm.value.description,
      assignedTo: createTaskForm.value.assignedTo,
      priority: createTaskForm.value.priority,
      dueDate: createTaskForm.value.dueDate.format('YYYY-MM-DD'),
      status: 'Pending'
    }
    
    todayTasks.value.push(newTask)
    message.destroy()
    message.success('Task created successfully!')
    createTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to create task')
  }
}

const editTask = (task) => {
  editTaskForm.value = {
    id: task.id,
    title: task.title,
    description: task.description,
    assignedTo: task.assignedTo,
    priority: task.priority,
    status: task.status,
    dueDate: dayjs(task.dueDate)
  }
  editTaskModalVisible.value = true
}

const confirmEditTask = async () => {
  try {
    message.loading('Updating task...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = todayTasks.value.findIndex(t => t.id === editTaskForm.value.id)
    if (taskIndex !== -1) {
      todayTasks.value[taskIndex] = {
        ...todayTasks.value[taskIndex],
        title: editTaskForm.value.title,
        description: editTaskForm.value.description,
        assignedTo: editTaskForm.value.assignedTo,
        priority: editTaskForm.value.priority,
        status: editTaskForm.value.status,
        dueDate: editTaskForm.value.dueDate.format('YYYY-MM-DD')
      }
    }
    
    message.destroy()
    message.success('Task updated successfully!')
    editTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to update task')
  }
}

const completeTask = async (task) => {
  try {
    message.loading('Completing task...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      todayTasks.value[taskIndex].status = 'Completed'
    }
    
    message.destroy()
    message.success('Task completed successfully!')
  } catch (error) {
    message.destroy()
    message.error('Failed to complete task')
  }
}

const deleteTask = async (task) => {
  try {
    message.loading('Deleting task...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      todayTasks.value.splice(taskIndex, 1)
    }
    
    message.destroy()
    message.success('Task deleted successfully!')
  } catch (error) {
    message.destroy()
    message.error('Failed to delete task')
  }
}

const viewAssignment = (assignment) => {
  selectedAssignment.value = assignment
  viewAssignmentModalVisible.value = true
}

const getWorkerTasks = (workerName) => {
  return todayTasks.value.filter(task => task.assignedTo === workerName)
}

const getMyTasks = () => {
  // For POA/Worker, show tasks assigned to them
  // In a real app, this would be based on the current user's ID
  // For now, we'll show all tasks for demonstration
  return todayTasks.value
}

const showRequestTaskModal = () => {
  requestTaskForm.value = {
    requestType: '',
    title: '',
    description: '',
    priority: '',
    reason: '',
    // Recurring task fields
    frequency: '',
    frequencyNumber: 1,
    timeOfDay: null,
    dayOfWeek: '',
    dayOfMonth: null,
    startDate: null,
    endDate: null
  }
  requestTaskModalVisible.value = true
}

const onRequestTypeChange = () => {
  // Reset frequency-specific fields when request type changes
  requestTaskForm.value.frequency = ''
  requestTaskForm.value.frequencyNumber = 1
  requestTaskForm.value.timeOfDay = null
  requestTaskForm.value.dayOfWeek = ''
  requestTaskForm.value.dayOfMonth = null
  requestTaskForm.value.startDate = null
  requestTaskForm.value.endDate = null
}

const confirmRequestTask = async () => {
  try {
    message.loading('Submitting request...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const requestId = Date.now()
    
    // Prepare request data with frequency information if it's a recurring task
    const requestData = {
      id: requestId,
      requester: 'POA',
      taskTitle: requestTaskForm.value.title,
      requestType: requestTaskForm.value.requestType,
      reason: requestTaskForm.value.reason,
      status: 'Pending'
    }
    
    // Add frequency data if it's a recurring task request
    if (requestTaskForm.value.requestType === 'recurring') {
      requestData.frequency = requestTaskForm.value.frequency
      requestData.frequencyNumber = requestTaskForm.value.frequencyNumber
      requestData.timeOfDay = requestTaskForm.value.timeOfDay?.format('HH:mm')
      requestData.dayOfWeek = requestTaskForm.value.dayOfWeek
      requestData.dayOfMonth = requestTaskForm.value.dayOfMonth
      requestData.startDate = requestTaskForm.value.startDate?.format('YYYY-MM-DD')
      requestData.endDate = requestTaskForm.value.endDate?.format('YYYY-MM-DD')
    }
    
    // Add to change requests for manager to review
    const newRequest = { ...requestData }
    
    // Add to POA's own requests for tracking
    const myNewRequest = {
      ...requestData,
      submittedDate: dayjs().format('YYYY-MM-DD'),
      approvalReason: null,
      rejectionReason: null
    }
    
    changeRequests.value.unshift(newRequest)
    myRequests.value.unshift(myNewRequest)
    
    message.destroy()
    message.success('Task request submitted successfully!')
    requestTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to submit request')
  }
}

const showConfirmModal = (task, action) => {
  pendingTaskAction.value = { task, action }
  confirmReason.value = ''
  
  if (action === 'approve') {
    confirmModalTitle.value = 'Approve Task Completion'
    confirmModalMessage.value = `Are you sure you want to approve the completion of task "${task.title}"?`
  } else if (action === 'reject') {
    confirmModalTitle.value = 'Reject Task Completion'
    confirmModalMessage.value = `Are you sure you want to reject the completion of task "${task.title}"? The worker will need to redo the task.`
  }
  
  taskConfirmModalVisible.value = true
}

const handleTaskConfirmation = async () => {
  if (!pendingTaskAction.value) return
  
  const { task, action } = pendingTaskAction.value
  
  try {
    if (action === 'approve') {
      message.loading('Approving task completion...', 0)
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex].status = 'Completed'
        if (confirmReason.value) {
          todayTasks.value[taskIndex].approvalReason = confirmReason.value
        }
      }
      
      message.destroy()
      message.success('Task completion approved!')
    } else if (action === 'reject') {
      message.loading('Rejecting task completion...', 0)
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
      if (taskIndex !== -1) {
        todayTasks.value[taskIndex].status = 'Rejected'
        if (confirmReason.value) {
          todayTasks.value[taskIndex].rejectionReason = confirmReason.value
        }
      }
      
      message.destroy()
      message.success('Task completion rejected! Worker will need to redo the task.')
    }
    
    taskConfirmModalVisible.value = false
    pendingTaskAction.value = null
  } catch (error) {
    message.destroy()
    message.error(`Failed to ${action} task completion`)
  }
}

// Request confirmation methods
const showRequestConfirmModal = (request, action) => {
  pendingRequestAction.value = { request, action }
  requestConfirmReason.value = ''
  
  if (action === 'approve') {
    requestConfirmModalTitle.value = 'Approve Request'
    requestConfirmModalMessage.value = `Are you sure you want to approve the request "${request.taskTitle}" from ${request.requester}?`
  } else if (action === 'reject') {
    requestConfirmModalTitle.value = 'Reject Request'
    requestConfirmModalMessage.value = `Are you sure you want to reject the request "${request.taskTitle}" from ${request.requester}?`
  }
  
  requestConfirmModalVisible.value = true
}

const viewRequestDetails = (request) => {
  selectedRequest.value = request
  requestDetailsModalVisible.value = true
}

const approveFromDetails = () => {
  requestDetailsModalVisible.value = false
  showRequestConfirmModal(selectedRequest.value, 'approve')
}

const rejectFromDetails = () => {
  requestDetailsModalVisible.value = false
  showRequestConfirmModal(selectedRequest.value, 'reject')
}

const handleRequestConfirmation = async () => {
  const { request, action } = pendingRequestAction.value
  
  try {
    message.loading(`${action === 'approve' ? 'Approving' : 'Rejecting'} request...`, 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const requestIndex = changeRequests.value.findIndex(r => r.id === request.id)
    if (requestIndex !== -1) {
      if (action === 'approve') {
        changeRequests.value[requestIndex].status = 'Approved'
        if (requestConfirmReason.value) {
          changeRequests.value[requestIndex].approvalReason = requestConfirmReason.value
        }
        
        // If it's a recurring task request, create the recurring task template
        if (request.requestType === 'recurring') {
          const newRecurringTask = {
            id: Date.now(),
            title: request.taskTitle,
            description: request.reason,
            assignedTo: 'A', // Default assignment, in real app this would be determined by manager
            frequency: request.frequency,
            frequencyNumber: request.frequencyNumber,
            timeOfDay: request.timeOfDay,
            dayOfWeek: request.dayOfWeek,
            dayOfMonth: request.dayOfMonth,
            startDate: request.startDate,
            endDate: request.endDate,
            isActive: true,
            createdAt: dayjs().format('YYYY-MM-DD')
          }
          recurringTasks.value.push(newRecurringTask)
        }
      } else if (action === 'reject') {
        changeRequests.value[requestIndex].status = 'Rejected'
        if (requestConfirmReason.value) {
          changeRequests.value[requestIndex].rejectionReason = requestConfirmReason.value
        }
      }
    }
    
    // Also update POA's request status
    const myRequestIndex = myRequests.value.findIndex(r => r.id === request.id)
    if (myRequestIndex !== -1) {
      if (action === 'approve') {
        myRequests.value[myRequestIndex].status = 'Approved'
        if (requestConfirmReason.value) {
          myRequests.value[myRequestIndex].approvalReason = requestConfirmReason.value
        }
      } else if (action === 'reject') {
        myRequests.value[myRequestIndex].status = 'Rejected'
        if (requestConfirmReason.value) {
          myRequests.value[myRequestIndex].rejectionReason = requestConfirmReason.value
        }
      }
    }
    
    message.destroy()
    message.success(`Request ${action === 'approve' ? 'approved' : 'rejected'} successfully!`)
    requestConfirmModalVisible.value = false
    pendingRequestAction.value = null
    requestConfirmReason.value = ''
    
  } catch (error) {
    message.destroy()
    message.error(`Failed to ${action} request`)
  }
}

const workerCompleteTask = async (task) => {
  try {
    message.loading('Marking task as completed...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = todayTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      todayTasks.value[taskIndex].status = 'Worker Completed'
    }
    
    message.destroy()
    message.success('Task marked as completed! Waiting for POA review.')
  } catch (error) {
    message.destroy()
    message.error('Failed to complete task')
  }
}

// Recurring task methods
const showCreateRecurringTaskModal = () => {
  createRecurringTaskForm.value = {
    title: '',
    description: '',
    assignedTo: '',
    frequency: '',
    frequencyNumber: 1,
    timeOfDay: null,
    dayOfWeek: '',
    dayOfMonth: null,
    startDate: dayjs(),
    endDate: null
  }
  createRecurringTaskModalVisible.value = true
}

const onFrequencyChange = () => {
  // Reset frequency-specific fields when frequency changes
  createRecurringTaskForm.value.dayOfWeek = ''
  createRecurringTaskForm.value.dayOfMonth = null
  createRecurringTaskForm.value.timeOfDay = null
}

const onEditFrequencyChange = () => {
  // Reset frequency-specific fields when frequency changes in edit modal
  editRecurringTaskForm.value.dayOfWeek = ''
  editRecurringTaskForm.value.dayOfMonth = null
  editRecurringTaskForm.value.timeOfDay = null
}

const confirmCreateRecurringTask = async () => {
  if (!createRecurringTaskForm.value.title || !createRecurringTaskForm.value.assignedTo || 
      !createRecurringTaskForm.value.frequency || !createRecurringTaskForm.value.startDate) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Creating recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const newRecurringTask = {
      id: Date.now(),
      title: createRecurringTaskForm.value.title,
      description: createRecurringTaskForm.value.description,
      assignedTo: createRecurringTaskForm.value.assignedTo,
      frequency: createRecurringTaskForm.value.frequency,
      frequencyNumber: createRecurringTaskForm.value.frequencyNumber,
      timeOfDay: createRecurringTaskForm.value.timeOfDay?.format('HH:mm'),
      dayOfWeek: createRecurringTaskForm.value.dayOfWeek,
      dayOfMonth: createRecurringTaskForm.value.dayOfMonth,
      startDate: createRecurringTaskForm.value.startDate.format('YYYY-MM-DD'),
      endDate: createRecurringTaskForm.value.endDate?.format('YYYY-MM-DD'),
      isActive: true,
      createdAt: dayjs().format('YYYY-MM-DD')
    }
    
    recurringTasks.value.push(newRecurringTask)
    
    // Generate tasks for the current date if applicable
    generateTasksFromTemplates(selectedDate.value)
    
    message.destroy()
    message.success('Recurring task template created successfully!')
    createRecurringTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to create recurring task template')
  }
}

const editRecurringTask = (task) => {
  editRecurringTaskForm.value = {
    id: task.id,
    title: task.title,
    description: task.description,
    assignedTo: task.assignedTo,
    frequency: task.frequency,
    frequencyNumber: task.frequencyNumber || 1,
    timeOfDay: task.timeOfDay ? dayjs(task.timeOfDay, 'HH:mm') : null,
    dayOfWeek: task.dayOfWeek,
    dayOfMonth: task.dayOfMonth,
    startDate: dayjs(task.startDate),
    endDate: task.endDate ? dayjs(task.endDate) : null,
    isActive: task.isActive
  }
  editRecurringTaskModalVisible.value = true
}

const confirmEditRecurringTask = async () => {
  if (!editRecurringTaskForm.value.title || !editRecurringTaskForm.value.assignedTo || 
      !editRecurringTaskForm.value.frequency || !editRecurringTaskForm.value.startDate) {
    message.error('Please fill in all required fields')
    return
  }
  
  try {
    message.loading('Updating recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === editRecurringTaskForm.value.id)
    if (taskIndex !== -1) {
      recurringTasks.value[taskIndex] = {
        ...recurringTasks.value[taskIndex],
        title: editRecurringTaskForm.value.title,
        description: editRecurringTaskForm.value.description,
        assignedTo: editRecurringTaskForm.value.assignedTo,
        frequency: editRecurringTaskForm.value.frequency,
        frequencyNumber: editRecurringTaskForm.value.frequencyNumber,
        timeOfDay: editRecurringTaskForm.value.timeOfDay?.format('HH:mm'),
        dayOfWeek: editRecurringTaskForm.value.dayOfWeek,
        dayOfMonth: editRecurringTaskForm.value.dayOfMonth,
        startDate: editRecurringTaskForm.value.startDate.format('YYYY-MM-DD'),
        endDate: editRecurringTaskForm.value.endDate?.format('YYYY-MM-DD'),
        isActive: editRecurringTaskForm.value.isActive
      }
    }
    
    message.destroy()
    message.success('Recurring task template updated successfully!')
    editRecurringTaskModalVisible.value = false
  } catch (error) {
    message.destroy()
    message.error('Failed to update recurring task template')
  }
}

const toggleRecurringTask = async (task) => {
  try {
    message.loading(`${task.isActive ? 'Deactivating' : 'Activating'} recurring task...`, 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      recurringTasks.value[taskIndex].isActive = !recurringTasks.value[taskIndex].isActive
    }
    
    message.destroy()
    message.success(`Recurring task ${task.isActive ? 'deactivated' : 'activated'} successfully!`)
  } catch (error) {
    message.destroy()
    message.error('Failed to update recurring task status')
  }
}

const deleteRecurringTask = async (task) => {
  try {
    message.loading('Deleting recurring task template...', 0)
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    const taskIndex = recurringTasks.value.findIndex(t => t.id === task.id)
    if (taskIndex !== -1) {
      recurringTasks.value.splice(taskIndex, 1)
    }
    
    message.destroy()
    message.success('Recurring task template deleted successfully!')
  } catch (error) {
    message.destroy()
    message.error('Failed to delete recurring task template')
  }
}

// Generate tasks from recurring templates for a specific date
const generateTasksFromTemplates = (date) => {
  const dateStr = date.format('YYYY-MM-DD')
  const dayOfWeek = date.format('dddd').toLowerCase()
  const dayOfMonth = date.date()
  
  recurringTasks.value.forEach(template => {
    if (!template.isActive) return
    
    const startDate = dayjs(template.startDate)
    const endDate = template.endDate ? dayjs(template.endDate) : null
    
    // Check if date is within range
    if (date.isBefore(startDate)) return
    if (endDate && date.isAfter(endDate)) return
    
    let shouldGenerate = false
    
    // Check if task should be generated for this date
    if (template.frequency === 'daily') {
      shouldGenerate = true
    } else if (template.frequency === 'weekly' && template.dayOfWeek === dayOfWeek) {
      shouldGenerate = true
    } else if (template.frequency === 'monthly' && template.dayOfMonth === dayOfMonth) {
      shouldGenerate = true
    }
    
    if (shouldGenerate) {
      // Check if task already exists for this date
      const existingTask = todayTasks.value.find(task => 
        task.title === template.title && 
        task.assignedTo === template.assignedTo &&
        task.dueDate === dateStr
      )
      
      if (!existingTask) {
        const newTask = {
          id: Date.now() + Math.random(),
          title: template.title,
          description: template.description,
          assignedTo: template.assignedTo,
          priority: 'normal', // Default priority for recurring tasks
          dueDate: dateStr,
          status: 'Pending',
          isRecurring: true,
          recurringTemplateId: template.id
        }
        
        todayTasks.value.push(newTask)
      }
    }
  })
}


// Helper functions
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
    case 'Assigned': return 'green'
    case 'Available': return 'blue'
    default: return 'default'
  }
}

const getRequestStatusColor = (status) => {
  switch (status) {
    case 'Pending': return 'orange'
    case 'Approved': return 'green'
    case 'Rejected': return 'red'
    default: return 'default'
  }
}

const getRequestTypeColor = (type) => {
  switch (type) {
    case 'new': return 'blue'
    case 'recurring': return 'purple'
    case 'modify': return 'orange'
    case 'remove': return 'red'
    case 'reschedule': return 'cyan'
    case 'Time Change': return 'orange'
    default: return 'default'
  }
}

const getRequestTypeText = (type) => {
  switch (type) {
    case 'new': return 'Add New Task'
    case 'recurring': return 'Add Recurring Task'
    case 'modify': return 'Edit Task'
    case 'remove': return 'Remove Task'
    case 'reschedule': return 'Reschedule Task'
    case 'Time Change': return 'Time Change'
    default: return type
  }
}

const getFrequencyColor = (frequency) => {
  switch (frequency) {
    case 'daily': return 'blue'
    case 'weekly': return 'green'
    case 'monthly': return 'orange'
    default: return 'default'
  }
}

const getFrequencyText = (frequency, frequencyNumber = 1) => {
  const number = frequencyNumber || 1
  switch (frequency) {
    case 'daily': return `${number} Day${number > 1 ? 's' : ''}`
    case 'weekly': return `${number} Week${number > 1 ? 's' : ''}`
    case 'monthly': return `${number} Month${number > 1 ? 's' : ''}`
    default: return frequency
  }
}

// Initialize
onMounted(async () => {
  selectedDate.value = dayjs()
  updateTasksForDate(selectedDate.value)
  
  // Get current user information
  try {
    const response = await getMe()
    currentUser.value = response.data
  } catch (error) {
    console.error('Failed to get user info:', error)
  }
})
</script>

<style scoped>
.tasks-page {
  padding: 24px;
}

.ant-card {
  margin-bottom: 16px;
}
</style>
