const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames, caseState} = require('../pages/common/constants.js');
const {
    leedsMultiplesJourney,
    getECMCaseNumber,
    leedsMultiplesTest,
    amendMultipleDetailsTest
} = require("../helpers/caseHelper");
let caseId;

Feature('Leeds Office Multiples');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Leeds Multiples Journey...', async ({I}) => {
    let ecmCaseNumber1 = await getECMCaseNumber(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE, caseState.ACCEPTED);
    let ecmCaseNumber2 = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE, caseState.CLOSED);

    await leedsMultiplesJourney(I, ecmCaseNumber1, ecmCaseNumber2);

}).tag('@bug')
    .retry(testConfig.TestRetryScenarios);

Scenario('Leeds Multiples Journey...', async ({I}) => {
    let ecmCase = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE, caseState.SUBMITTED);
    await leedsMultiplesTest(I, ecmCase);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Amend Multiple Details...', async ({I}) => {
    let caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    let ecmCase = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE, caseState.SUBMITTED);
    await amendMultipleDetailsTest(I, ecmCase);

}).tag('@wip')
    .retry(testConfig.TestRetryScenarios);

