package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CorrespondenceScotType {

    @JsonProperty("topLevel_Scot_Documents")
    private String topLevelScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00042")
    private String part1ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00043")
    private String part2ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00044")
    private String part3ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00045")
    private String part4ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00046")
    private String part5ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00047")
    private String part6ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00048")
    private String part7ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00049")
    private String part8ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00050")
    private String part10ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00051")
    private String part11ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00052")
    private String part12ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00053")
    private String part13ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00054")
    private String part14ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00055")
    private String part15ScotDocuments;
    @JsonProperty("EM-TRB-SCO-ENG-00056")
    private String part16ScotDocuments;
}
