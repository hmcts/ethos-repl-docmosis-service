const config = require('src/test/config.js');

exports.config = {
    'tests': config.TestPathToRun,
    'output': `${process.cwd()}/${config.TestOutputDir}`,
    'helpers': {
        'Puppeteer': {
            'url': config.TestFrontendUrl,
            'waitForTimeout': 60000,
            'getPageTimeout': 20000,
            'waitForAction': 1000,
            'show': config.TestShowBrowserWindow,
            'waitForNavigation': ['domcontentloaded', 'networkidle0'],
            'chrome': {
                'ignoreHTTPSErrors': true,
                'ignore-certificate-errors': true,
                args: [
                    '--headless', '--disable-gpu', '--no-sandbox', '--allow-running-insecure-content', '--ignore-certificate-errors',
                    //'--proxy-server=proxyout.reform.hmcts.net:8080',
                    //'--proxy-bypass-list=*beta*LB.reform.hmcts.net'
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
    'name': 'Caveat E2E Tests'
};
