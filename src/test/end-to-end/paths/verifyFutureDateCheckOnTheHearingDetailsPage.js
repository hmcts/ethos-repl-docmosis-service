const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, listHearing, allocateHearing, updateHearingDetails} = require("../helpers/caseHelper");
let caseNumber;

Feature('Verify future date check on the hearing details page');

Scenario('Validate future date check on the hearing details page', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await listHearing(I, eventNames.LIST_HEARING, 'Leeds');
    await allocateHearing(I, eventNames.ALLOCATE_HEARING, 'Leeds');
    await updateHearingDetails(I, eventNames.HEARING_DETAILS);

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
