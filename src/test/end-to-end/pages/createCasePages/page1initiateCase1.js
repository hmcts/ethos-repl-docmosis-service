'use strict';

const testConfig = require('../../../config');
const createCaseConfig = require('./createCaseConfig');
const randomstring = require('randomstring');
const commonConfig = require('../../pages/common/commonConfig');

module.exports = function() {
    const I = this;
    I.waitForText(createCaseConfig.page1_waitForText, testConfig.TestTimeToWaitForText);
  //  I.fillField('#ethosCaseReference', createCaseConfig.page1_case_reference_number+ randomstring.generate({length: 7, charset: 'numeric'}));
    I.fillField('#receiptDate-day',createCaseConfig.page1_dateOfReceipt_day);
    I.fillField('#receiptDate-month',createCaseConfig.page1_dateOfReceipt_month);
    I.fillField('#receiptDate-year',createCaseConfig.page1_dateOfReceipt_year);
    I.fillField('#feeGroupReference',createCaseConfig.page1_fee_Group_Reference + randomstring.generate({length: 9, charset: 'numeric'}));
    I.selectOption('#caseType','Single');
    I.navByClick(commonConfig.continueButton);
};
