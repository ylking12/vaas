#!/bin/bash

echo "Starting VaaS-CloudBackend"

nohup java -Dspring.config.location=file:///opt/etas/vaas/vaas_vehicle_simulator/application-dev.yml -Duser.timezone=Asia/Shanghai -jar /opt/etas/vaas/vaas_vehicle_simulator/vaas-trajectory-simulator-0.0.1-SNAPSHOT.jar >/dev/null 2>&1 &