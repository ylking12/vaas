#!/bin/bash

echo "Starting VaaS-Receiver"

if [ -f receiver.pid ]; then
  kill -9 $(cat receiver.pid)
  echo "kill previous cps process"
fi

java -Dspring.profiles.active=prod -jar /opt/etas/vaas/receiver/receiver.jar 2>&1 & echo $! > receiver.pid

