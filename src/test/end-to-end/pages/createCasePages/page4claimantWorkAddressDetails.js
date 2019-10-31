
const testConfig = require('src/test/config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.fillField('#claimantWorkAddress_claimant_work_address_claimant_work_address_postcodeInput',createCaseConfig.page2_Claimant_enter_postcode);
    I.click('Find address');
    I.waitForText(createCaseConfig.page2_Claimant_Address, testConfig.TestTimeToWaitForText);
    I.selectOption('#claimantWorkAddress_claimant_work_address_claimant_work_address_addressList',createCaseConfig.page2_Claimant_Address);
    I.fillField('#claimantWorkAddress_claimant_work_phone_number','07545480473');
    I.navByClick(commonConfig.continueButton);
}
