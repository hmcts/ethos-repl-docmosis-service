package uk.gov.hmcts.ethos.replacement.docmosis.model.ccd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CaseData {

    @JsonProperty("caseNote")
    private String caseNote;
    @JsonProperty("claimantCollection")
    private List<ClaimantItem> claimantCollection;
    @JsonProperty("positionType")
    private String positionType;
    @JsonProperty("disposeType")
    private DisposeType disposeType;
    @JsonProperty("judgementType")
    private JudgementType judgementType;
    @JsonProperty("jurCodesCollection")
    private List<JurCodesItem> jurCodesCollection;
    @JsonProperty("receiptDate")
    private String receiptDate;
    @JsonProperty("respondentCollection")
    private List<RespondentItem> respondentCollection;
    @JsonProperty("userLocation")
    private String userLocation;
    @JsonProperty("locationType")
    private String locationType;
    @JsonProperty("respondentType")
    private RespondentType respondentType;
    @JsonProperty("depositCollection")
    private List<DepositItem> depositCollection;
    @JsonProperty("caseType")
    private String caseType;
    @JsonProperty("panelCollection")
    private List<PanelItem> panelCollection;
    @JsonProperty("feeGroupReference")
    private String feeGroupReference;
    @JsonProperty("representedType")
    private RepresentedType representedType;
    @JsonProperty("scheduleType")
    private ScheduleType scheduleType;
    @JsonProperty("NH_JudgementType")
    private NhJudgementType nhJudgementType;
    @JsonProperty("claimantType")
    private ClaimantType claimantType;
    @JsonProperty("caseAssignee")
    private String caseAssignee;
    @JsonProperty("referToETJ")
    private List<ReferToETJItem> referToETJ;
    @JsonProperty("tribunalOffice")
    private String tribunalOffice;


}
