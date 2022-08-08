'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.runAccessibilityTest();
    await I.retry(5).click('#preAcceptCase_caseAccepted_Yes');
    await I.fillField('#dateAccepted-day', commonConfig.caseAcceptedDay);
    await I.fillField('#dateAccepted-month', commonConfig.caseAcceptedMonth);
    await I.fillField('#dateAccepted-year', commonConfig.caseAcceptedYear);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
    await I.wait(2);
};

