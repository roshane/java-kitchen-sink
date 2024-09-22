#!/bin/bash

printf "Step 01:[%b] running ./mvnw clean package\n" $(pwd)
./mvnw clean package
printf "Step 02:[%b] building docker image\n" $(pwd)
docker build . --tag opencsv-test:latest



