const testConfig = require('./../../config');
const {navigateCase, acceptCaseTest} = require("../helpers/caseHelper");
const {eventNames, states} = require('../pages/common/constants.js');

Feature('Execute Manchester Singles Case and put the case into Accepted state');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester Accept Case', async ({I}) => {
    await acceptCaseTest(I, testConfig.MOCase, eventNames.ACCEPT_CASE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
