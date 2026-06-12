#!/bin/bash
# VaaS 一键验证脚本
# 用法:
#   bash scripts/verify.sh           # 跑全部验证
#   bash scripts/verify.sh --skip-build  # 跳过前端构建
#   bash scripts/verify.sh --skip-mvn    # 跳过后端编译
#   bash scripts/verify.sh --skip-py     # 跳过 Python 测试

set +e  # 不让单步失败终止整个流程

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m'

info()  { echo -e "${BLUE}[▶]${NC} $1"; }
ok()    { echo -e "${GREEN}[✓]${NC} $1"; }
fail()  { echo -e "${RED}[✗]${NC} $1"; }
warn()  { echo -e "${YELLOW}[!]${NC} $1"; }

SKIP_BUILD=0
SKIP_MVN=0
SKIP_PY=0

while [ $# -gt 0 ]; do
  case "$1" in
    --skip-build) SKIP_BUILD=1; shift ;;
    --skip-mvn)   SKIP_MVN=1;   shift ;;
    --skip-py)    SKIP_PY=1;    shift ;;
    -h|--help)
      echo "用法: bash scripts/verify.sh [选项]"
      echo "  --skip-build  跳过前端 npm run build"
      echo "  --skip-mvn    跳过后端 mvn compile"
      echo "  --skip-py     跳过 Python 单元测试"
      echo "  -h, --help    显示帮助"
      exit 0
      ;;
    *) shift ;;
  esac
done

PASS=0
FAIL=0
START_T=$(date +%s)

echo ""
echo "=========================================="
echo "  VaaS 一键验证"
echo "=========================================="
echo ""

# 1. 前端构建
if [ "$SKIP_BUILD" -eq 0 ]; then
  info "[1/3] 前端构建 (npm run build)..."
  cd "$ROOT/frontend/dashboard"
  if npm run build > /tmp/vaas_verify_frontend.log 2>&1; then
    BUILD_SIZE=$(du -sh dist/ 2>/dev/null | awk '{print $1}')
    ok "前端构建成功 (dist: $BUILD_SIZE)"
    PASS=$((PASS+1))
  else
    fail "前端构建失败，查看 /tmp/vaas_verify_frontend.log"
    tail -20 /tmp/vaas_verify_frontend.log
    FAIL=$((FAIL+1))
  fi
  cd "$ROOT"
else
  warn "[1/3] 前端构建 已跳过"
fi

# 2. 后端编译
if [ "$SKIP_MVN" -eq 0 ]; then
  info "[2/3] 后端编译 (mvn compile)..."
  for module in vaas-common receiver detector4kt detector4motion vaas-backend admin-api; do
    if [ -d "$ROOT/backend/$module" ]; then
      info "  编译 $module..."
      cd "$ROOT/backend/$module"
      if mvn compile -DskipTests -q > /tmp/vaas_verify_$module.log 2>&1; then
        ok "  $module 编译成功"
        PASS=$((PASS+1))
      else
        fail "  $module 编译失败，查看 /tmp/vaas_verify_$module.log"
        tail -10 /tmp/vaas_verify_$module.log
        FAIL=$((FAIL+1))
      fi
    fi
  done
  cd "$ROOT"
else
  warn "[2/3] 后端编译 已跳过"
fi

# 3. Python 测试
if [ "$SKIP_PY" -eq 0 ]; then
  info "[3/3] Python 交叉验证测试..."
  if [ -f "$ROOT/scripts/test_python_cross_validation.py" ]; then
    cd "$ROOT"
    if python3 "$ROOT/scripts/test_python_cross_validation.py" > /tmp/vaas_verify_py.log 2>&1; then
      ok "Python 测试通过"
      PASS=$((PASS+1))
    else
      fail "Python 测试失败，查看 /tmp/vaas_verify_py.log"
      tail -20 /tmp/vaas_verify_py.log
      FAIL=$((FAIL+1))
    fi
  else
    warn "  Python 测试脚本不存在，跳过"
  fi
else
  warn "[3/3] Python 测试 已跳过"
fi

# 汇总
ELAPSED=$(( $(date +%s) - START_T ))
echo ""
echo "=========================================="
echo "  验证结果汇总"
echo "=========================================="
echo ""
echo -e "  ${GREEN}通过: $PASS${NC}  ${RED}失败: $FAIL${NC}  耗时: ${ELAPSED}s"
echo ""

if [ "$FAIL" -gt 0 ]; then
  fail "存在失败项，请检查"
  exit 1
else
  ok "全部验证通过"
  exit 0
fi
