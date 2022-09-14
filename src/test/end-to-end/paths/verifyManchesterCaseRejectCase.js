const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
let caseNumber;

Feature('Create a Manchester Single Case and move to Rejected state');

Scenario('Verify Manchester Reject Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await I.wait(5);
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseNumber);
    await I.chooseNextStep(eventNames.REJECT_CASE, 3);
    await I.rejectTheCase();

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
