const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, listHearing} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Single Case & Execute List Hearing');

Scenario('Verify Leeds case List Hearing', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await listHearing(I, eventNames.LIST_HEARING, 'Leeds');

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
