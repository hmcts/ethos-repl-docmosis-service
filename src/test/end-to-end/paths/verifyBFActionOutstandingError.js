const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, bfActionsOutstanding} = require("../helpers/caseHelper");

Feature('Verify whether the CW able to close the case if any bf outstanding actions are exists on the case ');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Close Case B/F Outstanding Actions Error Message', async ({I}) => {
    await bfActionsOutstanding(I, eventNames.BF_ACTION);

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
