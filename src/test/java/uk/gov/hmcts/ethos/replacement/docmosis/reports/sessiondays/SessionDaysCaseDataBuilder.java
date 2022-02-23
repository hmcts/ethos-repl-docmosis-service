package uk.gov.hmcts.ethos.replacement.docmosis.reports.sessiondays;

import java.util.*;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_HEARD;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_TYPE_JUDICIAL_MEDIATION_TCC;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysCaseData;
import uk.gov.hmcts.ecm.common.model.reports.sessiondays.SessionDaysSubmitEvent;

public class SessionDaysCaseDataBuilder {
    private final SessionDaysCaseData caseData = new SessionDaysCaseData();


    public void withNoHearings() {
        caseData.setHearingCollection(null);
    }

    public RespondentSumTypeItem getHearing(String respName) {
        RespondentSumTypeItem item = new RespondentSumTypeItem();
        RespondentSumType type = new RespondentSumType();
        type.setRespondentName(respName);
        item.setId(UUID.randomUUID().toString());
        item.setValue(type);
        return item;
    }

    public RepresentedTypeRItem getRepresentative(String respName, String repName) {
        RepresentedTypeRItem item = new RepresentedTypeRItem();
        RepresentedTypeR type = new RepresentedTypeR();
        type.setRespRepName(respName);
        type.setNameOfRepresentative(repName);
        item.setId(UUID.randomUUID().toString());
        item.setValue(type);
        return item;
    }

    public void withHearingData(String hearingStatus) {
        List<HearingTypeItem> hearings = new ArrayList<>();
        hearings.add(addHearingSession(hearingStatus, "ftcJudge"));
        hearings.add(addHearingSession(hearingStatus, "ptcJudge"));
        hearings.add(addHearingSession(hearingStatus, ""));
        caseData.setHearingCollection(hearings);
    }

    private HearingTypeItem addHearingSession(String hearingStatus, String judge) {
        HearingTypeItem item = new HearingTypeItem();
        item.setId(UUID.randomUUID().toString());
        HearingType type = new HearingType();
        type.setHearingSitAlone("Sit Alone");
        type.setHearingType(HEARING_TYPE_JUDICIAL_MEDIATION_TCC);
        type.setJudge(judge);
        type.setHearingNumber("1");
        item.setValue(type);
        DateListedTypeItem dItem = new DateListedTypeItem();
        dItem.setId(UUID.randomUUID().toString());
        DateListedType dType = new DateListedType();
        dType.setHearingStatus(hearingStatus);
        dType.setHearingClerk("Clerk A");
        dType.setListedDate("2022-01-20T11:00:00.000");
        dType.setHearingTimingStart("2022-01-20T11:00:00.000");
        dType.setHearingTimingFinish("2022-01-20T17:00:00.000");
        dType.setHearingTimingBreak("2022-01-20T13:00:00.000");
        dType.setHearingTimingResume("2022-01-20T13:30:00.000");
        dItem.setValue(dType);
        item.getValue().setHearingDateCollection(Collections.singletonList(dItem));
        return item;
    }

    public SessionDaysSubmitEvent buildAsSubmitEvent() {
        var submitEvent = new SessionDaysSubmitEvent();
        caseData.setEthosCaseReference("111");
        submitEvent.setCaseData(caseData);
        return submitEvent;
    }
}