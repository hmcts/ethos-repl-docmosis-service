package uk.gov.hmcts.ethos.replacement.docmosis.reports.hearingstojudgments;

import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsCaseData;
import uk.gov.hmcts.ecm.common.model.reports.hearingstojudgments.HearingsToJudgmentsSubmitEvent;

import java.util.ArrayList;

public class HearingsToJudgmentsCaseDataBuilder {

    private final HearingsToJudgmentsCaseData caseData = new HearingsToJudgmentsCaseData();

    public HearingsToJudgmentsCaseDataBuilder withEthosCaseReference(String ethosCaseReference) {
        caseData.setEthosCaseReference(ethosCaseReference);
        return this;
    }

    public HearingsToJudgmentsCaseDataBuilder withManagingOffice(String managingOffice) {
        caseData.setManagingOffice(managingOffice);
        return this;
    }

    public HearingsToJudgmentsCaseDataBuilder withHearing(String listedDate, String hearingStatus, String hearingType,
                                                          String disposed) {
        return withHearing(listedDate, hearingStatus, hearingType, disposed, null, null, null);
    }

    public HearingsToJudgmentsCaseDataBuilder withHearing(String listedDate, String hearingStatus, String hearingType,
                                                          String disposed, String hearingNumber, String judge,
                                                          String hearingReserved) {
        var dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(hearingStatus);
        dateListedType.setHearingReservedJudgement(hearingReserved);
        dateListedType.setHearingCaseDisposed(disposed);
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

    public HearingsToJudgmentsCaseDataBuilder withJudgment(String judgmentHearingDate, String dateJudgmentMade,
                                                           String dateJudgmentSent) {
        var judgementType = new JudgementType();
        judgementType.setJudgmentHearingDate(judgmentHearingDate);
        judgementType.setDateJudgmentSent(dateJudgmentSent);
        judgementType.setDateJudgmentMade(dateJudgmentMade);
        var judgementTypeItem = new JudgementTypeItem();
        judgementTypeItem.setValue(judgementType);

        if (caseData.getJudgementCollection() == null) {
            caseData.setJudgementCollection(new ArrayList<>());
        }
        caseData.getJudgementCollection().add(judgementTypeItem);

        return this;
    }

    public HearingsToJudgmentsCaseData build() {
        return caseData;
    }

    public HearingsToJudgmentsSubmitEvent buildAsSubmitEvent(String state) {
        var submitEvent = new HearingsToJudgmentsSubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(state);

        return submitEvent;
    }
}
