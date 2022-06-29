const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, scheduleHearingDuringTheWeekend} = require("../helpers/caseHelper");
let caseNumber;

Feature('Validation to stop users having the ability to list any type of hearing on a weekend (Saturday or Sunday).');

Scenario('Validate hearing error message if user schedules the hearing date during the weekend', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await scheduleHearingDuringTheWeekend(I, eventNames.LIST_HEARING, 'Leeds');

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
