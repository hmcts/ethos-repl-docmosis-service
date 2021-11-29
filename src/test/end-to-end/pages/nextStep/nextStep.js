'use strict';

const testConfig = require('src/test/config.js');
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function (nextStep, webDriverWait) {

    const I = this;

    await I.waitForEnabled({css: '#next-step'}, testConfig.TestTimeToWaitForText || 60);
    await I.retry(5).selectOption('#next-step', nextStep);
    await I.waitForEnabled(commonConfig.goButton, testConfig.TestTimeToWaitForText || 60);
    await I.waitForNavigationToComplete(commonConfig.goButton, webDriverWait);
};
