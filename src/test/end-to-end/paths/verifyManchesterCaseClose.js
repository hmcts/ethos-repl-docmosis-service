const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction, closeCase} = require("../helpers/caseHelper");
let caseNumber;

Feature('Execute Manchester Case Close Scenario');

Scenario('Verify Manchester Case Close', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);
    await closeCase(I, eventNames.CLOSE_CASE, 'A Clerk', 'Casework Dropback Shelf');

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
