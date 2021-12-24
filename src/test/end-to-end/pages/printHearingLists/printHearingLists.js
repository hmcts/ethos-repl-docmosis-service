'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (jurisdiction) {

    const I = this;
    I.waitForText(commonConfig.hearingList, testConfig.TestTimeToWaitForText);
    await I.click('#printHearingDetails_hearingDateType-Single');
    await I.fillField('#listingDate-day', commonConfig.hearingStartDate);
    await I.fillField('#listingDate-month', commonConfig.hearingStartDateMonth);
    await I.fillField('#listingDate-year', commonConfig.hearingStartDateYear);
    await I.selectOption('#printHearingDetails_listingVenue', jurisdiction);
    await I.selectOption('#printHearingDetails_hearingDocType', commonConfig.hearingDocType);
    await I.click('#printHearingDetails_hearingDocETCL-Public');
    await I.click('#printHearingDetails_roomOrNoRoom_Yes');
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
};
