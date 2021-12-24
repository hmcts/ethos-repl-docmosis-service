package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.exceptions.CaseCreationException;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ecm.common.model.ccd.items.BFActionTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.EccCounterClaimTypeItem;
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
@Service("caseTransferService")
public class CaseTransferService {

    static final String BF_ACTIONS_ERROR_MSG = "There are one or more open Brought Forward actions that must be "
            + "cleared before the case %s can be transferred";

    static final String HEARINGS_ERROR_MSG = "There are one or more hearings that have the status Listed. "
            + "These must be updated before the case %s can be transferred";

    private final PersistentQHelperService persistentQHelperService;
    private final CcdClient ccdClient;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    static class TransferEventParams {
        final String userToken;
        final String caseTypeId;
        final String officeCT;
        final String positionTypeCT;
        final String reasonForCT;
        final String jurisdiction;

        private TransferEventParams(String userToken, String caseTypeId, String officeCT, String positionTypeCT,
                                    String reasonForCT, String jurisdiction) {
            this.userToken = userToken;
            this.caseTypeId = caseTypeId;
            this.officeCT = officeCT;
            this.positionTypeCT = positionTypeCT;
            this.reasonForCT = reasonForCT;
            this.jurisdiction = jurisdiction;
        }
    }

    public List<String> createCaseTransfer(CaseDetails caseDetails, String userToken) {
        var errors = new ArrayList<String>();
        var caseDataList = getAllCasesToBeTransferred(caseDetails, userToken);

        caseDataList.forEach(caseData -> validateCase(caseData, errors));

        if (!errors.isEmpty()) {
            return errors;
        }

        return transferCases(caseDetails, caseDataList, userToken);
    }

    private CaseData getOriginalCase(CaseDetails caseDetails, String userToken) {
        try {
            var caseData = caseDetails.getCaseData();
            if (!Strings.isNullOrEmpty(caseData.getCounterClaim())) {
                List<SubmitEvent> submitEvents = ccdClient.retrieveCasesElasticSearch(userToken,
                        caseDetails.getCaseTypeId(), List.of(caseData.getCounterClaim()));
                return submitEvents.get(0).getCaseData();
            } else {
                return caseDetails.getCaseData();
            }

        } catch (Exception ex) {
            throw new CaseCreationException("Error getting original case number: "
                    + caseDetails.getCaseData().getEthosCaseReference() + " " + ex.getMessage());
        }
    }

    private List<CaseData> getAllCasesToBeTransferred(CaseDetails caseDetails, String userToken) {
        try {
            var originalCaseData = getOriginalCase(caseDetails, userToken);
            List<CaseData> cases = new ArrayList<>();
            cases.add(originalCaseData);

            if (CollectionUtils.isNotEmpty(originalCaseData.getEccCases())) {
                for (EccCounterClaimTypeItem counterClaimItem : originalCaseData.getEccCases()) {
                    var counterClaim = counterClaimItem.getValue().getCounterClaim();
                    var submitEvents = ccdClient.retrieveCasesElasticSearch(userToken, caseDetails.getCaseTypeId(),
                            new ArrayList<>(Collections.singleton(counterClaim)));
                    if (submitEvents != null && !submitEvents.isEmpty()) {
                        cases.add(submitEvents.get(0).getCaseData());
                    }
                }
            }

            return cases;
        } catch (Exception ex) {
            throw new CaseCreationException("Error getting all cases to be transferred for case number: "
                    + caseDetails.getCaseData().getEthosCaseReference() + " " + ex.getMessage());
        }
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
        if (caseData.getHearingCollection() == null) {
            return true;
        }
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

        return true;
    }

    private void validateCase(CaseData caseData, List<String> errors) {
        if (!checkBfActionsCleared(caseData)) {
            errors.add(String.format(BF_ACTIONS_ERROR_MSG, caseData.getEthosCaseReference()));
        }

        if (!checkHearingsNotListed(caseData)) {
            errors.add(String.format(HEARINGS_ERROR_MSG, caseData.getEthosCaseReference()));
        }
    }

    private List<String> transferCases(CaseDetails caseDetails, List<CaseData> caseDataList, String userToken) {
        var caseTypeId = caseDetails.getCaseTypeId();
        var officeCT = caseDetails.getCaseData().getOfficeCT().getValue().getCode();
        var positionTypeCT = caseDetails.getCaseData().getPositionTypeCT();
        var reasonForCT = caseDetails.getCaseData().getReasonForCT();
        var jurisdiction = caseDetails.getJurisdiction();
        var transferEventParams = new TransferEventParams(userToken, caseTypeId, officeCT, positionTypeCT, reasonForCT,
                jurisdiction);

        var errors = new ArrayList<String>();
        for (CaseData caseData : caseDataList) {
            createCaseTransferEvent(transferEventParams, caseData, errors);
        }

        return errors;
    }

    private void createCaseTransferEvent(TransferEventParams transferEventParams, CaseData caseData,
                                         List<String> errors) {
        persistentQHelperService.sendCreationEventToSingles(
                transferEventParams.userToken,
                transferEventParams.caseTypeId,
                transferEventParams.jurisdiction,
                errors,
                List.of(caseData.getEthosCaseReference()),
                transferEventParams.officeCT,
                transferEventParams.positionTypeCT,
                ccdGatewayBaseUrl,
                transferEventParams.reasonForCT,
                SINGLE_CASE_TYPE,
                NO,
                null
        );

        caseData.setLinkedCaseCT("Transferred to " + transferEventParams.officeCT);
        caseData.setPositionType(transferEventParams.positionTypeCT);
        log.info("Clearing the CT payload for case: " + caseData.getEthosCaseReference());
        caseData.setOfficeCT(null);
        caseData.setPositionTypeCT(null);
        caseData.setStateAPI(null);
    }
}

