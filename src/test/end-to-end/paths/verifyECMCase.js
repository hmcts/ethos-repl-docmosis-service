const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");

Feature('Create ECM Case...').retry(testConfig.TestRetryFeatures);
let caseNumber;

Scenario('Leeds Case', async () => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    console.log('CCD CaseID ==>::  ' + caseNumber);

}).retry(testConfig.TestRetryScenarios)
    .tag('@e2e')
    .tag('@nightly')

