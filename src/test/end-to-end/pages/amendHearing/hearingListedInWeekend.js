'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");
const {utilsComponent} = require("../../helpers/utils");

module.exports = async function (jurisdiction) {

    const I = this;
    I.waitForText(commonConfig.listHearing, testConfig.TestTimeToWaitForText);
    await I.wait(2);
    await I.click(commonConfig.addNewButton);
    await I.fillField('#hearingCollection_0_hearingNumber', commonConfig.hearingNumber);
    await I.selectOption('#hearingCollection_0_Hearing_type', commonConfig.hearingType);
    await I.click('#hearingCollection_0_hearingFormat-Video');
    await I.selectOption('#hearingCollection_0_Hearing_venue', jurisdiction);
    await I.fillField('#hearingCollection_0_hearingEstLengthNum', commonConfig.hearingLength);
    await I.selectOption('#hearingCollection_0_hearingEstLengthNumType', commonConfig.hearingLengthType);
    await I.click('//input[@id=\'hearingCollection_0_hearingSitAlone-Sit Alone\']');
    await I.click('//div[@id=\'hearingCollection_0_hearingDateCollection\']/div/button');

    let currentDate = await utilsComponent.isWeekend();
    await I.fillField('#listedDate-day', currentDate.split('-')[2]);
    await I.fillField('#listedDate-month', currentDate.split('-')[1]);
    await I.fillField('#listedDate-year', currentDate.split('-')[0]);
    await I.click(commonConfig.continue);
    await I.see(commonConfig.weekendHearingMsgError);
};
