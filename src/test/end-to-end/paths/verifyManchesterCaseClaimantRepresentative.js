const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantRepresentative} = require("../helpers/caseHelper");

Feature('Create A Manchester Single Case & Execute Claimant Representative...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Claimant Representative', async ({I}) => {
    await claimantRepresentative(I, eventNames.CLAIMANT_REPRESENTATIVE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
