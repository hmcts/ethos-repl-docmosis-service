const testConfig = require('./../../config');
const {eventNames} = require('../pages/common/constants.js');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const commonConfig = require('./../data/commonConfig.json');
const {
    rejectCaseEvent, navigateCase, caseTransfer,
    acceptCaseEvent, jurisdiction, closeCase
} = require("../helpers/caseHelper");

let caseId;

Feature('Leeds Office Scenarios ...');
BeforeSuite(async ({I}) => caseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json'));

Scenario('Verify Reject Case', async ({I}) => {
    await rejectCaseEvent(I, caseId, eventNames.REJECT_CASE);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Leeds Case Transfer', async ({I}) => {
    let caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await navigateCase(I, caseNumber);
    await caseTransfer(I, eventNames.CASE_TRANSFER);

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

Scenario('Verify Case Close', async ({I}) => {
    let caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await acceptCaseEvent(I, caseNumber, eventNames.ACCEPT_CASE);
    await jurisdiction(I, eventNames.JURISDICTION);
    await closeCase(I, eventNames.CLOSE_CASE, commonConfig.clerkResponsible, commonConfig.physicalLocation)

}).tag('@e2e')
    .retry(testConfig.TestRetryScenarios);

