'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    await I.click(commonConfig.addNewButton);
    I.waitForText(commonConfig.jurisdictionCode, testConfig.TestTimeToWaitForText);
    await I.selectOption('#jurCodesCollection_0_juridictionCodesList', commonConfig.jurisdictionCode);
    I.waitForText(commonConfig.jurisdictionRule, testConfig.TestTimeToWaitForText);
    await I.selectOption('#jurCodesCollection_0_judgmentOutcome', commonConfig.jurisdictionRule);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
};
