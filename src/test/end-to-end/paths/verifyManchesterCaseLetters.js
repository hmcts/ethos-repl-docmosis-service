const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, letters} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Single Case & Execute Letters');

Scenario('Verify Manchester case Letters', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await letters(I, eventNames.LETTERS);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
