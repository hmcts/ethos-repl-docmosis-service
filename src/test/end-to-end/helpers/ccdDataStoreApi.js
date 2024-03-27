const {Logger} = require('@hmcts/nodejs-logging');
const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
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
    return saveCaseResponse.id;
}

async function createECMCase(dataLocation = 'ccd-case-basic-data.json', jurisdiction = 'Leeds') {
    const authToken = await idamApi.getUserToken();
    const userId = await idamApi.getUserId(authToken);
    const serviceToken = await s2sService.getServiceToken();

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/event-triggers/initiateCase/token`;
    const ccdSaveCasePath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases`;
    const startCaseOption = await fetch(ccdApiUrl + ccdStartCasePath, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        }
    });

    const startCaseResponse = await startCaseOption.json();

    const eventToken = startCaseResponse.token;

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

    const saveCaseOptions = await fetch(ccdApiUrl + ccdSaveCasePath, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(saveBody)
    });

    return await saveCaseOptions.json();
}

async function updateECMCaseInCcd(caseId, eventId, dataLocation = 'ccd-accept-case.json', jurisdiction = 'Leeds') {

    const authToken = await idamApi.getUserToken();
    const userId = await idamApi.getUserId(authToken);
    const serviceToken = await s2sService.getServiceToken();
    logger.info('Updating case with id %s and event %s', caseId, eventId);

    const ccdApiUrl = `http://ccd-data-store-api-${env}.service.core-compute-${env}.internal`;
    const ccdStartEventPath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases/${caseId}/event-triggers/${eventId}/token`;
    const ccdSaveEventPath = `/caseworkers/${userId}/jurisdictions/EMPLOYMENT/case-types/${jurisdiction}/cases/${caseId}/events`;

    const startEventOptions = await fetch(ccdApiUrl + ccdStartEventPath, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        }
    });

    const startEventResponse = await startEventOptions.json();
    const eventToken = startEventResponse.token;

    let data = fs.readFileSync(dataLocation);
    let saveBody = {
        data: JSON.parse(data),
        event: {
            id: eventId,
            summary: 'Updating Case',
            description: 'For CCD E2E Test'
        },
        'event_token': eventToken
    };

    const saveEventOptions = await fetch(ccdApiUrl + ccdSaveEventPath, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${authToken}`,
            'ServiceAuthorization': `Bearer ${serviceToken}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(saveBody)
    });

    return await saveEventOptions.json();
}

module.exports = {
    createCaseInCcd,
    createECMCase,
    updateECMCaseInCcd,
};
