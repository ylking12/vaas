#!/bin/bash
# VaaS 数据库初始化
# 用法: bash scripts/init-db.sh

MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
SCRIPT="$(cd "$(dirname "$0")" && pwd)/init-db.sql"

if [ ! -f "$SCRIPT" ]; then
  echo "❌ 找不到 SQL 文件: $SCRIPT"
  exit 1
fi

echo "初始化 VaaS 数据库..."
mysql -u"$MYSQL_USER" -h"$MYSQL_HOST" < "$SCRIPT"
echo "✅ VaaS 数据库初始化完成"
