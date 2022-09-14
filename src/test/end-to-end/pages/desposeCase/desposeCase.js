'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (clerkResponsible, physicalLocation) {

    const I = this;
    I.waitForText(clerkResponsible, testConfig.TestTimeToWaitForText);
    await I.selectOption('#clerkResponsible', clerkResponsible);
    I.waitForText(physicalLocation, testConfig.TestTimeToWaitForText);
    await I.fillField('#fileLocation', physicalLocation);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
};
