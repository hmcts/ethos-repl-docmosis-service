'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.clerkResponsible, testConfig.TestTimeToWaitForText);
    await I.selectOption('#clerkResponsible', commonConfig.clerkResponsible);
    await I.wait(2);
    I.waitForText(commonConfig.physicalLocation, testConfig.TestTimeToWaitForText);
    await I.fillField('#fileLocation', commonConfig.physicalLocation);
    await I.wait(2);

    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
