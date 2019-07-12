#!/usr/bin/env bash

#############################################################################################
# Assumptions:										                                        #
# 1. VPN connected									                                        #
# 2. "az login" command executed							                                #
#############################################################################################

#Install postgres client
sudo apt-get install postgresql-client -y

#Clone ccd-docker repo if necessary
if [[ ! -d "../../../../../ccd-docker" ]]; then
    cd ../../
    git clone git@github.com:hmcts/ccd-docker.git
    cd -
fi

#ccd login
../../../../../ccd-docker/ccd login

#Pull necessary images from docker registry
../../../../../ccd-docker/ccd compose pull

#Start CCD and Docmosis services
../../../../../ccd-docker/ccd compose up -d

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

##Add roles to Idam
#psql -h localhost -p 5050 -U ccd -d idam -c "insert into role values ('caseworker-employment-tribunal-manchester','Caseworker Employment Tribunal Manchester'),('caseworker-employment','Caseworker Employment'),('caseworker-employment-tribunal-manchester-caseofficer','Caseworker Employment Tribunal Manchester Caseofficer')"
#psql -h localhost -p 5050 -U ccd -d idam -c "insert into role values ('caseworker-employment-tribunal-glasgow','Caseworker Employment Tribunal Glasgow'),('caseworker-employment-tribunal-glasgow-caseofficer','Caseworker Employment Tribunal Glasgow Caseofficer')"
#
#
##Steps to add users and roles
#../../../../../ccd-docker/bin/idam-create-caseworker.sh caseworker,caseworker-test,caseworker-employment-tribunal-manchester,caseworker-employment,caseworker-employment-tribunal-manchester-caseofficer eric.la.cooper@gmail.com password Cooper Eric
#../../../../../ccd-docker/bin/idam-create-caseworker.sh caseworker,caseworker-test,caseworker-employment-tribunal-glasgow,caseworker-employment,caseworker-employment-tribunal-glasgow-caseofficer andy_gla.collier@yahoo.com password Collier Andy
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester-caseofficer
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-manchester-casesupervisor
#../../../../../ccd-docker/bin/ccd-import-definition.sh "src/test/functional/resources/config/CCD_EmpTrib_Manc_v1.1.28 Local User Branch.xlsx"
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-glasgow
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-glasgow-caseofficer
#../../../../../ccd-docker/bin/ccd-add-role.sh caseworker-employment-tribunal-glasgow-casesupervisor
#../../../../../ccd-docker/bin/ccd-import-definition.sh "src/test/functional/resources/config/CCD_EmpTrib_Glas_v1.1.28 Local User Branch.xlsx"
