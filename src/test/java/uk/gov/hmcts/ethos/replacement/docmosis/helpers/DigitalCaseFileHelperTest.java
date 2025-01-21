package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_TIME_PATTERN;

class DigitalCaseFileHelperTest {

    private CaseData caseData;

    @BeforeEach
    void setUp() {
        CaseDataBuilder caseDataBuilder = new CaseDataBuilder();
        caseData = caseDataBuilder
                .withEthosCaseReference("123456/2021")
                .build();
        Bundle bundle = Bundle.builder()
                .value(createBundleDetails())
                .build();
        caseData.setCaseBundles(List.of(bundle));
    }

    @Test
    void addDcfToDocumentCollection() {
        DigitalCaseFileHelper.addDcfToDocumentCollection(caseData);
        assertNotNull(caseData.getDigitalCaseFile());
    }

    @Test
    void addDcfToDocumentCollectionNoBundle() {
        caseData.setCaseBundles(null);
        DigitalCaseFileHelper.addDcfToDocumentCollection(caseData);
        assertNull(caseData.getDigitalCaseFile());
    }

    @Test
    void dcf_failedToGenerate() {
        BundleDetails bundleDetails = BundleDetails.builder()
                .stitchStatus("FAILED")
                .stitchingFailureMessage("Failed to generate")
                .build();
        caseData.setCaseBundles(List.of(Bundle.builder().value(bundleDetails).build()));
        assertDoesNotThrow(() -> DigitalCaseFileHelper.addDcfToDocumentCollection(caseData));
        assertEquals("DCF Failed to generate: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN),
                caseData.getDigitalCaseFile().getStatus());
        assertEquals("Failed to generate", caseData.getDigitalCaseFile().getError());
    }

    @Test
    void setDcfUpdatingStatus() {
        DigitalCaseFileHelper.setUpdatingStatus(caseData);
        assertEquals("DCF Updating: " + LocalDateTime.now().format(NEW_DATE_TIME_PATTERN),
                caseData.getDigitalCaseFile().getStatus());
    }

    private BundleDetails createBundleDetails() {
        DocumentLink documentLink = DocumentLink.builder()
                .documentFilename("test.pdf")
                .documentUrl("http://test.com")
                .documentBinaryUrl("http://test.com/binary")
                .build();
        return BundleDetails.builder()
                .id(UUID.randomUUID().toString())
                .stitchStatus("DONE")
                .stitchedDocument(documentLink)
                .build();
    }
}