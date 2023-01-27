const testConfig = require('./../../config');
const {createCaseInCcd} = require("../helpers/ccdDataStoreApi");
const {eventNames} = require('../pages/common/constants.js');
const {navigateCase, judgment} = require("../helpers/caseHelper");
let caseNumber;

Feature('Manchester Office Singles Case & Execute Judgment Event');

Before(async ({I}) => {
    await navigateCase(I, testConfig.MOCase);
});

Scenario('Verify Manchester Case Judgment', async ({I}) => {
    await judgment(I, eventNames.JUDGMENT);

}).tag('@np')
    .retry(testConfig.TestRetryScenarios);
