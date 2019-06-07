package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CorrespondenceType {

    @JsonProperty("topLevel_Documents")
    private String topLevelDocuments;
    @JsonProperty("part_1_Documents")
    private String part1Documents;
    @JsonProperty("part_2_Documents")
    private String part2Documents;
    @JsonProperty("part_3_Documents")
    private String part3Documents;
    @JsonProperty("part_4_Documents")
    private String part4Documents;
    @JsonProperty("part_5_Documents")
    private String part5Documents;
    @JsonProperty("Part_6_Documents")
    private String part6Documents;
    @JsonProperty("Part_7_Documents")
    private String part7Documents;
    @JsonProperty("Part_9_Documents")
    private String part9Documents;
    @JsonProperty("Part_10_Documents")
    private String part10Documents;
    @JsonProperty("Part_11_Documents")
    private String part11Documents;
    @JsonProperty("Part_12_Documents")
    private String part12Documents;
    @JsonProperty("Part_13_Documents")
    private String part13Documents;
    @JsonProperty("Part_14_Documents")
    private String part14Documents;
    @JsonProperty("Part_15_Documents")
    private String part15Documents;
    @JsonProperty("Part_16_Documents")
    private String part16Documents;
    @JsonProperty("Part_17_Documents")
    private String part17Documents;
}
