const config = require('../config.js');
const {createCaseInCcd} = require("./helpers/ccdDataStoreApi");

exports.config = {
    tests: config.TestPathToRun,
    output: `${process.cwd()}/${config.TestOutputDir}`,
    async bootstrap() {
        config.CCDCaseId = await createCaseInCcd('src/test/end-to-end/data/ccd-case-basic-data.json');
        config.MOCase = await createCaseInCcd('src/test/end-to-end/data/ccd-case-manchester-data.json', 'Manchester');
    },
    helpers: {
        Puppeteer: {
            url: config.TestUrl,
            waitForTimeout: 40000,
            getPageTimeout: 40000,
            // waitForAction: 1000,
            show: config.TestShowBrowserWindow,
            waitForNavigation: ['domcontentloaded'],
            restart: true,
            keepCookies: false,
            keepBrowserState: false,
            chrome: {
                ignoreHTTPSErrors: true,
                'ignore-certificate-errors': true,
                'defaultViewport': {
                    'width': 1280,
                    'height': 960
                },
                args: [
                    '--headless',
                    '--disable-gpu',
                    '--no-sandbox',
                    '--allow-running-insecure-content',
                    '--ignore-certificate-errors',
                    '--window-size=1440,1400'
                ]
            },
        },
        PuppeteerHelper: {
            require: './helpers/PuppeteerHelper.js'
        },
        REST: {},
        JSONResponse: {},
        JSWait: {require: './helpers/JSWait.js'},
    },
    include: {
        I: './pages/steps.js'
    },
    plugins: {
        screenshotOnFail: {
            enabled: true,
            fullPageScreenshots: true
        },
        retryFailedStep: {
            enabled: true,
            retries: 1
        },
        autoDelay: {
            enabled: true
        }
    },
    mocha: {
        reporterOptions: {
            'codeceptjs-cli-reporter': {
                stdout: '-'
            },
            'mocha-junit-reporter': {
                stdout: '-',
                options: {mochaFile: './functional-output/result.xml'}
            },
            mochawesome: {
                stdout: './functional-output/ecm-e2e-mochawesome-stdout.log',
                options: {
                    reportDir: config.TestOutputDir || './functional-output',
                    reportFilename: 'ecm-e2e-result',
                    inlineAssets: true,
                    reportTitle: 'ECM CCD E2E Tests'
                }
            }
        }
    },
    multiple: {
        parallel: {
            chunks: 3,
            browsers: ['chrome']
        }
    },
    'name': 'ecm-ccd-e2e-tests'
};
