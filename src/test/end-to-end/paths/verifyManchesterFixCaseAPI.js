const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, fixCaseAPI} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Singles Case & Execute Fix Case API');

Scenario('Verify Manchester Fix Case API', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await fixCaseAPI(I, eventNames.FIX_CASE_API);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
