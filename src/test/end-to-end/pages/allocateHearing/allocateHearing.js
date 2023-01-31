'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (jurisdiction) {

    const I = this;
    I.waitForText(commonConfig.allocateHearing, testConfig.TestTimeToWaitForText);
    I.retry(3).selectOption('#hearingCollection_0_judge', 'A Judge');
    if (jurisdiction === 'Leeds')
    {
        I.selectOption('#hearingCollection_0_hearingDateCollection_0_hearingRoomLeeds', 'Leeds Magistrates');
    }
    if (jurisdiction === 'Manchester')
    {
        I.selectOption('#hearingCollection_0_hearingDateCollection_0_Hearing_room_M', 'Manchester');
    }
    I.selectOption('#hearingCollection_0_hearingDateCollection_0_Hearing_clerk', 'A Clerk');
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
    await I.waitForEnabled({css: '#next-step'}, testConfig.TestTimeToWaitForText || 5);
};
