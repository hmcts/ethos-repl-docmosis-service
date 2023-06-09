'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    await I.runAccessibilityTest();
    await I.retry(5).click('#preAcceptCase_caseAccepted_Yes');
    await I.retry(2).fillField('#dateAccepted-day', commonConfig.caseAcceptedDay);
    await I.retry(2).fillField('#dateAccepted-month', commonConfig.caseAcceptedMonth);
    await I.retry(2).fillField('#dateAccepted-year', commonConfig.caseAcceptedYear);
    await I.retry(2).click(commonConfig.submit)
    await I.wait(testConfig.TestTimeToWait);
};

