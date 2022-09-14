const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction} = require("../helpers/caseHelper");
let caseNumber;

Feature('Manchester Office Single Case & Execute Jurisdiction Event');

Scenario('Verify Jurisdiction', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
