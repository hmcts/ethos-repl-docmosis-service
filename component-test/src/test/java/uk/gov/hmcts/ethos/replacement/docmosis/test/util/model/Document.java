package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Document {
    @JsonProperty("document_url")
    private String url;
    @JsonProperty("document_binary_url")
    private String binaryUrl;
    @JsonProperty("document_filename")
    private String fileName;

}
