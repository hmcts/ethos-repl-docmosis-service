const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantDetails} = require("../helpers/caseHelper");

Feature('Create Manchester A Single Case And Execute Claimant Details...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Claimant Details', async ({I}) => {
    await claimantDetails(I, eventNames.CLAIMANT_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
