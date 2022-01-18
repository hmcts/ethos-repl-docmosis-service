const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, claimantDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create A Single Case And Execute Claimant Details...');

Scenario('Verify Claimant Details', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await claimantDetails(I, eventNames.CLAIMANT_DETAILS);

}).tag('@e2e')
    .tag('@manchester')
    .retry(testConfig.TestRetryScenarios);
