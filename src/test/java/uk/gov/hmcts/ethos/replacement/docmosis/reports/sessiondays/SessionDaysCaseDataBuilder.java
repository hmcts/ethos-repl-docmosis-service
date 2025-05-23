package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysCaseData;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_HEARING;

public class SessionDaysCaseDataBuilder {
    private final SessionDaysCaseData caseData = new SessionDaysCaseData();

    public void withNoHearings() {
        caseData.setHearingCollection(null);
    }

    public void withHearingData(String hearingStatus) {
        List<HearingTypeItem> hearings = new ArrayList<>();
        hearings.add(addHearingSession(hearingStatus, "ftcJudge"));
        hearings.add(addHearingSession(hearingStatus, "ptcJudge"));
        hearings.add(addHearingSession(hearingStatus, ""));
        hearings.add(addHearingSession(hearingStatus, "unknownJudge"));
        caseData.setHearingCollection(hearings);
    }

    private HearingTypeItem addHearingSession(String hearingStatus, String judge) {
        HearingTypeItem item = new HearingTypeItem();
        item.setId(UUID.randomUUID().toString());
        HearingType type = new HearingType();
        type.setHearingSitAlone("Sit Alone");
        type.setHearingFormat(Collections.singletonList("Telephone"));
        type.setHearingType(HEARING_TYPE_JUDICIAL_HEARING);
        type.setJudge(judge);
        type.setHearingNumber("1");
        item.setValue(type);
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setId(UUID.randomUUID().toString());
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingStatus(hearingStatus);
        dateListedType.setHearingClerk("Clerk A");
        dateListedType.setListedDate("2022-01-20T11:00:00.000");
        dateListedType.setHearingTimingStart("2022-01-20T11:00:00.000");
        dateListedType.setHearingTimingFinish("2022-01-20T17:00:00.000");
        dateListedType.setHearingTimingBreak("2022-01-20T13:00:00");
        dateListedType.setHearingTimingResume("2022-01-20T13:30:00.000");
        dateListedTypeItem.setValue(dateListedType);
        item.getValue().setHearingDateCollection(Collections.singletonList(dateListedTypeItem));
        return item;
    }

    public SessionDaysSubmitEvent buildAsSubmitEvent() {
        var submitEvent = new SessionDaysSubmitEvent();
        caseData.setEthosCaseReference("111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }
}
