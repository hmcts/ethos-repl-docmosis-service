const testConfig = require('./../../config');
const {caseState} = require('../pages/common/constants.js');
const commonConfig = require('../data/commonConfig.json');
let caseNumberText;
let ecmCaseID;

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
    await I.wait(testConfig.TestTimeToWait);
    await I.executeClaimantDetails();
}

async function claimantRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeClaimantRepresentative();
}

async function claimantRespondentDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeRespondentDetails();
}

async function respondentRepresentative(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeRespondentRepresentative();
}

async function jurisdiction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeAddAmendJurisdiction();
}

async function closeCase(I, eventName, clerkResponsible, physicalLocation) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeCloseCase(clerkResponsible, physicalLocation);
}

async function letters(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeLettersEvent();
}

async function restrictedReporting(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.setRestrictedReporting();
}

async function fixCaseAPI(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeFixCaseAPI();
}

async function bfAction(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeBFAction();
}

async function bfActionsOutstanding(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.executeBFActionsOutstanding();
}

async function listHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeAddAmendHearing(jurisdiction);
}

async function allocateHearing(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeAllocateHearing(jurisdiction);
}

async function hearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeHearingDetails();
}

async function updateHearingDetails(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.amendHearingDetails();
}

async function printHearingLists(I, eventName, jurisdiction) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executePrintHearingLists(jurisdiction);
}

async function caseTransfer(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeCaseTransfer();
}

async function judgment(I, eventName) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(testConfig.TestTimeToWait);
    await I.executeJudgment();
}

async function generateReport(I, jurisdiction, caseType, eventName) {
    await I.authenticateWithIdam();
    await I.wait(testConfig.TestTimeToWait);
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

async function getECMCaseNumber(I, caseId, eventName, eventState) {
    if (eventState === caseState.ACCEPTED) {
        await I.authenticateWithIdam();
        await I.amOnPage('/case-details/' + caseId);
        await I.wait(testConfig.TestTimeToWait);
    } else {
        await I.amOnPage('/case-details/' + caseId);
        await I.wait(testConfig.TestTimeToWait);
        await acceptCaseTest(I, caseId, eventName)
    }
    caseNumberText = await I.grabTextFrom(commonConfig.ecmCaseCss);
    ecmCaseID = caseNumberText.split(' ')[2];
    return ecmCaseID;
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
    navigateCase
};
