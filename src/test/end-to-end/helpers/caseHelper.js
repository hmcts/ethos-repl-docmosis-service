async function acceptCaseEvent(I, caseId, eventName) {
    await I.authenticateWithIdam();
    await I.wait(2);
    await I.amOnPage('/case-details/' + caseId);
    await I.chooseNextStep(eventName, 3);
    await I.acceptTheCase();
}

async function rejectCaseEvent(I, caseId, eventName) {
    await I.authenticateWithIdam();
    await I.amOnPage('/case-details/' + caseId);
    await I.chooseNextStep(eventName, 3);
    await I.rejectTheCase();
}

async function submittedState(I, caseId) {
    await I.authenticateWithIdam();
    await I.amOnPage('/case-details/' + caseId);
}

async function caseDetails(I, caseId, eventName, clerkResponcible, physicalLocation, conciliationTrack) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.amendTheCaseDetails(clerkResponcible, physicalLocation, conciliationTrack);
}

async function caseDetailsEvent(I, caseId, eventName, clerkResponcible, currentPosition, physicalLocation, conciliationTrack) {
    await I.chooseNextStep(eventName, 3);
    await I.amendCaseDetailsWithCaseCurrentPosition(clerkResponcible, currentPosition, physicalLocation, conciliationTrack);
}

async function claimantDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeClaimantDetails();
}

async function claimantRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeClaimantRepresentative();
}

async function claimantRespondentDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeRespondentDetails();
}

async function respondentRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeRespondentRepresentative();
}

async function jurisdiction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeAddAmendJurisdiction();
}

async function closeCase(I, eventName, clerkResponsible, physicalLocation) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeCloseCase(clerkResponsible, physicalLocation);
}

async function letters(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeLettersEvent();
}

async function restrictedReporting(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.setRestrictedReporting();
}

async function fixCaseAPI(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeFixCaseAPI();
}

async function bfAction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeBFAction();
}

async function bfActionsOutstanding(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeBFActionsOutstanding();
}

async function listHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeAddAmendHearing(jurisdiction);
}

async function allocateHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeAllocateHearing(jurisdiction);
}

async function hearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeHearingDetails();
}

async function updateHearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.amendHearingDetails();
}

async function printHearingLists(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(3);
    await I.executePrintHearingLists(jurisdiction);
}

async function caseTransfer(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeCaseTransfer();
}

async function judgment(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(2);
    await I.executeJudgment();
}

async function generateReport(I, jurisdiction, caseType, eventName) {
    await I.authenticateWithIdam();
    await I.wait(2);
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
    hearingDetails,
    caseTransfer,
    judgment,
    generateReport,
    updateHearingDetails,
    caseDetailsEvent,
    scheduleHearingDuringTheWeekend,
    bfActionsOutstanding,
    uploadDocumentEvent
};
