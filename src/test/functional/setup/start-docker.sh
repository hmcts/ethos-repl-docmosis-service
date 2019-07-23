#!/usr/bin/env bash
#Start CCD and Docmosis services
../../../../../ccd-docker/ccd compose start

#Wait until all services are up
echo ""
echo "Waiting for the services to come up.. "
echo ""
output=`docker ps | grep -i 'starting'`
while [[ ${output}  != '' ]]
do
    sleep 10
    output=`docker ps | grep -i 'starting'`
done
echo "All Services started successfully"
echo ""
