'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.waitForNavigationToComplete(commonConfig.claimantRepresentativeYes, 3);
    await I.fillField('#representativeClaimantType_name_of_representative', commonConfig.claimantRepresentativeName);
    await I.fillField('#representativeClaimantType_representative_address_representative_address_postcodeInput', commonConfig.claimantRepresentativePostCode);
    await I.click(commonConfig.findAddressButton);
    await I.wait(3);

    await I.selectOption('//select[@id="representativeClaimantType_representative_address_representative_address_addressList"]', commonConfig.claimantRepresentativeAddress);
    await I.wait(2);

    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
