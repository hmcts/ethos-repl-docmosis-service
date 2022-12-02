const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, letters} = require("../helpers/caseHelper");

Feature('Create a Leeds Single Case & Execute Letters');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Letters', async ({I}) => {
    await letters(I, eventNames.LETTERS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
