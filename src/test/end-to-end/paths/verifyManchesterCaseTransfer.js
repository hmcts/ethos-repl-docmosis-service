const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {caseTransfer} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Single Case & Execute Case Transfer');

Scenario('Verify Manchester Case Transfer', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json');
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseNumber);
    await caseTransfer(I, eventNames.CASE_TRANSFER);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
