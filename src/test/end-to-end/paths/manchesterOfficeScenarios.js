const testConfig = require('./../../config');
const {
    navigateCase,
    acceptCaseTest,
    caseDetails,
    claimantDetails,
    claimantRepresentative,
    claimantRespondentDetails,
    respondentRepresentative,
    letters,
    bfAction,
    restrictedReporting,
    fixCaseAPI,
    listHearing
} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');

Feature('Manchester Office Scenarios ');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseTest(I, testConfig.MOCase, eventNames.ACCEPT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Case Details ', async ({I}) => {
    await caseDetails(I, testConfig.MOCase, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Dropback Shelf', 'Standard track');

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Claimant Details', async ({I}) => {
    await claimantDetails(I, eventNames.CLAIMANT_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Claimant Representative', async ({I}) => {
    await claimantRepresentative(I, eventNames.CLAIMANT_REPRESENTATIVE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Respondent Details', async ({I}) => {
    await claimantRespondentDetails(I, eventNames.CLAIMANT_RESPONDENT_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Respondent Representative', async ({I}) => {
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);


Scenario('Verify List Hearing', async ({I}) => {
    await listHearing(I, eventNames.LIST_HEARING, 'Manchester');

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Letters', async ({I}) => {
    await letters(I, eventNames.LETTERS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

// Scenario('Verify B/F Action', async ({I}) => {
//     await bfAction(I, eventNames.BF_ACTION);
//
// }).tag('@e2e')
//     .retry(testConfig.TestRetryScenarios);

Scenario('Verify Restricted Reporting', async ({I}) => {
    await restrictedReporting(I, eventNames.RESTRICTED_REPORTING);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Fix Case API', async ({I}) => {
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
