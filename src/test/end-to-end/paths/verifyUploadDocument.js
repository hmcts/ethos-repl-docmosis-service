const testConfig = require('./../../config');
const {uploadDocumentEvent} = require("../helpers/caseHelper");
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {submittedState} = require("../helpers/caseHelper");
let caseNumber;

Feature('Validate Upload Document');

Scenario('Verify Upload Document', async ({I}) => {

    caseNumber = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
    await submittedState(I, caseNumber);
    await uploadDocumentEvent(I, eventNames.UPLOAD_DOCUMENT);

}).tag('@nightly')
    .retry(testConfig.TestRetryScenarios);
