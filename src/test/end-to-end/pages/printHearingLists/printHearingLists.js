'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (jurisdiction) {

    const I = this;
    I.waitForText(commonConfig.hearingList, testConfig.TestTimeToWaitForText);
    await I.click('#printHearingDetails_hearingDateType-Single');
    await I.fillField('#listingDate-day', commonConfig.hearingDate);
    await I.fillField('#listingDate-month', commonConfig.hearingDateMonth);
    await I.fillField('#listingDate-year', commonConfig.hearingDateYear);
    await I.selectOption('#printHearingDetails_listingVenue', jurisdiction);
    await I.selectOption('#printHearingDetails_hearingDocType', commonConfig.hearingDocType);
    await I.click('#printHearingDetails_hearingDocETCL-Public');
    await I.click('#printHearingDetails_roomOrNoRoom_Yes');
    await I.navByClick(commonConfig.continue);
    await I.waitForText(commonConfig.dailyCauseList, testConfig.TestTimeToWaitForText);
    await I.waitForText(jurisdiction, testConfig.TestTimeToWaitForText);
    await I.navByClick(commonConfig.continue);
    await I.click('Print List');
    await I.wait(2);
    await I.click('Close and Return to case details');

};
