#!/bin/bash
# VaaS 日志查看脚本
# 用法:
#   bash scripts/logs.sh                       # 列出所有日志
#   bash scripts/logs.sh vaas-backend          # 查看 vaas-backend 最近 100 行
#   bash scripts/logs.sh -f receiver           # 实时跟踪 receiver 日志
#   bash scripts/logs.sh -n 500 vaas-backend   # 查看最近 500 行

set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT/logs"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

info()  { echo -e "${GREEN}[✓]${NC} $1"; }
warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
header(){ echo -e "${BLUE}$1${NC}"; }

# 已知服务列表（与 start.sh / status.sh 保持一致）
KNOWN_SERVICES=(
  "receiver"
  "detector4kt"
  "detector4motion"
  "vaas-backend"
  "admin-api"
  "dashboard"
)

list_logs() {
  echo ""
  header "  VaaS 日志列表 ($LOG_DIR)"
  echo ""
  if [ ! -d "$LOG_DIR" ]; then
    warn "日志目录不存在: $LOG_DIR"
    return
  fi

  # 找所有 .log 文件，按修改时间倒序
  local found=0
  for log in $(ls -t "$LOG_DIR"/*.log 2>/dev/null); do
    if [ -f "$log" ]; then
      local name
      name=$(basename "$log" .log)
      local size
      size=$(du -h "$log" 2>/dev/null | awk '{print $1}')
      local mtime
      mtime=$(stat -f "%Sm" -t "%Y-%m-%d %H:%M" "$log" 2>/dev/null || stat -c "%y" "$log" 2>/dev/null | cut -d'.' -f1)
      printf "  ${GREEN}%-20s${NC} %8s  %s\n" "$name" "$size" "$mtime"
      found=$((found+1))
    fi
  done

  if [ "$found" -eq 0 ]; then
    warn "暂无日志文件"
  fi
  echo ""
  echo "用法: bash scripts/logs.sh <服务名> [-f] [-n 行数]"
  echo "示例: bash scripts/logs.sh vaas-backend -f"
  echo "      bash scripts/logs.sh receiver -n 200"
}

# 解析参数
FOLLOW=0
LINES=100
SERVICE=""

while [ $# -gt 0 ]; do
  case "$1" in
    -f|--follow)
      FOLLOW=1
      shift
      ;;
    -n|--lines)
      LINES="$2"
      shift 2
      ;;
    -h|--help)
      echo "用法: bash scripts/logs.sh [选项] <服务名>"
      echo ""
      echo "选项:"
      echo "  -f, --follow         实时跟踪日志 (类似 tail -f)"
      echo "  -n, --lines N        显示最近 N 行 (默认 100)"
      echo "  -h, --help           显示帮助"
      echo ""
      echo "服务名:"
      echo "  ${KNOWN_SERVICES[*]}"
      exit 0
      ;;
    -*)
      warn "未知选项: $1"
      shift
      ;;
    *)
      SERVICE="$1"
      shift
      ;;
  esac
done

# 无参数：列出日志
if [ -z "$SERVICE" ]; then
  list_logs
  exit 0
fi

# 检查服务名
valid=0
for s in "${KNOWN_SERVICES[@]}"; do
  if [ "$s" = "$SERVICE" ]; then
    valid=1
    break
  fi
done
if [ "$valid" -eq 0 ]; then
  warn "未知服务: $SERVICE"
  echo "可用服务: ${KNOWN_SERVICES[*]}"
  exit 1
fi

LOG_FILE="$LOG_DIR/$SERVICE.log"

if [ ! -f "$LOG_FILE" ]; then
  warn "日志文件不存在: $LOG_FILE"
  echo ""
  echo "该服务是否启动过？执行: bash scripts/start.sh"
  exit 1
fi

echo ""
header "=========================================="
header "  $SERVICE 日志  ($LOG_FILE)"
header "=========================================="
echo ""

if [ "$FOLLOW" -eq 1 ]; then
  info "实时跟踪模式 (Ctrl+C 退出)"
  echo ""
  tail -n "$LINES" -f "$LOG_FILE"
else
  tail -n "$LINES" "$LOG_FILE"
fi
