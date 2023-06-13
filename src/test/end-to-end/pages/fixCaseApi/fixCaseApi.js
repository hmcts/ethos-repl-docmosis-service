'use strict';
const commonConfig = require('../../data/commonConfig.json');

module.exports = async function () {

    const I = this;
    await I.click(commonConfig.submit);
};
