const testConfig = require('./../../config');
const {uploadDocumentEvent} = require("../helpers/caseHelper");
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase} = require("../helpers/caseHelper");

Feature('Validate Upload Document');

Before(async ({I}) => {
    await navigateCase(I, testConfig.CCDCaseId);
});

Scenario('Verify Upload Document', async ({I}) => {
    await uploadDocumentEvent(I, eventNames.UPLOAD_DOCUMENT);

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
