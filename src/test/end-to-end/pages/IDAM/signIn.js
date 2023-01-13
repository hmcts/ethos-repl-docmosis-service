'use strict';

const testConfig = require('../../../config');

module.exports = async function () {

    const I = this;
    await I.amOnLoadedPage('/', testConfig.TestTimeToWaitForText);
    await I.waitForText('Sign in');
    await I.fillField('username', testConfig.TestEnvCWUser);
    await I.fillField('password', testConfig.TestEnvCWPassword);
    await I.click('input[value="Sign in"]');
    await I.waitForText('Case list');
};
