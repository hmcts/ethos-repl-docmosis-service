const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
Feature('`Verify login smoke scenario');

Scenario('@smoke login to the manage case application', async ({ I }) => {
    I.authenticateWithIdamIfAvailable();
}); //.retry(testConfig.TestRetryScenarios);
