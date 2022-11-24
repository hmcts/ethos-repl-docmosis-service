const testConfig = require('./../../config');

Feature('Verify CCD Case Creation...').retry(testConfig.TestRetryFeatures);

Scenario('Check whether the user able to create a ccd case or not...', async () => {
    console.log('CCD CaseID ==>::  ' + testConfig.CCDCaseId);

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')
