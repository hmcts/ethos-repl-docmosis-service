package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET1;

@ExtendWith(SpringExtension.class)
class DigitalCaseFileServiceTest {
    @MockBean
    private BundleApiClient bundleApiClient;
    @Mock
    private AuthTokenGenerator authTokenGenerator;
    @MockBean
    private DigitalCaseFileService digitalCaseFileService;
    private CaseData caseData;
    private CaseDetails caseDetails;
    private final String authToken = "Bearer token";

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        digitalCaseFileService = new DigitalCaseFileService(bundleApiClient, authTokenGenerator);
        caseData = new CaseDataBuilder()
                .withEthosCaseReference("123456/2021")
                .withDocumentCollection(ET1)
                .build();
        caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseId("1234123412341234");
        when(bundleApiClient.newBundle(any(), any(), any()))
                .thenReturn(ResourceLoader.createDcfRequest());
        when(bundleApiClient.stitchCcdBundles(any(), any(), any()))
                .thenReturn(ResourceLoader.stitchDcfRequest());
        when(authTokenGenerator.generate()).thenReturn("authToken");
        ReflectionTestUtils.setField(digitalCaseFileService, "defaultBundle", "et-dcf-2.yaml");
    }

    @Test
    void createDcf() {
        caseData.setCaseBundles(digitalCaseFileService.createDigitalCaseFile(caseDetails, authToken));
        assertNotNull(caseData.getCaseBundles());
        assertEquals(YES, caseDetails.getCaseData().getCaseBundles().get(0).value().getEligibleForStitching());
    }

    @Test
    void stitchDcf() {
        digitalCaseFileService.stitchDigitalCaseFile(caseDetails, authToken);
        assertNotNull(caseData.getCaseBundles());
        assertEquals(1, caseDetails.getCaseData().getDigitalCaseFile().size());
    }

    @ParameterizedTest
    @MethodSource
    void shouldSetBundleConfiguration(String bundleConfig, String expectedConfig) {
        caseData.setBundleConfiguration(bundleConfig);
        digitalCaseFileService.setBundleConfig(caseData);
        assertEquals(caseData.getBundleConfiguration(), expectedConfig);
    }

    private static Stream<Arguments> shouldSetBundleConfiguration() {
        return Stream.of(
                Arguments.of("et-dcf-ordered.yaml", "et-dcf-ordered.yaml"),
                Arguments.of("", "et-dcf-2.yaml"),
                Arguments.of(null, "et-dcf-2.yaml")

        );
    }
}
