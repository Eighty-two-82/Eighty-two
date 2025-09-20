<template>
  <div class="tasks-page">
    <!-- 编辑弹窗 -->
    <a-modal 
      v-model:open="editModalOpen" 
      title="Edit Task" 
      @ok="saveTaskEdit" 
      @cancel="cancelEdit"
    >
      <a-form layout="vertical">
        <a-form-item label="Task Name">
          <a-input v-model:value="editTask.name" />
        </a-form-item>
        <a-form-item label="Description">
          <a-input v-model:value="editTask.description" />
        </a-form-item>
        <a-form-item label="Carer">
          <a-input v-model:value="editTask.carer" />
        </a-form-item>
        <a-form-item label="Priority">
          <a-select v-model:value="editTask.priority">
            <a-select-option value="very-urgent">Very Urgent</a-select-option>
            <a-select-option value="urgent">Urgent</a-select-option>
            <a-select-option value="normal">Normal</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-card>
      <template #title>
        <h2>Task Management</h2>
      </template>

      <a-row :gutter="24">
        <!-- 左边日历 -->
        <a-col :span="10">
          <div class="calendar-wrapper">
            <a-calendar 
              v-model:value="value" 
              :fullscreen="false" 
              @select="onDateSelect"
            />
          </div>
        </a-col>

        <!-- 右边任务列表 -->
        <a-col :span="14">
          <div class="tasks-info">
            <!-- 顶部按钮 -->
            <div style="display:flex; justify-content:flex-end; margin-bottom:8px;">
              <a-button type="primary" ghost @click="openEditModal()">Apply to edit task</a-button>
            </div>

            <!-- 标题 -->
            <h3>
              Today's Tasks ({{ value?.format('Do MMMM') || 'Select a date' }})
            </h3>

            <!-- 任务列表 -->
            <div 
              v-for="task in sortedTasksForDate" 
              :key="task.id" 
              class="task-card" 
              :class="{ done: task.status === 'done' }"
            >
              <div class="task-left">
                <span class="priority-dot" :class="task.priority"></span>
                <div>
                  <strong>{{ task.name }}</strong>
                  <div class="task-desc">{{ task.description || task.name }}</div>
                  <div class="task-carer">Carer: {{ task.carer }}</div>
                </div>
              </div>
              <div class="task-right">
                <a-button 
                  v-if="task.status !== 'done'" 
                  type="primary" 
                  size="small" 
                  @click="markAsDone(task.id)"
                >
                  Done
                </a-button>
                <a-button 
                  v-else 
                  type="default" 
                  size="small" 
                  disabled
                >
                  ✔ Done
                </a-button>

                <!-- 新增 Edit 按钮 -->
                <a-button 
                  type="default" 
                  size="small" 
                  style="margin-left:8px;"
                  @click="openEditModal(task)"
                >
                  Edit
                </a-button>
              </div>
            </div>

            <!-- legend -->
            <div class="legend">
              <span><span class="priority-dot very-urgent"></span> Very Urgent</span>
              <span><span class="priority-dot urgent"></span> Urgent</span>
              <span><span class="priority-dot normal"></span> Normal</span>
            </div>
          </div>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { Dayjs } from 'dayjs'

const editModalOpen = ref(false)
const editTask = ref<any>({})

const value = ref<Dayjs>()
const tasks = ref([
  { id: 1, date: '2025-08-08', name: 'Medicine Purchase', description: 'Purchase prescription medicine, budget ¥500', carer: 'NAME1', status: 'pending', priority: 'urgent' },
  { id: 2, date: '2025-08-08', name: 'Blood Pressure Check', description: 'Measure and record blood pressure before 9:00 AM', carer: 'NAME2', status: 'pending', priority: 'normal' },
  { id: 3, date: '2025-08-08', name: 'Blood Pressure Check', description: 'Measure and record blood pressure before 9:00 AM', carer: 'NAME3', status: 'done', priority: 'normal' },
  { id: 4, date: '2025-08-09', name: 'Doctor Visit', description: 'Go to hospital for routine check-up at 10:00 AM', carer: 'NAME3', status: 'pending', priority: 'very-urgent' },
  { id: 5, date: '2025-08-09', name: 'Grocery Shopping', description: 'Buy fruits and vegetables for the week', carer: 'NAME1', status: 'pending', priority: 'normal' },
])

const priorityOrder = { 'very-urgent': 3, 'urgent': 2, 'normal': 1 }

const sortedTasksForDate = computed(() => {
  if (!value.value) return []
  return tasks.value
    .filter(t => t.date === value.value.format('YYYY-MM-DD'))
    .sort((a, b) => priorityOrder[b.priority] - priorityOrder[a.priority])
})

const openEditModal = (task?: any) => {
  if (task) {
    // 编辑指定任务
    editTask.value = { ...task }
    editModalOpen.value = true
  } else if (sortedTasksForDate.value.length > 0) {
    // 默认当天第一条任务
    editTask.value = { ...sortedTasksForDate.value[0] }
    editModalOpen.value = true
  } else {
    alert("No task for this date.")
  }
}

const saveTaskEdit = () => {
  const index = tasks.value.findIndex(t => t.id === editTask.value.id)
  if (index !== -1) {
    tasks.value[index] = { ...editTask.value }
  }
  editModalOpen.value = false
}

const cancelEdit = () => {
  editModalOpen.value = false
}

const markAsDone = (id: number) => {
  const task = tasks.value.find(t => t.id === id)
  if (task) task.status = 'done'
}

const onDateSelect = (val: Dayjs) => {
  console.log('Date selected:', val.format('YYYY-MM-DD'))
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
.task-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 10px 14px;
  margin-bottom: 12px;
  background: #fff;
}
.task-card.done {
  background: #f5f5f5;
  text-decoration: none;
  opacity: 0.8;
}
.task-left {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}
.task-desc {
  font-size: 13px;
  color: #666;
}
.task-carer {
  font-size: 12px;
  color: #999;
}
.priority-dot {
  display: inline-block;
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 6px;
}
.priority-dot.very-urgent {
  background: red;
}
.priority-dot.urgent {
  background: orange;
}
.priority-dot.normal {
  background: green;
}
.legend {
  margin-top: 10px;
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #444;
}
</style>
