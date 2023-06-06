'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.hearingDetails, testConfig.TestTimeToWaitForText);
    await I.click(commonConfig.submit);
    await I.waitForEnabled({css: '#next-step'}, testConfig.TestTimeToWaitForText || 5);
};
