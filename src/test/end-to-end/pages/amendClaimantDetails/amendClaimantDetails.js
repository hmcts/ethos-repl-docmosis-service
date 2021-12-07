'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.navByClick(commonConfig.continue);
    await I.wait(2);
    await I.click(commonConfig.submit)
    await I.wait(5);
};
