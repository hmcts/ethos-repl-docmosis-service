const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, respondentRepresentative} = require("../helpers/caseHelper");
let caseNumber;

Feature('Manchester Office Individual Single Case & Execute Respondent Representative');

Scenario('Verify Respondent Representative', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await respondentRepresentative(I, eventNames.RESPONDENT_REPRESENTATIVE);

}).tag('@nightly')
    .tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
