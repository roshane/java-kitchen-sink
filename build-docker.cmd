@echo off
docker -l debug build --no-cache --force-rm --pull . -t spring-boot-gql:1.0.0