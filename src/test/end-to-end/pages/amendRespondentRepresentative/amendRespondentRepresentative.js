'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (jurisdiction) {

    const I = this;
    if (jurisdiction === 'Manchester') {
        await I.click(commonConfig.addNewButton);
        await I.wait(5);
        await I.fillField('#repCollection_0_resp_rep_name', commonConfig.respondentName);
    } if (jurisdiction === 'Leeds')   {
        await I.selectOption('#repCollection_0_dynamic_resp_rep_name', commonConfig.respondentName);
    }
    await I.wait(2);
    await I.fillField('#repCollection_0_name_of_representative', commonConfig.respondentRepresentativeName);

    await I.fillField('#repCollection_0_representative_address_representative_address_postcodeInput', commonConfig.respondentRepPostCode);
    await I.click(commonConfig.findAddressButton);
    await I.wait(3);
    I.waitForText(commonConfig.respondentRepAddress, testConfig.TestTimeToWaitForText);
    await I.selectOption('#repCollection_0_representative_address_representative_address_addressList', commonConfig.respondentRepAddress);
    await I.wait(3);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit);
    await I.wait(5);
};
