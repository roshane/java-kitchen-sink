#!/bin/bash
./mvnw clean package
docker build --tag opencsv-test:latest --build-arg POSTGRES_HOST=host.docker.internal . --progress=plain
