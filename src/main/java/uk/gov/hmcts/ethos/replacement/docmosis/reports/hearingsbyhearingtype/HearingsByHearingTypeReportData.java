package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import java.util.ArrayList;
import java.util.List;

@Getter
public class HearingsByHearingTypeReportData extends ListingData {

    @JsonIgnore
    private final HearingsByHearingTypeReportSummaryHdr reportSummaryHdr;
    @JsonIgnore
    private final List<HearingsByHearingTypeReportSummary> reportSummaryList = new ArrayList<>();
    @JsonIgnore
    private final List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList = new ArrayList<>();
    @JsonIgnore
    private final List<HearingsByHearingTypeReportSummary2> reportSummary2List = new ArrayList<>();
    @JsonIgnore
    private final List<HearingsByHearingTypeReportDetail> reportDetails = new ArrayList<>();

    public HearingsByHearingTypeReportData(HearingsByHearingTypeReportSummaryHdr reportSummaryHdr) {
        this.reportSummaryHdr = reportSummaryHdr;
    }

    public HearingsByHearingTypeReportSummaryHdr getReportSummaryHdr() {
        return reportSummaryHdr;
    }

    public List<HearingsByHearingTypeReportSummary> getReportSummaryList() {
        return reportSummaryList;
    }

    public List<HearingsByHearingTypeReportSummary2Hdr> getReportSummary2HdrList() {
        return reportSummary2HdrList;
    }

    public List<HearingsByHearingTypeReportSummary2> getReportSummary2List() {
        return reportSummary2List;
    }

    public List<HearingsByHearingTypeReportDetail> getReportDetails() {
        return reportDetails;
    }

    public void addReportSummaryList(List<HearingsByHearingTypeReportSummary> reportSummaryList) {
        this.reportSummaryList.addAll(reportSummaryList);
    }

    public void addReportSummary2HdrList(List<HearingsByHearingTypeReportSummary2Hdr> reportSummary2HdrList) {
        this.reportSummary2HdrList.addAll(reportSummary2HdrList);
    }

    public void addReportSummary2List(List<HearingsByHearingTypeReportSummary2> reportSummary2List) {
        this.reportSummary2List.addAll(reportSummary2List);
    }

    public void addReportDetail(List<HearingsByHearingTypeReportDetail> reportDetails) {
        this.reportDetails.addAll(reportDetails);
    }

}
