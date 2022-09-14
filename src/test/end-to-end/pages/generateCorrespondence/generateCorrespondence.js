'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.lettersCorrespondence, testConfig.TestTimeToWaitForText);
    await I.selectOption('#correspondenceType_topLevel_Documents', commonConfig.lettersCorrespondence);
    I.waitForText(commonConfig.lettersCorrespondence1, testConfig.TestTimeToWaitForText);
    await I.selectOption('#correspondenceType_part_2_Documents', commonConfig.lettersCorrespondence1);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};
