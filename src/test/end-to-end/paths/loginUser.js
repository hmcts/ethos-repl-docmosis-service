const testConfig = require('../../config');

Feature('ExUI Smoke Test');

Scenario('login to the manage case application', async ({I}) => {
    await I.authenticateWithIdam();

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')
