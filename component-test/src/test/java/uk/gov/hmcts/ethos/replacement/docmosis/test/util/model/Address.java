package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Address {

    @JsonProperty("AddressLine1")
    private String addressLine1;
    @JsonProperty("AddressLine2")
    private String addressLine2;
    @JsonProperty("AddressLine3")
    private String addressLine3;
    @JsonProperty("PostTown")
    private String postTown;
    @JsonProperty("County")
    private String county;
    @JsonProperty("PostCode")
    private String postCode;
    @JsonProperty("Country")
    private String country;

    public String toString() {
        return String.join(", ", notNullOrEmptyAddress(new ArrayList<>(), Arrays.asList(addressLine1,
                addressLine2, addressLine3, postTown, county, postCode, country)));
    }

    private List<String> notNullOrEmptyAddress(List<String> fullAddress, List<String> attributes) {
        for (String aux : attributes) {
            if (!isNullOrEmpty(aux)) fullAddress.add(aux);
        }
        return fullAddress;
    }

}

