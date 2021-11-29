'use strict';

const requireDirectory = require('require-directory');
const steps = requireDirectory(module);

module.exports = () => {
    return actor({
        authenticateWithIdam: steps.IDAM.signIn,
        chooseNextStep: steps.nextStep.nextStep,
        acceptTheCase: steps.preAcceptanceCase.acceptCase,
        selectNewCase: steps.newCase.newCase,
        selectCaseTypeOptions: steps.createCase.createCase,
        enterCreateCasePage1: steps.createCasePages.page1initiateCase1,
        enterClaimantDetailsPage2: steps.createCasePages.page2claimantDetails,
        enterRespondentDetailPage3: steps.createCasePages.page3RespondentsDetails,
        enterClaimantWorkDetailsPage4: steps.createCasePages.page4claimantWorkAddressDetails,
        enterClaimantOtherDetailsPage5: steps.createCasePages.page5ClaimantOtherDetails,
        enterBroughtForwardDatesPage6: steps.createCasePages.page6BroughtForwardDates,
        enterClaimantRepresentedPage7: steps.createCasePages.page7claimantRepresented,
        enterUploadDocPage8: steps.createCasePages.page8UploadDocumentsPage,
        submitPage9: steps.createCasePages.page9SubmitPage
    });
};
