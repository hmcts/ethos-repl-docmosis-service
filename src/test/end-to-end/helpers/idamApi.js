const {Logger} = require('@hmcts/nodejs-logging');
const requestModule = require('request-promise-native');
const request = requestModule.defaults();
const testConfig = require('../../config.js');
const logger = Logger.getLogger('helpers/idamApi.js');

async function getUserToken() {
    const username = testConfig.TestEnvCWUser;
    const password = testConfig.TestEnvCWPassword;
    const redirectUri = testConfig.RedirectUri;
    const idamClientSecret = testConfig.TestIdamClientSecret;
    const idamBaseUrl = testConfig.IdamBaseUrl;
    const idamCodePath = `/oauth2/authorize?response_type=code&client_id=xuiwebapp&redirect_uri=${redirectUri}`;

    const codeResponse = await request.post({
        uri: idamBaseUrl + idamCodePath,
        headers: {
            Authorization: 'Basic ' + Buffer.from(`${username}:${password}`).toString('base64'),
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).catch(error => {
        console.log(error);
    });

    const code = JSON.parse(codeResponse).code;
    const idamAuthPath = `/oauth2/token?grant_type=authorization_code&client_id=xuiwebapp&client_secret=${idamClientSecret}&redirect_uri=${redirectUri}&code=${code}`;

    const authTokenResponse = await request.post({
        uri: idamBaseUrl + idamAuthPath,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });

    logger.debug(JSON.parse(authTokenResponse)['access_token']);
    return JSON.parse(authTokenResponse)['access_token'];
}

async function getUserId(authToken) {
    const idamBaseUrl = testConfig.IdamBaseUrl;
    const idamDetailsPath = '/details';

    const userDetails = await request.get({
        uri: idamBaseUrl + idamDetailsPath,
        headers: {
            Authorization: `Bearer ${authToken}`
        }
    });

    logger.debug(JSON.parse(userDetails).id);
    return JSON.parse(userDetails).id;
}

module.exports = {
    getUserToken,
    getUserId
};
