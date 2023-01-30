const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantRepresentative} = require("../helpers/caseHelper");

Feature('Leeds Singles Case & Execute Claimant Representative...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Claimant Representative', async ({I}) => {
    await claimantRepresentative(I, eventNames.CLAIMANT_REPRESENTATIVE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
