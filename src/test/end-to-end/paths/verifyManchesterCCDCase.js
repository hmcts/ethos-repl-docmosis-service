const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, states} = require('../pages/common/constants.js');
const assert = require('assert');
const {acceptCaseEvent, caseDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Manchester Single Case and move to Accepted state');

Scenario('Verify Manchester Accept Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await I.wait(5);
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseNumber);
    await I.chooseNextStep(eventNames.ACCEPT_CASE, 3);
    await I.acceptTheCase();

}).tag('@e2e')
    .tag('@nightly')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Manchester Reject Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await I.wait(5);
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseNumber);
    await I.chooseNextStep(eventNames.REJECT_CASE, 3);
    await I.rejectTheCase();

}).tag('@e2e')
    .tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
