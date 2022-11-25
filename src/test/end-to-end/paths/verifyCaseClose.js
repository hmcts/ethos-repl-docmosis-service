const testConfig = require('./../../config');
const commonConfig = require('./../data/commonConfig.json');
const {navigateCase} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {jurisdiction, closeCase} = require("../helpers/caseHelper");

Feature('Execute Case Close Scenario');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
})
Scenario('Verify Case Close', async ({I}) => {
    await jurisdiction(I, eventNames.JURISDICTION);
    await closeCase(I, eventNames.CLOSE_CASE, commonConfig.clerkResponsible, commonConfig.physicalLocation)

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);
