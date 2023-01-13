const testConfig = require('./../../config');
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseTest} = require("../helpers/caseHelper");

Feature('Accept Case');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Leeds Singles Case and move to Accepted state', async ({I}) => {
    await acceptCaseTest(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
