const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, bfAction} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Singles Case & Execute B/F Action');

Scenario('Verify Manchester case B/F Action', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await bfAction(I, eventNames.BF_ACTION);

}).tag('@e2e')
    .tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
