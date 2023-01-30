const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, respondentRepresentative} = require("../helpers/caseHelper");

Feature('Leeds Office Individual Case & Execute Respondent Representative');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Respondent Representative', async ({I}) => {
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
