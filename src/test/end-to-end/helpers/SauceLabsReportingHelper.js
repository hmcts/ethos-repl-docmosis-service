'use strict';

const event = require('codeceptjs').event;
const container = require('codeceptjs').container;
const exec = require('child_process').exec;

function updateSauceLabsResult(result, sessionId) {
    return 'curl -X PUT -s -d \'{"passed": ' + result + '}\' -u ' + process.env.SAUCE_USERNAME + ':' + process.env.SAUCE_ACCESS_KEY + ' https://saucelabs.com/rest/v1/' + process.env.SAUCE_USERNAME + '/jobs/' + sessionId;
}

module.exports = function() {

    // Setting test success on SauceLabs
    event.dispatcher.on(event.test.passed, () => {

        const sessionId = container.helpers('WebDriverIO').browser.requestHandler.sessionID;
        exec(updateSauceLabsResult('true', sessionId));

    });

    // Setting test failure on SauceLabs
    event.dispatcher.on(event.test.failed, () => {

        const sessionId = container.helpers('WebDriverIO').browser.requestHandler.sessionID;
        exec(updateSauceLabsResult('false', sessionId));

    });
};
