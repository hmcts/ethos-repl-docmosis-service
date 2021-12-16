const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction} = require("../helpers/caseHelper");
let caseNumber;

Feature('Leeds Office Single Case & Execute Jurisdiction Event');

Scenario('Verify Jurisdiction', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);

}).tag('@e2e')
    .tag('@nightly')
    .tag('@ecm-490')
    .retry(testConfig.TestRetryScenarios);
