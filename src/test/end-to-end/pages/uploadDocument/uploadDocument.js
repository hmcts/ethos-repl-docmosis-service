'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.see('Upload Document');
    await I.click(commonConfig.addNewButton);
    await I.attachFile('#documentCollection_0_uploadedDocument', 'data/fileUpload.txt');
    await I.wait(5);
    await I.fillField('#documentCollection_0_shortDescription', commonConfig.shortDescription);
    await I.click(commonConfig.continue);
    await I.click(commonConfig.submit);
    await I.wait(5);
    await I.click("//div[text()='Documents']");
    await I.see('fileUpload.txt');
};
