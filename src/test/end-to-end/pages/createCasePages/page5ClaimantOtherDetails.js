
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../data/commonConfig.json');

module.exports = function () {
    const I = this;
    I.waitForText("Other details",testConfig.TestTimeToWaitForText);
    I.fillField('#claimantOtherType_claimant_occupation',"Claimant-Occupation")
    I.fillField('#claimantOtherType_claimant_employed_from-day',createCaseConfig.page2_Claimant_dateOfReceipt_day);
    I.fillField('#claimantOtherType_claimant_employed_from-month',createCaseConfig.page2_Claimant_dateOfReceipt_month);
    I.fillField('#claimantOtherType_claimant_employed_from-year',createCaseConfig.page2_Claimant_dateOfReceipt_year);
    I.click('#claimantOtherType_claimant_employed_currently-No');
    I.click('#claimantOtherType_claimant_disabled-No');
    I.navByClick(commonConfig.continueButton);
}


