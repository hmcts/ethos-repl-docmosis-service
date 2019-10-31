
const testConfig = require('src/test/config');
const assert = require('assert');
const createCaseConfig = require('./createCaseConfig');
const commonConfig = require('src/test/end-to-end/pages/common/commonConfig');

module.exports = function () {
    const I = this;
    I.waitForText("Case Reference:", testConfig.TestTimeToWaitForText);
    I.see('Case Reference');
}


