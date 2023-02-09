const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {
    navigateCase, acceptCaseTest, caseDetails, claimantDetails,
    claimantRepresentative, claimantRespondentDetails, respondentRepresentative,
    restrictedReporting, listHearing, letters, bfAction, fixCaseAPI, jurisdiction,
    judgment, allocateHearing, hearingDetails
} = require("../helpers/caseHelper");

Feature('Leeds Office Scenarios');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseTest(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Details State', async ({I}) => {
    await caseDetails(I, testConfig.CCDCaseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Claimant Details', async ({I}) => {
    await claimantDetails(I, eventNames.CLAIMANT_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Claimant Representative', async ({I}) => {
    await claimantRepresentative(I, eventNames.CLAIMANT_REPRESENTATIVE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Claimant Respondent Details', async ({I}) => {
    await claimantRespondentDetails(I, eventNames.CLAIMANT_RESPONDENT_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Respondent Representative', async ({I}) => {
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Restricted Reporting', async ({I}) => {
    await restrictedReporting(I, eventNames.RESTRICTED_REPORTING);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case List Hearing', async ({I}) => {
    await listHearing(I, eventNames.LIST_HEARING, 'Leeds');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Letters', async ({I}) => {
    await letters(I, eventNames.LETTERS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case B/F Action', async ({I}) => {
    await bfAction(I, eventNames.BF_ACTION);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Fix Case API', async ({I}) => {
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Jurisdiction', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Judgment', async ({I}) => {
    await judgment(I, eventNames.JUDGMENT);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);

// Scenario('Verify Leeds Case Allocate Hearing', async ({I}) => {
//     await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Leeds');
//
// }).tag('@e2e')
//     .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Hearing details', async ({I}) => {
    await hearingDetails(I, eventNames.HEARING_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
