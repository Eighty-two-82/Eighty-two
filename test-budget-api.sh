#!/bin/bash

# Budget API 测试脚本
# 这个脚本演示如何使用 curl 测试 Budget API

BASE_URL="http://localhost:8081"

echo "=== Budget API 测试 ==="
echo ""

# 1. 创建预算
echo "1. 创建预算..."
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL/api/budget" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "organizationId": "org456", 
    "createdBy": "user789",
    "totalBudget": 30000,
    "categories": [
      {
        "name": "Hygiene Products",
        "description": "Personal hygiene items",
        "categoryBudget": 30000,
        "subElements": [
          {
            "name": "Soap",
            "description": "Body soap", 
            "subElementBudget": 10000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          },
          {
            "name": "Body Wash",
            "description": "Shower gel",
            "subElementBudget": 10000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          },
          {
            "name": "Towels",
            "description": "Bath towels",
            "subElementBudget": 10000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          }
        ]
      }
    ]
  }')

echo "创建预算响应:"
echo "$CREATE_RESPONSE" | jq '.'
echo ""

# 2. 获取预算
echo "2. 获取预算..."
GET_RESPONSE=$(curl -s -X GET "$BASE_URL/api/budget/patient/patient123")
echo "获取预算响应:"
echo "$GET_RESPONSE" | jq '.'
echo ""

# 3. 获取预算统计
echo "3. 获取预算统计..."
STATS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/budget/statistics/patient123")
echo "预算统计响应:"
echo "$STATS_RESPONSE" | jq '.'
echo ""

# 4. 获取预算警告
echo "4. 获取预算警告..."
WARNINGS_RESPONSE=$(curl -s -X GET "$BASE_URL/api/budget/warnings/patient123")
echo "预算警告响应:"
echo "$WARNINGS_RESPONSE" | jq '.'
echo ""

echo "=== 测试完成 ==="
