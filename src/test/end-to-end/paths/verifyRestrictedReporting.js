const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, restrictedReporting} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Singles Case & Execute Restricted Reporting');

Scenario('Verify Restricted Reporting', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await restrictedReporting(I, eventNames.RESTRICTED_REPORTING);

}).tag('@e2e')
    .tag('@nightly')
    .tag('@ecm-518')
    .retry(testConfig.TestRetryScenarios);
