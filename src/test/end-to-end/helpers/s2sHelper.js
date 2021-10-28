const {Logger} = require('@hmcts/nodejs-logging');
const requestModule = require('request-promise-native');
const request = requestModule.defaults();
const testConfig = require('../tests/config.js');
const logger = Logger.getLogger('helpers/s2sHelper.js');

async function getServiceToken() {
    const serviceSecret = testConfig.TestS2SAuthSecret;
    const s2sBaseUrl = `http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal`;
    const s2sAuthPath = '/testing-support/lease';
    const oneTimePassword = require('otp')({
        secret: serviceSecret
    }).totp();

    const serviceToken = await request({
        method: 'POST',
        uri: s2sBaseUrl + s2sAuthPath,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({microservice: 'nfdiv_case_api', oneTimePassword})
    });

    logger.debug(serviceToken);
    return serviceToken;
}

module.exports = {
    getServiceToken
}
