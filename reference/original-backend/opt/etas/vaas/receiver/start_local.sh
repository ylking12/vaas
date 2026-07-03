#!/bin/bash

echo "Starting VaaS-Receiver"

java -Dspring.config.location=file:///opt/etas/vaas/receiver/config.yaml -jar /opt/etas/vaas/receiver/receiver.jar
