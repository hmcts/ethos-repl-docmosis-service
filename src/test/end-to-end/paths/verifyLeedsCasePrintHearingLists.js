const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, listHearing, allocateHearing, printHearingLists} = require("../helpers/caseHelper");
let caseNumber;

Feature('Create a Leeds Single Case & Execute Print Hearing Lists');

Scenario('Verify Print Hearing Lists', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await listHearing(I, eventNames.LIST_HEARING, 'Leeds');
    await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Leeds');
    await printHearingLists(I, eventNames.PRINT_HEARING_LISTS, 'Leeds');

}).tag('@printHearing')
    .retry(testConfig.TestRetryScenarios);
