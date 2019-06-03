package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CorrespondenceType {

    @JsonProperty("topLevel_Documents")
    private String topLevelDocuments;
    @JsonProperty("EM-TRB-EGW-ENG-00026")
    private String part1Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00027")
    private String part2Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00028")
    private String part3Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00029")
    private String part4Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00030")
    private String part5Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00031")
    private String part6Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00032")
    private String part7Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00033")
    private String part9Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00034")
    private String part10Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00035")
    private String part11Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00036")
    private String part12Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00037")
    private String part13Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00038")
    private String part14Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00039")
    private String part15Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00040")
    private String part16Documents;
    @JsonProperty("EM-TRB-EGW-ENG-00041")
    private String part17Documents;
}
