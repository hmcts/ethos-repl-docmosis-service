
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.navByClick(commonConfig.continueButton);
}


