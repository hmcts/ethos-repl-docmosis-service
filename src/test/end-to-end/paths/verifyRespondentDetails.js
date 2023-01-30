const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantRespondentDetails} = require("../helpers/caseHelper");

Feature('Create A Leeds Singles Case & Execute Claimant Respondent Details...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Respondent Details', async ({I}) => {
    await claimantRespondentDetails(I, eventNames.CLAIMANT_RESPONDENT_DETAILS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
