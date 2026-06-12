#!/bin/bash
# VaaS 服务状态检查脚本
# 用法:
#   bash scripts/status.sh                  # 检查所有服务
#   bash scripts/status.sh vaas-backend     # 只检查指定服务

set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT/logs"

# 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

ok()    { echo -e "  ${GREEN}●${NC} $1"; }
down()  { echo -e "  ${RED}●${NC} $1"; }
warn()  { echo -e "  ${YELLOW}●${NC} $1"; }
header(){ echo -e "${BLUE}$1${NC}"; }

# 服务定义: 名称|端口|健康检查路径(可空)|类型
SERVICES=(
  "receiver|50412||java"
  "detector4kt|50413||java"
  "detector4motion|50414||java"
  "vaas-backend|50410|/spring/v1/get_weather|java"
  "admin-api|50415||java"
  "dashboard|8082||node"
)

check_service() {
  local name=$1
  local port=$2
  local health_path=$3
  local pid_file="$LOG_DIR/$name.pid"

  if [ ! -f "$pid_file" ]; then
    down "$name (port $port)  ❌ 未运行（无 pid 文件）"
    return 1
  fi

  local pid
  pid=$(cat "$pid_file" 2>/dev/null || echo "")
  if [ -z "$pid" ] || ! kill -0 "$pid" 2>/dev/null; then
    down "$name (port $port)  ❌ 僵死进程 (PID=$pid)"
    return 1
  fi

  # HTTP 健康检查
  if [ -n "$health_path" ]; then
    local url="http://localhost:$port$health_path"
    if curl -s -o /dev/null -w '' --max-time 2 "$url" 2>/dev/null; then
      ok "$name (port $port)  ✓ 运行中 (PID=$pid)"
      return 0
    else
      warn "$name (port $port)  ⚠ 进程在但 HTTP 不响应 (PID=$pid)"
      return 1
    fi
  else
    # 仅 PID 存活 + 端口监听
    if lsof -i :"$port" >/dev/null 2>&1; then
      ok "$name (port $port)  ✓ 运行中 (PID=$pid)"
      return 0
    else
      warn "$name (port $port)  ⚠ 进程在但端口未监听 (PID=$pid)"
      return 1
    fi
  fi
}

check_infra() {
  local name=$1
  local process=$2
  if pgrep -x "$process" >/dev/null 2>&1; then
    ok "$name  ✓ 运行中"
    return 0
  else
    down "$name  ❌ 未运行"
    return 1
  fi
}

print_all() {
  echo ""
  echo "=========================================="
  header "  VaaS 服务状态"
  echo "=========================================="
  echo ""

  header "[基础设施]"
  check_infra "MySQL"  "mysqld"
  check_infra "Redis"  "redis-server"

  echo ""
  header "[微服务]"
  local total=0
  local up=0
  for svc in "${SERVICES[@]}"; do
    IFS='|' read -r name port path type <<< "$svc"
    total=$((total+1))
    if check_service "$name" "$port" "$path"; then
      up=$((up+1))
    fi
  done

  echo ""
  header "[Python 模拟器]"
  local py_count
  py_count=$(ps aux | grep -E "[p]ython.*(run_weather|main_6axis)" | wc -l | tr -d ' ')
  if [ "$py_count" -gt 0 ]; then
    ok "Python 模拟器 ($py_count 个进程)"
  else
    warn "Python 模拟器  未运行"
  fi

  echo ""
  echo "------------------------------------------"
  echo -e "  ${GREEN}$up${NC} / $total 微服务运行中"
  echo "------------------------------------------"
  echo ""
}

# 主流程
if [ $# -eq 0 ]; then
  print_all
else
  # 单服务检查
  target=$1
  found=0
  for svc in "${SERVICES[@]}"; do
    IFS='|' read -r name port path type <<< "$svc"
    if [ "$name" = "$target" ]; then
      check_service "$name" "$port" "$path"
      found=1
      break
    fi
  done
  if [ "$found" -eq 0 ]; then
    echo -e "${RED}未知服务: $target${NC}"
    echo "可用服务: ${SERVICES[*]//|*/}"
    echo "        mysql | redis"
    exit 1
  fi
fi
