'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");
const {eventNames} = require('../pages/common/constants.js');

module.exports = async function (jurisdiction, caseType, event) {
    const I = this;
    await I.waitForText(commonConfig.createCase, testConfig.TestTimeToWaitForText);
    await I.wait(5);
    await I.selectOption('#cc-jurisdiction', jurisdiction)
    await I.selectOption('#cc-case-type', caseType);
    await I.selectOption('#cc-event', event);
    await I.navByClick(commonConfig.start);
    await I.selectOption('//option[text()=\'Cases Completed\']', commonConfig.reportType );
    await I.navByClick(commonConfig.continue);
    await I.navByClick(commonConfig.submit);
    await I.wait(3);
    await I.chooseNextStep(eventNames.GENERATE_REPORT, 5);
    await I.navByClick('#hearingDateType-Single');
    await I.fillField('#listingDate-day', commonConfig.listingDateDay);
    await I.fillField('#listingDate-month', commonConfig.listingDateMonth);
    await I.fillField('#listingDate-year', commonConfig.listingDateYear);
    await I.selectOption('//option[text()=\'Leeds\']', commonConfig.tribunalOffice);
    await I.navByClick(commonConfig.continue);
    await I.waitForNavigationToComplete(commonConfig.generateReport, 10);
    await I.navByClick('//button[text()=\'Close and Return to case details\']');
};

