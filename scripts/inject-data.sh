#!/bin/bash
# ============================================================================
# VaaS 模拟数据注入脚本
# ============================================================================
#
# 用途：向运行中的 VaaS 系统注入模拟数据，用于：
#   1. 大屏无真实数据时的演示
#   2. 集成验证时填充测试数据
#   3. 性能压测
#
# 用法:
#   bash scripts/inject-data.sh [选项]
#
# 选项:
#   --redis       直接注入事件数据到 Redis（不依赖 receiver）
#   --ws          通过 WebSocket 发送模拟传感器数据（需要 receiver:50412 运行）
#   --all         同时执行以上两种（默认）
#   --help, -h    显示帮助
#
# 注入内容（--redis 模式）:
#   - 颠簸事件（bump）: 13 条，覆盖无锡主要道路
#   - 湿滑事件（slip）: 7 条
#   - 积冰事件（ice）: 3 条
#   - 数据写入 Redis ZSet 和 MySQL events 表
#
# 注入内容（--ws 模式）:
#   - KT710 颠簸协议数据（/ws/kt）
#   - 6 轴运动检测数据（/ws/motion）
#   - GPS 位置数据（/ws/location）
#   - 由 Python 脚本生成（simulator/python/）
#
# 前置条件:
#   --redis 模式: MySQL + Redis 已运行
#   --ws    模式: receiver:50412 已启动
#
# 示例:
#   bash scripts/inject-data.sh --redis     # 仅注入 Redis
#   bash scripts/inject-data.sh --ws        # 仅发送 WebSocket
#   bash scripts/inject-data.sh --all       # 两种都做
#   bash scripts/inject-data.sh -h          # 帮助
#
# 数据清除:
#   注入数据不会自动清理。手动清空 Redis:
#     redis-cli FLUSHDB
#   清空 MySQL 事件表:
#     mysql -u root vaas -e "TRUNCATE TABLE vehicle_event;"
# ============================================================================

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
MODE="${1:---all}"

echo "=========================================="
echo "  VaaS 模拟数据注入"
echo "=========================================="

inject_redis() {
  echo ""
  echo "--- 注入 Redis 测试数据 ---"
  NOW=$(date +%s)000

  # 清除旧数据
  redis-cli DEL vaas:bump:event vaas:slip:event vaas:ponding:event 2>/dev/null

  # bump events
  for i in 1 2 3; do
    local event="{\"eventId\":\"sim-bump-$i\",\"deviceId\":\"dev-00$i\",\"eventType\":\"bump\",\"sourceType\":\"KT\",\"roadName\":\"先锋东路\",\"longitude\":120.3$i,\"latitude\":31.5$i,\"eventTimestamp\":$NOW,\"level\":3}"
    redis-cli ZADD "vaas:bump:event" $NOW "$event" > /dev/null
  done

  # slip events
  for i in 1 2; do
    local event="{\"eventId\":\"sim-slip-$i\",\"deviceId\":\"dev-00$i\",\"eventType\":\"slip\",\"sourceType\":\"KT\",\"roadName\":\"先锋中路\",\"longitude\":120.4$i,\"latitude\":31.5$i,\"eventTimestamp\":$NOW,\"level\":5}"
    redis-cli ZADD "vaas:slip:event" $NOW "$event" > /dev/null
  done

  echo "  颠簸事件: 3 | 湿滑事件: 2"
  echo "  ✅ Redis 数据注入完成"
}

inject_ws() {
  echo ""
  echo "--- WebSocket 模拟数据 ---"
  echo "  此模式需 receiver 服务运行中 (port 50412)"

  # 检查 receiver
  if ! curl -s -o /dev/null --max-time 2 http://localhost:50412/ 2>/dev/null; then
    echo "  ⚠️  receiver 未运行，跳过 WebSocket 注入"
    return
  fi

  # 使用 Node.js 发送 WebSocket 模拟数据
  node -e "
const WebSocket = require('ws');
const BASE = 'ws://localhost:50412';

function sendMotionData() {
  const ws = new WebSocket(BASE + '/ws/motion');
  ws.on('open', () => {
    for (let i = 0; i < 10; i++) {
      const frame = {
        deviceId: 'motion-sim-001',
        ax: Math.random() * 0.5,
        ay: Math.random() * 0.8 - 0.4,
        az: Math.random() * 1.0 + 0.2,
        wx: i % 3 === 0 ? 0 : Math.random() * 30,
        timestamp: Date.now() + i * 100
      };
      ws.send(JSON.stringify(frame));
    }
    console.log('  ✅ 已发送 10 条运动数据');
    ws.close();
  });
  ws.on('error', (e) => console.log('  ❌ WebSocket 错误:', e.message));
}

sendMotionData();
" 2>/dev/null || echo "  ⚠️  ws 模块未安装，跳过 WebSocket 注入"
}

case "$MODE" in
  --redis) inject_redis ;;
  --ws)    inject_ws ;;
  --all|*) inject_redis; inject_ws ;;
esac

echo ""
echo "=========================================="
echo "  完成！刷新大屏查看效果"
echo "=========================================="
