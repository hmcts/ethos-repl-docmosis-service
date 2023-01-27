const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, respondentRepresentative} = require("../helpers/caseHelper");

Feature('Manchester Office Individual Single Case & Execute Respondent Representative');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Respondent Representative', async ({I}) => {
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
