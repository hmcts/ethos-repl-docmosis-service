package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
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
    private SingleReferenceManchester previousManchesterReference;
    private SingleReferenceManchester previousManchesterMaxReference;
    private SingleReferenceManchester manchesterMaxReference;
    private SingleReferenceScotland previousScotlandReference;
    private SingleReferenceScotland scotlandReference;
    private String caseId;
    private String currentYear;

    @Before
    public void setUp() {
        caseId = "1232132";
        currentYear = String.valueOf(LocalDate.now().getYear());
        previousManchesterReference = new SingleReferenceManchester();
        previousManchesterReference.setRef("00011");
        previousManchesterReference.setCaseId(caseId);
        previousManchesterReference.setYear(currentYear);
        manchesterReference = new SingleReferenceManchester();
        manchesterReference.setRef("00012");
        manchesterReference.setCaseId(caseId);
        manchesterReference.setYear(currentYear);
        previousScotlandReference = new SingleReferenceScotland();
        previousScotlandReference.setRef("00014");
        previousScotlandReference.setCaseId(caseId);
        previousScotlandReference.setYear(currentYear);
        scotlandReference = new SingleReferenceScotland();
        scotlandReference.setRef("00015");
        scotlandReference.setCaseId(caseId);
        scotlandReference.setYear(currentYear);
        previousManchesterMaxReference = new SingleReferenceManchester();
        previousManchesterMaxReference.setRef(DEFAULT_MAX_REF);
        previousManchesterMaxReference.setCaseId(caseId);
        previousManchesterMaxReference.setYear(currentYear);
        manchesterMaxReference = new SingleReferenceManchester();
        manchesterMaxReference.setRef("00001");
        manchesterMaxReference.setCaseId(caseId);
        manchesterMaxReference.setYear(currentYear);
    }

    @Test
    public void createManchesterReference() {
        when(singleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousManchesterReference);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterReference);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceMaxPreviousRef() {
        when(singleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousManchesterMaxReference);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterMaxReference);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00001/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceWithNotPreviousReference() {
        manchesterMaxReference.setRef(DEFAULT_INIT_REF);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(manchesterMaxReference);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + DEFAULT_INIT_REF + "/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(singleRefScotlandRepository.findTopByOrderByIdDesc()).thenReturn(previousScotlandReference);
        when(singleRefScotlandRepository.save(isA(SingleReferenceScotland.class))).thenReturn(scotlandReference);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00015/" + currentYear;
        assertEquals(referenceService.createReference(SCOTLAND_CASE_TYPE_ID, caseId), scotlandRef);
    }

    @Test
    public void createOtherReference() {
        when(singleRefScotlandRepository.findTopByOrderByIdDesc()).thenReturn(previousScotlandReference);
        when(singleRefScotlandRepository.save(isA(SingleReferenceScotland.class))).thenReturn(scotlandReference);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00015/" + currentYear;
        assertEquals(referenceService.createReference(LEEDS_USERS_CASE_TYPE_ID, caseId), scotlandRef);
    }

}