'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.waitForNavigationToComplete(commonConfig.claimantRepresentativeYes, 3);
    await I.fillField('#representativeClaimantType_name_of_representative', commonConfig.claimantRepresentativeName);
    await I.fillField('#representativeClaimantType_representative_address_representative_address_postcodeInput', 'E14 3XA')
    await I.click(commonConfig.findAddressButton);
    await I.wait(3);

    await I.waitForElement('//select[@id="address[addressList]"]');
    await I.selectOption('//select[@id="address[addressList]"]', ' Flat 13, Vermeer Court, 1 Rembrandt Close, London, E14 3XA');
    await I.wait(2);

    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
