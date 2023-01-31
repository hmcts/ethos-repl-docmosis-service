const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantDetails} = require("../helpers/caseHelper");

Feature('Leeds Singles Case And Execute Claimant Details...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Claimant Details', async ({I}) => {
    await claimantDetails(I, eventNames.CLAIMANT_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
