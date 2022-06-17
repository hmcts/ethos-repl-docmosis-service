'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.requestedBy, testConfig.TestTimeToWaitForText);
    await I.selectOption('#restrictedReporting_dynamicRequestedBy', commonConfig.requestedBy);
    await I.click('#restrictedReporting_imposed_Yes');
    await I.click('#restrictedReporting_rule503b_Yes');
    await I.fillField('#startDate-day', commonConfig.rule503bStartDate);
    await I.fillField('#startDate-month', commonConfig.rule503bStartDateMonth);
    await I.fillField('#startDate-year', commonConfig.rule503bStartDateYear);
    I.waitForText(commonConfig.excludedFromRegister, testConfig.TestTimeToWaitForText);
    await I.selectOption('#restrictedReporting_excludedRegister', commonConfig.excludedFromRegister);
    await I.click('#restrictedReporting_deletedPhyRegister_Yes');
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};
