# 密码重置邮件功能测试

## 功能说明
现在密码重置功能已经更新，用户请求重置密码时会收到包含token的邮件，而不是直接返回token。

## 测试步骤

### 1. 请求密码重置
**端点**: `POST /api/auth/forgot-password`

**请求体**:
```json
{
  "identifier": "user@example.com"
}
```

**预期响应**:
```json
{
  "code": "200",
  "msg": "Password reset email sent successfully!",
  "data": "Email sent"
}
```

### 2. 检查邮箱
用户应该收到一封来自 `caretrack3@gmail.com` 的邮件，包含：
- 主题: "CareTrack - Password Reset Request"
- 内容包含密码重置token
- Token有效期15分钟

### 3. 使用token重置密码
**端点**: `POST /api/auth/reset-password`

**请求体**:
```json
{
  "token": "从邮件中复制的token",
  "newPassword": "newpassword123"
}
```

**预期响应**:
```json
{
  "code": "200",
  "msg": "Password reset successfully!",
  "data": true
}
```

## 注意事项
- 需要设置有效的 `SENDGRID_API_KEY` 环境变量
- 发送方邮箱 `caretrack3@gmail.com` 需要在SendGrid中验证
- Token有效期为15分钟
- 如果用户不存在，返回404错误
- 如果邮件发送失败，返回404错误（但token仍会生成）

## 测试用例

### 测试用例1: 正常流程
1. 使用存在的用户邮箱请求重置
2. 检查邮箱收到邮件
3. 使用邮件中的token重置密码
4. 验证新密码可以登录

### 测试用例2: 用户不存在
1. 使用不存在的邮箱请求重置
2. 应该返回404错误

### 测试用例3: Token过期
1. 请求重置密码
2. 等待15分钟后使用token
3. 应该返回400错误

### 测试用例4: 无效token
1. 使用无效token重置密码
2. 应该返回400错误
