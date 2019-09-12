package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.*;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

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
    @Mock
    private SingleRefLeedsRepository singleRefLeedsRepository;
    @Mock
    private SingleRefMidlandsWestRepository singleRefMidlandsWestRepository;
    @Mock
    private SingleRefMidlandsEastRepository singleRefMidlandsEastRepository;
    @Mock
    private SingleRefBristolRepository singleRefBristolRepository;
    @Mock
    private SingleRefWalesRepository singleRefWalesRepository;
    @Mock
    private SingleRefNewcastleRepository singleRefNewcastleRepository;
    @Mock
    private SingleRefWatfordRepository singleRefWatfordRepository;
    @Mock
    private SingleRefLondonCentralRepository singleRefLondonCentralRepository;

    private SingleReferenceManchester referenceManchester;
    private SingleReferenceManchester previousReferenceManchester;
    private SingleReferenceManchester previousMaxReferenceManchester;
    private SingleReferenceManchester maxReferenceManchester;
    private SingleReferenceScotland previousReferenceScotland;
    private SingleReferenceScotland referenceScotland;
    private SingleReferenceLeeds referenceLeeds;
    private SingleReferenceMidlandsWest referenceMidlandsWest;
    private SingleReferenceMidlandsEast referenceMidlandsEast;
    private SingleReferenceBristol referenceBristol;
    private SingleReferenceWales referenceWales;
    private SingleReferenceNewcastle referenceNewcastle;
    private SingleReferenceWatford referenceWatford;
    private SingleReferenceLondonCentral referenceLondonCentral;
    private String caseId;
    private String currentYear;

    @Before
    public void setUp() {
        caseId = "1232132";
        currentYear = String.valueOf(LocalDate.now().getYear());
        previousReferenceManchester = new SingleReferenceManchester();
        previousReferenceManchester.setRef("00011");
        previousReferenceManchester.setCaseId(caseId);
        previousReferenceManchester.setYear(currentYear);
        referenceManchester = new SingleReferenceManchester();
        referenceManchester.setRef("00012");
        referenceManchester.setCaseId(caseId);
        referenceManchester.setYear(currentYear);
        previousReferenceScotland = new SingleReferenceScotland();
        previousReferenceScotland.setRef("00014");
        previousReferenceScotland.setCaseId(caseId);
        previousReferenceScotland.setYear(currentYear);
        referenceScotland = new SingleReferenceScotland();
        referenceScotland.setRef("00015");
        referenceScotland.setCaseId(caseId);
        referenceScotland.setYear(currentYear);
        previousMaxReferenceManchester = new SingleReferenceManchester();
        previousMaxReferenceManchester.setRef(DEFAULT_MAX_REF);
        previousMaxReferenceManchester.setCaseId(caseId);
        previousMaxReferenceManchester.setYear(currentYear);
        maxReferenceManchester = new SingleReferenceManchester();
        maxReferenceManchester.setRef("00001");
        maxReferenceManchester.setCaseId(caseId);
        maxReferenceManchester.setYear(currentYear);
        referenceLeeds = new SingleReferenceLeeds();
        referenceLeeds.setRef("00005");
        referenceLeeds.setCaseId(caseId);
        referenceLeeds.setYear(currentYear);
        referenceMidlandsWest = new SingleReferenceMidlandsWest();
        referenceMidlandsWest.setRef("00008");
        referenceMidlandsWest.setCaseId(caseId);
        referenceMidlandsWest.setYear(currentYear);
        referenceMidlandsEast = new SingleReferenceMidlandsEast();
        referenceMidlandsEast.setRef("00009");
        referenceMidlandsEast.setCaseId(caseId);
        referenceMidlandsEast.setYear(currentYear);
        referenceBristol = new SingleReferenceBristol();
        referenceBristol.setRef("00010");
        referenceBristol.setCaseId(caseId);
        referenceBristol.setYear(currentYear);
        referenceWales = new SingleReferenceWales();
        referenceWales.setRef("00011");
        referenceWales.setCaseId(caseId);
        referenceWales.setYear(currentYear);
        referenceNewcastle = new SingleReferenceNewcastle();
        referenceNewcastle.setRef("00012");
        referenceNewcastle.setCaseId(caseId);
        referenceNewcastle.setYear(currentYear);
        referenceWatford = new SingleReferenceWatford();
        referenceWatford.setRef("00013");
        referenceWatford.setCaseId(caseId);
        referenceWatford.setYear(currentYear);
        referenceLondonCentral = new SingleReferenceLondonCentral();
        referenceLondonCentral.setRef("00014");
        referenceLondonCentral.setCaseId(caseId);
        referenceLondonCentral.setYear(currentYear);
    }

    @Test
    public void createManchesterReference() {
        when(singleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousReferenceManchester);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(referenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceMaxPreviousRef() {
        when(singleRefManchesterRepository.findTopByOrderByIdDesc()).thenReturn(previousMaxReferenceManchester);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(maxReferenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00001/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createManchesterReferenceWithNotPreviousReference() {
        maxReferenceManchester.setRef(DEFAULT_INIT_REF);
        when(singleRefManchesterRepository.save(isA(SingleReferenceManchester.class))).thenReturn(maxReferenceManchester);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + DEFAULT_INIT_REF + "/" + currentYear;
        assertEquals(referenceService.createReference(MANCHESTER_CASE_TYPE_ID, caseId), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(singleRefScotlandRepository.findTopByOrderByIdDesc()).thenReturn(previousReferenceScotland);
        when(singleRefScotlandRepository.save(isA(SingleReferenceScotland.class))).thenReturn(referenceScotland);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00015/" + currentYear;
        assertEquals(referenceService.createReference(SCOTLAND_CASE_TYPE_ID, caseId), scotlandRef);
    }

    @Test
    public void createLeedsReference() {
        when(singleRefLeedsRepository.findTopByOrderByIdDesc()).thenReturn(referenceLeeds);
        when(singleRefLeedsRepository.save(isA(SingleReferenceLeeds.class))).thenReturn(referenceLeeds);
        String leedsRef = LEEDS_OFFICE_NUMBER + "00005/" + currentYear;
        assertEquals(referenceService.createReference(LEEDS_USERS_CASE_TYPE_ID, caseId), leedsRef);
    }

    @Test
    public void createMidlandsWestReference() {
        when(singleRefMidlandsWestRepository.findTopByOrderByIdDesc()).thenReturn(referenceMidlandsWest);
        when(singleRefMidlandsWestRepository.save(isA(SingleReferenceMidlandsWest.class))).thenReturn(referenceMidlandsWest);
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + "00008/" + currentYear;
        assertEquals(referenceService.createReference(MIDLANDS_WEST_USERS_CASE_TYPE_ID, caseId), midlandsWestRef);
    }

    @Test
    public void createMidlandsEastReference() {
        when(singleRefMidlandsEastRepository.findTopByOrderByIdDesc()).thenReturn(referenceMidlandsEast);
        when(singleRefMidlandsEastRepository.save(isA(SingleReferenceMidlandsEast.class))).thenReturn(referenceMidlandsEast);
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + "00009/" + currentYear;
        assertEquals(referenceService.createReference(MIDLANDS_EAST_USERS_CASE_TYPE_ID, caseId), midlandsEastRef);
    }

    @Test
    public void createBristolReference() {
        when(singleRefBristolRepository.findTopByOrderByIdDesc()).thenReturn(referenceBristol);
        when(singleRefBristolRepository.save(isA(SingleReferenceBristol.class))).thenReturn(referenceBristol);
        String bristolRef = BRISTOL_OFFICE_NUMBER + "00010/" + currentYear;
        assertEquals(referenceService.createReference(BRISTOL_USERS_CASE_TYPE_ID, caseId), bristolRef);
    }

    @Test
    public void createWalesReference() {
        when(singleRefWalesRepository.findTopByOrderByIdDesc()).thenReturn(referenceWales);
        when(singleRefWalesRepository.save(isA(SingleReferenceWales.class))).thenReturn(referenceWales);
        String walesRef = WALES_OFFICE_NUMBER + "00011/" + currentYear;
        assertEquals(referenceService.createReference(WALES_USERS_CASE_TYPE_ID, caseId), walesRef);
    }

    @Test
    public void createNewcastleReference() {
        when(singleRefNewcastleRepository.findTopByOrderByIdDesc()).thenReturn(referenceNewcastle);
        when(singleRefNewcastleRepository.save(isA(SingleReferenceNewcastle.class))).thenReturn(referenceNewcastle);
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(referenceService.createReference(NEWCASTLE_USERS_CASE_TYPE_ID, caseId), newcastleRef);
    }

    @Test
    public void createWatfordReference() {
        when(singleRefWatfordRepository.findTopByOrderByIdDesc()).thenReturn(referenceWatford);
        when(singleRefWatfordRepository.save(isA(SingleReferenceWatford.class))).thenReturn(referenceWatford);
        String watfordRef = WATFORD_OFFICE_NUMBER + "00013/" + currentYear;
        assertEquals(referenceService.createReference(WATFORD_USERS_CASE_TYPE_ID, caseId), watfordRef);
    }

    @Test
    public void createLondonCentralReference() {
        when(singleRefLondonCentralRepository.findTopByOrderByIdDesc()).thenReturn(referenceLondonCentral);
        when(singleRefLondonCentralRepository.save(isA(SingleReferenceLondonCentral.class))).thenReturn(referenceLondonCentral);
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + "00014/" + currentYear;
        assertEquals(referenceService.createReference(LONDON_CENTRAL_USERS_CASE_TYPE_ID, caseId), londonCentralRef);
    }

}