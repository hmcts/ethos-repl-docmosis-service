package uk.gov.hmcts.ethos.replacement.docmosis.reports.casesawaitingjudgment;

import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.reports.casesawaitingjudgment.CaseData;
import uk.gov.hmcts.ecm.common.model.reports.casesawaitingjudgment.CasesAwaitingJudgmentSubmitEvent;

import java.util.ArrayList;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

public class CaseDataBuilder {

    private final CaseData caseData = new CaseData();

    public CaseDataBuilder withEthosCaseReference(String ethosCaseReference) {
        caseData.setEthosCaseReference(ethosCaseReference);
        return this;
    }

    public CaseDataBuilder withSingleCaseType() {
        caseData.setEcmCaseType(SINGLE_CASE_TYPE);
        return this;
    }

    public CaseDataBuilder withMultipleCaseType(String multipleReference) {
        caseData.setEcmCaseType(MULTIPLE_CASE_TYPE);
        caseData.setMultipleReference(multipleReference);
        return this;
    }

    public CaseDataBuilder withCurrentPosition(String currentPosition) {
        caseData.setCurrentPosition(currentPosition);
        return this;
    }

    public CaseDataBuilder withDateToPosition(String dateToPosition) {
        caseData.setDateToPosition(dateToPosition);
        return this;
    }

    public CaseDataBuilder withConciliationTrack(String conciliationTrack) {
        caseData.setConciliationTrack(conciliationTrack);
        return this;
    }

    public CaseDataBuilder withHearing(String listedDate, String hearingStatus) {
        return withHearing(listedDate, hearingStatus, null, null, null);
    }

    public CaseDataBuilder withHearing(String listedDate, String hearingStatus, String hearingNumber,
                                       String hearingType, String judge) {
        var dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(hearingStatus);
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);

        var hearingDates = new ArrayList<DateListedTypeItem>();
        hearingDates.add(dateListedTypeItem);

        var type = new HearingType();
        type.setHearingNumber(hearingNumber);
        type.setHearingType(hearingType);
        type.setJudge(judge);
        type.setHearingDateCollection(hearingDates);

        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(type);

        if (caseData.getHearingCollection() == null) {
            caseData.setHearingCollection(new ArrayList<>());
        }
        caseData.getHearingCollection().add(hearingTypeItem);

        return this;
    }

    public CaseDataBuilder withJudgment() {
        var judgementType = new JudgementType();
        var judgementTypeItem = new JudgementTypeItem();
        judgementTypeItem.setValue(judgementType);

        if (caseData.getJudgementCollection() == null) {
            caseData.setJudgementCollection(new ArrayList<>());
        }
        caseData.getJudgementCollection().add(judgementTypeItem);

        return this;
    }

    public CaseDataBuilder withPositionType(String positionType) {
        caseData.setPositionType(positionType);
        return this;
    }

    public CaseData build() {
        return caseData;
    }

    public CasesAwaitingJudgmentSubmitEvent buildAsSubmitEvent(String state) {
        var submitEvent = new CasesAwaitingJudgmentSubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(state);

        return submitEvent;
    }

}
