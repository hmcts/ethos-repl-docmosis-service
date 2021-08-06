package uk.gov.hmcts.ethos.replacement.docmosis.service;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Service("caseTransferService")
public class CaseTransferService {

    private final PersistentQHelperService persistentQHelperService;
    private final CcdClient ccdClient;
    private String caseTypeId;
    private String jurisdiction;
    private String officeCT;
    private String positionTypeCT;
    private String reasonForCT;

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    private CaseData getOriginalCase(CaseDetails caseDetails, String userToken) {
        try {
            var caseData = caseDetails.getCaseData();
            if (!Strings.isNullOrEmpty(caseData.getCounterClaim())) {
                List<SubmitEvent> submitEvents =  ccdClient.retrieveCasesElasticSearch(userToken,
                        caseDetails.getCaseTypeId(), Arrays.asList(caseData.getCounterClaim()));
                return submitEvents.get(0).getCaseData();
            }
            else {
                return caseDetails.getCaseData();
            }

        }
        catch (Exception ex) {
            throw new CaseCreationException("Error getting original case number: " + caseDetails.getCaseData().getEthosCaseReference() + " " + ex.getMessage());
        }
    }

    private List<CaseData> getAllCasesToBeTransferred(CaseDetails caseDetails, String userToken) {
        try {
            var originalCaseData = getOriginalCase(caseDetails, userToken);
            List<CaseData> cases = new ArrayList<>();
            String counterClaim;
            cases.add(originalCaseData);
            if (originalCaseData.getEccCases() != null && !originalCaseData.getEccCases().isEmpty()) {

                for (EccCounterClaimTypeItem counterClaimItem:originalCaseData.getEccCases()) {
                    counterClaim =  counterClaimItem.getValue().getCounterClaim();
                    List<SubmitEvent>   submitEvents = ccdClient.retrieveCasesElasticSearch(userToken,caseDetails.getCaseTypeId(),new ArrayList<>(Collections.singleton(counterClaim)));
                    if (submitEvents != null && !submitEvents.isEmpty()) {
                        cases.add(submitEvents.get(0).getCaseData());
                    }
                }
            }

            return cases;
        }
        catch (Exception ex) {
            throw new CaseCreationException("Error getting all cases to be transferred for case number: " + caseDetails.getCaseData().getEthosCaseReference() + " " + ex.getMessage());
        }
    }

    public void createCaseTransferEvent(CaseData caseData, List<String> errors, String userToken) {

        persistentQHelperService.sendCreationEventToSingles(
                userToken,
                caseTypeId,
                jurisdiction,
                errors,
                new ArrayList<>(Collections.singletonList(caseData.getEthosCaseReference())),
                officeCT,
                positionTypeCT,
                ccdGatewayBaseUrl,
                reasonForCT,
                SINGLE_CASE_TYPE,
                NO,
                null
        );
        caseData.setLinkedCaseCT("Transferred to " + officeCT);
        caseData.setPositionType(positionTypeCT);
        log.info("Clearing the CT payload for case: " + caseData.getEthosCaseReference());
        caseData.setOfficeCT(null);
        caseData.setPositionTypeCT(null);
        caseData.setStateAPI(null);
    }

    public void createCaseTransfer(CaseDetails caseDetails, List<String> errors, String userToken) {

        caseTypeId = caseDetails.getCaseTypeId();
        officeCT = caseDetails.getCaseData().getOfficeCT().getValue().getCode();
        positionTypeCT = caseDetails.getCaseData().getPositionTypeCT();
        reasonForCT = caseDetails.getCaseData().getReasonForCT();
        jurisdiction = caseDetails.getJurisdiction();
        List<CaseData> caseDataList = getAllCasesToBeTransferred(caseDetails, userToken);
        for (CaseData caseData : caseDataList) {

            if (!checkBfActionsCleared(caseData)) {
                errors.add(
                        "There are one or more open Brought Forward actions that must be cleared before the case "
                                + caseData.getEthosCaseReference() + " can "
                                + "be transferred");
            }

            if (!checkHearingsNotListed(caseData)) {
                errors.add(
                        "There are one or more hearings that have the status Listed. These must be updated before the case "
                                + caseData.getEthosCaseReference() + " can be transferred");
            }
        }

        if (!errors.isEmpty()) {
            return;
        }

        for (CaseData caseData : caseDataList) {
            createCaseTransferEvent(caseData, errors, userToken);
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

}

