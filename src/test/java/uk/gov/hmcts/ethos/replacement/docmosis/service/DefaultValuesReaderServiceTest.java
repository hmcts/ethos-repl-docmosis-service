package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.DefaultValues;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class DefaultValuesReaderServiceTest {

    @InjectMocks
    private DefaultValuesReaderService defaultValuesReaderService;

    private DefaultValues preDefaultValues;
    private DefaultValues postDefaultValues;

    @Before
    public void setUp() {
        preDefaultValues = DefaultValues.builder().claimantTypeOfClaimant("Individual").build();
        postDefaultValues = DefaultValues.builder().positionType("Awaiting ET3").build();
    }

    @Test
    public void getPreDefaultValues() {
        DefaultValues preDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.PRE_DEFAULT_XLSX_FILE_PATH);
        assertEquals(preDefaultValues, preDefaultValues1);
    }

    @Test
    public void getPostDefaultValues() {
        DefaultValues postDefaultValues1 = defaultValuesReaderService.getDefaultValues(DefaultValuesReaderService.POST_DEFAULT_XLSX_FILE_PATH);
        assertEquals(postDefaultValues, postDefaultValues1);
    }
}