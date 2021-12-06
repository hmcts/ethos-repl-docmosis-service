'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function (clerkResponcible, physicalLocation, conciliationTrack) {

    const I = this;
    await I.selectOption('#clerkResponsible', clerkResponcible);
    await I.selectOption('#fileLocation', physicalLocation)
    await I.selectOption('#conciliationTrack', conciliationTrack)

    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
