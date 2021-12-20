'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.lettersCorrespondence, testConfig.TestTimeToWaitForText);
    await I.selectOption('#correspondenceType_topLevel_Documents', commonConfig.lettersCorrespondence);
    await I.wait(2);
    I.waitForText(commonConfig.lettersCorrespondence1, testConfig.TestTimeToWaitForText);
    await I.selectOption('#correspondenceType_part_2_Documents', commonConfig.lettersCorrespondence1);
    await I.wait(2);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
