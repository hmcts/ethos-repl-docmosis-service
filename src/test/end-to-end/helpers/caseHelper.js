const testConfig = require('./../../config');
const commonConfig = require('../data/commonConfig.json');
let caseNumberText;

async function acceptCaseEvent(I, caseId, eventName) {
    await navigateCase(I, caseId);
    await I.chooseNextStep(eventName, 3);
    await I.acceptTheCase();
}

async function rejectCaseEvent(I, caseId, eventName) {
    await navigateCase(I, caseId);
    await I.chooseNextStep(eventName, 3);
    await I.rejectTheCase();
}

async function submittedState(I, caseId) {
    await navigateCase(I, caseId);
}

async function caseDetails(I, caseId, eventName, clerkResponcible, physicalLocation, conciliationTrack) {
    await I.chooseNextStep(eventName, 3);
    await I.amendTheCaseDetails(clerkResponcible, physicalLocation, conciliationTrack);
}

async function caseDetailsEvent(I, caseId, eventName, clerkResponcible, currentPosition, physicalLocation, conciliationTrack) {
    await I.chooseNextStep(eventName, 3);
    await I.amendCaseDetailsWithCaseCurrentPosition(clerkResponcible, currentPosition, physicalLocation, conciliationTrack);
}

async function claimantDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeClaimantDetails();
}

async function claimantRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeClaimantRepresentative();
}

async function claimantRespondentDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeRespondentDetails();
}

async function respondentRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeRespondentRepresentative();
}

async function jurisdiction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeAddAmendJurisdiction();
}

async function closeCase(I, eventName, clerkResponsible, physicalLocation) {
    await I.chooseNextStep(eventName, 3);
    await I.executeCloseCase(clerkResponsible, physicalLocation);
}

async function letters(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeLettersEvent();
}

async function restrictedReporting(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.setRestrictedReporting();
}

async function fixCaseAPI(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeFixCaseAPI();
}

async function bfAction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeBFAction();
}

async function bfActionsOutstanding(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeBFActionsOutstanding();
}

async function listHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.executeAddAmendHearing(jurisdiction);
}

async function allocateHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.executeAllocateHearing(jurisdiction);
}

async function hearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeHearingDetails();
}

async function updateHearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.amendHearingDetails();
}

async function printHearingLists(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.executePrintHearingLists(jurisdiction);
}

async function caseTransfer(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeCaseTransfer();
}

async function judgment(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeJudgment();
}

async function generateReport(I, jurisdiction, caseType, eventName) {
    await I.authenticateWithIdam();
    await I.executeCreateReport(jurisdiction, caseType, eventName);
}

async function scheduleHearingDuringTheWeekend(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.executeHearingListedInWeekend(jurisdiction);
}

async function uploadDocumentEvent(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeUploadDocument();
}

async function leedsMultiplesJourney(I, caseId1, caseId2) {
    await I.amOnPage(testConfig.TestUrl);
    await I.createMultiplesCase(caseId1, caseId2);
}

async function leedsMultiplesTest(I, ecmCase) {
    await I.amOnPage(testConfig.TestUrl);
    await I.multiplesSingleCase(ecmCase);
}

async function amendMultipleDetailsTest(I, eventName, ecmCase) {
    await I.amOnPage('/case-details/' + testConfig.MultiplesCaseId);
    await I.wait(testConfig.TestTimeToWait);
    await I.chooseNextStep(eventName, 3);
    await I.amendMultipleDetails(ecmCase);
}

async function getECMCaseNumber(I, caseId, eventName) {
    await I.authenticateWithIdam();
    await I.amOnPage('/case-details/' + caseId);
    await I.wait(testConfig.TestTimeToWait);
    if (caseId !== testConfig.CCDCaseId) {
        await acceptCaseTest(I, caseId, eventName)
    }
    caseNumberText = await I.grabTextFrom(commonConfig.ecmCaseCss);
    return caseNumberText.split(' ')[2];
}

async function acceptCaseTest(I, caseId, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.acceptTheCase();
}

async function navigateCase(I, caseId) {
    await I.authenticateWithIdam();
    await I.amOnPage('/case-details/' + caseId);
    await I.wait(testConfig.TestTimeToWait);
}

module.exports = {
    acceptCaseEvent,
    rejectCaseEvent,
    submittedState,
    caseDetails,
    claimantDetails,
    claimantRepresentative,
    claimantRespondentDetails,
    respondentRepresentative,
    restrictedReporting,
    jurisdiction,
    closeCase,
    letters,
    fixCaseAPI,
    bfAction,
    listHearing,
    printHearingLists,
    allocateHearing,
    hearingDetails,
    caseTransfer,
    judgment,
    generateReport,
    updateHearingDetails,
    caseDetailsEvent,
    scheduleHearingDuringTheWeekend,
    bfActionsOutstanding,
    uploadDocumentEvent,
    leedsMultiplesJourney,
    getECMCaseNumber,
    acceptCaseTest,
    navigateCase,
    leedsMultiplesTest,
    amendMultipleDetailsTest
};
