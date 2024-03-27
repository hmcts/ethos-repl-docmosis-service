const {Logger} = require('@hmcts/nodejs-logging');
const fetch = (...args) => import('node-fetch').then(({default: fetch}) => fetch(...args));
const testConfig = require('../../config.js');
const logger = Logger.getLogger('helpers/idamApi.js');

async function getUserToken() {
    const username = testConfig.TestEnvCWUser;
    const password = testConfig.TestEnvCWPassword;
    const redirectUri = testConfig.RedirectUri;
    const idamClientSecret = testConfig.TestIdamClientSecret;
    const idamBaseUrl = testConfig.IdamBaseUrl;
    const idamCodePath = `/oauth2/authorize?response_type=code&client_id=xuiwebapp&redirect_uri=${redirectUri}`;

    const idamCodeResponse = await fetch(idamBaseUrl + idamCodePath, {
        method: 'POST',
        headers: {
            'Authorization': 'Basic ' + Buffer.from(`${username}:${password}`).toString('base64'),
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).catch(error => {
        console.log(error);
    });

    const codeJson = await idamCodeResponse.json();

    const code = codeJson.code;

    const idamAuthPath = `/oauth2/token?grant_type=authorization_code&client_id=xuiwebapp&client_secret=${idamClientSecret}&redirect_uri=${redirectUri}&code=${code}`;

    const authTokenResponse = await fetch(idamBaseUrl + idamAuthPath, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    });

    const accessTokenResponse = await authTokenResponse.json();
    return accessTokenResponse.access_token;
}

async function getUserId(authToken) {
    const idamBaseUrl = testConfig.IdamBaseUrl;
    const idamDetailsPath = '/details';

    const userDetails = await fetch(idamBaseUrl + idamDetailsPath, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${authToken}`
        }
    });
    const userDetailsJson = await userDetails.json();
    return userDetailsJson.id;
}

module.exports = {
    getUserToken,
    getUserId
};
