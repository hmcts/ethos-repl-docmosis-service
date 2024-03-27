const {Logger} = require('@hmcts/nodejs-logging');
const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
const testConfig = require('../../config.js');
const env = testConfig.TestEnv;

async function getServiceToken() {
    const serviceSecret = testConfig.TestS2SAuthSecret;
    const s2sBaseUrl = `http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal`;
    const s2sAuthPath = '/testing-support/lease';
    const oneTimePassword = require('otp')({
        secret: serviceSecret
    }).totp();

    const serviceTokenRequest = await fetch(s2sBaseUrl + s2sAuthPath, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({microservice: 'ethos_repl_service', oneTimePassword})
    });
    return await serviceTokenRequest.text();
}

module.exports = {
    getServiceToken
}
