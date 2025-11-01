#!/bin/bash

# 停止现有服务
pkill -f "spring-boot:run" || pkill -f "java.*CareAppApplication" || true
sleep 2

# 设置环境变量
export SENDGRID_API_KEY="SGJc-X3sTrT82albBISVmMSQ.uEouloJx-WALvayQpDX1iD3hFibp0CKuGaUi9wxu-xs"

# 设置云端 MongoDB 连接
export MONGODB_URI="mongodb+srv://yuanmengy:mangodb@cluster0.ml473ep.mongodb.net/eighty-two?retryWrites=true&w=majority&appName=Cluster0"

# 启动后端（使用相对路径）
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR/backend"
nohup ./mvnw spring-boot:run > /tmp/careapp-backend.log 2>&1 &

echo "✅ 后端服务正在启动..."
echo "📊 MongoDB: 云端数据库"
echo "📧 SendGrid: 已配置"
echo "查看日志: tail -f /tmp/careapp-backend.log"
echo ""
echo "等待10秒后测试邮件配置..."
sleep 10
curl -s http://localhost:8081/api/mail/status | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8081/api/mail/status
