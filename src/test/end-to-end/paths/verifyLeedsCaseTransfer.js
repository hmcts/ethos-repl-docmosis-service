const testConfig = require('./../../config');
const {navigateCase} = require("../helpers/caseHelper");
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {caseTransfer} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Single Case & Execute Case Transfer');

Scenario('Verify Leeds Case Transfer', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await navigateCase(I, caseNumber);
    await caseTransfer(I, eventNames.CASE_TRANSFER);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
