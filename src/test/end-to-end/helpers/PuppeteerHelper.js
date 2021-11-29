'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';
const testConfig = require('src/test/config.js');

class PuppeteerHelper extends Helper {

    async waitForNavigationToComplete(locator) {
        const page = this.helpers[helperName].page;
        const promises = [];

        promises.push(page.waitForNavigation({timeout: 1200000, waitUntil: ['domcontentloaded']}));

        if (locator) {
            promises.push(page.click(locator));
        }
        await Promise.all(promises);
    }

    async clickTab(tabTitle) {
        const helper = this.helpers[helperName];
        if (testConfig.TestForXUI) {
            const tabXPath = `//div[text()='${tabTitle}']`;

            await helper.page.waitForXPath(tabXPath);
            const clickableTab = await helper.page.$x(tabXPath);
            await helper.page.evaluate(el => el.click(), clickableTab[0]);
        } else {
            helper.click(tabTitle);
        }
    }
}

module.exports = PuppeteerHelper;
