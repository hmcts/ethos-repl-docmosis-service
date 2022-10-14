const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, createLeedsOfficeMultiples, getECMCaseID, getECMCaseNumber} = require("../helpers/caseHelper");
let caseNumber1;
let caseNumber2;

Feature('Create Leeds Multiples Office Case');

Scenario('Leeds Multiples Case...', async ({I}) => {

    caseNumber1 = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber1, eventNames.ACCEPT_CASE);
    let ecmCaseNumber1 = await getECMCaseID(I, caseNumber1);

    caseNumber2 = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    let ecmCaseNumber2 = await getECMCaseNumber(I, eventNames.ACCEPT_CASE, caseNumber2);

    await createLeedsOfficeMultiples(I, ecmCaseNumber1, ecmCaseNumber2)

}).tag('@test99')
    .retry(testConfig.TestRetryScenarios);
