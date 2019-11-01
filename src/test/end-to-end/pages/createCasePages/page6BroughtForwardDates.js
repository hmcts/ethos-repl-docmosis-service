
const testConfig = require('src/test/config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.navByClick(commonConfig.continueButton);
}


