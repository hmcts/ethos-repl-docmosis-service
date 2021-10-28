const {Logger} = require('@hmcts/nodejs-logging');
const requestModule = require('request-promise-native');
const request = requestModule.defaults();
const fs = require('fs');
const testConfig = require('../tests/config.js');
const logger = Logger.getLogger('helpers/s2sService.js');
const env = testConfig.TestEnv;

async function getServiceToken() {

    logger.info('Getting Service Token');
    const serviceSecret = testConfig.TestS2SAuthSecret;

    const s2sBaseUrl = `http://rpe-service-auth-provider-${env}.service.core-compute-${env}.internal`;
    const s2sAuthPath = '/lease';
    const oneTimePassword = require('otp')({
        secret: serviceSecret
    }).totp();

    const serviceToken = await request({
        method: 'POST',
        uri: s2sBaseUrl + s2sAuthPath,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            microservice: 'ethos_repl_service',
            oneTimePassword
        })
    });

    logger.debug(serviceToken);
    return serviceToken;
}

module.exports = {
    getServiceToken
};
