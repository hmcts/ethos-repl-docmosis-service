'use strict';
const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");
const {eventNames} = require("../common/constants");
const {utilsComponent} = require("../../helpers/utils");

module.exports = async function (caseID) {

    const I = this;
    await I.click(commonConfig.addNewButton);
    await I.waitForText(commonConfig.bfActionDescription, testConfig.TestTimeToWaitForText);
    await I.selectOption('#bfActions_0_cwActions', commonConfig.bfActionDescription);
    let currentDate = await utilsComponent.isWeekend();
    await I.fillField('#bfDate-day', currentDate.split('-')[2]);
    await I.fillField('#bfDate-month', currentDate.split('-')[1]);
    await I.fillField('#bfDate-year', currentDate.split('-')[0]);
    await I.navByClick(commonConfig.continue);
    await I.click(commonConfig.submit);
    await I.chooseNextStep(eventNames.CLOSE_CASE, 3);
    await I.see(commonConfig.bfActionsOutstandingErrorMsgCheck.replace('CaseID', caseID));
};
