const randomstring = require('randomstring');
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../data/commonConfig.json');

module.exports = function () {
    const I = this;
    I.click("#claimant_TypeOfClaimant-Individual");
    I.selectOption('#claimantIndType_claimant_title1',createCaseConfig.page2_Claimant_Title);
    I.fillField('#claimantIndType_claimant_first_names',createCaseConfig.page2_Claimant_First_Name + randomstring.generate({length: 5, charset: 'numeric'}));
    I.fillField('#claimantIndType_claimant_last_name',createCaseConfig.page2_Claimant_Last_Name);
    I.fillField('#claimantIndType_claimant_date_of_birth-day',createCaseConfig.page2_Claimant_dateOfReceipt_day);
    I.fillField('#claimantIndType_claimant_date_of_birth-month',createCaseConfig.page2_Claimant_dateOfReceipt_month);
    I.fillField('#claimantIndType_claimant_date_of_birth-year',createCaseConfig.page2_Claimant_dateOfReceipt_year);
    I.selectOption('#claimantIndType_claimant_gender',createCaseConfig.page2_Claimant_gender);
    I.fillField('#claimantType_claimant_addressUK_claimant_addressUK_postcodeInput',createCaseConfig.page2_Claimant_enter_postcode);
    I.click('.button');
    I.waitForText(createCaseConfig.page2_Claimant_Address, testConfig.TestTimeToWaitForText);
    I.selectOption('#claimantType_claimant_addressUK_claimant_addressUK_addressList',createCaseConfig.page2_Claimant_Address);
    I.navByClick(commonConfig.continueButton);
}
