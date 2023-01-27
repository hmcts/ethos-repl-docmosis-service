const testConfig = require('./../../config');
const {letters} = require("../helpers/caseHelper");
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');

Feature('Create a Manchester Single Case & Execute Letters');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester case Letters', async ({I}) => {
    await letters(I, eventNames.LETTERS);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
