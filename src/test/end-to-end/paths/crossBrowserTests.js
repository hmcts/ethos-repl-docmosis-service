const testConfig = require('./../../config');
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseTest, caseDetails} = require("../helpers/caseHelper");
let caseId;

Feature('Leeds Singles Case and move to Accepted state');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('test/end-to-end/data/ccd-case-basic-data.json'));

Before(async ({I}) => {
    await navigateCase(I, caseId);
});

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseTest(I, caseId, eventNames.ACCEPT_CASE);

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);

Scenario('Leeds Singles Case and move to Case Details state', async ({I}) => {
    await caseDetails(I, caseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);
