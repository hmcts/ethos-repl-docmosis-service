'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    await I.click(commonConfig.addNewButton);
    await I.waitForText(commonConfig.bfActionDescription, testConfig.TestTimeToWaitForText);
    await I.selectOption('#bfActions_0_cwActions', commonConfig.bfActionDescription);

    await I.fillField('#bfDate-day', commonConfig.bfDateDay);
    await I.fillField('#bfDate-month', commonConfig.bfDateMonth);
    await I.fillField('#bfDate-year', commonConfig.bfDateYear);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};
