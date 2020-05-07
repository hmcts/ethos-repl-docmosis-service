package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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

    private String manchesterMultipleReference;
    private String scotlandMultipleReference;
    private String leedsMultipleReference;
    private String midlandsWestReference;
    private String midlandsEastReference;
    private String bristolReference;
    private String walesReference;
    private String newcastleReference;
    private String watfordReference;
    private String londonCentralReference;
    private String londonSouthReference;
    private String londonEastReference;
    private String multipleRef;

    @Before
    public void setUp() {
        multipleRef = "10000";
        manchesterMultipleReference = MANCHESTER_OFFICE_NUMBER + multipleRef;
        scotlandMultipleReference = GLASGOW_OFFICE_NUMBER + multipleRef;
        leedsMultipleReference =  LEEDS_OFFICE_NUMBER + multipleRef;
        midlandsWestReference = MIDLANDS_WEST_OFFICE_NUMBER + multipleRef;
        midlandsEastReference = MIDLANDS_EAST_OFFICE_NUMBER + multipleRef;
        bristolReference = BRISTOL_OFFICE_NUMBER + multipleRef;
        walesReference = WALES_OFFICE_NUMBER + multipleRef;
        newcastleReference = NEWCASTLE_OFFICE_NUMBER + multipleRef;
        watfordReference = WATFORD_OFFICE_NUMBER + multipleRef;
        londonCentralReference = LONDON_CENTRAL_OFFICE_NUMBER + multipleRef;
        londonSouthReference = LONDON_SOUTH_OFFICE_NUMBER + multipleRef;
        londonEastReference = LONDON_EAST_OFFICE_NUMBER + multipleRef;
    }

    @Test
    public void createManchesterReference() {
        when(subMultipleRefManchesterRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(manchesterMultipleReference), 1,
                MANCHESTER_CASE_TYPE_ID)).thenReturn(manchesterMultipleReference + "/1");
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + multipleRef + "/1";
        assertEquals(subMultipleReferenceService.createReference(MANCHESTER_DEV_BULK_CASE_TYPE_ID, manchesterMultipleReference, 1), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(subMultipleRefScotlandRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(scotlandMultipleReference), 1,
                SCOTLAND_CASE_TYPE_ID)).thenReturn(scotlandMultipleReference + "/1");
        String scotlandRef = GLASGOW_OFFICE_NUMBER + multipleRef + "/1";
        assertEquals(subMultipleReferenceService.createReference(SCOTLAND_DEV_BULK_CASE_TYPE_ID, scotlandMultipleReference, 1), scotlandRef);
    }

    @Test
    public void createLeedsReference() {
        when(subMultipleRefLeedsRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(leedsMultipleReference), 1,
                LEEDS_CASE_TYPE_ID)).thenReturn(leedsMultipleReference + "/5");
        String leedsRef = LEEDS_OFFICE_NUMBER + multipleRef + "/5";
        assertEquals(subMultipleReferenceService.createReference(LEEDS_BULK_CASE_TYPE_ID, leedsMultipleReference, 1), leedsRef);
    }

    @Test
    public void createMidlandsWestReference() {
        when(subMultipleRefMidlandsWestRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(midlandsWestReference), 1,
                MIDLANDS_WEST_CASE_TYPE_ID)).thenReturn(midlandsWestReference + "/8");
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + multipleRef + "/8";
        assertEquals(subMultipleReferenceService.createReference(MIDLANDS_WEST_BULK_CASE_TYPE_ID, midlandsWestReference, 1), midlandsWestRef);
    }

    @Test
    public void createMidlandsEastReference() {
        when(subMultipleRefMidlandsEastRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(midlandsEastReference), 1,
                MIDLANDS_EAST_CASE_TYPE_ID)).thenReturn(midlandsEastReference + "/9");
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + multipleRef + "/9";
        assertEquals(subMultipleReferenceService.createReference(MIDLANDS_EAST_BULK_CASE_TYPE_ID, midlandsEastReference, 1), midlandsEastRef);
    }

    @Test
    public void createBristolReference() {
        when(subMultipleRefBristolRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(bristolReference), 1,
                BRISTOL_CASE_TYPE_ID)).thenReturn(bristolReference + "/10");
        String bristolRef = BRISTOL_OFFICE_NUMBER + multipleRef + "/10";
        assertEquals(subMultipleReferenceService.createReference(BRISTOL_BULK_CASE_TYPE_ID, bristolReference, 1), bristolRef);
    }

    @Test
    public void createWalesReference() {
        when(subMultipleRefWalesRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(walesReference), 1,
                WALES_CASE_TYPE_ID)).thenReturn(walesReference + "/11");
        String walesRef = WALES_OFFICE_NUMBER + multipleRef + "/11";
        assertEquals(subMultipleReferenceService.createReference(WALES_BULK_CASE_TYPE_ID, walesReference, 1), walesRef);
    }

    @Test
    public void createNewcastleReference() {
        when(subMultipleRefNewcastleRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(newcastleReference), 1,
                NEWCASTLE_CASE_TYPE_ID)).thenReturn(newcastleReference + "/12");
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + multipleRef + "/12";
        assertEquals(subMultipleReferenceService.createReference(NEWCASTLE_BULK_CASE_TYPE_ID, newcastleReference, 1), newcastleRef);
    }

    @Test
    public void createWatfordReference() {
        when(subMultipleRefWatfordRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(watfordReference), 1,
                WATFORD_CASE_TYPE_ID)).thenReturn(watfordReference + "/13");
        String watfordRef = WATFORD_OFFICE_NUMBER + multipleRef + "/13";
        assertEquals(subMultipleReferenceService.createReference(WATFORD_BULK_CASE_TYPE_ID, watfordReference, 1), watfordRef);
    }

    @Test
    public void createLondonCentralReference() {
        when(subMultipleRefLondonCentralRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(londonCentralReference), 1,
                LONDON_CENTRAL_CASE_TYPE_ID)).thenReturn(londonCentralReference + "/14");
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + multipleRef + "/14";
        assertEquals(subMultipleReferenceService.createReference(LONDON_CENTRAL_BULK_CASE_TYPE_ID, londonCentralReference, 1), londonCentralRef);
    }

    @Test
    public void createLondonSouthReference() {
        when(subMultipleRefLondonSouthRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(londonSouthReference), 1,
                LONDON_SOUTH_CASE_TYPE_ID)).thenReturn(londonSouthReference + "/15");
        String londonSouthRef = LONDON_SOUTH_OFFICE_NUMBER + multipleRef + "/15";
        assertEquals(subMultipleReferenceService.createReference(LONDON_SOUTH_BULK_CASE_TYPE_ID, londonSouthReference, 1), londonSouthRef);
    }

    @Test
    public void createLondonEastReference() {
        when(subMultipleRefLondonEastRepository.ethosSubMultipleCaseRefGen(Integer.parseInt(londonEastReference), 1,
                LONDON_EAST_CASE_TYPE_ID)).thenReturn(londonEastReference + "/16");
        String londonEastRef = LONDON_EAST_OFFICE_NUMBER + multipleRef + "/16";
        assertEquals(subMultipleReferenceService.createReference(LONDON_EAST_BULK_CASE_TYPE_ID, londonEastReference, 1), londonEastRef);
    }

}