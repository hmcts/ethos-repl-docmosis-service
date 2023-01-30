const testConfig = require('./../../config');
const commonConfig = require('./../data/commonConfig.json');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {acceptCaseEvent, jurisdiction, closeCase} = require("../helpers/caseHelper");
let caseNumber;

Feature('Execute Case Close Scenario');

Scenario('Verify Case Close', async ({I}) => {
    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);
    await closeCase(I, eventNames.CLOSE_CASE, commonConfig.clerkResponsible, commonConfig.physicalLocation)

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
