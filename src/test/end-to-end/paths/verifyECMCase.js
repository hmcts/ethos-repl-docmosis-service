const testConfig = require('./../../config');

Feature(' Create CCD Case...').retry(testConfig.TestRetryFeatures);

Scenario('Leeds Office Case', async () => {
    console.log('Leeds Office Case ==>::  ' + testConfig.CCDCaseId);

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')

Scenario('Manchester Office Case', async () => {
    console.log('Manchester Office Case ==>::  ' + testConfig.MOCase);

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')
