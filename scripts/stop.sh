#!/bin/bash
# VaaS 停止脚本
# 优雅关闭所有微服务和前端
# 用法: bash scripts/stop.sh [-f]
#   -f  强制模式：直接 kill -9，跳过优雅停止

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT/logs"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()  { echo -e "${GREEN}[✓]${NC} $1"; }
warn()  { echo -e "${YELLOW}[!]${NC} $1"; }

FORCE=0
while [ $# -gt 0 ]; do
  case "$1" in
    -f|--force) FORCE=1; shift ;;
    -h|--help)
      echo "用法: bash scripts/stop.sh [-f]"
      echo "  -f, --force   强制 kill -9，跳过优雅停止"
      exit 0
      ;;
    *) shift ;;
  esac
done

if [ "$FORCE" -eq 1 ]; then
  echo -e "${YELLOW}[强制模式] 直接 kill -9${NC}"
fi

echo ""
echo "=========================================="
echo "  VaaS 停止服务"
echo "=========================================="
echo ""

# 停止微服务
for name in receiver detector4kt detector4motion vaas-backend admin-api; do
  pid_file="$LOG_DIR/$name.pid"
  if [ -f "$pid_file" ]; then
    pid=$(cat "$pid_file")
    if kill -0 "$pid" 2>/dev/null; then
      if [ "$FORCE" -eq 1 ]; then
        kill -9 "$pid" 2>/dev/null && warn "已强制 kill $name (PID: $pid)"
      else
        echo -n "停止 $name (PID: $pid)..."
        kill "$pid" 2>/dev/null
        sleep 2
        if kill -0 "$pid" 2>/dev/null; then
          kill -9 "$pid" 2>/dev/null
          warn "强制终止"
        else
          info "已停止"
        fi
      fi
    else
      warn "$name 未运行"
    fi
    rm -f "$pid_file"
  else
    # 按进程名查找并停止
    pid=$(ps aux | grep "[j]ava.*$name" | awk '{print $2}')
    if [ -n "$pid" ]; then
      if [ "$FORCE" -eq 1 ]; then
        kill -9 "$pid" 2>/dev/null && warn "已强制 kill $name (PID: $pid)"
      else
        kill "$pid" 2>/dev/null && info "已停止 $name (PID: $pid)"
      fi
    fi
  fi
done

# 停止前端 dev server
pid_file="$LOG_DIR/dashboard.pid"
if [ -f "$pid_file" ]; then
  pid=$(cat "$pid_file")
  if [ "$FORCE" -eq 1 ]; then
    kill -9 "$pid" 2>/dev/null && warn "已强制 kill 前端 dev server (PID: $pid)"
  else
    kill "$pid" 2>/dev/null && info "前端 dev server 已停止" || warn "前端 dev server 未运行"
  fi
  rm -f "$pid_file"
fi

# 停止 Python 模拟器
ps aux | grep "[p]ython.*run_weather\|[p]ython.*main_6axis" | awk '{print $2}' | while read pid; do
  if [ "$FORCE" -eq 1 ]; then
    kill -9 "$pid" 2>/dev/null && warn "已强制 kill Python 模拟器 (PID: $pid)"
  else
    kill "$pid" 2>/dev/null && info "已停止 Python 模拟器 (PID: $pid)"
  fi
done

echo ""
info "所有 VaaS 服务已停止"
