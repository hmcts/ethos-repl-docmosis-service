const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, createLeedsOfficeMultiples} = require("../helpers/caseHelper");
let caseNumber1;
let caseNumber2;

Feature('Create Leeds Multiples Office Case');

Scenario('Leeds Multiples Case Creation...', async ({I}) => {

    caseNumber1 = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber1, eventNames.ACCEPT_CASE);

    caseNumber2 = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber2, eventNames.ACCEPT_CASE)
    await createLeedsOfficeMultiples(I, caseNumber1, caseNumber2)

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
