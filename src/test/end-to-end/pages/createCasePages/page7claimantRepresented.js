
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.click('#claimantRepresentedQuestion-Yes');
    I.fillField('#representativeClaimantType_name_of_representative','Claimant representative Name');
    I.fillField('#representativeClaimantType_name_of_organisation','Claimant representative Organisation');
    I.fillField('#representativeClaimantType_representative_reference','Claimant representative Reference');
    I.selectOption('#representativeClaimantType_representative_occupation','Solicitor')
    I.fillField('#representativeClaimantType_representative_address_representative_address_postcodeInput',createCaseConfig.page2_Claimant_enter_postcode);
    I.click('Find address');
    I.waitForText(createCaseConfig.page2_Claimant_Address, testConfig.TestTimeToWaitForText);
    I.selectOption('#representativeClaimantType_representative_address_representative_address_addressList',createCaseConfig.page2_Claimant_Address);
    I.navByClick(commonConfig.continueButton);
}


