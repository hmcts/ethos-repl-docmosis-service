const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {
    acceptCaseEvent,
    createLeedsOfficeMultiplesJourney,
    getECMCaseID,
    getECMCaseNumber
} = require("../helpers/caseHelper");
let caseId;

Feature('Create Leeds Office Multiples Case');

BeforeSuite(async ({I}) => caseId = await createCaseInCcd('test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Leeds Office Multiples Journey...', async ({I}) => {

    await acceptCaseEvent(I, caseId, eventNames.ACCEPT_CASE);
    let ecmCaseNumber1 = await getECMCaseID(I, caseId);

    let caseId1 = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    let ecmCaseNumber2 = await getECMCaseNumber(I, eventNames.ACCEPT_CASE, caseId1);

    await createLeedsOfficeMultiplesJourney(I, ecmCaseNumber1, ecmCaseNumber2)

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
