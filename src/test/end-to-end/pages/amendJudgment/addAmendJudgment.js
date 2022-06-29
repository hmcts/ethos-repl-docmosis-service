'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function () {

    const I = this;
    I.waitForText(commonConfig.judgment, testConfig.TestTimeToWaitForText);
    await I.click('#judgementCollection_0_non_hearing_judgment_Yes');
    await I.selectOption('#judgementCollection_0_judgement_type', commonConfig.judgmentType);
    await I.click('//div[@id=\'judgementCollection_0_jurisdictionCodes\']/div/button');
    await I.wait(2);
    await I.selectOption('#judgementCollection_0_jurisdictionCodes_0_juridictionCodesList', commonConfig.jurisdictionCode);
    await I.fillField('#date_judgment_made-day', commonConfig.judgmentMadeDate);
    await I.fillField('#date_judgment_made-month', commonConfig.judgmentMadeMonth);
    await I.fillField('#date_judgment_made-year', commonConfig.judgmentMadeYear);
    await I.fillField('#date_judgment_sent-day', commonConfig.judgmentSentDate);
    await I.fillField('#date_judgment_sent-month', commonConfig.judgmentSentMonth);
    await I.fillField('#date_judgment_sent-year', commonConfig.judgmentSentYear);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
    await I.waitForText('has been updated with event: Judgment', testConfig.TestTimeToWaitForText);

};
