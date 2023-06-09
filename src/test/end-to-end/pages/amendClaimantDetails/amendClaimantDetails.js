'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.continue)
    await I.wait(15);
};
