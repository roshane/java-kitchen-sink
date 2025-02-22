#!/bin/bash

#printf "Step 01:[%b] running ./mvnw clean package\n" $(pwd)
#./mvnw clean package #-DskipTests
#printf "Step 02:[%b] building docker image\n" $(pwd)
#docker build . --tag opencsv-test:latest

printf "Step 01:[%s] building in docker environment" $(pwd)
docker build --no-cache --file Build.Dockerfile --tag opencsv-test:latest --build-arg POSTGRES_HOST=host.docker.internal . --progress=plain
#docker build --no-cache --file Build.Dockerfile --tag opencsv-test:latest --build-arg POSTGRES_HOST=172.17.0.2 . --progress=plain
#docker build --builder custom-buildx --no-cache --file Build.Dockerfile --tag opencsv-test:latest --build-arg POSTGRES_HOST=opencsv-test-postgres-1 .
