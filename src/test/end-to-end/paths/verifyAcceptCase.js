const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent} = require("../helpers/caseHelper");
let caseNumber;

Feature('Leeds Singles Case and move to Accepted state');

Scenario('Verify Accept Case', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);

}).tag('@e2e')
    .tag('@xb')
    .retry(testConfig.TestRetryScenarios);
