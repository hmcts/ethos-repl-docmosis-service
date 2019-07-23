#!/usr/bin/env bash

#Stop CCD and Docmosis services
../../../../../ccd-docker/ccd compose down
docker stop $(docker ps -aq) > /dev/null 2>&1
docker rm $(docker ps -qa) > /dev/null 2>&1

#Cleanup docker
docker rm -vf $(docker ps -aq)
docker rmi -f $(docker images -aq)
docker volume prune -f
docker volume rm $(docker volume ls -qf dangling=true)