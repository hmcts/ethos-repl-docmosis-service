package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefBristolRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLeedsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonCentralRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefLondonSouthRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefManchesterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefMidlandsEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefMidlandsWestRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefNewcastleRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefScotlandRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefWalesRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.SingleRefWatfordRepository;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_OFFICE_NUMBER;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleReferenceServiceTest {

    @InjectMocks
    private SingleReferenceService singleReferenceService;
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
    @Mock
    private SingleRefLondonSouthRepository singleRefLondonSouthRepository;
    @Mock
    private SingleRefLondonEastRepository singleRefLondonEastRepository;

    private String currentYear;

    @Before
    public void setUp() {
        currentYear = String.valueOf(LocalDate.now().getYear());
    }

    @Test
    public void createManchesterReference() {
        when(singleRefManchesterRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                MANCHESTER_CASE_TYPE_ID)).thenReturn("00012/" + currentYear);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(singleReferenceService.createReference(MANCHESTER_DEV_CASE_TYPE_ID, 1), manchesterRef);
    }

    @Test
    public void createManchesterReferenceMultipleCases() {
        when(singleRefManchesterRepository.ethosCaseRefGen(2, Integer.parseInt(currentYear),
                MANCHESTER_CASE_TYPE_ID)).thenReturn("00012/" + currentYear);
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(singleReferenceService.createReference(MANCHESTER_DEV_CASE_TYPE_ID, 2), manchesterRef);
    }

    @Test
    public void createScotlandReference() {
        when(singleRefScotlandRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                SCOTLAND_CASE_TYPE_ID)).thenReturn("00012/" + currentYear);
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(singleReferenceService.createReference(SCOTLAND_DEV_CASE_TYPE_ID, 1), scotlandRef);
    }

    @Test
    public void createLeedsReference() {
        when(singleRefLeedsRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                LEEDS_CASE_TYPE_ID)).thenReturn("00006/" + currentYear);
        String leedsRef = LEEDS_OFFICE_NUMBER + "00006/" + currentYear;
        assertEquals(singleReferenceService.createReference(LEEDS_CASE_TYPE_ID, 1), leedsRef);
    }

    @Test
    public void createMidlandsWestReference() {
        when(singleRefMidlandsWestRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                MIDLANDS_WEST_CASE_TYPE_ID)).thenReturn("00009/" + currentYear);
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + "00009/" + currentYear;
        assertEquals(singleReferenceService.createReference(MIDLANDS_WEST_CASE_TYPE_ID, 1), midlandsWestRef);
    }

    @Test
    public void createMidlandsEastReference() {
        when(singleRefMidlandsEastRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                MIDLANDS_EAST_CASE_TYPE_ID)).thenReturn("00010/" + currentYear);
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + "00010/" + currentYear;
        assertEquals(singleReferenceService.createReference(MIDLANDS_EAST_CASE_TYPE_ID, 1), midlandsEastRef);
    }

    @Test
    public void createBristolReference() {
        when(singleRefBristolRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                BRISTOL_CASE_TYPE_ID)).thenReturn("00011/" + currentYear);
        String bristolRef = BRISTOL_OFFICE_NUMBER + "00011/" + currentYear;
        assertEquals(singleReferenceService.createReference(BRISTOL_CASE_TYPE_ID, 1), bristolRef);
    }

    @Test
    public void createWalesReference() {
        when(singleRefWalesRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                WALES_CASE_TYPE_ID)).thenReturn("00012/" + currentYear);
        String walesRef = WALES_OFFICE_NUMBER + "00012/" + currentYear;
        assertEquals(singleReferenceService.createReference(WALES_CASE_TYPE_ID, 1), walesRef);
    }

    @Test
    public void createNewcastleReference() {
        when(singleRefNewcastleRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                NEWCASTLE_CASE_TYPE_ID)).thenReturn("00013/" + currentYear);
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + "00013/" + currentYear;
        assertEquals(singleReferenceService.createReference(NEWCASTLE_CASE_TYPE_ID, 1), newcastleRef);
    }

    @Test
    public void createWatfordReference() {
        when(singleRefWatfordRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                WATFORD_CASE_TYPE_ID)).thenReturn("00014/" + currentYear);
        String watfordRef = WATFORD_OFFICE_NUMBER + "00014/" + currentYear;
        assertEquals(singleReferenceService.createReference(WATFORD_CASE_TYPE_ID, 1), watfordRef);
    }

    @Test
    public void createLondonCentralReference() {
        when(singleRefLondonCentralRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                LONDON_CENTRAL_CASE_TYPE_ID)).thenReturn("00015/" + currentYear);
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + "00015/" + currentYear;
        assertEquals(singleReferenceService.createReference(LONDON_CENTRAL_CASE_TYPE_ID, 1), londonCentralRef);
    }

    @Test
    public void createLondonSouthReference() {
        when(singleRefLondonSouthRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                LONDON_SOUTH_CASE_TYPE_ID)).thenReturn("00016/" + currentYear);
        String londonSouthRef = LONDON_SOUTH_OFFICE_NUMBER + "00016/" + currentYear;
        assertEquals(singleReferenceService.createReference(LONDON_SOUTH_CASE_TYPE_ID, 1), londonSouthRef);
    }

    @Test
    public void createLondonEastReference() {
        when(singleRefLondonEastRepository.ethosCaseRefGen(1, Integer.parseInt(currentYear),
                LONDON_EAST_CASE_TYPE_ID)).thenReturn("00017/" + currentYear);
        String londonEastRef = LONDON_EAST_OFFICE_NUMBER + "00017/" + currentYear;
        assertEquals(singleReferenceService.createReference(LONDON_EAST_CASE_TYPE_ID, 1), londonEastRef);
    }

}