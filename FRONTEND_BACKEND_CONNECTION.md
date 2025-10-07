# 前后端连接指南

## 概述
前端已经成功配置为连接真实的后端API，不再使用模拟数据。

## 主要更改

### 1. API服务配置 (`frontend/src/services/api.js`)
- 添加了请求拦截器来自动添加认证token
- 添加了响应拦截器来处理401未授权错误
- 设置了10秒超时和默认headers

### 2. 用户服务 (`frontend/src/services/userService.js`)
- **登录功能**: 连接到 `/api/auth/login-email` 端点
- **注册功能**: 连接到 `/api/auth/register` 端点
- **获取用户信息**: 连接到 `/api/auth/me` 端点
- **邀请码功能**: 连接到 `/api/auth/invite-status` 和 `/api/auth/submit-invite-code` 端点
- **忘记密码**: 连接到 `/api/auth/forgot-password` 和 `/api/auth/reset-password` 端点

### 3. 页面更新
- **注册页面**: 使用真实API进行用户注册
- **忘记密码页面**: 使用真实API发送重置token
- **邀请码页面**: 已经使用真实API

## 启动步骤

### 1. 启动后端
```bash
cd backend
./mvnw spring-boot:run
```
后端将在 `http://localhost:8081` 运行

### 2. 启动前端
```bash
cd frontend
npm run dev
```
前端将在 `http://localhost:5173` 运行

### 3. 确保MongoDB运行
确保MongoDB在 `mongodb://localhost:27017/care_scheduling` 运行

## 测试连接

### 自动测试
运行测试脚本：
```bash
cd frontend
node test-connection.js
```

### 手动测试
1. 打开浏览器访问 `http://localhost:5173`
2. 尝试注册新用户
3. 使用注册的凭据登录
4. 测试邀请码功能（使用测试代码：`123`, `abc`, `test`, `valid`, `code`）

## API端点映射

| 前端功能 | 后端端点 | 方法 |
|---------|---------|------|
| 登录 | `/api/auth/login-email` | POST |
| 注册 | `/api/auth/register` | POST |
| 获取用户信息 | `/api/auth/me` | GET |
| 邀请状态 | `/api/auth/invite-status` | GET |
| 提交邀请码 | `/api/auth/submit-invite-code` | POST |
| 忘记密码 | `/api/auth/forgot-password` | POST |
| 重置密码 | `/api/auth/reset-password` | POST |

## 数据格式

### 登录请求
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### 注册请求
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "worker",
  "userType": "WORKER"
}
```

### 响应格式
```json
{
  "code": "0",
  "msg": "Success",
  "data": { ... }
}
```

## 角色映射

| 前端角色 | 后端userType |
|---------|-------------|
| poa | POA |
| worker | WORKER |
| manager | MANAGER |

## 故障排除

### 常见问题

1. **CORS错误**
   - 确保后端CORS配置正确
   - 检查 `GlobalCorsConfig.java`

2. **连接被拒绝**
   - 确保后端在8081端口运行
   - 检查防火墙设置

3. **MongoDB连接错误**
   - 确保MongoDB运行在27017端口
   - 检查数据库名称 `care_scheduling`

4. **认证失败**
   - 检查token是否正确传递
   - 验证用户凭据

### 调试技巧

1. 打开浏览器开发者工具查看网络请求
2. 检查控制台错误信息
3. 查看后端日志输出
4. 使用测试脚本验证连接

## 下一步

1. 实现JWT token认证
2. 添加更多API端点
3. 实现错误处理和重试机制
4. 添加API响应缓存
5. 实现离线功能
