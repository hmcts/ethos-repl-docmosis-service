const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent} = require("../helpers/caseHelper");
let caseNumber;

Feature('ECM Case Report Generation... ');

Scenario('Generate Report for ECM Case', async ({I}) => {
    await I.authenticateWithIdam();

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
