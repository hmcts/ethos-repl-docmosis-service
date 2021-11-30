const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");

Feature('Verify CCD Case Creation...').retry(testConfig.TestRetryFeatures);
let caseNumber;

Scenario('Check whether the user able to create a ccd case or not...', async () => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    console.log('CCD CaseID ==>::  ' + caseNumber);

}).retry(testConfig.TestRetryScenarios)
    .tag('@smoke')
    .tag('@nightly')

