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
    let ecmCase = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE);
    await leedsMultiplesTest(I, ecmCase);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Amend Multiple Details...', async ({I}) => {
    let ecmCase2 = await getECMCaseNumber(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);
    await amendMultipleDetailsTest(I, eventNames.AMEND_MULTIPLE_DETAILS, ecmCase2);

}).tag('@disabled');

Scenario('Leeds Multiples Journey ( Adding Two Singles)...', async ({I}) => {
    let ecmCaseNumber1 = await getECMCaseNumber(I, testConfig.CCDCaseId, eventNames.ACCEPT_CASE);
    let ecmCaseNumber2 = await getECMCaseNumber(I, caseId, eventNames.ACCEPT_CASE, caseState.CLOSED);

    await leedsMultiplesJourney(I, ecmCaseNumber1, ecmCaseNumber2);

}).tag('@bug')
    .retry(testConfig.TestRetryScenarios);
