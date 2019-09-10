package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceServiceTest {

    @InjectMocks
    private ReferenceService referenceService;
    @Mock
    private SingleRefManchesterRepository singleRefManchesterRepository;
    @Mock
    private SingleRefScotlandRepository singleRefScotlandRepository;

    private SingleReferenceManchester manchesterReference;
    private SingleReferenceManchester manchesterMaxReference;
    private SingleReferenceScotland scotlandReference;
    private String caseId;

    @Before
    public void setUp() {
        caseId = "1232132";
        String previousYear = "2019";
        manchesterReference = new SingleReferenceManchester(caseId, "12", previousYear);
        manchesterMaxReference = new SingleReferenceManchester(caseId, DEFAULT_MAX_REF, previousYear);
        scotlandReference = new SingleReferenceScotland(caseId, "15", previousYear);
    }

    @Test
    public void createManchesterReference() {
        when(singleRefManchesterRepository.findFirstByOrderByIdAsc()).thenReturn(manchesterReference);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId).getCaseId(), caseId);
    }

    @Test
    public void createManchesterReferenceMaxPreviousId() {
        when(singleRefManchesterRepository.findFirstByOrderByIdAsc()).thenReturn(manchesterMaxReference);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterMaxReference);
        manchesterReference.setRef("321/2019");
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterReference);
    }

    @Test
    public void createManchesterReferenceWithPreviousReference() {
        when(singleRefManchesterRepository.findFirstByOrderByIdAsc()).thenReturn(manchesterReference);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId).getCaseId(), caseId);
    }

    @Test
    public void createManchesterReferenceWithNotPreviousReference() {
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterReference);
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId).getCaseId(), caseId);
    }

    @Test
    public void createScotlandReference() {
        when(singleRefScotlandRepository.findFirstByOrderByIdAsc()).thenReturn(scotlandReference);
        when(singleRefScotlandRepository.save(isA(SingleReferenceScotland.class))).thenReturn(scotlandReference);
        assertEquals(referenceService.createReference(SCOTLAND_CASE_TYPE_ID, caseId), scotlandReference);
        assertEquals(referenceService.createReference(SCOTLAND_CASE_TYPE_ID, caseId).getCaseId(), caseId);
    }

    @Test
    public void createOtherReference() {
        when(singleRefScotlandRepository.findFirstByOrderByIdAsc()).thenReturn(scotlandReference);
        when(singleRefScotlandRepository.save(isA(SingleReferenceScotland.class))).thenReturn(scotlandReference);
        assertEquals(referenceService.createReference(LEEDS_USERS_CASE_TYPE_ID, caseId), scotlandReference);
        assertEquals(referenceService.createReference(LEEDS_USERS_CASE_TYPE_ID, caseId).getCaseId(), caseId);
    }

//    @Test
//    public void getPreviousManchesterReference() {
//        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(manchesterReference);
//        assertEquals(referenceService.getPreviousReference(), manchesterReference);
//    }
//
//    @Test
//    public void getPreviousManchesterReferenceNull() {
//        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(null);
//        assertNull(referenceService.getPreviousReference());
//    }
//
//    @Test
//    public void getPreviousScotlandReference() {
//        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(scotlandReference);
//        assertEquals(referenceService.getPreviousReference(), scotlandReference);
//    }
//
//    @Test
//    public void getPreviousReferenceNotFound() {
//        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(null);
//        assertNull(referenceService.getPreviousReference());
//    }

}