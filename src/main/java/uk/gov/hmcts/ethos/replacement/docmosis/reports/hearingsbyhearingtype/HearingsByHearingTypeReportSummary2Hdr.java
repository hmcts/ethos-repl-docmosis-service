package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingsbyhearingtype;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HearingsByHearingTypeReportSummary2Hdr {

    HearingsByHearingTypeReportSummary2Hdr(String subSplit) {
        fields = new ReportFields();
        fields.setSubSplit(subSplit);
        initReportFields(fields);
    }

    private ReportFields fields;

    private void initReportFields(ReportFields reportFields) {
        reportFields.setHearingCount("0");
        reportFields.setCmCount("0");
        reportFields.setCostsCount("0");
        reportFields.setTotal("0");
        reportFields.setHearingPrelimCount("0");
        reportFields.setReconsiderCount("0");
        reportFields.setRemedyCount("0");
        reportFields.setDate("");
    }
}
