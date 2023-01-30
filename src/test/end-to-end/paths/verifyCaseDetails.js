const testConfig = require('./../../config');
const {updateECMCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
const assert = require('assert');
const {caseDetails, navigateCase} = require("../helpers/caseHelper");

const verifyState = (eventResponse, state) => {
    assert.strictEqual(JSON.parse(eventResponse).state, state);
};

Feature('Leeds Singles Case and move to Case Details state');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Case Details ', async ({I}) => {
    await caseDetails(I, testConfig.CCDCaseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Case Details ( Using API)', async ({I}) => {
    const acceptedState = await updateECMCaseInCcd(testConfig.CCDCaseId, eventNames.PRE_ACCEPTANCE_CASE, 'src/test/end-to-end/data/ccd-accept-case.json');
    verifyState(acceptedState, states.ACCEPTED);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
