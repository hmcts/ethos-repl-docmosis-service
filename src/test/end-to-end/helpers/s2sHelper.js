const testConfig = require('../../config.js');
const env = testConfig.TestEnv;
const totp = require("totp-generator")
const {I} = inject();
const {expect} = require('chai');
const s2sBaseUrl = `http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal/testing-support/lease`;

async function getServiceToken() {
    const oneTimePassword = totp(testConfig.S2SAuthSecret, {digits: 6, period: 30});

    let s2sHeaders = {
        'Content-Type': 'application/json'
    };

    let s2sPayload = {
        'microservice': 'ccd_gw',
        'oneTimePassword': oneTimePassword
    }

    const s2sResponse = await I.sendPostRequest(s2sBaseUrl, s2sPayload, s2sHeaders);
    let serviceToken = s2sResponse.data;
    expect(s2sResponse.status).to.eql(200)
    return serviceToken;
}

module.exports = {
    getServiceToken
}
