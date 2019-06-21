package uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
class UploadedDocumentType {

    @JsonProperty("document_binary_url")
    private String documentBinaryUrl;
    @JsonProperty("document_filename")
    private String documentFilename;
    @JsonProperty("document_url")
    private String documentUrl;
}
