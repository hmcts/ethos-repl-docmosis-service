package uk.gov.hmcts.ethos.replacement.docmosis.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_INIT_REF;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_MAX_REF;

@RunWith(SpringJUnit4ClassRunner.class)
public class SingleReferenceTest {

    @InjectMocks
    private SingleReference singleReference;

    @Test
    public void generateRefNumber() {
        String expectedRefNumber = "00013";
        assertEquals(singleReference.generateRefNumber("12", "2019", "2019"), expectedRefNumber);
    }

    @Test
    public void generateRefNumber1() {
        String expectedRefNumber = "01113";
        assertEquals(singleReference.generateRefNumber("1112", "2019", "2019"), expectedRefNumber);
    }

    @Test
    public void generateRefNumberNoPrevious() {
        assertEquals(singleReference.generateRefNumber("", "", "2019"), DEFAULT_INIT_REF);
    }

    @Test
    public void generateRefNumberDifferentYear() {
        String expectedRefNumber = "00001";
        assertEquals(singleReference.generateRefNumber("54", "2018", "2019"), expectedRefNumber);
    }

    @Test
    public void generateRefNumberMaxValue() {
        String expectedRefNumber = "00001";
        assertEquals(singleReference.generateRefNumber(DEFAULT_MAX_REF, "2019", "2019"), expectedRefNumber);
    }
}