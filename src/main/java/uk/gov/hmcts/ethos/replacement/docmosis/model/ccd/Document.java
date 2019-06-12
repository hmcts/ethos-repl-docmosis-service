package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Document {
    @JsonProperty("document_url")
    private String url;
    @JsonProperty("document_binary_url")
    private String binaryUrl;
    @JsonProperty("document_filename")
    private String fileName;

}
