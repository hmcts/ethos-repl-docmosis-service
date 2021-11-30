
const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../data/commonConfig.json');

module.exports = function () {
    const I = this;
    I.navByClick(commonConfig.continueButton);
}


