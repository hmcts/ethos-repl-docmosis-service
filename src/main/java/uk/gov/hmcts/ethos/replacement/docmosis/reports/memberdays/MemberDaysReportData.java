package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberDaysReportData extends ListingData {

    @JsonIgnore
    public String office;

    @JsonIgnore
    public String durationDescription;

    @JsonIgnore
    private String fullDaysTotal;

    @JsonIgnore
    private String halfDaysTotal;

    @JsonIgnore
    private String totalDays;

    @JsonIgnore
    private final List<MemberDaySummaryItem> memberDaySummaryItems = new ArrayList<>();

    @JsonIgnore
    private final List<MemberDaysReportDetail> reportDetails = new ArrayList<>();

}
