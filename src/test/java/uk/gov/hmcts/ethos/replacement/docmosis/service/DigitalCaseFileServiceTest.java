package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.client.BundleApiClient;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.ResourceLoader;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;
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
        caseData.getDocumentCollection().getFirst().getValue().setDateOfCorrespondence("2000-01-01");
        caseData.getDocumentCollection().get(1).getValue().setExcludeFromDcf(List.of(YES));
        caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseId("1234123412341234");
        when(bundleApiClient.asyncStitchBundle(any(), any(), any()))
                .thenReturn(ResourceLoader.stitchBundleRequest());
        when(authTokenGenerator.generate()).thenReturn("authToken");

    }

    @Test
    void createDcf() {
        caseData.setUploadOrRemoveDcf("Create");
        assertDoesNotThrow(() -> digitalCaseFileService.createUploadRemoveDcf("authToken", caseDetails));
        assertEquals("DCF Updating: " + LocalDateTime.now(ZoneId.of("Europe/London")).format(NEW_DATE_TIME_PATTERN),
                caseData.getDigitalCaseFile().getStatus());
    }

}
