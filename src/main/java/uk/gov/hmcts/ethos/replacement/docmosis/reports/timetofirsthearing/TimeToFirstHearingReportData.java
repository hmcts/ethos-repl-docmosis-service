package uk.gov.hmcts.ethos.replacement.docmosis.reports.timetofirsthearing;

import lombok.Getter;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;


@Getter
public class TimeToFirstHearingReportData extends ListingData {

    // JsonIgnore is required on properties so that the report data is not
    // returned to CCD in any callback response.
    // Otherwise this would trigger a CCD Case Data Validation error
    // because the properties are not in the CCD config

   // @JsonIgnore
   // private final ReportSummary reportSummary;



}
