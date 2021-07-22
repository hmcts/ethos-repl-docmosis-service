package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;

import java.util.ArrayList;

public class CaseDataBuilder {

    private CaseData caseData = new CaseData();

    public CaseDataBuilder withEthosCaseReference(String ethosCaseReference) {
        caseData.setEthosCaseReference(ethosCaseReference);
        return this;
    }

    public CaseDataBuilder withHearing(String listedDate, String hearingStatus) {
        var dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(hearingStatus);
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);

        var hearingDates = new ArrayList<DateListedTypeItem>();
        hearingDates.add(dateListedTypeItem);

        var hearingType = new HearingType();
        hearingType.setHearingDateCollection(hearingDates);

        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(hearingType);

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

    public SubmitEvent buildAsSubmitEvent(String state) {
        var submitEvent = new SubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(state);

        return submitEvent;
    }

}
