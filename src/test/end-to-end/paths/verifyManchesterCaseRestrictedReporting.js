const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, restrictedReporting} = require("../helpers/caseHelper");

Feature('Create a Manchester Singles Case & Execute Restricted Reporting');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester case Restricted Reporting', async ({I}) => {
    await restrictedReporting(I, eventNames.RESTRICTED_REPORTING);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
