package uk.gov.hmcts.ethos.replacement.docmosis.reports.casescompleted;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;

@RequiredArgsConstructor
@Data
class HearingSession {
    private final HearingType hearingType;
    private final DateListedType dateListedType;
    private long sessionDays;
}
