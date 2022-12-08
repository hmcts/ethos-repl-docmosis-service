const testConfig = require('./../../config');
const {rejectCaseEvent} = require("../helpers/caseHelper");
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
let caseNumber;

Feature('Create a Manchester Single Case and move to Rejected state');

Scenario('Verify Manchester Reject Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await rejectCaseEvent(I, caseNumber, eventNames.REJECT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
