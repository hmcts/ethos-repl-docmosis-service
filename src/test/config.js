module.exports = {
    TestFrontendUrl: process.env.TEST_E2E_URL || 'https://www-ccd.demo.platform.hmcts.net',
    TestShowBrowserWindow: process.env.SHOW_BROWSER_WINDOW || true,
    TestRetryFeatures: process.env.RETRY_FEATURES || 0,
    TestRetryScenarios: process.env.RETRY_SCENARIOS || 0,
    TestPathToRun: './paths/**/manchester-Individual.js',
    TestOutputDir: process.env.E2E_OUTPUT_DIR || './output',
    TestDocumentToUpload: 'uploadDocuments/test_file_for_document_upload.png',
    TestTimeToWaitForText: 20,
    TestWaitForTextToAppear: 20,
    TestEnvUser: process.env.CW_USER_EMAIL || 'eric.ccdcooper@gmail.com',
    TestEnvPassword: process.env.CW_USER_PASSWORD || 'Nagoya0102'
};
