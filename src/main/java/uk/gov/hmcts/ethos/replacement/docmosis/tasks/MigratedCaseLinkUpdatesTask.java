package uk.gov.hmcts.ethos.replacement.docmosis.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ecm.common.client.CcdClient;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.CaseDetails;
import uk.gov.hmcts.ecm.common.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.service.AdminUserService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.CaseRetrievalForCaseWorkerService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.FeatureToggleService;
import uk.gov.hmcts.ethos.replacement.docmosis.service.exceptions.CaseDuplicateSearchException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.ACCEPTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLOSED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.REJECTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SUBMITTED_STATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TRANSFERRED_STATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class MigratedCaseLinkUpdatesTask {
    private final AdminUserService adminUserService;
    private final CaseRetrievalForCaseWorkerService caseRetrievalForCaseWorkerService;
    private final CcdClient ccdClient;
    private final FeatureToggleService featureToggleService;
    private final List<String> validStates = List.of(TRANSFERRED_STATE, ACCEPTED_STATE, REJECTED_STATE,
            SUBMITTED_STATE, CLOSED_STATE);
    private static final String EVENT_ID = "migrateCaseLinkDetails";

    @Value("${cron.caseLinkCaseTypeId}")
    private String caseLinkCaseTypeIdString;

    @Value("${cron.maxCasesPerSearch}")
    private int maxCases;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private final List<String> caseTypeIdsToCheck = List.of("ET_EnglandWales", "ET_Scotland", "Bristol",
            "Leeds", "LondonCentral", "LondonEast", "LondonSouth",
            "Manchester", "MidlandsEast", "MidlandsWest", "Newcastle",
            "Scotland", "Wales", "Watford");

    @Scheduled(cron = "${cron.updateTransferredCaseLinks}")
    public void updateTransferredCaseLinks() {

        if (!featureToggleService.isUpdateTransferredCaseLinksEnabled()) {
            return;
        }

        String query = buildStartQuery();
        String adminUserToken = adminUserService.getAdminUserToken();
        String[] caseLinkCaseTypeIds = caseLinkCaseTypeIdString.split(",");

        List.of(caseLinkCaseTypeIds).forEach(caseTypeId -> {
            try {
                log.info("Case type: {} - started\n", caseTypeId);
                //Get transferred cases by case type
                log.info("Start Query: {} - \n", query);
                List<SubmitEvent> transferredCases =
                        ccdClient.buildAndGetElasticSearchRequest(adminUserToken, caseTypeId, query);
                if (!transferredCases.isEmpty()) {
                    log.info("{} - Migrated case link updates task - Retrieved {} cases", caseTypeId,
                            transferredCases.size());
                }

                for (SubmitEvent submitEvent : transferredCases) {
                    if (!featureToggleService.isUpdateTransferredCaseLinksEnabled()) {
                        return;
                    }

                    //find possible duplicate cases by ethos reference
                    //list of pairs of 'case type id' and 'list of submit events'
                    log.info("Searching for duplicates {} case types with ethos ref: {}", caseTypeId,
                            submitEvent.getCaseData().getEthosCaseReference());
                    List<Pair<String, List<SubmitEvent>>> listOfPairs =
                            findCaseByEthosReference(adminUserToken,
                                    submitEvent.getCaseData().getEthosCaseReference());
                    log.info("The count of result of the search for duplicates {} case types with ethos ref {} is: {}",
                            caseTypeId, submitEvent.getCaseData().getEthosCaseReference(), listOfPairs.size());

                    //identify the target case and source case
                    SubmitEvent targetSubmitEvent = getTargetSubmitEventFromPair(
                            listOfPairs, caseTypeId);
                    SubmitEvent sourceSubmitEvent = getSourceSubmitEventFromPair(
                            listOfPairs, caseTypeId);
                    String sourceCaseTypeId = getSourceCaseTypeId(listOfPairs, caseTypeId);
                    //check if duplicates have same checked field values
                    if (targetSubmitEvent != null && sourceSubmitEvent != null && sourceCaseTypeId != null
                            && haveSameCheckedFieldValues(targetSubmitEvent, sourceSubmitEvent)) {
                        //update valid matching duplicates by triggering event for case
                        triggerEventForCase(adminUserToken, targetSubmitEvent, sourceSubmitEvent, caseTypeId,
                                sourceCaseTypeId);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    public SubmitEvent getTargetSubmitEventFromPair(
            List<Pair<String, List<SubmitEvent>>> listOfPairs, String caseTypeId) {
        if (listOfPairs.size() != TWO) {
            return null;
        }

        Pair<String, List<SubmitEvent>> targetPairList = listOfPairs.stream()
                .filter(pair -> pair.getLeft().equals(caseTypeId))
                .findFirst().orElse(null);

        if (targetPairList != null && targetPairList.getRight().size() == ONE) {
            return targetPairList.getRight().get(0);
        }

        return null;
    }

    public SubmitEvent getSourceSubmitEventFromPair(
            List<Pair<String, List<SubmitEvent>>> listOfPairs, String caseTypeId) {
        if (listOfPairs.size() != TWO) {
            return null;
        }

        Pair<String, List<SubmitEvent>> sourcePairList = listOfPairs.stream()
                .filter(pair -> !pair.getLeft().equals(caseTypeId))
                .findFirst().orElse(null);
        if (sourcePairList != null && sourcePairList.getRight().size() == ONE) {
            return sourcePairList.getRight().get(0);
        }
        return null;
    }

    private String getSourceCaseTypeId(
            List<Pair<String, List<SubmitEvent>>> listOfPairs, String caseTypeId) {
        Pair<String, List<SubmitEvent>> sourcePairList = listOfPairs.stream()
                .filter(pair -> !pair.getLeft().equals(caseTypeId))
                .findFirst().orElse(null);
        if (sourcePairList == null) {
            return null;
        }
        return sourcePairList.getLeft();
    }

    // Checked field values : ethos ref, claimant, submission ref(i.e. FeeGroupReference),
    // and date of receipt
    public boolean haveSameCheckedFieldValues(SubmitEvent targetSubmitEvent,
                                              SubmitEvent sourceSubmitEvent) {

        if (targetSubmitEvent.getCaseData() == null || sourceSubmitEvent.getCaseData() == null) {
            return false;
        }

        CaseData targetCaseData = targetSubmitEvent.getCaseData();
        CaseData sourceCaseData = sourceSubmitEvent.getCaseData();
        boolean checkResult = sourceCaseData.getEthosCaseReference().equals(targetCaseData.getEthosCaseReference())
                && sourceCaseData.getClaimant().equals(targetCaseData.getClaimant())
                && sourceCaseData.getFeeGroupReference().equals(targetCaseData.getFeeGroupReference())
                && sourceCaseData.getReceiptDate().equals(targetCaseData.getReceiptDate());
        log.info("The haveSameCheckedFieldValues method result is {} ", checkResult);
        return checkResult;
    }

    public  List<Pair<String, List<SubmitEvent>>> findCaseByEthosReference(
            String adminUserToken, String ethosReference) {
        List<Pair<String, List<SubmitEvent>>> pairsList = new ArrayList<>();
        //search for duplicates in all case types and group the result by case type id
        caseTypeIdsToCheck.forEach(sourceCaseTypeId -> {
            try {
                String followUpQuery = buildFollowUpQuery(ethosReference);
                //for each transferred case, get duplicates by ethos ref
                List<SubmitEvent> duplicateCases =
                        ccdClient.buildAndGetElasticSearchRequest(adminUserToken, sourceCaseTypeId, followUpQuery);
                if (duplicateCases.size() == ONE) {
                    pairsList.add(Pair.of(sourceCaseTypeId, duplicateCases));
                }
            } catch (IOException exception) {
                String errorMessage = String.format(
                        "Error searching for duplicates by Ethos reference for case type: %s", sourceCaseTypeId);
                log.info(errorMessage, exception);
                throw new CaseDuplicateSearchException(exception.getMessage(), exception);
            }
        });

        return pairsList;
    }

    public void triggerEventForCase(String adminUserToken, SubmitEvent targetSubmitEvent,
                                    SubmitEvent sourceSubmitEvent, String targetCaseTypeId, String sourceCaseTypeId) {
        try {
            if (sourceSubmitEvent == null || sourceSubmitEvent.getCaseData() == null) {
                log.info("In triggerEventForCase method: Source case data null");
                return;
            }

            log.info("In triggerEventForCase method: Updating case {} with source case {}",
                    targetSubmitEvent.getCaseId(), sourceSubmitEvent.getCaseData().getCcdID());

            CCDRequest returnedRequest = ccdClient.startEventForCase(adminUserToken, targetCaseTypeId,
                    "EMPLOYMENT", String.valueOf(targetSubmitEvent.getCaseId()), EVENT_ID);
            CaseDetails targetCaseDetails = returnedRequest.getCaseDetails();

            // update target's two fields that will be used for link construction by triggering
            // the migrateCaseLinkDetails event for case
            targetCaseDetails.getCaseData().setTransferredCaseLinkSourceCaseId(
                    String.valueOf(sourceSubmitEvent.getCaseId()));
            targetCaseDetails.getCaseData().setTransferredCaseLinkSourceCaseTypeId(sourceCaseTypeId);

            ccdClient.submitEventForCase(adminUserToken, targetCaseDetails.getCaseData(),
                    targetCaseDetails.getCaseTypeId(), targetCaseDetails.getJurisdiction(),
                    returnedRequest, targetCaseDetails.getCaseId());
        } catch (Exception e) {
            log.info("An exception occurred while trying to update target two fields. \nMessage: {}", e.getMessage());
        }
    }

    private String buildStartQuery() {
        return new SearchSourceBuilder()
                .size(maxCases)
                .query(new BoolQueryBuilder()
                        .must(new TermsQueryBuilder("state.keyword", validStates.get(0)))
                        .mustNot(new ExistsQueryBuilder("data.transferredCaseLink"))
                ).toString();
    }

    private String buildFollowUpQuery(String ethosCaseReference) {
        return new SearchSourceBuilder()
                .size(maxCases)
                .query(new BoolQueryBuilder()
                        .must(new TermQueryBuilder("data.ethosCaseReference.keyword", ethosCaseReference))
                ).toString();
    }
}
