const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
let caseNumber;

Feature('Create a Leeds Single Case and move it to Accept Case state');

Scenario('Verify Accept Case', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await I.wait(5);
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseNumber);
    await I.waitForText("Next step");
    await I.chooseNextStep(eventNames.ACCEPT_CASE, 3);
    await I.acceptTheCase();

}).tag('@e2e')
    .tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
