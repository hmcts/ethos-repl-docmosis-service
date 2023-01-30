const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, restrictedReporting} = require("../helpers/caseHelper");

Feature('Create a Leeds Singles Case & Execute Restricted Reporting');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Restricted Reporting', async ({I}) => {
    await restrictedReporting(I, eventNames.RESTRICTED_REPORTING);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
