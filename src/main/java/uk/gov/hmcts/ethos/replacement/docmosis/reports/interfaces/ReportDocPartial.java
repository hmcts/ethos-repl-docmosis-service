package uk.gov.hmcts.ethos.replacement.docmosis.reports.interfaces;

import uk.gov.hmcts.ecm.common.model.listing.ListingData;

public interface ReportDocPartial {
    StringBuilder getReportDocPart(ListingData data);
}
