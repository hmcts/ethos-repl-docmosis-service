const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const assert = require('assert');
const {rejectCaseEvent, caseDetailsEvent} = require("../helpers/caseHelper");
let caseNumber;

Feature('Verify whether the user able to move rejected case to case closed state');

Scenario('Move rejected case to case closed state error check', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await rejectCaseEvent(I, caseNumber, eventNames.REJECT_CASE);
    await caseDetailsEvent(I, caseNumber, eventNames.CASE_DETAILS, 'A Clerk', 'Case closed', 'Casework Table', 'Standard Track');

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
