const {Logger} = require('@hmcts/nodejs-logging');
const requestModule = require('request-promise-native');
const request = requestModule.defaults();
const fs = require('fs');
const testConfig = require('../tests/config.js');
const idamApi = require('./idamApi');
const s2sService = require('./s2sService');
const logger = Logger.getLogger('helpers/ccdDataCoreApi.js');
const env = testConfig.TestEnv;

async function createCaseInCcd(dataLocation = 'data/ccd-case-basic-data.json') {
    const saveCaseResponse = await createCaseAndFetchResponse(dataLocation).catch(error => {
        console.log(error);
    });
    const caseId = JSON.parse(saveCaseResponse).id;
    logger.info('Created case: %s', caseId);
    return caseId;
}

async function createCaseAndFetchResponse(dataLocation = 'data/ccd-case-basic-data.json') {
    const authToken = await idamApi.getUserToken();
    const userId = await idamApi.getUserId(authToken);
    const serviceToken = await s2sService.getServiceToken();
    logger.info('Creating Case ....');

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/Leeds/event-triggers/initiateCase/token`;
    const ccdSaveCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/Leeds/cases`;

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
    console.log(startCaseResponse);
    const eventToken = JSON.parse(startCaseResponse).token;

    var data = fs.readFileSync(dataLocation);
    var saveBody = {
        data: JSON.parse(data),
        event: {
            id: 'hwfCreate',
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

async function updateCaseInCcd(caseId, eventId, dataLocation = 'data/ccd-update-data.json') {
    const authToken = await getUserToken();
    const userId = await getUserId(authToken);
    const serviceToken = await getServiceToken();
    logger.info('Updating case with id %s and event %s', caseId, eventId);

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartEventPath = `/caseworkers/${userId}/jurisdictions/DIVORCE/case-types/DIVORCE/cases/${caseId}/event-triggers/${eventId}/token`;
    const ccdSaveEventPath = `/caseworkers/${userId}/jurisdictions/DIVORCE/case-types/DIVORCE/cases/${caseId}/events`;

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

const getBaseUrl = () => {
    return 'manage-case.aat.platform.hmcts.net';
};

module.exports = {
    createCaseInCcd,
    createCaseAndFetchResponse,
    updateCaseInCcd,
    getBaseUrl
};
