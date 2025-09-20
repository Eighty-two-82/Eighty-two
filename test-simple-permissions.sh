#!/bin/bash

# 简化的权限控制测试脚本
BASE_URL="http://localhost:8081"

echo "=== 简化权限控制测试 ==="
echo ""

# 测试1: 没有用户ID的情况（应该失败）
echo "1. 测试没有用户ID的情况（应该失败）..."
curl -s -X GET "$BASE_URL/api/budget/patient/patient123" | jq '.code, .msg'
echo ""

# 测试2: 有用户ID的情况（应该成功）
echo "2. 测试有用户ID的情况（应该成功）..."
curl -s -X GET "$BASE_URL/api/budget/patient/patient123" \
  -H "X-User-ID: manager-user-123" | jq '.code, .msg'
echo ""

# 测试3: 测试创建预算
echo "3. 测试创建预算..."
curl -s -X POST "$BASE_URL/api/budget" \
  -H "Content-Type: application/json" \
  -H "X-User-ID: manager-user-123" \
  -d '{
    "patientId": "patient999",
    "organizationId": "org456",
    "totalBudget": 25000,
    "categories": [
      {
        "name": "Test Category",
        "description": "Test category for permission testing",
        "categoryBudget": 25000,
        "subElements": [
          {
            "name": "Test Item",
            "description": "Test item",
            "subElementBudget": 25000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          }
        ]
      }
    ]
  }' | jq '.code, .msg'
echo ""

# 测试4: 测试更新月度使用情况
echo "4. 测试更新月度使用情况..."
curl -s -X POST "$BASE_URL/api/budget/monthly-usage" \
  -H "Content-Type: application/json" \
  -H "X-User-ID: manager-user-123" \
  -d '{
    "patientId": "patient123",
    "categoryId": "cc078cde-bd8e-42ad-b9cf-d843675f0631",
    "subElementId": "9ba06215-ab74-43b0-bd72-2de0c538d48d",
    "month": 1,
    "amount": 1000.0
  }' | jq '.code, .msg'
echo ""

# 测试5: 测试获取预算统计
echo "5. 测试获取预算统计..."
curl -s -X GET "$BASE_URL/api/budget/statistics/patient123" \
  -H "X-User-ID: manager-user-123" | jq '.code, .msg'
echo ""

# 测试6: 测试获取预算警告
echo "6. 测试获取预算警告..."
curl -s -X GET "$BASE_URL/api/budget/warnings/patient123" \
  -H "X-User-ID: manager-user-123" | jq '.code, .msg'
echo ""

echo "=== 测试完成 ==="
