const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, caseState} = require('../pages/common/constants.js');
const {leedsMultiplesJourney, getECMCaseNumber} = require("../helpers/caseHelper");
let caseId;

Feature('Create Leeds Office Multiples Case');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Leeds Office Multiples Journey...', async ({I}) => {
    let ecmCaseNumber1 = await getECMCaseNumber(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE, caseState.ACCEPTED);
    let ecmCaseNumber2 = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE, caseState.CLOSED);
    await leedsMultiplesJourney(I, ecmCaseNumber1, ecmCaseNumber2)

}).tag('@wip')
    .retry(testConfig.TestRetryScenarios);
