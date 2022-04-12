const testConfig = require('./../../config');
const {createCaseInCcd, updateECMCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
const assert = require('assert');
const {acceptCaseEvent, caseDetails} = require("../helpers/caseHelper");
let caseNumber;

const verifyState = (eventResponse, state) => {
    assert.strictEqual(JSON.parse(eventResponse).state, state);
};

Feature('Leeds Singles Case and move to Case Details state');

Scenario('Verify Case Details ', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await caseDetails(I, caseNumber, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@e2e')
    .tag('@nightly')
    .tag('@crossbrowser')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Case Details ( Using API)', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    console.log('..... caseCreated in CCD , caseNumber is ==  ' + caseNumber);

    const acceptedState = await updateECMCaseInCcd(caseNumber, eventNames.PRE_ACCEPTANCE_CASE, 'src/test/end-to-end/data/ccd-accept-case.json');
    verifyState(acceptedState, states.ACCEPTED);

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
