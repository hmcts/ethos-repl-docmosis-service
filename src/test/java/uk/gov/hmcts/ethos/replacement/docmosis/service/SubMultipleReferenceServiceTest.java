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
public class SubMultipleReferenceServiceTest {

    @InjectMocks
    private SubMultipleReferenceService subMultipleReferenceService;
    @Mock
    private SubMultipleRefManchesterRepository subMultipleRefManchesterRepository;
    @Mock
    private SubMultipleRefScotlandRepository subMultipleRefScotlandRepository;
    @Mock
    private SubMultipleRefLeedsRepository subMultipleRefLeedsRepository;
    @Mock
    private SubMultipleRefMidlandsWestRepository subMultipleRefMidlandsWestRepository;
    @Mock
    private SubMultipleRefMidlandsEastRepository subMultipleRefMidlandsEastRepository;
    @Mock
    private SubMultipleRefBristolRepository subMultipleRefBristolRepository;
    @Mock
    private SubMultipleRefWalesRepository subMultipleRefWalesRepository;
    @Mock
    private SubMultipleRefNewcastleRepository subMultipleRefNewcastleRepository;
    @Mock
    private SubMultipleRefWatfordRepository subMultipleRefWatfordRepository;
    @Mock
    private SubMultipleRefLondonCentralRepository subMultipleRefLondonCentralRepository;
    @Mock
    private SubMultipleRefLondonSouthRepository subMultipleRefLondonSouthRepository;
    @Mock
    private SubMultipleRefLondonEastRepository subMultipleRefLondonEastRepository;

    private SubMultipleReferenceManchester referenceManchester;
    private SubMultipleReferenceManchester previousReferenceManchester;
    private SubMultipleReferenceManchester maxReferenceManchester;
    private SubMultipleReferenceScotland previousReferenceScotland;
    private SubMultipleReferenceScotland referenceScotland;
    private SubMultipleReferenceLeeds referenceLeeds;
    private SubMultipleReferenceMidlandsWest referenceMidlandsWest;
    private SubMultipleReferenceMidlandsEast referenceMidlandsEast;
    private SubMultipleReferenceBristol referenceBristol;
    private SubMultipleReferenceWales referenceWales;
    private SubMultipleReferenceNewcastle referenceNewcastle;
    private SubMultipleReferenceWatford referenceWatford;
    private SubMultipleReferenceLondonCentral referenceLondonCentral;
    private SubMultipleReferenceLondonSouth referenceLondonSouth;
    private SubMultipleReferenceLondonEast referenceLondonEast;
    private String multipleReference;
    private String multipleRef;

    @Before
    public void setUp() {
        multipleReference = "2310000";
        multipleRef = multipleReference.substring(2);
        previousReferenceManchester = new SubMultipleReferenceManchester();
        previousReferenceManchester.setRef("11");
        previousReferenceManchester.setMultipleRef(multipleReference);
        referenceManchester = new SubMultipleReferenceManchester();
        referenceManchester.setRef("12");
        referenceManchester.setMultipleRef(multipleReference);
        previousReferenceScotland = new SubMultipleReferenceScotland();
        previousReferenceScotland.setRef("14");
        previousReferenceScotland.setMultipleRef(multipleReference);
        referenceScotland = new SubMultipleReferenceScotland();
        referenceScotland.setRef("15");
        referenceScotland.setMultipleRef(multipleReference);
        maxReferenceManchester = new SubMultipleReferenceManchester();
        maxReferenceManchester.setRef("1");
        maxReferenceManchester.setMultipleRef(multipleReference);
        referenceLeeds = new SubMultipleReferenceLeeds();
        referenceLeeds.setRef("5");
        referenceLeeds.setMultipleRef(multipleReference);
        referenceMidlandsWest = new SubMultipleReferenceMidlandsWest();
        referenceMidlandsWest.setRef("8");
        referenceMidlandsWest.setMultipleRef(multipleReference);
        referenceMidlandsEast = new SubMultipleReferenceMidlandsEast();
        referenceMidlandsEast.setRef("9");
        referenceMidlandsEast.setMultipleRef(multipleReference);
        referenceBristol = new SubMultipleReferenceBristol();
        referenceBristol.setRef("10");
        referenceBristol.setMultipleRef(multipleReference);
        referenceWales = new SubMultipleReferenceWales();
        referenceWales.setRef("11");
        referenceWales.setMultipleRef(multipleReference);
        referenceNewcastle = new SubMultipleReferenceNewcastle();
        referenceNewcastle.setRef("12");
        referenceNewcastle.setMultipleRef(multipleReference);
        referenceWatford = new SubMultipleReferenceWatford();
        referenceWatford.setRef("13");
        referenceWatford.setMultipleRef(multipleReference);
        referenceLondonCentral = new SubMultipleReferenceLondonCentral();
        referenceLondonCentral.setRef("14");
        referenceLondonCentral.setMultipleRef(multipleReference);
        referenceLondonSouth = new SubMultipleReferenceLondonSouth();
        referenceLondonSouth.setRef("15");
        referenceLondonSouth.setMultipleRef(multipleReference);
        referenceLondonEast = new SubMultipleReferenceLondonEast();
        referenceLondonEast.setRef("16");
        referenceLondonEast.setMultipleRef(multipleReference);
    }

    @Test
    public void createManchesterReference() {
        when(subMultipleRefManchesterRepository.findTopByOrderByMultipleRefDesc()).thenReturn(previousReferenceManchester);
        when(subMultipleRefManchesterRepository.save(isA(SubMultipleReferenceManchester.class))).thenReturn(referenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + multipleRef + "/12";
        assertEquals(subMultipleReferenceService.createReference(MANCHESTER_BULK_CASE_TYPE_ID, multipleReference), manchesterRef);
    }

    @Test
    public void createManchesterReferenceWithNotPreviousReference() {
        maxReferenceManchester.setRef(DEFAULT_INIT_SUB_REF);
        when(subMultipleRefManchesterRepository.save(isA(SubMultipleReferenceManchester.class))).thenReturn(maxReferenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + multipleRef + "/" + DEFAULT_INIT_SUB_REF;
        assertEquals(subMultipleReferenceService.createReference(MANCHESTER_BULK_CASE_TYPE_ID, multipleReference), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(subMultipleRefScotlandRepository.findTopByOrderByMultipleRefDesc()).thenReturn(previousReferenceScotland);
        when(subMultipleRefScotlandRepository.save(isA(SubMultipleReferenceScotland.class))).thenReturn(referenceScotland);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + multipleRef + "/15";
        assertEquals(subMultipleReferenceService.createReference(SCOTLAND_BULK_CASE_TYPE_ID, multipleReference), scotlandRef);
    }

    @Test
    public void createLeedsReference() {
        when(subMultipleRefLeedsRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceLeeds);
        when(subMultipleRefLeedsRepository.save(isA(SubMultipleReferenceLeeds.class))).thenReturn(referenceLeeds);
        String leedsRef = LEEDS_OFFICE_NUMBER + multipleRef + "/5";
        assertEquals(subMultipleReferenceService.createReference(LEEDS_USERS_BULK_CASE_TYPE_ID, multipleReference), leedsRef);
    }

    @Test
    public void createMidlandsWestReference() {
        when(subMultipleRefMidlandsWestRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceMidlandsWest);
        when(subMultipleRefMidlandsWestRepository.save(isA(SubMultipleReferenceMidlandsWest.class))).thenReturn(referenceMidlandsWest);
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + multipleRef + "/8";
        assertEquals(subMultipleReferenceService.createReference(MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID, multipleReference), midlandsWestRef);
    }

    @Test
    public void createMidlandsEastReference() {
        when(subMultipleRefMidlandsEastRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceMidlandsEast);
        when(subMultipleRefMidlandsEastRepository.save(isA(SubMultipleReferenceMidlandsEast.class))).thenReturn(referenceMidlandsEast);
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + multipleRef + "/9";
        assertEquals(subMultipleReferenceService.createReference(MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID, multipleReference), midlandsEastRef);
    }

    @Test
    public void createBristolReference() {
        when(subMultipleRefBristolRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceBristol);
        when(subMultipleRefBristolRepository.save(isA(SubMultipleReferenceBristol.class))).thenReturn(referenceBristol);
        String bristolRef = BRISTOL_OFFICE_NUMBER + multipleRef + "/10";
        assertEquals(subMultipleReferenceService.createReference(BRISTOL_USERS_BULK_CASE_TYPE_ID, multipleReference), bristolRef);
    }

    @Test
    public void createWalesReference() {
        when(subMultipleRefWalesRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceWales);
        when(subMultipleRefWalesRepository.save(isA(SubMultipleReferenceWales.class))).thenReturn(referenceWales);
        String walesRef = WALES_OFFICE_NUMBER + multipleRef + "/11";
        assertEquals(subMultipleReferenceService.createReference(WALES_USERS_BULK_CASE_TYPE_ID, multipleReference), walesRef);
    }

    @Test
    public void createNewcastleReference() {
        when(subMultipleRefNewcastleRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceNewcastle);
        when(subMultipleRefNewcastleRepository.save(isA(SubMultipleReferenceNewcastle.class))).thenReturn(referenceNewcastle);
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + multipleRef + "/12";
        assertEquals(subMultipleReferenceService.createReference(NEWCASTLE_USERS_BULK_CASE_TYPE_ID, multipleReference), newcastleRef);
    }

    @Test
    public void createWatfordReference() {
        when(subMultipleRefWatfordRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceWatford);
        when(subMultipleRefWatfordRepository.save(isA(SubMultipleReferenceWatford.class))).thenReturn(referenceWatford);
        String watfordRef = WATFORD_OFFICE_NUMBER + multipleRef + "/13";
        assertEquals(subMultipleReferenceService.createReference(WATFORD_USERS_BULK_CASE_TYPE_ID, multipleReference), watfordRef);
    }

    @Test
    public void createLondonCentralReference() {
        when(subMultipleRefLondonCentralRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceLondonCentral);
        when(subMultipleRefLondonCentralRepository.save(isA(SubMultipleReferenceLondonCentral.class))).thenReturn(referenceLondonCentral);
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + multipleRef + "/14";
        assertEquals(subMultipleReferenceService.createReference(LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID, multipleReference), londonCentralRef);
    }

    @Test
    public void createLondonSouthReference() {
        when(subMultipleRefLondonSouthRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceLondonSouth);
        when(subMultipleRefLondonSouthRepository.save(isA(SubMultipleReferenceLondonSouth.class))).thenReturn(referenceLondonSouth);
        String londonSouthRef = LONDON_SOUTH_OFFICE_NUMBER + multipleRef + "/15";
        assertEquals(subMultipleReferenceService.createReference(LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID, multipleReference), londonSouthRef);
    }

    @Test
    public void createLondonEastReference() {
        when(subMultipleRefLondonEastRepository.findTopByOrderByMultipleRefDesc()).thenReturn(referenceLondonEast);
        when(subMultipleRefLondonEastRepository.save(isA(SubMultipleReferenceLondonEast.class))).thenReturn(referenceLondonEast);
        String londonEastRef = LONDON_EAST_OFFICE_NUMBER + multipleRef + "/16";
        assertEquals(subMultipleReferenceService.createReference(LONDON_EAST_USERS_BULK_CASE_TYPE_ID, multipleReference), londonEastRef);
    }

}