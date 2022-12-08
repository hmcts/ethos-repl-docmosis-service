const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, fixCaseAPI} = require("../helpers/caseHelper");

Feature('Create a Manchester Singles Case & Execute Fix Case API');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester Fix Case API', async ({I}) => {
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
