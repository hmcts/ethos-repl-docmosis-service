const testConfig = require('./../../config');

Feature('Create CCD Case...').retry(testConfig.TestRetryFeatures);

Scenario('Verify CCD case created or not?', async () => {
    console.log('CCD CaseID ==>::  ' + testConfig.CCDCaseId);

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')
