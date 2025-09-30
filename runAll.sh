#!/bin/bash

echo "Starting Service Discovery..."
(cd Service-Discovery && mvn spring-boot:run) &
sleep 15  

echo "Starting Auth Service..."
(cd Auth-Service && mvn spring-boot:run) &
sleep 10

echo "Starting Finance Service..."
(cd Finance-Service && mvn spring-boot:run) &
sleep 10

echo "Starting Report Service..."
(cd Reporting-Service && mvn spring-boot:run) &
sleep 10

echo "Starting API Gateway..."
(cd API-Gateway && mvn spring-boot:run) &
