const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, jurisdiction, judgment} = require("../helpers/caseHelper");

Feature('Manchester Office Single Case & Execute Jurisdiction Event');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Jurisdiction', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
