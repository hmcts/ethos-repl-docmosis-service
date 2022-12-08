const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, caseDetails} = require("../helpers/caseHelper");
let caseId;

Feature('Execute Accept Case Test');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseEvent(I, caseId, eventNames.ACCEPT_CASE);

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);

Scenario('Execute Case Details Test', async ({I}) => {
    await acceptCaseEvent(I, caseId, eventNames.ACCEPT_CASE);
    await caseDetails(I, caseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@ignore')
    .retry(testConfig.TestRetryScenarios);
