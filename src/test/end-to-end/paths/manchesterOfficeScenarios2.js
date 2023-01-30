const testConfig = require('./../../config');
const {
    navigateCase,
    jurisdiction,
    judgment,
    allocateHearing,
    hearingDetails
} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');

Feature('Manchester Office Scenarios...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Jurisdiction', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Judgment', async ({I}) => {
    await judgment(I, eventNames.JUDGMENT);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Allocate Hearing', async ({I}) => {
    await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Manchester');

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Hearing details', async ({I}) => {
    await hearingDetails(I, eventNames.HEARING_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
