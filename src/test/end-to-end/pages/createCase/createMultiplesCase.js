'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (ecmCase1, ecmCase2) {
    const I = this;
    await I.wait(testConfig.TestTimeToWaitForMultiples);
    await I.navByClick(commonConfig.createCase);
    await I.wait(testConfig.TestTimeToWaitForMultiples);

    // await I.selectOption('#cc-jurisdiction', commonConfig.jurisdictionType);
    // await I.wait(testConfig.TestTimeToWaitForMultiples);
    // await I.scrollDown('#content > div')

    await I.selectOption('#cc-case-type', commonConfig.leedsMultiples);
    await I.wait(testConfig.TestTimeToWaitForMultiples);

    // await I.selectOption('#cc-event', commonConfig.createMultiple);
    // await I.navByClick(commonConfig.start);

    await I.click(commonConfig.continueButton.replace('Continue', 'Start'));

    await I.fillField('#multipleName', commonConfig.multiplesTest);
    await I.click(commonConfig.continueButton)

    await I.fillField('#leadCase', ecmCase1);
    await I.click(commonConfig.continueButton.replace('Continue', 'Add new'))
    await I.wait(testConfig.TestTimeToWaitForMultiples);

    await I.fillField('#caseIdCollection_0_ethos_CaseReference', ecmCase2);
    await I.navByClick(commonConfig.goButton);
    await I.navByClick(commonConfig.goButton);

    let multiplesCaseId = await I.grabTextFrom('.markdown > h1:nth-of-type(2)');
    await I.wait(testConfig.TestTimeToWaitForMultiples);
    console.log(multiplesCaseId);

};
