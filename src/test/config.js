module.exports = {
    TestUrl: process.env.TEST_E2E_URL || 'https://manage-case.aat.platform.hmcts.net',
    TestEndToEndUrl: process.env.TEST_E2E_URL || 'https://manage-case.aat.platform.hmcts.net',
    IdamBaseUrl: process.env.IDAM_URL || 'https://idam-api.aat.platform.hmcts.net',
    RedirectUri: process.env.REDIRECT_URI || `https://manage-case.aat.platform.hmcts.net/oauth2/callback`,
    S2SProvider: process.env.S2S_PROVIDER || 'http://rpe-service-auth-provider-aat.service.core-compute-aat.internal',
    CCDDataStoreApi: process.env.CCD_DATA_STORE_API_URL || 'http://ccd-data-store-api-aat.service.core-compute-aat.internal',
    TestEnv: process.env.RUNNING_ENV || 'aat',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || false,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 2,
    TestPathToRun: process.env.E2E_TEST_PATH || './paths/**/*.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './functional-output',
    TestTimeToWaitForText: parseInt(process.env.E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 30),
    TestTimeToWaitForMultiples:parseInt(process.env.E2E_TEST_TIME_TO_WAIT_FOR_TEXT || 5),
    TestTimeToWait: parseInt(process.env.E2E_TEST_TIME_TO_WAIT || 3),
    TestEnvCWUser: process.env.CCD_CASEWORKER_E2E_EMAIL || '',
    TestEnvCWPassword: process.env.CCD_CASEWORKER_E2E_PASSWORD || '',
    TestForXUI: process.env.TESTS_FOR_XUI_SERVICE === 'true',
    TestAutoDelayEnabled: process.env.E2E_AUTO_DELAY_ENABLED === 'true',
    TestForAccessibility: process.env.TESTS_FOR_ACCESSIBILITY === 'true',
    TestForCrossBrowser: process.env.TESTS_FOR_CROSS_BROWSER === 'true',
    TestIdamClientSecret: process.env.IDAM_CLIENT_SECRET || '',
    TestS2SAuthSecret: process.env.SERVICE_SECRET || '',
    S2SAuthSecret: process.env.MICROSERVICE_CCD_GW || '',
    CCDCaseId: '',
    MOCase: '',
    MultiplesCaseId:''
};
