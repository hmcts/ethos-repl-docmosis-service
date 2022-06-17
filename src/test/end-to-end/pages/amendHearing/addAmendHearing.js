'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (jurisdiction) {

    const I = this;
    I.waitForText(commonConfig.listHearing, testConfig.TestTimeToWaitForText);
    await I.click(commonConfig.addNewButton);
    await I.fillField('#hearingCollection_0_hearingNumber', commonConfig.hearingNumber);
    await I.selectOption('#hearingCollection_0_Hearing_type', commonConfig.hearingType);
    await I.click('#hearingCollection_0_hearingFormat-Video');
    await I.selectOption('#hearingCollection_0_Hearing_venue', jurisdiction);
    await I.fillField('#hearingCollection_0_hearingEstLengthNum', commonConfig.hearingLength);
    await I.selectOption('#hearingCollection_0_hearingEstLengthNumType', commonConfig.hearingLengthType);
    await I.click('//input[@id=\'hearingCollection_0_hearingSitAlone-Sit Alone\']');
    await I.click('//div[@id=\'hearingCollection_0_hearingDateCollection\']/div/button');
    await I.fillField('#listedDate-day', commonConfig.hearingDate);
    await I.fillField('#listedDate-month', commonConfig.hearingDateMonth);
    await I.fillField('#listedDate-year', commonConfig.hearingDateYear);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
    await I.waitForEnabled({css: '#next-step'}, testConfig.TestTimeToWaitForText || 5);
};
