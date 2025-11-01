# Worker-Manager 绑定显示机制实现记录

## 核心实现要点

### 1. 后端实现

#### Worker 实体
- Worker 实体包含 `managerId` 字段用于存储绑定的 manager ID

#### WorkerService
- `bindWorkerToManager(String workerId, String managerId)`: 绑定 worker 到 manager
  - 查找 worker
  - 设置 managerId
  - 保存更新
- `getWorkersByManagerId(String managerId)`: 获取指定 manager 的所有 workers
  - 查找所有 workers
  - 过滤 managerId 匹配的 workers
  - 返回列表

#### WorkerController
- `POST /api/workers/{id}/bind-manager/{managerId}`: 绑定 endpoint
- `GET /api/workers/manager/{managerId}`: 获取 workers endpoint（**必须在 /{id} 之前，避免路径冲突**）

#### InviteCodeServiceImpl
- 在 `useInviteCode` 方法中，当 WORKER 类型的 invite code 被使用时：
  - 调用 `userService.bindWorkerToManager(usedBy, managerId)` 在 User 实体中绑定
  - 调用 `workerService.bindWorkerToManager(usedBy, managerId)` 在 Worker 实体中绑定
  - 如果 Worker 实体不存在，从 User 实体创建 Worker 实体

### 2. 前端实现

#### workerService.js
```javascript
export async function getWorkersByManagerId(managerId) {
  const response = await api.get(`/workers/manager/${managerId}`);
  // 返回 response.data
}
```

#### WorkerManagement.vue
- `loadWorkers()` 函数中：
  ```javascript
  if (currentUser.value?.role === 'manager' && currentUser.value?.id) {
    response = await getWorkersByManagerId(currentUser.value.id)
  }
  ```
- Manager 角色用户只看到绑定到自己的 workers

### 3. 关键注意事项

1. **路由顺序很重要**：`/manager/{managerId}` 必须在 `/{id}` 之前定义，否则会被路径冲突
2. **双重绑定**：既要在 User 实体中绑定（用于权限），也要在 Worker 实体中绑定（用于 Worker API 查询）
3. **Worker 实体创建**：如果 Worker 实体不存在，在 invite code 使用时需要从 User 实体创建

