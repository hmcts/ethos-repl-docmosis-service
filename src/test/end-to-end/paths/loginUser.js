const testConfig = require('../../config');

Feature('`Verify login smoke scenario');

Scenario('@smoke login to the manage case application', async ({ I }) => {
    I.authenticateWithIdamIfAvailable();
}).retry(testConfig.TestRetryScenarios);
