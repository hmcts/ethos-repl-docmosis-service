package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AddressTest {
    private Address address;
    private Address addressMissingFields;

    @Before
    public void setUp() throws Exception {
        String json = "{"
                + " \"AddressLine1\" : \"Unit 14\", "
                + " \"AddressLine2\" : \"3 Edgar Buildings\", "
                + " \"AddressLine3\" : \"George Street\", "
                + " \"PostTown\" : \"Bath\", "
                + " \"County\" : \"Somerset\", "
                + " \"PostCode\" : \"BA1 2FJ\", "
                + " \"Country\" : \"England\" "
                + "} ";
        String json2 = "{"
                + " \"AddressLine1\" : \"Unit 14\", "
                + " \"PostTown\" : \"Bath\", "
                + " \"County\" : \"Somerset\", "
                + " \"PostCode\" : \"BA1 2FJ\", "
                + " \"Country\" : \"England\" "
                + "} ";
        ObjectMapper mapper = new ObjectMapper();
        address = mapper.readValue(json, Address.class);
        ObjectMapper mapper2 = new ObjectMapper();
        addressMissingFields = mapper2.readValue(json2, Address.class);
    }

    @Test
    public void shouldCreateAddressFromJson() {
        assertThat(address.getAddressLine1(), is("Unit 14"));
        assertThat(address.getAddressLine2(), is("3 Edgar Buildings"));
        assertThat(address.getAddressLine3(), is("George Street"));
        assertThat(address.getPostTown(), is("Bath"));
        assertThat(address.getCounty(), is("Somerset"));
        assertThat(address.getPostCode(), is("BA1 2FJ"));
        assertThat(address.getCountry(), is("England"));
    }

    @Test
    public void shouldReturnAddressAllFields() {
        assertThat(address.toString(), is("Unit 14, 3 Edgar Buildings, George Street, Bath, Somerset, BA1 2FJ, England"));
    }

    @Test
    public void shouldReturnAddressMissingFields() {
        assertThat(addressMissingFields.toString(), is("Unit 14, Bath, Somerset, BA1 2FJ, England"));
    }

}