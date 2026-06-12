#!/bin/bash
# VaaS 一键启动脚本
# 按依赖顺序启动所有微服务
# 用法: bash scripts/start.sh

set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

# 颜色
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()  { echo -e "${GREEN}[✓]${NC} $1"; }
warn()  { echo -e "${YELLOW}[!]${NC} $1"; }
error() { echo -e "${RED}[✗]${NC} $1"; }

# ============================================================
# JDK 自动检测（按优先级降序）
# ============================================================
detect_jdk() {
  # 1. 环境变量 JAVA_HOME 优先
  if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "$JAVA_HOME"
    return 0
  fi

  # 2. 系统 PATH 中的 java
  if command -v java >/dev/null 2>&1; then
    local java_bin
    java_bin=$(command -v java)
    if [ -L "$java_bin" ]; then
      local resolved
      resolved=$(readlink "$java_bin" 2>/dev/null || true)
      if [ -n "$resolved" ] && [ -x "$resolved" ]; then
        local candidate
        candidate=$(cd "$(dirname "$resolved")/.." && pwd)
        if [ -x "$candidate/bin/java" ]; then
          echo "$candidate"
          return 0
        fi
      fi
    fi
    local candidate2
    candidate2=$(cd "$(dirname "$java_bin")/.." && pwd)
    if [ -x "$candidate2/bin/java" ]; then
      echo "$candidate2"
      return 0
    fi
  fi

  # 3. macOS 专用：/usr/libexec/java_home
  if [ "$(uname)" = "Darwin" ] && [ -x /usr/libexec/java_home ]; then
    local mac_java
    mac_java=$(/usr/libexec/java_home -v 17 2>/dev/null || /usr/libexec/java_home 2>/dev/null || true)
    if [ -n "$mac_java" ] && [ -x "$mac_java/bin/java" ]; then
      echo "$mac_java"
      return 0
    fi
  fi

  # 4. Linux 常见路径
  for p in /usr/lib/jvm/java-17-* /usr/lib/jvm/default-java /opt/java/openjdk; do
    if [ -x "$p/bin/java" ]; then
      echo "$p"
      return 0
    fi
  done

  return 1
}

# 解析 JDK 路径
if ! JAVA_HOME=$(detect_jdk); then
  error "未找到 JDK 17。请设置 JAVA_HOME 环境变量或安装 JDK 17 后重试"
  exit 1
fi
export JAVA_HOME
JAVA="$JAVA_HOME/bin/java"

info "JDK: $($JAVA -version 2>&1 | head -1)  (JAVA_HOME=$JAVA_HOME)"

##################################################
# 1. 启动 MySQL
##################################################
start_mysql() {
  if pgrep -x mysqld > /dev/null 2>&1; then
    warn "MySQL 已运行"
  else
    echo -n "启动 MySQL..."
    brew services start mysql 2>/dev/null || mysql.server start 2>/dev/null || true
    sleep 3
    if pgrep -x mysqld > /dev/null 2>&1; then
      info "MySQL 启动成功"
    else
      error "MySQL 启动失败，请手动启动"
    fi
  fi
}

##################################################
# 2. 启动 Redis
##################################################
start_redis() {
  if pgrep -x redis-server > /dev/null 2>&1; then
    warn "Redis 已运行"
  else
    echo -n "启动 Redis..."
    brew services start redis 2>/dev/null || redis-server --daemonize yes 2>/dev/null || true
    sleep 2
    if pgrep -x redis-server > /dev/null 2>&1; then
      info "Redis 启动成功"
    else
      error "Redis 启动失败，请手动启动"
    fi
  fi
}

##################################################
# 3. 启动 Spring Boot 微服务
##################################################
check_port() {
  local port=$1
  if lsof -i :"$port" >/dev/null 2>&1; then
    # 查找占用进程的 PID 和命令
    local occupant
    occupant=$(lsof -i :"$port" -sTCP:LISTEN -n -P 2>/dev/null | tail -1 | awk '{print $1, $2}')
    return 1
  fi
  return 0
}

start_jar() {
  local name=$1
  local jar=$2
  local port=$3
  local log="$LOG_DIR/$name.log"
  local pid_file="$LOG_DIR/$name.pid"

  if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
    warn "$name 已在运行 (PID: $(cat $pid_file))"
    return
  fi

  if [ ! -f "$jar" ]; then
    error "$name JAR 不存在: $jar"
    return
  fi

  # 端口冲突检测
  if ! check_port "$port"; then
    warn "端口 $port 已被占用，跳过 $name 启动"
    warn "  占用进程: $(lsof -i :$port -sTCP:LISTEN -n -P 2>/dev/null | tail -1 | awk '{print $1, $2}')"
    warn "  如需重启该端口: kill <PID> 后重试，或 bash scripts/stop.sh -f"
    return
  fi

  echo -n "启动 $name (port $port)..."
  nohup "$JAVA" -jar "$jar" > "$log" 2>&1 &
  echo $! > "$pid_file"

  # 等待服务就绪
  local waited=0
  while [ $waited -lt 30 ]; do
    if curl -s -o /dev/null -w '' --max-time 2 http://localhost:$port/ 2>/dev/null; then
      info "$name 启动成功 (PID: $(cat $pid_file))"
      return
    fi
    # 对 vaas-backend 使用专门的检查路径
    if [ "$name" = "vaas-backend" ]; then
      if curl -s -o /dev/null -w '' --max-time 2 http://localhost:$port/spring/v1/get_weather 2>/dev/null; then
        info "$name 启动成功 (PID: $(cat $pid_file))"
        return
      fi
    fi
    sleep 2
    waited=$((waited + 2))
  done
  error "$name 启动超时，查看日志: $log"
}

##################################################
# 4. 启动前端开发服务器
##################################################
start_frontend() {
  local pid_file="$LOG_DIR/dashboard.pid"
  if [ -f "$pid_file" ] && kill -0 $(cat "$pid_file") 2>/dev/null; then
    warn "前端 dev server 已在运行 (PID: $(cat $pid_file))"
    return
  fi
  echo -n "启动前端 dev server (port 8082)..."
  cd "$ROOT/frontend/dashboard"
  nohup npm run dev -- --port 8082 > "$LOG_DIR/dashboard.log" 2>&1 &
  echo $! > "$pid_file"
  sleep 4
  if curl -s -o /dev/null -w '' --max-time 2 http://localhost:8082/ 2>/dev/null; then
    info "前端 dev server 启动成功 (PID: $(cat $pid_file))"
  else
    error "前端启动超时"
  fi
}

##################################################
# 主流程
##################################################
echo ""
echo "=========================================="
echo "  VaaS 一键启动"
echo "=========================================="
echo ""

start_mysql
start_redis

echo ""
echo "--- 启动微服务 ---"
start_jar "receiver"         "$ROOT/backend/receiver/target/receiver.jar"         50412
start_jar "detector4kt"      "$ROOT/backend/detector4kt/target/detector4kt.jar"   50413
start_jar "detector4motion"  "$ROOT/backend/detector4motion/target/detector4motion.jar" 50414
start_jar "vaas-backend"     "$ROOT/backend/vaas-backend/target/vaas-backend.jar" 50410
start_jar "admin-api"        "$ROOT/backend/admin-api/target/admin-api.jar"       50415

echo ""
echo "--- 启动前端 ---"
start_frontend

echo ""
echo "=========================================="
echo -e "${GREEN}  VaaS 启动完成${NC}"
echo "  大屏:  http://localhost:8082"
echo "  后端:  http://localhost:50410/spring/v1"
echo "  管理后台: http://localhost:8081"
echo "=========================================="
