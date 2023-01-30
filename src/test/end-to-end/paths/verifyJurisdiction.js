const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, jurisdiction} = require("../helpers/caseHelper");

Feature('Leeds Office Singles Case & Execute Jurisdiction Event');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Jurisdiction', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
