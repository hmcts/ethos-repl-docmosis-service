package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;

import java.util.List;

@Data
public class NoPositionChangeCaseData {
    @JsonProperty("ethosCaseReference")
    private String ethosCaseReference;

    @JsonProperty("receiptDate")
    private String receiptDate;

    @JsonProperty("respondentCollection")
    private List<RespondentSumTypeItem> respondentCollection;

    @JsonProperty("respondent")
    private String respondent;

    @JsonProperty("multipleReference")
    private String multipleReference;

    @JsonProperty("caseType")
    private String caseType;

    @JsonProperty("currentPosition")
    private String currentPosition;

    @JsonProperty("dateToPosition")
    private String dateToPosition;
}
