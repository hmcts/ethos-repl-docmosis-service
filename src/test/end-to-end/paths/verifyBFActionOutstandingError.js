const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, bfActionsOutstanding} = require("../helpers/caseHelper");
let caseNumber;

Feature('Verify whether the CW able to close the case if any bf outstanding actions are exists on the case ');

Scenario('Verify Close Case B/F Outstanding Actions Error Message', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await bfActionsOutstanding(I, eventNames.BF_ACTION);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
