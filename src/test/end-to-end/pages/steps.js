'use strict';

const requireDirectory = require('require-directory');
const steps = requireDirectory(module);

module.exports = () => {
    return actor({
        authenticateWithIdam: steps.IDAM.signIn,
        chooseNextStep: steps.nextStep.nextStep,
        acceptTheCase: steps.acceptanceCase.acceptCase,
        rejectTheCase: steps.acceptanceCase.rejectCase,
        executeClaimantDetails: steps.amendClaimantDetails.amendClaimantDetails,
        executeClaimantRepresentative: steps.amendClaimantRepresentative.amendClaimantRepresentative,
        executeRespondentDetails: steps.amendRespondentDetails.amendRespondentDetails,
        amendTheCaseDetails: steps.amendCaseDetails.amendCaseDetails,
        amendCaseDetailsWithCaseCurrentPosition: steps.amendCaseDetails.amendCaseDetailsWithCasePosition,
        executeRespondentRepresentative: steps.amendRespondentRepresentative.amendRespondentRepresentative,
        executeAddAmendJurisdiction: steps.amendJurisdiction.addAmendJurisdiction,
        executeCloseCase: steps.desposeCase.desposeCase,
        executeLettersEvent: steps.generateCorrespondence.generateCorrespondence,
        setRestrictedReporting: steps.restrictedCases.restrictedCases,
        executeFixCaseAPI: steps.fixCaseApi.fixCaseApi,
        executeBFAction: steps.broughtForward.broughtForward,
        executeBFActionsOutstanding: steps.broughtForward.bfActionsOutstandingError,
        executeAddAmendHearing: steps.amendHearing.addAmendHearing,
        executeAllocateHearing: steps.allocateHearing.allocateHearing,
        executeHearingDetails: steps.hearingDetails.hearingDetails,
        amendHearingDetails: steps.hearingDetails.updateHearingDetails,
        executePrintHearingLists: steps.printHearingLists.printHearingLists,
        executeCaseTransfer: steps.caseTransfer.caseTransfer,
        executeJudgment: steps.amendJudgment.addAmendJudgment,
        executeCreateReport: steps.createReport.createReport,
        executeHearingListedInWeekend: steps.amendHearing.hearingListedInWeekend,
        executeUploadDocument: steps.uploadDocument.uploadDocument
    });
};
