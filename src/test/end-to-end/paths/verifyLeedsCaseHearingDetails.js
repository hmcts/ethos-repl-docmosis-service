const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, listHearing, allocateHearing, hearingDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Single Case & Execute Hearing details');

Scenario('Verify Leeds case Hearing details', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await listHearing(I, eventNames.LIST_HEARING, 'Leeds');
    await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Leeds');
    await hearingDetails(I, eventNames.HEARING_DETAILS);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
