package uk.gov.hmcts.ethos.replacement.docmosis.utils;

import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.JudgementTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.JudgementType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;

import java.util.ArrayList;
import java.util.UUID;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LEEDS_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

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

    public CaseDataBuilder withHearing(String hearingNumber, String hearingType, String judge) {
        if (caseData.getHearingCollection() == null) {
            caseData.setHearingCollection(new ArrayList<>());
        }

        var type = new HearingType();
        type.setHearingNumber(hearingNumber);
        type.setHearingType(hearingType);
        type.setJudge(judge);

        var hearingTypeItem = new HearingTypeItem();
        hearingTypeItem.setValue(type);
        caseData.getHearingCollection().add(hearingTypeItem);

        return this;
    }

    public CaseDataBuilder withHearingSession(int hearingIndex, String listedDate, String hearingStatus,
                                              boolean disposed) {

        var dateListedType = new DateListedType();
        dateListedType.setListedDate(listedDate);
        dateListedType.setHearingStatus(hearingStatus);
        dateListedType.setHearingCaseDisposed(disposed ? YES : NO);
        var dateListedTypeItem = new DateListedTypeItem();
        dateListedTypeItem.setValue(dateListedType);

        var hearing = caseData.getHearingCollection().get(hearingIndex);
        if (hearing.getValue().getHearingDateCollection() == null) {
            hearing.getValue().setHearingDateCollection(new ArrayList<>());
        }

        hearing.getValue().getHearingDateCollection().add(dateListedTypeItem);

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

    public static CaseDataBuilder builder() {
        return new CaseDataBuilder();
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

    public CCDRequest buildAsCcdRequest(String token) {
        CCDRequest ccdRequest = new CCDRequest();
        ccdRequest.setToken(token);
        ccdRequest.setCaseDetails(buildAsCaseDetails(ACCEPTED_STATE, LEEDS_CASE_TYPE_ID));
        return ccdRequest;
    }

    public CaseDetails buildAsCaseDetails(String state, String caseTypeId) {
        CaseDetails caseDetails = new CaseDetails();
        caseDetails.setCaseData(caseData);
        caseDetails.setCaseId("1234567890123456");
        caseDetails.setState(state);
        caseDetails.setCaseTypeId(caseTypeId);
        return caseDetails;
    }

    public CaseDataBuilder withDocumentCollection(String docType) {
        if (caseData.getDocumentCollection() == null) {
            caseData.setDocumentCollection(new ArrayList<>());
        }
        UploadedDocumentType uploadedDocumentType = new UploadedDocumentType();
        uploadedDocumentType.setDocumentFilename("test.pdf");
        uploadedDocumentType.setDocumentBinaryUrl("http://dummy.link");
        DocumentType documentType = new DocumentType();
        documentType.setTypeOfDocument(docType);
        documentType.setUploadedDocument(uploadedDocumentType);
        DocumentTypeItem documentTypeItem = new DocumentTypeItem();
        documentTypeItem.setValue(documentType);
        documentTypeItem.setId(UUID.randomUUID().toString());
        caseData.getDocumentCollection().add(documentTypeItem);
        return this;
    }
}

