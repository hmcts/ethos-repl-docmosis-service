const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, listHearing} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Single Case & Execute List Hearing');

Scenario('Verify Manchester case List Hearing', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await listHearing(I, eventNames.LIST_HEARING, 'Manchester');

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
