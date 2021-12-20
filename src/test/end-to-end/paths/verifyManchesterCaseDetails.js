const testConfig = require('./../../config');
const {createCaseInCcd, updateECMCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
const {acceptCaseEvent, caseDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Single Case and move to Case Details state');

Scenario('Verify Case Details ', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await caseDetails(I, caseNumber, eventNames.CASE_DETAILS, 'A Clerk', 'Casework Dropback Shelf', 'Standard track');

}).tag('@e2e')
    .tag('@manchester')
    .retry(testConfig.TestRetryScenarios);
