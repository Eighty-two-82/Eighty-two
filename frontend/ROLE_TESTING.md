# 角色测试说明

## 如何修改用户角色进行测试

### 方法1：修改 userService.js（推荐）
打开 `src/services/userService.js` 文件，找到以下两处：

1. **第14行** - login函数中：
```javascript
role: "manager", // 改为 'poa', 'manager', 或 'worker'
```

2. **第31行** - getMe函数中：
```javascript
role: "manager", // 改为 'poa', 'manager', 或 'worker'
```

### 方法2：修改 router/index.js（备用）
打开 `src/router/index.js` 文件，找到第18行：
```javascript
return { role: 'manager' } // 改为 'poa', 'manager', 或 'worker'
```

## 角色说明

- **poa**: POA/FM角色 - 显示完整菜单（Home, Tasks, Carer Team, Budget, Upload, Setting）
- **manager**: 管理员角色 - 显示管理菜单（Home, Worker Management, Budget, Tasks, Upload, Communication, Setting）
- **worker**: 员工角色 - 显示基础菜单（Home, Tasks, Upload, Setting）

## 邀请码测试

在 `userService.js` 中还可以修改邀请码状态：
```javascript
// 第19行和第43行
valid: false, // 改为 true 可以跳过邀请码流程
```

## 测试流程

1. 修改角色设置
2. 保存文件
3. 刷新浏览器
4. 重新登录测试

## 注意事项

- 修改后需要保存文件并刷新浏览器
- 确保两个文件中的角色设置一致
- manager 和 worker 角色会触发邀请码流程（如果 valid: false）
