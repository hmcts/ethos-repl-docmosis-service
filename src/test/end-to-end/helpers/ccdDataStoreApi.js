const {Logger} = require('@hmcts/nodejs-logging');
const requestModule = require('request-promise-native');
const request = requestModule.defaults();
const fs = require('fs');
const testConfig = require('../../config.js');
const idamApi = require('./idamApi');
const s2sService = require('./s2sHelper');
const logger = Logger.getLogger('helpers/ccdDataStoreApi.js');
const env = testConfig.TestEnv;

async function createCaseInCcd(dataLocation = 'ccd-case-basic-data.json', jurisdiction = 'Leeds') {
    const saveCaseResponse = await createECMCase(dataLocation, jurisdiction).catch(error => {
        console.log(error);
    });
    const caseId = JSON.parse(saveCaseResponse).id;
    return caseId;
}

async function createECMCase(dataLocation = 'ccd-case-basic-data.json', jurisdiction = 'Leeds') {
    const authToken = await idamApi.getUserToken();
    const userId = await idamApi.getUserId(authToken);
    const serviceToken = await s2sService.getServiceToken();

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/event-triggers/initiateCase/token`;
    const ccdSaveCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases`;

    const startCaseOptions = {
        method: 'GET',
        uri: ccdApiUrl + ccdStartCasePath,
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        }
    };

    const startCaseResponse = await request(startCaseOptions);
    const eventToken = JSON.parse(startCaseResponse).token;

    const data = fs.readFileSync(dataLocation);
    const saveBody = {
        data: JSON.parse(data),
        event: {
            id: 'initiateCase',
            summary: 'Creating Case',
            description: 'For CCD E2E Test'
        },
        'event_token': eventToken
    };

    const saveCaseOptions = {
        method: 'POST',
        uri: ccdApiUrl + ccdSaveCasePath,
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(saveBody)
    };

    const saveCaseResponse = await request(saveCaseOptions);
    return saveCaseResponse;
}

async function updateECMCaseInCcd(caseId, eventId, dataLocation = 'ccd-accept-case.json', jurisdiction = 'Leeds') {

    const authToken = await idamApi.getUserToken();
    const userId = await idamApi.getUserId(authToken);
    const serviceToken = await s2sService.getServiceToken();
    logger.info('Updating case with id %s and event %s', caseId, eventId);

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartEventPath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases/${caseId}/event-triggers/${eventId}/token`;
    const ccdSaveEventPath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases/${caseId}/events`;

    const startEventOptions = {
        method: 'GET',
        uri: ccdApiUrl + ccdStartEventPath,
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        }
    };

    const startEventResponse = await request(startEventOptions);
    const eventToken = JSON.parse(startEventResponse).token;

    var data = fs.readFileSync(dataLocation);
    var saveBody = {
        data: JSON.parse(data),
        event: {
            id: eventId,
            summary: 'Updating Case',
            description: 'For CCD E2E Test'
        },
        'event_token': eventToken
    };

    const saveEventOptions = {
        method: 'POST',
        uri: ccdApiUrl + ccdSaveEventPath,
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(saveBody)
    };

    const saveEventResponse = await request(saveEventOptions);
    return saveEventResponse;
}

module.exports = {
    createCaseInCcd,
    createECMCase,
    updateECMCaseInCcd,
};
