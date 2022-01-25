const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, claimantRespondentDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create A Single Case & Execute Claimant Respondent Details...');

Scenario('Verify Respondent Details', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await claimantRespondentDetails(I, eventNames.CLAIMANT_RESPONDENT_DETAILS);

}).tag('@e2e')
    .tag('@manchester')
    .tag('@crossbrowser')
    .retry(testConfig.TestRetryScenarios);
