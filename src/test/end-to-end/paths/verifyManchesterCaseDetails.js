const testConfig = require('./../../config');
const {eventNames} = require("../pages/common/constants");
const {caseDetails, navigateCase} = require("../helpers/caseHelper");

Feature('Manchester Single Case and move to Case Details state');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Case Details ', async ({I}) => {
    await caseDetails(I, testConfig.MOCase, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Dropback Shelf', 'Standard track');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
