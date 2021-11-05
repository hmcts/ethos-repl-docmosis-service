'use strict';
const testConfig = require('src/test/config');
const createCaseConfig = require('src/test/end-to-end/pages/createCase/createCaseConfig');
Feature('Manchester individual Journey').retry(testConfig.TestRetryFeatures);

Scenario('01 BO Caveat E2E - Order summons', async ({ I }) =>  {

    // IdAM
    I.authenticateWithIdamIfAvailable();
    I.selectNewCase();
    I.selectCaseTypeOptions(createCaseConfig.list1_text, createCaseConfig.list2_text_case_type_manchester, createCaseConfig.list3_text_event);
    I.enterCreateCasePage1();
    I.enterClaimantDetailsPage2();
    I.enterRespondentDetailPage3();
    I.enterClaimantWorkDetailsPage4();
    I.enterClaimantOtherDetailsPage5();
    I.enterBroughtForwardDatesPage6();
    I.enterClaimantRepresentedPage7();
    I.enterUploadDocPage8();
    I.submitPage9();
}).retry(testConfig.TestRetryScenarios);
