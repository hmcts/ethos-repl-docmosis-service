const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, caseDetailsEvent} = require("../helpers/caseHelper");

Feature('Verify whether the user able to move accepted case to case closed state');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
})

Scenario('Move Accepted case to case closed state error check', async ({I}) => {
    await caseDetailsEvent(I, testConfig.CCDCaseId, eventNames.CASE_DETAILS, 'A Clerk', 'Case closed', 'Casework Table', 'Standard Track');

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
