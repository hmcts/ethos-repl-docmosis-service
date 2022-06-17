const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const assert = require('assert');
const {acceptCaseEvent, caseDetailsEvent} = require("../helpers/caseHelper");
let caseNumber;

const verifyState = (eventResponse, state) => {
    assert.strictEqual(JSON.parse(eventResponse).state, state);
};

Feature('Verify whether the user able to move accepted/rejected/submitted case to case closed state');

Scenario('Validate whether the user able to move Accepted case to case closed state', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await caseDetailsEvent(I, caseNumber, eventNames.CASE_DETAILS, 'A Clerk', 'Case closed', 'Casework Table', 'Standard Track');

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
