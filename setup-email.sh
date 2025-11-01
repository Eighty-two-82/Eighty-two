#!/bin/bash

echo "=========================================="
echo "  📧 CareTrack 邮件功能配置助手"
echo "=========================================="
echo ""

# 检查是否已配置
if [ -n "$SENDGRID_API_KEY" ]; then
    echo "✅ 检测到已配置的 SENDGRID_API_KEY"
    echo "当前值: ${SENDGRID_API_KEY:0:15}..."
    echo ""
    read -p "是否要重新配置？(y/n): " reconfig
    if [ "$reconfig" != "y" ] && [ "$reconfig" != "Y" ]; then
        echo "保持现有配置。"
        exit 0
    fi
fi

echo "📋 配置步骤："
echo ""
echo "1. 如果你还没有 SendGrid 账户，请先注册："
echo "   → 访问 https://sendgrid.com/"
echo "   → 点击 'Sign Up' 或 'Start for Free'"
echo "   → 填写信息并验证邮箱"
echo ""
echo "2. 创建 API Key："
echo "   → 登录 SendGrid"
echo "   → Settings → API Keys → Create API Key"
echo "   → 输入名称（如：CareTrack）"
echo "   → 选择 'Full Access'（最简单）"
echo "   → 复制 API Key（格式：SG.xxxxxxxxxxxx）"
echo "   ⚠️  注意：API Key 只显示一次，请立即复制！"
echo ""
read -p "你已经有 SendGrid API Key 了吗？(y/n): " has_key

if [ "$has_key" != "y" ] && [ "$has_key" != "Y" ]; then
    echo ""
    echo "📝 请先完成以下步骤："
    echo ""
    echo "【步骤 1】注册 SendGrid 账户"
    echo "1. 打开浏览器访问：https://sendgrid.com/"
    echo "2. 点击右上角 'Sign Up'"
    echo "3. 填写邮箱、用户名、密码"
    echo "4. 验证邮箱（检查收件箱）"
    echo ""
    echo "【步骤 2】创建 API Key"
    echo "1. 登录 SendGrid"
    echo "2. 左侧菜单：Settings → API Keys"
    echo "3. 点击 'Create API Key'"
    echo "4. 名称：CareTrack App"
    echo "5. 权限：选择 'Full Access'（最简单）"
    echo "6. 点击 'Create & View'"
    echo "7. ⚠️  立即复制 API Key（类似：SG.xxxxxxxxxxxx）"
    echo ""
    echo "完成后再运行这个脚本，或者直接输入 API Key 继续："
    read -p "请输入你的 SendGrid API Key（或按 Enter 退出）: " api_key
    if [ -z "$api_key" ]; then
        echo ""
        echo "好的，等你准备好 API Key 后再运行这个脚本即可。"
        echo "或者你可以手动配置："
        echo "export SENDGRID_API_KEY=SG.your_api_key_here"
        exit 0
    fi
else
    echo ""
    read -p "请输入你的 SendGrid API Key (格式：SG.xxxxxxxxxxxx): " api_key
fi

if [ -z "$api_key" ]; then
    echo "❌ 未输入 API Key，配置已取消"
    exit 1
fi

# 验证格式
if [[ ! $api_key == SG.* ]]; then
    echo ""
    echo "⚠️  警告：API Key 通常以 'SG.' 开头"
    read -p "你确定这个 API Key 是正确的吗？(y/n): " confirm
    if [ "$confirm" != "y" ] && [ "$confirm" != "Y" ]; then
        echo "配置已取消"
        exit 1
    fi
fi

# 确定配置文件
if [ -f "$HOME/.zshrc" ]; then
    PROFILE="$HOME/.zshrc"
    SHELL_NAME="zsh"
elif [ -f "$HOME/.bash_profile" ]; then
    PROFILE="$HOME/.bash_profile"
    SHELL_NAME="bash"
elif [ -f "$HOME/.bashrc" ]; then
    PROFILE="$HOME/.bashrc"
    SHELL_NAME="bash"
else
    PROFILE="$HOME/.profile"
    SHELL_NAME="profile"
fi

echo ""
echo "📝 正在配置 $SHELL_NAME 配置文件..."

# 删除旧的配置（如果存在）
if grep -q "SENDGRID_API_KEY" "$PROFILE" 2>/dev/null; then
    sed -i.bak '/SENDGRID_API_KEY/d' "$PROFILE" 2>/dev/null
    echo "   已移除旧的配置"
fi

# 添加新配置
echo "" >> "$PROFILE"
echo "# SendGrid API Key for CareTrack Email Service" >> "$PROFILE"
echo "export SENDGRID_API_KEY=\"$api_key\"" >> "$PROFILE"

echo "✅ API Key 已添加到 $PROFILE"
echo ""

# 立即导出到当前会话
export SENDGRID_API_KEY="$api_key"
echo "✅ API Key 已设置到当前终端会话"
echo ""

echo "=========================================="
echo "  ✨ 配置完成！"
echo "=========================================="
echo ""
echo "📋 接下来需要做的："
echo ""
echo "1. 重新加载配置（或关闭并重新打开终端）："
echo "   source $PROFILE"
echo ""
echo "2. 重启后端服务："
echo "   cd /Users/cc/Desktop/it/Eighty-two/backend"
echo "   ./mvnw spring-boot:run"
echo ""
echo "3. 测试邮件配置："
echo "   访问：http://localhost:8081/api/mail/status"
echo "   应该显示：\"configured\": true"
echo ""
echo "4. 发送测试邮件："
echo "   curl -X POST \"http://localhost:8081/api/mail/test?to=你的邮箱@example.com\""
echo ""
echo "🎉 配置完成后，邮件功能就可以使用了！"
echo ""
echo "邮件功能包括："
echo "  ✅ 忘记密码邮件（用户申请重置密码时）"
echo "  ✅ 预算预警邮件（预算使用率 80% 或 100% 时）"
echo ""


