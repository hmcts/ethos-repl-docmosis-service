const testConfig = require('./../../config');
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseTest} = require("../helpers/caseHelper");

Feature('Leeds Singles Case and move to Accepted state');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseTest(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
