'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.caseTransfer, testConfig.TestTimeToWaitForText);
    I.selectOption('#officeCT', commonConfig.caseTransferOffice);
    I.selectOption('#positionTypeCT', commonConfig.caseTransferPositionType);
    I.fillField('#reasonForCT', 'Claimant lives near to the selected Jurisdiction');
    await I.navByClick(commonConfig.continue);
    I.waitForText('Check your answers', testConfig.TestTimeToWaitForText);
    await I.click('Transfer Case');
    await I.waitForText(`Case Transfer: Transferred to ${commonConfig.caseTransferOffice}`, testConfig.TestTimeToWaitForText);
};
