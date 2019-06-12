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
    private DefaultValues postDefaultValues;

    @Before
    public void setUp() {
        preDefaultValues = DefaultValues.builder().claimantTypeOfClaimant("Individual").build();
        postDefaultValues = DefaultValues.builder()
                .positionType("Awaiting ET3")
                .tribunalCorrespondenceAddress("35 La Nava S3 6AD, Southampton")
                .tribunalCorrespondenceTelephone("3577131270")
                .tribunalCorrespondenceFax("7577126570")
                .tribunalCorrespondenceDX("123456")
                .tribunalCorrespondenceEmail("manchester@gmail.com")
                .build();
    }

    @Test
    public void getPreDefaultValues() {
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.PRE_DEFAULT_XLSX_FILE_PATH, GLASGOW_CASE_TYPE_ID);
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.POST_DEFAULT_XLSX_FILE_PATH, MANCHESTER_CASE_TYPE_ID);
        assertEquals(postDefaultValues, postDefaultValues1);
    }
}