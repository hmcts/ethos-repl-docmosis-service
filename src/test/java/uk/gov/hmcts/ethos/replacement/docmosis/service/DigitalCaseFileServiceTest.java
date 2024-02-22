package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET1;
import static uk.gov.hmcts.ethos.replacement.docmosis.util.DocumentConstants.ET1_ATTACHMENT;

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

    @BeforeEach
    void setUp() throws URISyntaxException, IOException {
        digitalCaseFileService = new DigitalCaseFileService(authTokenGenerator, bundleApiClient);
        CaseDataBuilder caseDataBuilder = new CaseDataBuilder();
        caseData = caseDataBuilder
                .withEthosCaseReference("123456/2021")
                .withDocumentCollection(ET1)
                .withDocumentCollection(ET1_ATTACHMENT)
                .build();
        caseData.getDocumentCollection().get(0).getValue().setDateOfCorrespondence("2000-01-01");
        caseData.getDocumentCollection().get(1).getValue().setExcludeFromDcf(List.of(YES));
        caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseId("1234123412341234");
        when(bundleApiClient.stitchBundle(any(), any(), any()))
                .thenReturn(ResourceLoader.stitchBundleRequest());
        when(authTokenGenerator.generate()).thenReturn("authToken");
        ReflectionTestUtils.setField(digitalCaseFileService, "defaultBundle", "et-dcf-2.yaml");

    }

    @Test
    void createBundleRequest() {
        caseData.setCaseBundles(digitalCaseFileService.createCaseFileRequest(caseData));
        assertNotNull(caseData.getCaseBundles());
        assertEquals(1, caseData.getCaseBundles().get(0).value().getDocuments().size());
        assertEquals(YES, caseDetails.getCaseData().getCaseBundles().get(0).value().getEligibleForStitching());
    }

    @Test
    void stitchBundleRequest() {
        String authToken = "Bearer token";
        caseDetails.getCaseData().setCaseBundles(digitalCaseFileService.stitchCaseFile(caseDetails, authToken));
        assertNotNull(caseDetails.getCaseData().getCaseBundles());
        assertNotNull(caseDetails.getCaseData().getCaseBundles().get(0).value().getStitchedDocument());
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
