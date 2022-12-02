const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, bfAction} = require("../helpers/caseHelper");

Feature('Create a Manchester Singles Case & Execute B/F Action');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester case B/F Action', async ({I}) => {
    await bfAction(I, eventNames.BF_ACTION);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
