const testConfig = require('../../config.js');
const querystring = require("querystring");
const env = testConfig.TestEnv;
const {expect} = require('chai');
const {I} = inject();
const username = testConfig.TestEnvCWUser;
const password = testConfig.TestEnvCWPassword;
const idamBaseUrl = `https://idam-api.${env}.platform.hmcts.net/loginUser`;
const getIdamUserDetails = `https://idam-api.${env}.platform.hmcts.net/details`;

async function getUserToken() {

    let payload = querystring.stringify({
        username: `${username}`,
        password: `${password}`,
    })

    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    const authTokenResponse = await I.sendPostRequest(idamBaseUrl, payload, headers);
    expect(authTokenResponse.status).to.eql(200);
    const authToken = authTokenResponse.data.access_token;
    return authToken;
}

async function getUserId(authToken) {
    let getIDAMUserID =
        {
            'Authorization': `Bearer ${authToken}`
        };

    const userDetails = await I.sendGetRequest(getIdamUserDetails, getIDAMUserID);
    const userId = userDetails.data.id;
    return userId;
}

module.exports = {
    getUserToken,
    getUserId
};
