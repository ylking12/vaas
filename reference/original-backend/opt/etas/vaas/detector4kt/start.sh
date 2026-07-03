#!/bin/bash

echo "Starting VaaS-Detector4kt"

PID_FILE="/opt/etas/vaas/detector4kt/detector4kt.pid"

# 如果有旧的进程，先杀掉
if [ -f "$PID_FILE" ]; then
    OLD_PID=$(cat "$PID_FILE")
    if ps -p $OLD_PID > /dev/null 2>&1; then
        echo "Stopping old process $OLD_PID"
        kill -9 $OLD_PID
    fi
    rm -f "$PID_FILE"
fi

# 启动新进程
nohup java -Duser.timezone=Asia/Shanghai \
    -jar /opt/etas/vaas/detector4kt/detector4kt.jar >/dev/null 2>&1 &

# 记录新的 PID
echo $! > "$PID_FILE"
echo "New process started with PID $(cat $PID_FILE)"

