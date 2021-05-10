package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;

@Slf4j
@RequiredArgsConstructor
@Service("caseCreationForCaseWorkerService")
public class CaseCreationForCaseWorkerService {

    private static final String MESSAGE = "Failed to create new case for case id : ";
    private final CcdClient ccdClient;
    private final SingleReferenceService singleReferenceService;
    private final MultipleReferenceService multipleReferenceService;
    private final PersistentQHelperService persistentQHelperService;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    public SubmitEvent caseCreationRequest(CCDRequest ccdRequest, String userToken) {
        CaseDetails caseDetails = ccdRequest.getCaseDetails();
        log.info("EventId: " + ccdRequest.getEventId());
        try {
            return ccdClient.submitCaseCreation(userToken, caseDetails,
                    ccdClient.startCaseCreation(userToken, caseDetails));
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseId() + ex.getMessage());
        }
    }

    public CaseData generateCaseRefNumbers(CCDRequest ccdRequest) {
        CaseData caseData = ccdRequest.getCaseDetails().getCaseData();
        if (caseData.getCaseRefNumberCount() != null && Integer.parseInt(caseData.getCaseRefNumberCount()) > 0) {
            log.info("Case Type: " + ccdRequest.getCaseDetails().getCaseTypeId());
            log.info("Count: " + Integer.parseInt(caseData.getCaseRefNumberCount()));
            caseData.setStartCaseRefNumber(singleReferenceService.createReference(
                    ccdRequest.getCaseDetails().getCaseTypeId(),
                    Integer.parseInt(caseData.getCaseRefNumberCount())));
            caseData.setMultipleRefNumber(multipleReferenceService.createReference(
                    UtilHelper.getBulkCaseTypeId(ccdRequest.getCaseDetails().getCaseTypeId()), 1));
        }
        return caseData;
    }

    private boolean checkBfActionsCleared(CaseData caseData) {
        if (caseData.getBfActions() != null) {
            for (BFActionTypeItem bfActionTypeItem : caseData.getBfActions()) {
                if (isNullOrEmpty(bfActionTypeItem.getValue().getCleared())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkHearingsNotListed(CaseData caseData) {
        if (caseData.getHearingCollection() != null) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null) {
                    for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
                        if (dateListedTypeItem.getValue().getHearingStatus() != null
                                && dateListedTypeItem.getValue().getHearingStatus().equals(HEARING_STATUS_LISTED)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void createCaseTransfer(CaseDetails caseDetails, List<String> errors, String userToken) {

        CaseData caseData = caseDetails.getCaseData();

        if (!checkBfActionsCleared(caseData)) {

            errors.add(
                    "There are one or more open Brought Forward actions that must be cleared before this case can "
                            + "be transferred");
        }

        if (!checkHearingsNotListed(caseData)) {

            errors.add(
                    "There are one or more hearings that have the status Listed. These must be updated before this "
                            + "case can be transferred");
        }

        if (!errors.isEmpty()) {

            return;

        }

        persistentQHelperService.sendCreationEventToSingles(
                userToken,
                caseDetails.getCaseTypeId(),
                caseDetails.getJurisdiction(),
                errors,
                new ArrayList<>(Collections.singletonList(caseData.getEthosCaseReference())),
                caseData.getOfficeCT().getValue().getCode(),
                caseData.getPositionTypeCT(),
                ccdGatewayBaseUrl,
                caseData.getReasonForCT(),
                SINGLE_CASE_TYPE,
                NO
        );

        caseData.setLinkedCaseCT("Transferred to " + caseData.getOfficeCT().getValue().getCode());
        caseData.setPositionType(caseData.getPositionTypeCT());

        log.info("Clearing the CT payload");

        caseData.setOfficeCT(null);
        caseData.setPositionTypeCT(null);
        caseData.setStateAPI(null);

    }

}