# Spring Boot 登录演示项目

## 项目简介
这是一个基于Spring Boot的用户登录注册演示项目，包含完整的后端API接口。

## 技术栈
- Spring Boot 2.3.12
- Spring Data JPA
- MySQL 8.0
- Maven

## 环境要求
- Java 8 或更高版本
- MySQL 数据库
- Maven（可选，项目包含Maven Wrapper）

## 快速开始

### 1. 数据库准备
确保MySQL服务正在运行，并创建数据库：
```sql
CREATE DATABASE logindemo;
```

### 2. 配置数据库连接
编辑 `src/main/resources/application.properties` 文件，修改数据库连接信息：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logindemo?serverTimezone=UTC
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

### 3. 启动应用
在项目根目录执行：
```bash
# 给mvnw添加执行权限（首次运行）
chmod +x mvnw

# 启动应用
./mvnw spring-boot:run
```

### 4. 测试API
应用启动后，可以通过以下接口测试：

#### 用户注册
```bash
curl -X POST 'http://localhost:8081/user/register' \
  -H 'Content-Type: application/json' \
  -d '{"uname":"testuser","password":"123456"}'
```

#### 用户登录
```bash
curl -X POST 'http://localhost:8081/user/login' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'uname=testuser&password=123456'
```

## 项目结构
```
src/main/java/com/springboot/springbootlogindemo/
├── controller/          # 控制器层
│   └── UserController.java
├── service/            # 服务层
│   ├── UserService.java
│   └── serviceImpl/
│       └── UserServiceImpl.java
├── repository/         # 数据访问层
│   └── UserDao.java
├── domain/            # 实体类
│   └── User.java
├── config/            # 配置类
│   └── GlobalCorsConfig.java
└── utils/             # 工具类
    └── Result.java
```

## 数据库表结构
```sql
CREATE TABLE users (
    uid INT AUTO_INCREMENT PRIMARY KEY,
    uname VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

## 注意事项
1. 确保MySQL服务正在运行
2. 检查数据库连接配置是否正确
3. 如果端口8081被占用，可以在application.properties中修改server.port
4. 首次运行可能需要下载Maven依赖，请保持网络连接

## 常见问题
- **端口占用**：修改application.properties中的server.port
- **数据库连接失败**：检查MySQL服务状态和连接配置
- **权限问题**：确保mvnw文件有执行权限

## 联系方式
如有问题，请联系项目开发者。 