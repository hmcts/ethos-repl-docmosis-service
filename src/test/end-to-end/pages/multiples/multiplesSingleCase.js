'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (ecmCase1) {
    const I = this;
    await I.wait(testConfig.TestTimeToWaitForMultiples);
    await I.navByClick(commonConfig.createCase);
    await I.wait(testConfig.TestTimeToWaitForMultiples);

    await I.selectOption('#cc-case-type', commonConfig.leedsMultiples);
    await I.waitForText('Start', testConfig.TestTimeToWaitForMultiples, commonConfig.goButton);
    await I.navByClick(commonConfig.start);

    await I.fillField('#multipleName', commonConfig.multiplesTest);
    await I.navByClick(commonConfig.continue);

    await I.fillField('#leadCase', ecmCase1);
    await I.navByClick(commonConfig.goButton);
    await I.navByClick(commonConfig.goButton);

    console.log(await I.grabTextFrom('.markdown > h1:nth-of-type(2)'));
    await I.wait(testConfig.TestTimeToWaitForMultiples);

    let url = await I.grabCurrentUrl();
    let caseId = url.split('/');
    testConfig.MultiplesCaseId = caseId[5].split('#')[0];
    console.log(testConfig.MultiplesCaseId);
};
