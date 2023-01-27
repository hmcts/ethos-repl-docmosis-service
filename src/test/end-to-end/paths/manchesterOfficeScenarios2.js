const testConfig = require('./../../config');
const {
    navigateCase,
    jurisdiction,
    judgment,
    allocateHearing,
    hearingDetails,
    printHearingLists,
    caseTransfer,
    closeCase

} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');

Feature('Manchester Office Scenarios ');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Jurisdiction', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester Case Judgment', async ({I}) => {
    await judgment(I, eventNames.JUDGMENT);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester case Allocate Hearing', async ({I}) => {
    await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Manchester');

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);


Scenario('Verify Manchester case Hearing details', async ({I}) => {
    await hearingDetails(I, eventNames.HEARING_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester Print Hearing Lists', async ({I}) => {
    await printHearingLists(I, eventNames.PRINT_HEARING_LISTS, 'Manchester');

}).tag('@bug')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester Case Transfer', async ({I}) => {
    await caseTransfer(I, eventNames.CASE_TRANSFER);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester Case Close', async ({I}) => {
    await closeCase(I, eventNames.CLOSE_CASE, 'A Clerk', 'Casework Dropback Shelf');

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

