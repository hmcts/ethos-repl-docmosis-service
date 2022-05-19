const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, claimantRepresentative} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create A Manchester Single Case & Execute Claimant Representative...');

Scenario('Verify Claimant Representative', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await claimantRepresentative(I, eventNames.CLAIMANT_REPRESENTATIVE);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
