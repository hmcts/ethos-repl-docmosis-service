'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    await I.click(commonConfig.claimantRepresentativeYes);
    await I.fillField('#representativeClaimantType_name_of_representative', commonConfig.claimantRepresentativeName);
    await I.fillField('#representativeClaimantType_representative_address_representative_address_postcodeInput', commonConfig.claimantRepresentativePostCode);
    await I.click(commonConfig.findAddressButton);
    await I.wait(3);
    I.waitForText(commonConfig.claimantRepresentativeAddress, testConfig.TestTimeToWaitForText);
    await I.selectOption('#representativeClaimantType_representative_address_representative_address_addressList', commonConfig.claimantRepresentativeAddress);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};
