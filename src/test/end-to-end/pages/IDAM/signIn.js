'use strict';

const testConfig = require('../../../config');

module.exports = function () {

    const I = this;

    I.amOnPage('/');
    I.see('Sign in');
    I.waitForText('Sign in');
    I.fillField('username', testConfig.TestEnvCWUser);
    I.fillField('password', testConfig.TestEnvCWPassword);
    I.click('input[value="Sign in"]');
    I.waitForText('Case list');
};
