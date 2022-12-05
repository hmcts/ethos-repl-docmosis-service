const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, claimantRespondentDetails} = require("../helpers/caseHelper");

Feature('Manchester Single Case & Execute Claimant Respondent Details...');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Respondent Details', async ({I}) => {
    await claimantRespondentDetails(I, eventNames.CLAIMANT_RESPONDENT_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
