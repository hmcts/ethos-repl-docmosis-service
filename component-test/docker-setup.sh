#!/usr/bin/env bash

##############################################################################################
# It is assumed that "az login" command has already been executed before running this script #
##############################################################################################

#Clone ccd-docker repo if necessary
if [[ ! -d "../../ccd-docker" ]]; then
    cd ../../
    git clone git@github.com:hmcts/ccd-docker.git
    cd -
fi

#ccd login
../../ccd-docker/ccd login

#Create docker images for projects below using "demo" branch
../../ccd-docker/ccd set ccd-data-store-api demo
../../ccd-docker/ccd set ccd-definition-store-api demo
../../ccd-docker/ccd set ccd-user-profile-api demo
../../ccd-docker/ccd set ccd-api-gateway demo
../../ccd-docker/ccd set ccd-case-management-web demo

#Pull necessary images from docker registry
../../ccd-docker/ccd compose pull

#Start CCD and Docmosis services
../../ccd-docker/ccd compose up -d

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

#Execute these steps manually
#../../ccd-docker/bin/idam-create-caseworker.sh caseworker,caseworker-test,caseworker-employment-tribunal-manchester,caseworker-employment,caseworker-employment-tribunal-manchester-caseofficer eric.la.cooper@gmail.com password Cooper Eric
#../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester
#../../ccd-docker/bin/ccd-add-role.sh caseworker-employment
#../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester-caseofficer
#../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester-casesupervisor
#../../ccd-docker/bin/ccd-import-definition.sh src/test/resources/config/CCD_EmpTrib_Manc_v1.0.09.xlsx