const config = require('src/test/config.js');

exports.config = {
    'tests': config.TestPathToRun,
    'output': `${process.cwd()}/${config.TestOutputDir}`,
    'helpers': {
        'Puppeteer': {
            'url': config.TestUrl,
            'waitForTimeout': 90000,
            'getPageTimeout': 90000,
            'waitForAction': 1000,
            'show': config.TestShowBrowserWindow,
            'waitForNavigation': ['domcontentloaded', 'networkidle0'],
            'chrome': {
                'ignoreHTTPSErrors': true,
                'ignore-certificate-errors': true,
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
                stdout: '-',
                options: {steps: true}
            },
            'mocha-junit-reporter': {
                stdout: '-',
                options: {mochaFile: './functional-output/result.xml'}
            },
            mochawesome: {
                stdout: './functional-output/console.log',
                options: {
                    reportDir: config.TestOutputDir || './functional-output',
                    reportName: 'index',
                    inlineAssets: true
                }
            }
        }
    },
    multiple: {
        parallel: {
            chunks: 2,
            browsers: ['chrome']
        }
    },
    'name': 'ecm-ccd-e2e-tests'
};
