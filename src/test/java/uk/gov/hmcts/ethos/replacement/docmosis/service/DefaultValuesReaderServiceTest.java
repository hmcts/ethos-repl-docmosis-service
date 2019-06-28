package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DefaultValuesReaderService.GLASGOW_CASE_TYPE_ID;
import static uk.gov.hmcts.ethos.replacement.docmosis.service.DefaultValuesReaderService.MANCHESTER_CASE_TYPE_ID;

@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultValuesReaderServiceTest {

    @InjectMocks
    private DefaultValuesReaderService defaultValuesReaderService;

    private DefaultValues preDefaultValues;
    private DefaultValues postDefaultValuesManchester;
    private DefaultValues postDefaultValuesGlasgow;

    @Before
    public void setUp() {
        preDefaultValues = DefaultValues.builder().claimantTypeOfClaimant("Individual").build();
        postDefaultValuesManchester = DefaultValues.builder()
                .positionType("Manually Created")
                .tribunalCorrespondenceAddressLine1("Manchester Employment Tribunal,")
                .tribunalCorrespondenceAddressLine2("Alexandra House,")
                .tribunalCorrespondenceAddressLine3("14-22 The Parsonage,")
                .tribunalCorrespondenceTown("Manchester,")
                .tribunalCorrespondencePostCode("M3 2JA")
                .tribunalCorrespondenceTelephone("0161 833 6100")
                .tribunalCorrespondenceFax("0870 739 4433")
                .tribunalCorrespondenceDX("DX 743570")
                .tribunalCorrespondenceEmail("Manchesteret@justice.gov.uk")
                .build();
        postDefaultValuesGlasgow = DefaultValues.builder()
                .positionType("Manually Created")
                .tribunalCorrespondenceAddressLine1("Eagle Building,")
                .tribunalCorrespondenceAddressLine2("215 Bothwell Street,")
                .tribunalCorrespondenceTown("Glasgow,")
                .tribunalCorrespondencePostCode("G2 7TS")
                .tribunalCorrespondenceTelephone("0141 204 0730")
                .tribunalCorrespondenceFax("01264 785 177")
                .tribunalCorrespondenceDX("DX 580003")
                .tribunalCorrespondenceEmail("glasgowet@justice.gov.uk")
                .build();
    }

    @Test
    public void getPreDefaultValues() {
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.PRE_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID);
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getManchesterPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.POST_DEFAULT_XLSX_FILE_PATH, MANCHESTER_CASE_TYPE_ID);
        assertEquals(postDefaultValuesManchester, postDefaultValues1);
    }

    @Test
    public void getGlasgowPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.POST_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID);
        assertEquals(postDefaultValuesGlasgow, postDefaultValues1);
    }
}