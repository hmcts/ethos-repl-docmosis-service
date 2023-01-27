const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, listHearing} = require("../helpers/caseHelper");

Feature('Create a Manchester Single Case & Execute List Hearing');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester case List Hearing', async ({I}) => {
    await listHearing(I, eventNames.LIST_HEARING, 'Manchester');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
