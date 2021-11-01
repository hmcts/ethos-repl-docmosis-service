module.exports = {
    TestUrl: process.env.TEST_E2E_URL || 'http://manage-case.aat.platform.hmcts.net/',
    TestEnv: process.env.RUNNING_ENV || 'aat',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 2,
    TestPathToRun: process.env.E2E_TEST_PATH || 'tests/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output/xui',
    TestEnvCWUser: process.env.CCD_CASEWORKER_E2E_EMAIL || '',
    TestEnvCWPassword: process.env.CCD_CASEWORKER_E2E_PASSWORD || '',
    TestEnvProfUser: process.env.PROF_USER_EMAIL || '',
    TestEnvProfPassword: process.env.PROF_USER_PASSWORD || '',
    TestForXUI: process.env.TESTS_FOR_XUI_SERVICE === 'true',
    TestForAccessibility: process.env.TESTS_FOR_ACCESSIBILITY === 'true',
    TestForCrossBrowser: process.env.TESTS_FOR_CROSS_BROWSER === 'true',
    TestIdamClientSecret: process.env.IDAM_CLIENT_SECRET || '',
    TestS2SAuthSecret: process.env.SERVICE_SECRET || '',
    TestSystemUser: process.env.IDAM_SYSTEMUPDATE_USERNAME || '',
    TestSystemUserPassword: process.env.IDAM_SYSTEMUPDATE_PASSWORD || '',

    CaseWorkerUser: {
        password: process.env.CW_USER_PASSWORD,
        email: process.env.CW_USER_EMAIL
    },
    ApiUser: {
        password: process.env.API_USER_PASSWORD,
        email: process.env.API_USER_EMAIL
    },
    url: {
        authProviderApi: process.env.SERVICE_AUTH_PROVIDER_API_BASE_URL || 'http://rpe-service-auth-provider-aat.service.core-compute-aat.internal',
        ccdDataStore: process.env.CCD_DATA_STORE_URL || 'http://ccd-data-store-api-aat.service.core-compute-aat.internal',
        idamApi: process.env.IDAM_API_URL || 'https://idam-api.aat.platform.hmcts.net',
    },
    definition: {
        jurisdiction: 'Employment',
        caseType: 'Leeds'
    },
    s2s: {
        microservice: process.env.S2S_MICROSERVICE_KEY_ECM,
        secret: process.env.S2S_MICROSERVICE_KEY_PWD,
    },
    env: process.env.TEST_ENV || 'local',
    proxy: ''
};
