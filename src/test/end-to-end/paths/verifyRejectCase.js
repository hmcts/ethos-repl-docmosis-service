const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {rejectCaseEvent} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Singles Case and move to Rejected state');

Scenario('Verify Reject Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await rejectCaseEvent(I, caseNumber, eventNames.REJECT_CASE);

}).tag('@e2e')
    .tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
