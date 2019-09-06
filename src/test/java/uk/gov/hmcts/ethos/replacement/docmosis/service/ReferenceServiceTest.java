package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.Reference;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.ReferenceRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ReferenceServiceTest {

    @InjectMocks
    private ReferenceService referenceService;
    @Mock
    private ReferenceRepository referenceRepository;

    private Reference reference;
    private String caseId;
    private String previousId;

    @Before
    public void setUp() {
        caseId = "1232132";
        previousId = "12";
        reference = new Reference(caseId, previousId);
    }

    @Test
    public void createReference() {
        when(referenceRepository.save(isA(Reference.class))).thenReturn(reference);
        assertEquals(referenceService.createReference(caseId), reference);
        assertEquals(referenceService.createReference(caseId).getCaseId(), caseId);
    }

    @Test
    public void getReference() {
        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(reference);
        assertEquals(referenceService.getReference(), reference);
    }

    @Test
    public void getReferenceNotFound() {
        when(referenceRepository.findFirstByOrderByIdAsc()).thenReturn(null);
        assertNull(referenceService.getReference());
    }

}