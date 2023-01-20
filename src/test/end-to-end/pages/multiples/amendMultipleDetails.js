'use strict';

const commonConfig = require('../../data/commonConfig.json');
const testConfig = require("../../../config");

module.exports = async function (ecmCase1) {
    const I = this;
    let multiplesCaseId = await I.grabTextFrom('.markdown > h1:nth-of-type(2)');
    await I.wait(testConfig.TestTimeToWaitForMultiples);
    console.log(multiplesCaseId);

};
