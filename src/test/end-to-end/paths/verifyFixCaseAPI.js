const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, fixCaseAPI} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Singles Case & Execute Fix Case API');

Scenario('Verify Fix Case API', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@e2e')
    .tag('@nightly')
    .tag('@ecm-520')
    .retry(testConfig.TestRetryScenarios);
