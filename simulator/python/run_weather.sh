#!/bin/bash
# 定时拉取天气数据写入 MySQL（每5分钟执行）
cd "$(dirname "$0")"
while true; do
  python3 weather_mock.py
  sleep 300
done
