'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.click('#preAcceptCase_caseAccepted_No');
    await I.fillField('#dateRejected-day', commonConfig.caseAcceptedDay);
    await I.fillField('#dateRejected-month', commonConfig.caseAcceptedMonth);
    await I.fillField('#dateRejected-year', commonConfig.caseAcceptedYear);
    await I.checkOption('#preAcceptCase_rejectReason-Not\\ on\\ Prescribed\\ Form');
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit)
};

