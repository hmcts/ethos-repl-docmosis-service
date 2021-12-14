package uk.gov.hmcts.ethos.replacement.docmosis.reports.nochangeincurrentposition;

import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;

import java.util.ArrayList;

public class NoPositionChangeCaseDataBuilder {
    private final NoPositionChangeCaseData caseData = new NoPositionChangeCaseData();

    public NoPositionChangeCaseDataBuilder withEthosCaseReference(String ethosCaseReference) {
        caseData.setEthosCaseReference(ethosCaseReference);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withReceiptDate(String receiptDate) {
        caseData.setReceiptDate(receiptDate);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withMultipleReference(String reference) {
        caseData.setMultipleReference(reference);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withCaseType(String caseType) {
        caseData.setCaseType(caseType);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withCurrentPosition(String currentPosition) {
        caseData.setCurrentPosition(currentPosition);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withDateToPosition(String dateToPosition) {
        caseData.setDateToPosition(dateToPosition);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withFirstRespondent(String name) {
        caseData.setRespondent(name);
        withRespondent(name);
        return this;
    }

    public NoPositionChangeCaseDataBuilder withRespondent(String name) {
        var respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName(name);
        var respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);

        if (caseData.getRespondentCollection() == null) {
            caseData.setRespondentCollection(new ArrayList<>());
        }
        caseData.getRespondentCollection().add(respondentSumTypeItem);

        return this;
    }

    public NoPositionChangeCaseData build() {
        return caseData;
    }

    public NoPositionChangeSubmitEvent buildAsSubmitEvent(String state) {
        var submitEvent = new NoPositionChangeSubmitEvent();
        submitEvent.setCaseData(caseData);
        submitEvent.setState(state);

        return submitEvent;
    }

}
