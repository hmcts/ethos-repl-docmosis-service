'use strict';

const Helper = codecept_helper;
const helperName = 'Puppeteer';
const testConfig = require('../../config');
const {runAccessibility} = require("./accessibility");

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

    async runAccessibilityTest() {
        if (!testConfig.TestForAccessibility) {
            return;
        }
        const url = await this.helpers[helperName].grabCurrentUrl();
        const {page} = await this.helpers[helperName];

        await runAccessibility(url, page);
    }
}

module.exports = PuppeteerHelper;
