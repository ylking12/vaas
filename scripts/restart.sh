#!/bin/bash
# VaaS 一键重启脚本
# 用法:
#   bash scripts/restart.sh                      # 重启所有服务
#   bash scripts/restart.sh vaas-backend         # 只重启 vaas-backend
#   bash scripts/restart.sh -f receiver          # 强制 kill 后重启
#   bash scripts/restart.sh --no-start vaas-backend  # 只 stop，不 start

set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
SCRIPT_DIR="$ROOT/scripts"
LOG_DIR="$ROOT/logs"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}[✓]${NC} $1"; }
warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
error() { echo -e "${RED}[✗]${NC} $1"; }

FORCE=0
NO_START=0
SERVICE=""

# 与 start.sh 启动顺序保持一致
MICRO_SERVICES=(
  "receiver"
  "detector4kt"
  "detector4motion"
  "vaas-backend"
  "admin-api"
  "dashboard"
)

# 解析参数
while [ $# -gt 0 ]; do
  case "$1" in
    -f|--force)
      FORCE=1
      shift
      ;;
    --no-start)
      NO_START=1
      shift
      ;;
    -h|--help)
      echo "用法: bash scripts/restart.sh [选项] [服务名]"
      echo ""
      echo "选项:"
      echo "  -f, --force         强制 kill (kill -9) 后再启动"
      echo "  --no-start          只停止，不启动"
      echo "  -h, --help          显示帮助"
      echo ""
      echo "服务名（不指定则重启全部）:"
      echo "  ${MICRO_SERVICES[*]}"
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

# 验证服务名
if [ -n "$SERVICE" ]; then
  valid=0
  for s in "${MICRO_SERVICES[@]}"; do
    if [ "$s" = "$SERVICE" ]; then
      valid=1
      break
    fi
  done
  if [ "$valid" -eq 0 ]; then
    error "未知服务: $SERVICE"
    echo "可用服务: ${MICRO_SERVICES[*]}"
    exit 1
  fi
fi

# 选择要重启的服务列表
if [ -n "$SERVICE" ]; then
  TARGETS=("$SERVICE")
else
  TARGETS=("${MICRO_SERVICES[@]}")
fi

echo ""
echo "=========================================="
echo "  VaaS 重启服务"
echo "  目标: ${TARGETS[*]}"
[ "$FORCE" -eq 1 ] && echo "  模式: 强制"
[ "$NO_START" -eq 1 ] && echo "  模式: 仅停止"
echo "=========================================="
echo ""

# 复用的停止函数（提取自 stop.sh 核心逻辑）
stop_one() {
  local name=$1
  local pid_file="$LOG_DIR/$name.pid"

  if [ ! -f "$pid_file" ]; then
    # 兜底按进程名找
    local found_pid
    found_pid=$(ps aux | grep "[j]ava.*$name\|[n]ode.*$name" | awk '{print $2}' | head -1)
    if [ -n "$found_pid" ]; then
      info "通过进程名发现 $name (PID=$found_pid)"
      if [ "$FORCE" -eq 1 ]; then
        kill -9 "$found_pid" 2>/dev/null && warn "已强制 kill $name (PID=$found_pid)"
      else
        kill "$found_pid" 2>/dev/null
        sleep 1
        kill -0 "$found_pid" 2>/dev/null && kill -9 "$found_pid" 2>/dev/null
        info "已停止 $name (PID=$found_pid)"
      fi
    else
      warn "$name 未运行"
    fi
    return
  fi

  local pid
  pid=$(cat "$pid_file")
  if [ -z "$pid" ] || ! kill -0 "$pid" 2>/dev/null; then
    warn "$name 僵死/未运行，清理 pid 文件"
    rm -f "$pid_file"
    return
  fi

  if [ "$FORCE" -eq 1 ]; then
    kill -9 "$pid" 2>/dev/null && warn "已强制 kill $name (PID=$pid)"
  else
    kill "$pid" 2>/dev/null
    sleep 2
    if kill -0 "$pid" 2>/dev/null; then
      kill -9 "$pid" 2>/dev/null && warn "$name 优雅停止超时，强制 kill"
    else
      info "已停止 $name (PID=$pid)"
    fi
  fi
  rm -f "$pid_file"
}

# 单服务启动函数（提取自 start.sh，按服务名分发）
start_one() {
  local name=$1
  case "$name" in
    receiver)
      bash "$SCRIPT_DIR/start.sh" "$$" 2>/dev/null || true
      # 不能直接复用 start.sh（会重启所有），这里直接调底层函数
      # 改用 inline 调用
      _start_jar_inline "receiver" "$ROOT/backend/receiver/target/receiver.jar" 50412
      ;;
    detector4kt)
      _start_jar_inline "detector4kt" "$ROOT/backend/detector4kt/target/detector4kt.jar" 50413
      ;;
    detector4motion)
      _start_jar_inline "detector4motion" "$ROOT/backend/detector4motion/target/detector4motion.jar" 50414
      ;;
    vaas-backend)
      _start_jar_inline "vaas-backend" "$ROOT/backend/vaas-backend/target/vaas-backend.jar" 50410 "/spring/v1/get_weather"
      ;;
    admin-api)
      _start_jar_inline "admin-api" "$ROOT/backend/admin-api/target/admin-api.jar" 50415
      ;;
    dashboard)
      _start_dashboard_inline
      ;;
    *)
      error "未知服务: $name"
      return 1
      ;;
  esac
}

# ============ Inline 启动函数（与 start.sh 行为保持一致） ============
# 由于 start.sh 是一体化脚本，直接 source 它就能复用其函数
# 但 source 会执行 start.sh 的主流程，所以我们用 export 函数 + 单独调用模式
# 这里用更简单的方式：直接复用 start.sh 中已 export 的逻辑
# 通过 source start.sh 并跳过主流程

# 改用更可靠的方式：让 restart.sh 通过 start.sh 的内部函数重启
# 但 start.sh 没 export 函数，所以我们自己复制关键逻辑

# 检测 JDK
detect_jdk() {
  if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "$JAVA_HOME"
    return 0
  fi
  if command -v java >/dev/null 2>&1; then
    local java_bin
    java_bin=$(command -v java)
    if [ -L "$java_bin" ]; then
      local resolved
      resolved=$(readlink "$java_bin" 2>/dev/null || true)
      if [ -n "$resolved" ] && [ -x "$resolved" ]; then
        local candidate
        candidate=$(cd "$(dirname "$resolved")/.." && pwd)
        [ -x "$candidate/bin/java" ] && { echo "$candidate"; return 0; }
      fi
    fi
    local candidate2
    candidate2=$(cd "$(dirname "$java_bin")/.." && pwd)
    [ -x "$candidate2/bin/java" ] && { echo "$candidate2"; return 0; }
  fi
  if [ "$(uname)" = "Darwin" ] && [ -x /usr/libexec/java_home ]; then
    /usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home 2>/dev/null
  fi
  for p in /usr/lib/jvm/java-17-* /usr/lib/jvm/default-java /opt/java/openjdk; do
    [ -x "$p/bin/java" ] && { echo "$p"; return 0; }
  done
  return 1
}

if ! JAVA_HOME=$(detect_jdk); then
  error "未找到 JDK 17"
  exit 1
fi
export JAVA_HOME
JAVA="$JAVA_HOME/bin/java"

_start_jar_inline() {
  local name=$1
  local jar=$2
  local port=$3
  local health_path=${4:-}
  local log="$LOG_DIR/$name.log"
  local pid_file="$LOG_DIR/$name.pid"

  if [ ! -f "$jar" ]; then
    error "$name JAR 不存在: $jar"
    return 1
  fi

  echo -n "启动 $name (port $port)..."
  nohup "$JAVA" -jar "$jar" > "$log" 2>&1 &
  echo $! > "$pid_file"

  local waited=0
  while [ $waited -lt 30 ]; do
    sleep 2
    waited=$((waited + 2))
    local url="http://localhost:$port${health_path}"
    if curl -s -o /dev/null -w '' --max-time 2 "$url" 2>/dev/null; then
      info "$name 启动成功 (PID=$(cat $pid_file))"
      return 0
    fi
  done
  error "$name 启动超时，查看日志: $log"
}

_start_dashboard_inline() {
  local pid_file="$LOG_DIR/dashboard.pid"
  echo -n "启动 dashboard (port 8082)..."
  cd "$ROOT/frontend/dashboard"
  nohup npm run dev -- --port 8082 > "$LOG_DIR/dashboard.log" 2>&1 &
  echo $! > "$pid_file"
  sleep 4
  if curl -s -o /dev/null -w '' --max-time 2 http://localhost:8082/ 2>/dev/null; then
    info "dashboard 启动成功 (PID=$(cat $pid_file))"
  else
    error "dashboard 启动超时"
  fi
}

# ============ 主流程 ============

# 1. 停止
echo "[1/2] 停止目标服务..."
for t in "${TARGETS[@]}"; do
  stop_one "$t"
done

# 等待端口释放
sleep 1

# 2. 启动
if [ "$NO_START" -eq 0 ]; then
  echo ""
  echo "[2/2] 启动目标服务..."
  for t in "${TARGETS[@]}"; do
    start_one "$t"
  done
else
  echo ""
  info "已跳过启动（--no-start）"
fi

echo ""
echo "=========================================="
info "重启操作完成"
echo "  状态查询: bash scripts/status.sh"
echo "  日志查看: bash scripts/logs.sh <服务名> [-f]"
echo "=========================================="
