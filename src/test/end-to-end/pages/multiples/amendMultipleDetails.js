'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (ecmCase) {
    const I = this;
    await I.click(commonConfig.addCasesToMultipleCss);
    await I.navByClick(commonConfig.continue);
    await I.wait(testConfig.TestTimeToWaitForMultiples);
    await I.click(commonConfig.addNewBtn);
    await I.wait(testConfig.TestTimeToWait);
    await I.retry(5).fillField('#caseIdCollection_0_ethos_CaseReference', ecmCase);
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.submit);
    await I.wait(testConfig.TestTimeToWait);
};
