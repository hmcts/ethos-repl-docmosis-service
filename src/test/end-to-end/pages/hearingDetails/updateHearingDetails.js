'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.hearingDetails, testConfig.TestTimeToWaitForText);
    await I.selectOption('#hearingCollection_0_hearingDateCollection_0_Hearing_status', commonConfig.HearingStatus);
    await I.wait(2);
    await I.click('#hearingCollection_0_hearingDateCollection_0_hearingCaseDisposed_No');
    await I.fillField('#hearingTimingStart-day', commonConfig.caseAcceptedDay);
    await I.fillField('#hearingTimingStart-month', commonConfig.HearingTimingBreakMonth);
    await I.fillField('#hearingTimingStart-year', commonConfig.HearingTimingBreakYear);
    await I.fillField('#hearingTimingBreak-day', commonConfig.caseAcceptedDay);
    await I.fillField('#hearingTimingBreak-month', commonConfig.HearingTimingBreakMonth);
    await I.fillField('#hearingTimingBreak-year', commonConfig.HearingTimingBreakYear);
    await I.fillField('#hearingTimingBreak-hour', commonConfig.HearingTimingFinishHour);
    await I.fillField('#hearingTimingBreak-minute', commonConfig.HearingTimingFinishHour);
    await I.fillField('#hearingTimingBreak-second', commonConfig.HearingTimingFinishHour);
    await I.fillField('#hearingTimingFinish-day', commonConfig.caseAcceptedDay);
    await I.fillField('#hearingTimingFinish-month', commonConfig.HearingTimingBreakMonth);
    await I.fillField('#hearingTimingFinish-year', commonConfig.HearingTimingBreakYear);
    await I.fillField('#hearingTimingFinish-hour', commonConfig.HearingTimingFinishHour);
    await I.click(commonConfig.continue);
    await I.see(commonConfig.startBreakFinishTimeErrorCheck);
};
