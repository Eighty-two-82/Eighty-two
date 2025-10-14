# API 测试用例文档

## 访问 Swagger UI
启动后端服务后，访问：
```
http://localhost:8080/swagger-ui.html
```

---

## 1. 日常排班管理功能测试

### 1.1 批量创建排班
**接口**: `POST /api/schedules/batch-create`

**请求头**:
```json
X-Organization-Id: org-001
X-User-Id: manager-001
```

**请求体**:
```json
{
  "scheduleDate": "2025-01-20",
  "morningShiftWorkerIds": ["W001", "W002"],
  "eveningShiftWorkerIds": ["W003", "W004"],
  "scheduleNotes": "正常排班"
}
```

**预期结果**: 返回创建的4个排班记录

---

### 1.2 批量更新排班状态
**接口**: `PUT /api/schedules/batch-update-status`

**请求体**:
```json
{
  "scheduleIds": ["schedule_id_1", "schedule_id_2"],
  "status": "confirmed"
}
```

**预期结果**: 返回更新后的排班列表

---

### 1.3 复制排班
**接口**: `POST /api/schedules/copy`

**请求头**:
```json
X-Organization-Id: org-001
X-User-Id: manager-001
```

**请求体**:
```json
{
  "sourceDate": "2025-01-20",
  "targetDate": "2025-01-21",
  "organizationId": "org-001",
  "managerId": "manager-001"
}
```

**预期结果**: 将2025-01-20的排班复制到2025-01-21

---

### 1.4 获取周排班
**接口**: `GET /api/schedules/weekly?startDate=2025-01-20&organizationId=org-001`

**预期结果**: 返回从2025-01-20开始的一周排班

---

### 1.5 验证排班冲突
**接口**: `GET /api/schedules/validate?workerId=W001&date=2025-01-20&shiftType=morning`

**预期结果**: 返回true（无冲突）或false（有冲突）

---

### 1.6 删除指定日期的所有排班
**接口**: `DELETE /api/schedules/date/2025-01-20?organizationId=org-001`

**预期结果**: 返回删除的排班数量

---

## 2. 员工照片上传功能测试

### 2.1 简化上传照片接口
**接口**: `POST /api/workers/{workerId}/photo`

**路径参数**: `workerId = W001`

**请求体**:
```json
{
  "photoUrl": "https://example.com/photos/worker1.jpg"
}
```

**预期结果**: 返回更新后的员工信息，包含照片URL

---

### 2.2 批量上传照片
**接口**: `POST /api/workers/batch-upload-photos`

**请求体**:
```json
{
  "W001": "https://example.com/photos/worker1.jpg",
  "W002": "https://example.com/photos/worker2.jpg",
  "W003": "https://example.com/photos/worker3.jpg"
}
```

**预期结果**: 返回3个更新后的员工信息

---

### 2.3 删除员工照片
**接口**: `DELETE /api/workers/W001/photo`

**预期结果**: 返回更新后的员工信息，photoUrl为null

---

### 2.4 获取没有照片的员工
**接口**: `GET /api/workers/organization/org-001/without-photos`

**预期结果**: 返回没有上传照片的员工列表

---

## 3. 通知功能测试

### 3.1 获取我的通知
**接口**: `GET /api/notifications/my`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回当前用户的所有通知

---

### 3.2 获取未读通知
**接口**: `GET /api/notifications/unread`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回未读通知列表

---

### 3.3 获取未读通知数量
**接口**: `GET /api/notifications/unread/count`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回未读通知数量

---

### 3.4 标记通知为已读
**接口**: `PUT /api/notifications/{notificationId}/read`

**预期结果**: 返回更新后的通知，isRead=true

---

### 3.5 标记所有通知为已读
**接口**: `PUT /api/notifications/read-all`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回标记为已读的通知数量

---

### 3.6 创建任务分配通知
**接口**: `POST /api/notifications/task-assigned`

**请求体**:
```json
{
  "workerId": "W001",
  "taskId": "task-001",
  "taskTitle": "协助病人用餐",
  "assignedBy": "manager-001"
}
```

**预期结果**: 创建一个任务分配通知

---

### 3.7 创建任务完成通知
**接口**: `POST /api/notifications/task-completed`

**请求体**:
```json
{
  "managerId": "manager-001",
  "taskId": "task-001",
  "taskTitle": "协助病人用餐",
  "completedBy": "W001"
}
```

**预期结果**: 创建一个任务完成通知

---

### 3.8 创建排班更新通知
**接口**: `POST /api/notifications/schedule-updated`

**请求体**:
```json
{
  "workerId": "W001",
  "scheduleDate": "2025-01-20",
  "shiftType": "morning",
  "updatedBy": "manager-001"
}
```

**预期结果**: 创建一个排班更新通知

---

### 3.9 删除通知
**接口**: `DELETE /api/notifications/{notificationId}`

**预期结果**: 通知被删除

---

### 3.10 按类别获取通知
**接口**: `GET /api/notifications/category/task`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回任务类别的通知

---

## 4. 通信功能测试

### 4.1 发送消息
**接口**: `POST /api/messages`

**请求头**:
```json
X-User-Id: user-001
X-Organization-Id: org-001
```

**请求体**:
```json
{
  "subject": "关于病人P1的护理计划",
  "content": "您好，我想讨论一下P1的护理计划调整。",
  "toUserId": "user-002",
  "toUserName": "张医生",
  "fromUserName": "李护士",
  "category": "general"
}
```

**预期结果**: 消息发送成功，同时创建通知给接收者

---

### 4.2 获取收件箱
**接口**: `GET /api/messages/inbox`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回用户接收的所有消息

---

### 4.3 获取发件箱
**接口**: `GET /api/messages/sent`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回用户发送的所有消息

---

### 4.4 获取未读消息
**接口**: `GET /api/messages/unread`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回未读消息列表

---

### 4.5 获取未读消息数量
**接口**: `GET /api/messages/unread/count`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回未读消息数量

---

### 4.6 回复消息
**接口**: `POST /api/messages/{messageId}/reply`

**请求头**:
```json
X-User-Id: user-002
X-Organization-Id: org-001
```

**请求体**:
```json
{
  "content": "好的，我们可以在明天上午10点讨论这个问题。",
  "fromUserName": "张医生"
}
```

**预期结果**: 回复发送成功，subject自动添加"Re:"前缀

---

### 4.7 标记消息为已读
**接口**: `PUT /api/messages/{messageId}/read`

**预期结果**: 消息标记为已读，status更新为"read"

---

### 4.8 获取消息详情
**接口**: `GET /api/messages/{messageId}`

**预期结果**: 返回完整的消息信息

---

### 4.9 归档消息
**接口**: `PUT /api/messages/{messageId}/archive`

**预期结果**: 消息状态更新为"archived"

---

### 4.10 删除消息
**接口**: `DELETE /api/messages/{messageId}`

**预期结果**: 消息标记为删除（软删除）

---

### 4.11 永久删除消息
**接口**: `DELETE /api/messages/{messageId}/permanent`

**预期结果**: 消息从数据库中永久删除

---

### 4.12 获取对话记录
**接口**: `GET /api/messages/conversation/user-002`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回user-001和user-002之间的所有消息

---

### 4.13 获取消息的回复
**接口**: `GET /api/messages/{messageId}/replies`

**预期结果**: 返回该消息的所有回复

---

### 4.14 按类别获取消息
**接口**: `GET /api/messages/category/urgent`

**请求头**:
```json
X-User-Id: user-001
```

**预期结果**: 返回紧急类别的消息

---

## 5. 测试流程建议

### 5.1 排班管理完整流程
1. 创建员工（如果还没有）
2. 批量创建排班 → 验证排班冲突
3. 获取周排班查看结果
4. 批量更新排班状态
5. 复制排班到下一天
6. 删除指定日期排班

### 5.2 员工照片管理完整流程
1. 获取没有照片的员工列表
2. 为单个员工上传照片
3. 批量上传多个员工照片
4. 验证照片已上传
5. 删除某个员工的照片

### 5.3 通知功能完整流程
1. 创建任务分配通知
2. 获取未读通知数量
3. 获取未读通知列表
4. 标记单个通知为已读
5. 创建其他类型通知（排班更新、任务完成等）
6. 按类别查询通知
7. 标记所有通知为已读
8. 删除通知

### 5.4 通信功能完整流程
1. 用户A发送消息给用户B
2. 用户B获取未读消息数量
3. 用户B查看收件箱
4. 用户B阅读消息（标记为已读）
5. 用户B回复消息
6. 用户A获取对话记录
7. 查看消息的所有回复
8. 归档或删除消息

---

## 6. 常见测试场景

### 场景1: 管理员创建一周排班
```
1. POST /api/schedules/batch-create (创建周一排班)
2. POST /api/schedules/copy (复制到周二)
3. POST /api/schedules/copy (复制到周三)
4. ... 重复到周日
5. GET /api/schedules/weekly (查看整周排班)
```

### 场景2: 工作人员收到任务并完成
```
1. Manager: 创建任务 (自动触发任务分配通知)
2. Worker: GET /api/notifications/unread (查看未读通知)
3. Worker: PUT /api/notifications/{id}/read (标记通知为已读)
4. Worker: 完成任务
5. Worker: POST /api/notifications/task-completed (通知管理员)
6. Manager: 收到任务完成通知
```

### 场景3: 用户之间的消息往来
```
1. User A: POST /api/messages (发送消息给User B)
2. User B: GET /api/messages/unread/count (查看未读数量)
3. User B: GET /api/messages/inbox (查看收件箱)
4. User B: PUT /api/messages/{id}/read (标记为已读)
5. User B: POST /api/messages/{id}/reply (回复消息)
6. User A: GET /api/messages/conversation/{userB} (查看对话)
```

---

## 7. 注意事项

1. **请求头**: 大部分接口需要 `X-User-Id` 和/或 `X-Organization-Id` 请求头
2. **日期格式**: 使用 `yyyy-MM-dd` 格式 (如: 2025-01-20)
3. **ID格式**: 员工ID使用 `W001`, `W002` 等格式
4. **默认值**: 如果不提供请求头，系统会使用默认值（如 org-001, default-user-001）
5. **关联功能**: 发送消息时会自动创建通知

---

## 8. 快速测试数据

### 员工ID示例
```
W001, W002, W003, W004, W005
```

### 用户ID示例
```
manager-001, user-001, user-002, worker-001
```

### 组织ID
```
org-001
```

### 日期示例
```
2025-01-20, 2025-01-21, 2025-01-22
```

---

祝测试顺利！🚀



