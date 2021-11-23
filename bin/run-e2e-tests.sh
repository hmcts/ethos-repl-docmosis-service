#!/bin/bash
set -ex
### FOR DEMO

export CCD_CASEWORKER_E2E_EMAIL='employment_service@mailinator.com'
export CCD_CASEWORKER_E2E_PASSWORD='Nagoya0102'
export IDAM_CLIENT_SECRET=
export SERVICE_SECRET='DBssSOq0KKLNBf2z'


export TESTS_FOR_ACCESSIBILITY='true'
export E2E_OUTPUT_DIR='./functional-output/'

yarn test:functional
