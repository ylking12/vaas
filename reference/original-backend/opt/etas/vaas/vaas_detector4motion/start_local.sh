#!/bin/bash

echo "Starting VaaS Motion Detector"

# kill -9 $(lsof -i:50412 -t)

nohup java -Duser.timezone=Asia/Shanghai -Dspring.config.location=file:///opt/etas/vaas/vaas_detector4motion/application.yaml -jar /opt/etas/vaas/vaas_detector4motion/vaas_detector4motion.jar  >/dev/null 2>&1 & 

