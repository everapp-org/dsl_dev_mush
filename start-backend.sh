#!/bin/bash
# Start backend from correct directory
cd /home/gpetrov/src/dsl_dev_mush/mcms-jhipster-base
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
