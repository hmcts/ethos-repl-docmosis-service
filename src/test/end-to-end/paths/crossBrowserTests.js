const testConfig = require('./../../config');
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseTest, caseDetails} = require("../helpers/caseHelper");

Feature('Leeds Singles Case and move to Accepted state');
// BeforeSuite(async ({I}) => caseId = await createCaseInCcd('test/end-to-end/data/ccd-case-basic-data.json'));

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseTest(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);

Scenario('Leeds Singles Case and move to Case Details state', async ({I}) => {
    await caseDetails(I, testConfig.CCDCaseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);
