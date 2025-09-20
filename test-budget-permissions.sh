#!/bin/bash

# Budget API 权限控制测试脚本
# 这个脚本演示如何使用不同的用户ID测试权限控制

BASE_URL="http://localhost:8081"

echo "=== Budget API 权限控制测试 ==="
echo ""

# 测试1: 没有用户ID的情况（使用默认用户）
echo "1. 测试默认用户访问预算..."
curl -s -X GET "$BASE_URL/api/budget/patient/patient123" | jq '.'
echo ""

# 测试2: 使用Manager用户ID
echo "2. 测试Manager用户访问预算..."
curl -s -X GET "$BASE_URL/api/budget/patient/patient123" \
  -H "X-User-ID: manager-user-123" | jq '.'
echo ""

# 测试3: 使用Worker用户ID（没有权限）
echo "3. 测试Worker用户访问预算（应该被拒绝）..."
curl -s -X GET "$BASE_URL/api/budget/patient/patient123" \
  -H "X-User-ID: worker-user-456" | jq '.'
echo ""

# 测试4: 检查用户可访问的患者列表
echo "4. 检查Manager用户可访问的患者列表..."
curl -s -X GET "$BASE_URL/api/budget/accessible-patients" \
  -H "X-User-ID: manager-user-123" | jq '.'
echo ""

# 测试5: 检查用户是否为预算管理员
echo "5. 检查Manager用户是否为预算管理员..."
curl -s -X GET "$BASE_URL/api/budget/is-admin" \
  -H "X-User-ID: manager-user-123" | jq '.'
echo ""

# 测试6: 检查Worker用户是否为预算管理员
echo "6. 检查Worker用户是否为预算管理员..."
curl -s -X GET "$BASE_URL/api/budget/is-admin" \
  -H "X-User-ID: worker-user-456" | jq '.'
echo ""

# 测试7: 尝试创建预算（Manager用户）
echo "7. 测试Manager用户创建预算..."
curl -s -X POST "$BASE_URL/api/budget" \
  -H "Content-Type: application/json" \
  -H "X-User-ID: manager-user-123" \
  -d '{
    "patientId": "patient456",
    "organizationId": "org456", 
    "createdBy": "manager-user-123",
    "totalBudget": 20000,
    "categories": [
      {
        "name": "Test Category",
        "description": "Test category for permission testing",
        "categoryBudget": 20000,
        "subElements": [
          {
            "name": "Test Item",
            "description": "Test item",
            "subElementBudget": 20000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          }
        ]
      }
    ]
  }' | jq '.'
echo ""

# 测试8: 尝试创建预算（Worker用户，应该被拒绝）
echo "8. 测试Worker用户创建预算（应该被拒绝）..."
curl -s -X POST "$BASE_URL/api/budget" \
  -H "Content-Type: application/json" \
  -H "X-User-ID: worker-user-456" \
  -d '{
    "patientId": "patient789",
    "organizationId": "org456", 
    "createdBy": "worker-user-456",
    "totalBudget": 20000,
    "categories": [
      {
        "name": "Test Category",
        "description": "Test category for permission testing",
        "categoryBudget": 20000,
        "subElements": [
          {
            "name": "Test Item",
            "description": "Test item",
            "subElementBudget": 20000,
            "monthlyUsage": [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
          }
        ]
      }
    ]
  }' | jq '.'
echo ""

echo "=== 权限控制测试完成 ==="
