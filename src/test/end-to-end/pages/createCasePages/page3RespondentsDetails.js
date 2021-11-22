const randomstring = require('randomstring');
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.click('Add new');
    I.waitForText(createCaseConfig.page3_Name_Of_respondent,testConfig.TestTimeToWaitForText);
    I.fillField('#respondentCollection_0_respondent_name',createCaseConfig.page3_Name_Of_respondent + randomstring.generate({length: 7, charset: 'numeric'}))
    I.click('#respondentCollection_0_respondent_ACAS_question-Yes');
    I.fillField('#respondentCollection_0_respondent_ACAS','4324242323')
    I.fillField('#respondentCollection_0_respondent_phone1','07545480473');
    I.fillField('#respondentCollection_0_respondent_address_respondent_address_postcodeInput',createCaseConfig.page2_Claimant_enter_postcode);
    I.click('Find address');
    I.waitForText(createCaseConfig.page2_Claimant_Address, testConfig.TestTimeToWaitForText);
    I.selectOption('#respondentCollection_0_respondent_address_respondent_address_addressList',createCaseConfig.page2_Claimant_Address);
    I.wait(5);
    I.click('#claimantWorkAddressQuestion-No');
    I.navByClick(commonConfig.continueButton);
}
