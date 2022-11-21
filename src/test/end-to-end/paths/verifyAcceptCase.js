const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, caseDetails} = require("../helpers/caseHelper");
let caseId;

Feature('Leeds Singles Case and move to Accepted state');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Verify Accept Case', async ({I}) => {
    await acceptCaseEvent(I, caseId, eventNames.ACCEPT_CASE);

}).tag('@e2e')
    .tag('@xb')
    .retry(testConfig.TestRetryScenarios);

Scenario('Leeds Singles Case and move to Case Details state ', async ({I}) => {
    await caseDetails(I, caseId, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Table', 'Standard Track');

}).tag('@xb')
    .retry(testConfig.TestRetryScenarios);
