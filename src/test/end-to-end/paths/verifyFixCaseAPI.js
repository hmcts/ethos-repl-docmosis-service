const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, fixCaseAPI} = require("../helpers/caseHelper");

Feature('Execute Fix Case API');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Fix Case API', async ({I}) => {
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
