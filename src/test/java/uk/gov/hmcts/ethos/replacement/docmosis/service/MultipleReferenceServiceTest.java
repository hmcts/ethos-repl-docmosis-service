package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleReferenceServiceTest {

    @InjectMocks
    private MultipleReferenceService multipleReferenceService;
    @Mock
    private MultipleRefManchesterRepository multipleRefManchesterRepository;
    @Mock
    private MultipleRefScotlandRepository multipleRefScotlandRepository;
    @Mock
    private MultipleRefLeedsRepository multipleRefLeedsRepository;
    @Mock
    private MultipleRefMidlandsWestRepository multipleRefMidlandsWestRepository;
    @Mock
    private MultipleRefMidlandsEastRepository multipleRefMidlandsEastRepository;
    @Mock
    private MultipleRefBristolRepository multipleRefBristolRepository;
    @Mock
    private MultipleRefWalesRepository multipleRefWalesRepository;
    @Mock
    private MultipleRefNewcastleRepository multipleRefNewcastleRepository;
    @Mock
    private MultipleRefWatfordRepository multipleRefWatfordRepository;
    @Mock
    private MultipleRefLondonCentralRepository multipleRefLondonCentralRepository;
    @Mock
    private MultipleRefLondonSouthRepository multipleRefLondonSouthRepository;
    @Mock
    private MultipleRefLondonEastRepository multipleRefLondonEastRepository;

    private MultipleReferenceManchester referenceManchester;
    private MultipleReferenceManchester previousReferenceManchester;
    private MultipleReferenceManchester previousMaxReferenceManchester;
    private MultipleReferenceManchester maxReferenceManchester;
    private MultipleReferenceScotland previousReferenceScotland;
    private MultipleReferenceScotland referenceScotland;
    private MultipleReferenceLeeds referenceLeeds;
    private MultipleReferenceMidlandsWest referenceMidlandsWest;
    private MultipleReferenceMidlandsEast referenceMidlandsEast;
    private MultipleReferenceBristol referenceBristol;
    private MultipleReferenceWales referenceWales;
    private MultipleReferenceNewcastle referenceNewcastle;
    private MultipleReferenceWatford referenceWatford;
    private MultipleReferenceLondonCentral referenceLondonCentral;
    private MultipleReferenceLondonSouth referenceLondonSouth;
    private MultipleReferenceLondonEast referenceLondonEast;
    private String caseId;

    @Before
    public void setUp() {
        caseId = "1232132";
        previousReferenceManchester = new MultipleReferenceManchester();
        previousReferenceManchester.setRef("00011");
        previousReferenceManchester.setCaseId(caseId);
        referenceManchester = new MultipleReferenceManchester();
        referenceManchester.setRef("00012");
        referenceManchester.setCaseId(caseId);
        previousReferenceScotland = new MultipleReferenceScotland();
        previousReferenceScotland.setRef("00014");
        previousReferenceScotland.setCaseId(caseId);
        referenceScotland = new MultipleReferenceScotland();
        referenceScotland.setRef("00015");
        referenceScotland.setCaseId(caseId);
        previousMaxReferenceManchester = new MultipleReferenceManchester();
        previousMaxReferenceManchester.setRef(DEFAULT_MAX_REF);
        previousMaxReferenceManchester.setCaseId(caseId);
        maxReferenceManchester = new MultipleReferenceManchester();
        maxReferenceManchester.setRef("00001");
        maxReferenceManchester.setCaseId(caseId);
        referenceLeeds = new MultipleReferenceLeeds();
        referenceLeeds.setRef("00005");
        referenceLeeds.setCaseId(caseId);
        referenceMidlandsWest = new MultipleReferenceMidlandsWest();
        referenceMidlandsWest.setRef("00008");
        referenceMidlandsWest.setCaseId(caseId);
        referenceMidlandsEast = new MultipleReferenceMidlandsEast();
        referenceMidlandsEast.setRef("00009");
        referenceMidlandsEast.setCaseId(caseId);
        referenceBristol = new MultipleReferenceBristol();
        referenceBristol.setRef("00010");
        referenceBristol.setCaseId(caseId);
        referenceWales = new MultipleReferenceWales();
        referenceWales.setRef("00011");
        referenceWales.setCaseId(caseId);
        referenceNewcastle = new MultipleReferenceNewcastle();
        referenceNewcastle.setRef("00012");
        referenceNewcastle.setCaseId(caseId);
        referenceWatford = new MultipleReferenceWatford();
        referenceWatford.setRef("00013");
        referenceWatford.setCaseId(caseId);
        referenceLondonCentral = new MultipleReferenceLondonCentral();
        referenceLondonCentral.setRef("00014");
        referenceLondonCentral.setCaseId(caseId);
        referenceLondonSouth = new MultipleReferenceLondonSouth();
        referenceLondonSouth.setRef("00015");
        referenceLondonSouth.setCaseId(caseId);
        referenceLondonEast = new MultipleReferenceLondonEast();
        referenceLondonEast.setRef("00016");
        referenceLondonEast.setCaseId(caseId);
    }

    @Test
    public void createManchesterReference() {
        when(multipleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousReferenceManchester);
        when(multipleRefManchesterRepository.save(isA(MultipleReferenceManchester.class))).thenReturn(referenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012";
        assertEquals(multipleReferenceService.createReference(MANCHESTER_BULK_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceMaxPreviousRef() {
        when(multipleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousMaxReferenceManchester);
        when(multipleRefManchesterRepository.save(isA(MultipleReferenceManchester.class))).thenReturn(maxReferenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00001";
        assertEquals(multipleReferenceService.createReference(MANCHESTER_BULK_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceWithNotPreviousReference() {
        maxReferenceManchester.setRef(DEFAULT_INIT_REF);
        when(multipleRefManchesterRepository.save(isA(MultipleReferenceManchester.class))).thenReturn(maxReferenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + DEFAULT_INIT_REF;
        assertEquals(multipleReferenceService.createReference(MANCHESTER_BULK_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(multipleRefScotlandRepository.findTopByOrderByIdDesc()).thenReturn(previousReferenceScotland);
        when(multipleRefScotlandRepository.save(isA(MultipleReferenceScotland.class))).thenReturn(referenceScotland);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00015";
        assertEquals(multipleReferenceService.createReference(SCOTLAND_BULK_CASE_TYPE_ID, caseId), scotlandRef);
    }

    @Test
    public void createLeedsReference() {
        when(multipleRefLeedsRepository.findTopByOrderByIdDesc()).thenReturn(referenceLeeds);
        when(multipleRefLeedsRepository.save(isA(MultipleReferenceLeeds.class))).thenReturn(referenceLeeds);
        String leedsRef = LEEDS_OFFICE_NUMBER + "00005";
        assertEquals(multipleReferenceService.createReference(LEEDS_USERS_BULK_CASE_TYPE_ID, caseId), leedsRef);
    }

    @Test
    public void createMidlandsWestReference() {
        when(multipleRefMidlandsWestRepository.findTopByOrderByIdDesc()).thenReturn(referenceMidlandsWest);
        when(multipleRefMidlandsWestRepository.save(isA(MultipleReferenceMidlandsWest.class))).thenReturn(referenceMidlandsWest);
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + "00008";
        assertEquals(multipleReferenceService.createReference(MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID, caseId), midlandsWestRef);
    }

    @Test
    public void createMidlandsEastReference() {
        when(multipleRefMidlandsEastRepository.findTopByOrderByIdDesc()).thenReturn(referenceMidlandsEast);
        when(multipleRefMidlandsEastRepository.save(isA(MultipleReferenceMidlandsEast.class))).thenReturn(referenceMidlandsEast);
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + "00009";
        assertEquals(multipleReferenceService.createReference(MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID, caseId), midlandsEastRef);
    }

    @Test
    public void createBristolReference() {
        when(multipleRefBristolRepository.findTopByOrderByIdDesc()).thenReturn(referenceBristol);
        when(multipleRefBristolRepository.save(isA(MultipleReferenceBristol.class))).thenReturn(referenceBristol);
        String bristolRef = BRISTOL_OFFICE_NUMBER + "00010";
        assertEquals(multipleReferenceService.createReference(BRISTOL_USERS_BULK_CASE_TYPE_ID, caseId), bristolRef);
    }

    @Test
    public void createWalesReference() {
        when(multipleRefWalesRepository.findTopByOrderByIdDesc()).thenReturn(referenceWales);
        when(multipleRefWalesRepository.save(isA(MultipleReferenceWales.class))).thenReturn(referenceWales);
        String walesRef = WALES_OFFICE_NUMBER + "00011";
        assertEquals(multipleReferenceService.createReference(WALES_USERS_BULK_CASE_TYPE_ID, caseId), walesRef);
    }

    @Test
    public void createNewcastleReference() {
        when(multipleRefNewcastleRepository.findTopByOrderByIdDesc()).thenReturn(referenceNewcastle);
        when(multipleRefNewcastleRepository.save(isA(MultipleReferenceNewcastle.class))).thenReturn(referenceNewcastle);
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + "00012";
        assertEquals(multipleReferenceService.createReference(NEWCASTLE_USERS_BULK_CASE_TYPE_ID, caseId), newcastleRef);
    }

    @Test
    public void createWatfordReference() {
        when(multipleRefWatfordRepository.findTopByOrderByIdDesc()).thenReturn(referenceWatford);
        when(multipleRefWatfordRepository.save(isA(MultipleReferenceWatford.class))).thenReturn(referenceWatford);
        String watfordRef = WATFORD_OFFICE_NUMBER + "00013";
        assertEquals(multipleReferenceService.createReference(WATFORD_USERS_BULK_CASE_TYPE_ID, caseId), watfordRef);
    }

    @Test
    public void createLondonCentralReference() {
        when(multipleRefLondonCentralRepository.findTopByOrderByIdDesc()).thenReturn(referenceLondonCentral);
        when(multipleRefLondonCentralRepository.save(isA(MultipleReferenceLondonCentral.class))).thenReturn(referenceLondonCentral);
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + "00014";
        assertEquals(multipleReferenceService.createReference(LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID, caseId), londonCentralRef);
    }

    @Test
    public void createLondonSouthReference() {
        when(multipleRefLondonSouthRepository.findTopByOrderByIdDesc()).thenReturn(referenceLondonSouth);
        when(multipleRefLondonSouthRepository.save(isA(MultipleReferenceLondonSouth.class))).thenReturn(referenceLondonSouth);
        String londonSouthRef = LONDON_SOUTH_OFFICE_NUMBER + "00015";
        assertEquals(multipleReferenceService.createReference(LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID, caseId), londonSouthRef);
    }

    @Test
    public void createLondonEastReference() {
        when(multipleRefLondonEastRepository.findTopByOrderByIdDesc()).thenReturn(referenceLondonEast);
        when(multipleRefLondonEastRepository.save(isA(MultipleReferenceLondonEast.class))).thenReturn(referenceLondonEast);
        String londonEastRef = LONDON_EAST_OFFICE_NUMBER + "00016";
        assertEquals(multipleReferenceService.createReference(LONDON_EAST_USERS_BULK_CASE_TYPE_ID, caseId), londonEastRef);
    }

}