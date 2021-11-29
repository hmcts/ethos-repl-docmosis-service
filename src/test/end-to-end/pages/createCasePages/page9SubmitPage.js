
const testConfig = require('../../../config');
const assert = require('assert');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('../../data/commonConfig.json');

module.exports = function () {
    const I = this;
    I.waitForText("Case Reference:", testConfig.TestTimeToWaitForText);
    I.see('Case Reference');
}


