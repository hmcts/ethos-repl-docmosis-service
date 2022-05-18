const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction, judgment} = require("../helpers/caseHelper");
let caseNumber;

Feature('Manchester Office Singles Case & Execute Judgment Event');

Scenario('Verify Manchester Case Judgment', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);
    await judgment(I, eventNames.JUDGMENT);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
