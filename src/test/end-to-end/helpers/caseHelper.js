async function acceptCaseEvent(I, caseId, eventName) {
    await I.wait(5);
    await I.authenticateWithIdam();
    await I.wait(5);
    await I.amOnPage('/case-details/' + caseId);
    await I.chooseNextStep(eventName, 3);
    await I.acceptTheCase();
}

async function caseDetails(I, caseId, eventName, clerkResponcible, physicalLocation, conciliationTrack) {
    await I.chooseNextStep(eventName, 3);
    await I.wait(5);
    await I.amendTheCaseDetails(clerkResponcible, physicalLocation, conciliationTrack);
}

module.exports = {
    acceptCaseEvent,
    caseDetails
};
