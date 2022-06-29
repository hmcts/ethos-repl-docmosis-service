'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");
const {eventNames} = require('../common/constants.js');

module.exports = async function (jurisdiction, caseType, eventName) {
    const I = this;
    await I.waitForText(commonConfig.createCase, testConfig.TestTimeToWaitForText);
    await I.selectOption('#cc-jurisdiction', jurisdiction);
    await I.selectOption('#cc-case-type', caseType);
    await I.selectOption('#cc-event', eventName);
    await I.navByClick(commonConfig.start);
    await I.selectOption('//option[text()=\'Cases Completed\']', commonConfig.reportType );
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.submit);
    await I.chooseNextStep(eventNames.GENERATE_REPORT, 5);
    await I.navByClick('#hearingDateType-Single');
    await I.fillField('#listingDate-day', commonConfig.listingDateDay);
    await I.fillField('#listingDate-month', commonConfig.listingDateMonth);
    await I.fillField('#listingDate-year', commonConfig.listingDateYear);
    await I.selectOption('//option[text()=\'Leeds\']', commonConfig.tribunalOffice);
    await I.navByClick(commonConfig.continue);
    await I.waitForNavigationToComplete(commonConfig.generateReport, 10);
    await I.navByClick(commonConfig.closeAndReturnToCaseDetailsButton);
};

