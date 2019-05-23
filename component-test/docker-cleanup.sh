#!/usr/bin/env bash

#Start CCD and Docmosis services
docker-compose -f docker/app.yml down

docker rm $(docker ps -qa) > /dev/null 2>&1



