package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CorrespondenceScotType {

    @JsonProperty("topLevel_Scot_Documents")
    private String topLevelScotDocuments;
    @JsonProperty("part_1_Scot_Documents")
    private String part1ScotDocuments;
    @JsonProperty("part_2_Scot_Documents")
    private String part2ScotDocuments;
    @JsonProperty("part_3_Scot_Documents")
    private String part3ScotDocuments;
    @JsonProperty("part_4_Scot_Documents")
    private String part4ScotDocuments;
    @JsonProperty("part_5_Scot_Documents")
    private String part5ScotDocuments;
    @JsonProperty("Part_6_Scot_Documents")
    private String part6ScotDocuments;
    @JsonProperty("Part_7_Scot_Documents")
    private String part7ScotDocuments;
    @JsonProperty("Part_8_Scot_Documents")
    private String part8ScotDocuments;
    @JsonProperty("Part_9_Scot_Documents")
    private String part9ScotDocuments;
    @JsonProperty("Part_10_Scot_Documents")
    private String part10ScotDocuments;
    @JsonProperty("Part_11_Scot_Documents")
    private String part11ScotDocuments;
    @JsonProperty("Part_12_Scot_Documents")
    private String part12ScotDocuments;
    @JsonProperty("Part_13_Scot_Documents")
    private String part13ScotDocuments;
    @JsonProperty("Part_14_Scot_Documents")
    private String part14ScotDocuments;
    @JsonProperty("Part_15_Scot_Documents")
    private String part15ScotDocuments;
    @JsonProperty("claimantORrespondent")
    private String claimantOrRespondent;
    @JsonProperty("hearingNumber")
    private String hearingNumber;
}
