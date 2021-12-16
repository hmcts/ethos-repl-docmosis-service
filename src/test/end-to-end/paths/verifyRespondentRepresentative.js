const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, respondentRepresentative} = require("../helpers/caseHelper");
let caseNumber;

Feature('Leeds Office Individual Single Case & Execute Respondent Representative');

Scenario('Verify Respondent Representative', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@e2e')
    .tag('@nightly')
    .tag('@ecm-522')
    .retry(testConfig.TestRetryScenarios);
