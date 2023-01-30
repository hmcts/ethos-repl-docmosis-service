const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction, judgment} = require("../helpers/caseHelper");
let caseNumber;

Feature('Leeds Office Singles Case & Execute Judgment Event');

Scenario('Verify Leeds Case Judgment', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);
    await judgment(I, eventNames.JUDGMENT);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
