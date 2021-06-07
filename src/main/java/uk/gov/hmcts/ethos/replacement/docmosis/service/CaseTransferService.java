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
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@RequiredArgsConstructor
@Service("caseTransferService")
public class CaseTransferService {

    private final PersistentQHelperService persistentQHelperService;
    private final CcdClient ccdClient;
    private static final String MESSAGE = "Failed to retrieve the case for case id : ";

    @Value("${ccd_gateway_base_url}")
    private String ccdGatewayBaseUrl;

    private CaseData getOriginalCase(CaseDetails caseDetails, String userToken) {
        try {
            CaseData caseData = caseDetails.getCaseData();
            if (!Strings.isNullOrEmpty(caseData.getCounterClaim())) {
             List<SubmitEvent> submitEvents =  ccdClient.retrieveCasesElasticSearch(userToken, caseDetails.getCaseTypeId(), Arrays.asList(caseData.getCounterClaim()));
            return submitEvents.get(0).getCaseData();
            }
            else {
                return caseDetails.getCaseData();
            }

        }
         catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseTypeId() + ex.getMessage());
        }
    }
    private List<CaseData> getAllCasesToBeTransferred(CaseDetails caseDetails, String userToken) {
        try {
            CaseData caseData = getOriginalCase(caseDetails, userToken);
            List<CaseData> cases = new ArrayList<>();
            cases.add(caseData);
             if (caseData.getEccCases() != null && !caseData.getEccCases().isEmpty()) {
                List<String> counterClaimList =  caseData.getEccCases().stream().map(eccCounterClaimTypeItem -> eccCounterClaimTypeItem.getValue().getCounterClaim()).collect(Collectors.toList());
                cases.addAll(ccdClient.retrieveCasesElasticSearch(userToken,caseDetails.getCaseTypeId(),counterClaimList).stream().map(submitEvent -> submitEvent.getCaseData()).collect(Collectors.toList()));
            }
            return cases;
        }
        catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + caseDetails.getCaseTypeId() + ex.getMessage());
        }
    }

    public void createCaseTransfer(CaseDetails caseDetails, List<String> errors, String userToken) {

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
            if (caseData.getEccCases() !=null && !caseData.getEccCases().isEmpty()) {
                log.info("ECCCases before: " + caseData.getEccCases().get(0).getValue().getCounterClaim());
            }
            persistentQHelperService.sendCreationEventToSingles(
                    userToken,
                    caseDetails.getCaseTypeId(),
                    caseDetails.getJurisdiction(),
                    errors,
                    new ArrayList<>(Collections.singletonList(caseData.getEthosCaseReference())),
                    caseDetails.getCaseData().getOfficeCT().getValue().getCode(),
                    caseDetails.getCaseData().getPositionTypeCT(),
                    ccdGatewayBaseUrl,
                    caseDetails.getCaseData().getReasonForCT(),
                    SINGLE_CASE_TYPE,
                    NO
            );
            if (caseData.getEccCases() !=null && !caseData.getEccCases().isEmpty()) {
                log.info("ECCCases after: " + caseData.getEccCases().get(0).getValue().getCounterClaim());
            }
            caseData.setLinkedCaseCT("Transferred to " + caseDetails.getCaseData().getOfficeCT().getValue().getCode());
            caseData.setPositionType(caseDetails.getCaseData().getPositionTypeCT());
        }

        log.info("Clearing the CT payload");
        caseDetails.getCaseData().setPositionTypeCT(null);
        caseDetails.getCaseData().setStateAPI(null);
        caseDetails.getCaseData().setOfficeCT(null);

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

}
