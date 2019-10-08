package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
@Service("bulkSearchService")
public class BulkSearchService {

    private static final String MESSAGE = "Failed to search cases for case id : ";
    private final CcdClient ccdClient;

    @Autowired
    public BulkSearchService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    public BulkRequestPayload bulkSearchLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getMultipleCollection() != null) {
            List<SearchTypeItem> searchTypeItemList = searchCasesByFieldsRequest(bulkDetails);
            bulkDetails.getCaseData().setSearchCollectionCount(String.valueOf(searchTypeItemList.size()));
            bulkDetails.getCaseData().setSearchCollection(searchTypeItemList);
            bulkDetails.getCaseData().getSearchCollection().forEach(searchTypeItem -> log.info("Searched collection: " + searchTypeItem.toString()));
            bulkDetails.setCaseData(clearUpFields(bulkDetails.getCaseData()));
        } else {
            errors.add("There are not cases in this multiple to search");
            bulkRequestPayload.setErrors(errors);
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData clearUpFields(BulkData bulkData) {
        bulkData.setEthosCaseReference(null);
        bulkData.setClaimantSurname(null);
        bulkData.setRespondentSurname(null);
        bulkData.setClaimantRep(null);
        bulkData.setRespondentRep(null);
        return bulkData;
    }

    public BulkCasesPayload bulkCasesRetrievalRequest(BulkDetails bulkDetails, String authToken) {
        try {
            List<String> caseIds = BulkHelper.getCaseIds(bulkDetails);
            if (caseIds != null && !caseIds.isEmpty()) {
                return filterSubmitEvents(ccdClient.retrieveCases(authToken, BulkHelper.getCaseTypeId(bulkDetails.getCaseTypeId()),
                        bulkDetails.getJurisdiction()), caseIds, bulkDetails.getCaseData().getMultipleReference());
            } else {
                BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
                bulkCasesPayload.setAlreadyTakenIds(new ArrayList<>());
                bulkCasesPayload.setSubmitEvents(new ArrayList<>());
                bulkCasesPayload.setMultipleTypeItems(new ArrayList<>());
                return bulkCasesPayload;
            }
        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
    }

    BulkCasesPayload filterSubmitEvents(List<SubmitEvent> submitEvents, List<String> caseIds, String multipleReference) {
        log.info("Cases found: " + submitEvents.size());
        BulkCasesPayload bulkCasesPayload = new BulkCasesPayload();
        multipleReference = multipleReference != null ? multipleReference : "";
        List<String> alreadyTakenIds = new ArrayList<>();
        List<SubmitEvent> submitEventFiltered = new ArrayList<>();
        for (SubmitEvent submitEvent : submitEvents) {
            CaseData caseData = submitEvent.getCaseData();
            if (caseIds.contains(caseData.getEthosCaseReference())) {
                if (caseData.getMultipleReference() != null && !caseData.getMultipleReference().trim().isEmpty()
                        && !caseData.getMultipleReference().equals(multipleReference)) {
                    alreadyTakenIds.add(caseData.getEthosCaseReference());
                }
                submitEventFiltered.add(submitEvent);
            }
        }
        bulkCasesPayload.setSubmitEvents(submitEventFiltered);
        bulkCasesPayload.setAlreadyTakenIds(alreadyTakenIds);
        return bulkCasesPayload;
    }

    List<SearchTypeItem> searchCasesByFieldsRequest(BulkDetails bulkDetails) {
        //log.info("Bulk Details: " + bulkDetails);
        try {
            BulkData bulkData = bulkDetails.getCaseData();
            String claimantFilter = bulkData.getClaimantSurname();
            String ethosCaseRefFilter = bulkData.getEthosCaseReference();
            String respondentFilter = bulkData.getRespondentSurname();
            String claimantRepFilter = bulkData.getClaimantRep();
            String respondentRepFilter = bulkData.getRespondentRep();
            //List<JurCodesTypeItem> jurCodesTypeItemsFilter = bulkData.getJurCodesCollection();
            List<SearchTypeItem> searchTypeItemList = new ArrayList<>();
            List<MultipleTypeItem> multipleTypeItemList = bulkDetails.getCaseData().getMultipleCollection();
            if (multipleTypeItemList != null && !multipleTypeItemList.isEmpty()) {
                Predicate<MultipleTypeItem> claimantPredicate = d-> d.getValue() != null && claimantFilter.equals(d.getValue().getClaimantSurnameM());
                Predicate<MultipleTypeItem> ethosCaseRefPredicate = d-> d.getValue() != null && ethosCaseRefFilter.equals(d.getValue().getEthosCaseReferenceM());
                Predicate<MultipleTypeItem> respondentPredicate = d-> d.getValue() != null && respondentFilter.equals(d.getValue().getRespondentSurnameM());
                Predicate<MultipleTypeItem> claimantRepPredicate = d-> d.getValue() != null && claimantRepFilter.equals(d.getValue().getClaimantRepM());
                Predicate<MultipleTypeItem> respondentRepPredicate = d-> d.getValue() != null && respondentRepFilter.equals(d.getValue().getRespondentRepM());
                //Predicate<MultipleTypeItem> jurCodesTypeItemsPredicate = d-> d.getValue() != null && BulkHelper.containsAllJurCodes(jurCodesTypeItemsFilter, d.getValue().getJurCodesCollectionM());
                List<MultipleTypeItem> searchedList = new ArrayList<>();
                boolean filtered = false;
                if (!isNullOrEmpty(claimantFilter)) {
                    searchedList = filterByField(multipleTypeItemList, claimantPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(ethosCaseRefFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemList, ethosCaseRefPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(respondentFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemList, respondentPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(claimantRepFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemList, claimantRepPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(respondentRepFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemList, respondentRepPredicate);
                    //filtered = true;
                }
//                if (!isNullOrEmpty(jurCodesTypeItemsFilter)) {
//                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemList, jurCodesTypeItemsPredicate);
//                }
                for (MultipleTypeItem multipleTypeItem : searchedList) {
                    SearchTypeItem searchTypeItem = new SearchTypeItem();
                    searchTypeItem.setId(multipleTypeItem.getId());
                    searchTypeItem.setValue(BulkHelper.getSearchTypeFromMultipleType(multipleTypeItem.getValue()));
                    searchTypeItemList.add(searchTypeItem);
                }
            }
            return searchTypeItemList;

        } catch (Exception ex) {
            throw new CaseCreationException(MESSAGE + bulkDetails.getCaseId() + ex.getMessage());
        }
    }

    private List<MultipleTypeItem> filterByField(List<MultipleTypeItem> listToSearchBy, Predicate<MultipleTypeItem> predicate) {
        return listToSearchBy.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

}
