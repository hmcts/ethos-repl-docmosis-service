'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    await I.fillField('#responseReceivedDate-day', commonConfig.caseAcceptedDay);
    await I.fillField('#responseReceivedDate-month', commonConfig.caseAcceptedMonth);
    await I.fillField('#responseReceivedDate-year', commonConfig.caseAcceptedYear);

    await I.fillField('#respondentCollection_0_responseRespondentAddress_responseRespondentAddress_postcodeInput', commonConfig.respondentPostCode);
    await I.click('#respondentCollection_0_responseRespondentAddress_responseRespondentAddress_postcodeLookup > button');
    await I.wait(2);
    I.waitForText(commonConfig.respondentAddress, testConfig.TestTimeToWaitForText);
    await I.selectOption('#respondentCollection_0_responseRespondentAddress_responseRespondentAddress_addressList', commonConfig.respondentAddress);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};
