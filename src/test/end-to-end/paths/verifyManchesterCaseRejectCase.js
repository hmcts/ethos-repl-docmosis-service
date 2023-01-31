const testConfig = require('./../../config');
const {rejectCaseEvent} = require("../helpers/caseHelper");
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
let caseId;

Feature('Create a Manchester Single Case and move to Rejected state');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester'));

Scenario('Verify Manchester Reject Case', async ({I}) => {
    await rejectCaseEvent(I, caseId, eventNames.REJECT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
