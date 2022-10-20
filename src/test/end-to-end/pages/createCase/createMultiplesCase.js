'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (caseId1, caseId2) {

    const I = this;
    await I.navByClick(commonConfig.createCase);
    await I.selectOption('#cc-jurisdiction', commonConfig.jurisdictionType);
    await I.selectOption('#cc-case-type', commonConfig.leedsMultiples);
    await I.selectOption('#cc-event', commonConfig.createMultiple);

    await I.navByClick(commonConfig.start);
    await I.waitForText(commonConfig.createMultiple, testConfig.TestTimeToWaitForText);
    await I.fillField('#multipleName', commonConfig.multiplesTest);
    await I.navByClick(commonConfig.continue);

    await I.fillField('#leadCase', caseId1);
    await I.click(commonConfig.addNewButton);
    await I.fillField('#caseIdCollection_0_ethos_CaseReference', caseId2);
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.submit);

    let multiplesCaseId = await I.grabTextFrom('.markdown > h1:nth-of-type(2)');
    console.log("Multiples Case Number ==>::" + multiplesCaseId);

};
