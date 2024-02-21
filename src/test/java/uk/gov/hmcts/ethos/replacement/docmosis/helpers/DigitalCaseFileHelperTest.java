package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.ecm.common.model.bundle.Bundle;
import uk.gov.hmcts.ecm.common.model.bundle.BundleDetails;
import uk.gov.hmcts.ecm.common.model.bundle.DocumentLink;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.utils.CaseDataBuilder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    private BundleDetails createBundleDetails() {
        DocumentLink documentLink = DocumentLink.builder()
                .documentFilename("test.pdf")
                .documentUrl("http://test.com")
                .documentBinaryUrl("http://test.com/binary")
                .build();
        return BundleDetails.builder()
                .id(UUID.randomUUID().toString())
                .stitchedDocument(documentLink)
                .build();
    }
}