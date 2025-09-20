#!/bin/bash

echo "=== 测试简化的Budget API (无需X-User-ID) ==="
echo

# 1. 获取预算
echo "1. 获取预算:"
curl -s -X GET "http://localhost:8081/api/budget/patient/patient123" | jq '.code, .msg, (.data | {id, patientId, totalBudget})'
echo

# 2. 获取预算统计
echo "2. 获取预算统计:"
curl -s -X GET "http://localhost:8081/api/budget/statistics/patient123" | jq '.code, .msg, .data'
echo

# 3. 获取预算警告
echo "3. 获取预算警告:"
curl -s -X GET "http://localhost:8081/api/budget/warnings/patient123" | jq '.code, .msg, .data'
echo

# 4. 更新月度使用量
echo "4. 更新月度使用量:"
curl -s -X POST "http://localhost:8081/api/budget/monthly-usage" \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "patient123",
    "categoryId": "cc078cde-bd8e-42ad-b9cf-d843675f0631",
    "subElementId": "9ba06215-ab74-43b0-bd72-2de0c538d48d",
    "month": 1,
    "amount": 500.0
  }' | jq '.code, .msg, (.data | {id, patientId, totalBudget})'
echo

# 5. 再次获取统计 (查看更新后的数据)
echo "5. 更新后的统计:"
curl -s -X GET "http://localhost:8081/api/budget/statistics/patient123" | jq '.code, .msg, .data'
echo

echo "=== 测试完成 ==="
