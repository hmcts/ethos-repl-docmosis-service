const testConfig = require('./../../config');
const {generateReport} = require("../helpers/caseHelper");
const commonConfig = require('../data/commonConfig.json');
const {eventNames} = require('../pages/common/constants.js');

Feature('Create Report... ');

Scenario('Generate Report for ECM Case', async ({I}) => {
    await generateReport(I, commonConfig.jurisdictionType, commonConfig.caseType, eventNames.CREATE_REPORT);

}).tag('@report')
    .retry(testConfig.TestRetryScenarios);
