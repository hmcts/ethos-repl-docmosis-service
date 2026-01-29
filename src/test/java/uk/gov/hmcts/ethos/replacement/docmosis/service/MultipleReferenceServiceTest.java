package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefBristolRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLeedsRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonCentralRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefLondonSouthRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefManchesterRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefMidlandsEastRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefMidlandsWestRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefNewcastleRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefScotlandRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefWalesRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.MultipleRefWatfordRepository;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BRISTOL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_CENTRAL_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LONDON_SOUTH_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MANCHESTER_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_EAST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MIDLANDS_WEST_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_DEV_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WALES_OFFICE_NUMBER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_BULK_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.WATFORD_OFFICE_NUMBER;

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

    @Test
    public void createManchesterReference() {
        when(multipleRefManchesterRepository.ethosMultipleCaseRefGen(1, MANCHESTER_CASE_TYPE_ID)).thenReturn("00012");
        String manchesterRef = MANCHESTER_OFFICE_NUMBER + "00012";
        assertEquals(manchesterRef, multipleReferenceService.createReference(MANCHESTER_DEV_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createScotlandReference() {
        when(multipleRefScotlandRepository.ethosMultipleCaseRefGen(1, SCOTLAND_CASE_TYPE_ID)).thenReturn("00015");
        String scotlandRef = GLASGOW_OFFICE_NUMBER + "00015";
        assertEquals(scotlandRef, multipleReferenceService.createReference(SCOTLAND_DEV_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createLeedsReference() {
        when(multipleRefLeedsRepository.ethosMultipleCaseRefGen(1, LEEDS_CASE_TYPE_ID)).thenReturn("00005");
        String leedsRef = LEEDS_OFFICE_NUMBER + "00005";
        assertEquals(leedsRef, multipleReferenceService.createReference(LEEDS_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createMidlandsWestReference() {
        when(multipleRefMidlandsWestRepository.ethosMultipleCaseRefGen(1, MIDLANDS_WEST_CASE_TYPE_ID))
            .thenReturn("00008");
        String midlandsWestRef = MIDLANDS_WEST_OFFICE_NUMBER + "00008";
        assertEquals(midlandsWestRef, multipleReferenceService.createReference(MIDLANDS_WEST_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createMidlandsEastReference() {
        when(multipleRefMidlandsEastRepository.ethosMultipleCaseRefGen(1, MIDLANDS_EAST_CASE_TYPE_ID))
            .thenReturn("00009");
        String midlandsEastRef = MIDLANDS_EAST_OFFICE_NUMBER + "00009";
        assertEquals(midlandsEastRef, multipleReferenceService.createReference(MIDLANDS_EAST_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createBristolReference() {
        when(multipleRefBristolRepository.ethosMultipleCaseRefGen(1, BRISTOL_CASE_TYPE_ID)).thenReturn("00010");
        String bristolRef = BRISTOL_OFFICE_NUMBER + "00010";
        assertEquals(bristolRef, multipleReferenceService.createReference(BRISTOL_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createWalesReference() {
        when(multipleRefWalesRepository.ethosMultipleCaseRefGen(1, WALES_CASE_TYPE_ID)).thenReturn("00011");
        String walesRef = WALES_OFFICE_NUMBER + "00011";
        assertEquals(walesRef, multipleReferenceService.createReference(WALES_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createNewcastleReference() {
        when(multipleRefNewcastleRepository.ethosMultipleCaseRefGen(1, NEWCASTLE_CASE_TYPE_ID)).thenReturn("00012");
        String newcastleRef = NEWCASTLE_OFFICE_NUMBER + "00012";
        assertEquals(newcastleRef, multipleReferenceService.createReference(NEWCASTLE_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createWatfordReference() {
        when(multipleRefWatfordRepository.ethosMultipleCaseRefGen(1, WATFORD_CASE_TYPE_ID)).thenReturn("00013");
        String watfordRef = WATFORD_OFFICE_NUMBER + "00013";
        assertEquals(watfordRef, multipleReferenceService.createReference(WATFORD_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createLondonCentralReference() {
        when(multipleRefLondonCentralRepository.ethosMultipleCaseRefGen(1, LONDON_CENTRAL_CASE_TYPE_ID))
            .thenReturn("00014");
        String londonCentralRef = LONDON_CENTRAL_OFFICE_NUMBER + "00014";
        assertEquals(londonCentralRef, multipleReferenceService.createReference(LONDON_CENTRAL_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createLondonSouthReference() {
        when(multipleRefLondonSouthRepository.ethosMultipleCaseRefGen(1, LONDON_SOUTH_CASE_TYPE_ID))
            .thenReturn("00015");
        String londonSouthRef = LONDON_SOUTH_OFFICE_NUMBER + "00015";
        assertEquals(londonSouthRef, multipleReferenceService.createReference(LONDON_SOUTH_BULK_CASE_TYPE_ID, 1));
    }

    @Test
    public void createLondonEastReference() {
        when(multipleRefLondonEastRepository.ethosMultipleCaseRefGen(1, LONDON_EAST_CASE_TYPE_ID)).thenReturn("00016");
        String londonEastRef = LONDON_EAST_OFFICE_NUMBER + "00016";
        assertEquals(londonEastRef, multipleReferenceService.createReference(LONDON_EAST_BULK_CASE_TYPE_ID, 1));
    }

}