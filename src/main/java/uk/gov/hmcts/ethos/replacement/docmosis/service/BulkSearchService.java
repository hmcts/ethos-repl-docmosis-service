package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient;
import uk.gov.hmcts.ethos.replacement.docmosis.exceptions.CaseCreationException;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MidSearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.SearchTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkCasesPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.DEFAULT_SELECT_ALL_VALUE;

@Slf4j
@Service("bulkSearchService")
public class BulkSearchService {

    private static final String MESSAGE = "Failed to search cases for case id : ";
    private final CcdClient ccdClient;

    @Autowired
    public BulkSearchService(CcdClient ccdClient) {
        this.ccdClient = ccdClient;
    }

    private List<MultipleTypeItem> getMultipleCollectionForFilter(BulkDetails bulkDetails) {
        if (bulkDetails.getCaseData().getSelectAll() != null && bulkDetails.getCaseData().getSelectAll().equals("Yes")) {
            return bulkDetails.getCaseData().getMultipleCollection();
        } else if (bulkDetails.getCaseData().getSubMultipleDynamicList() != null) {
            String ref = bulkDetails.getCaseData().getSubMultipleDynamicList().getValue().getCode();
            if (!ref.equals(DEFAULT_SELECT_ALL_VALUE)) {
                return bulkDetails.getCaseData().getMultipleCollection().stream()
                        .filter(multipleTypeItem -> multipleTypeItem.getValue().getSubMultipleM().equals(ref))
                        .collect(Collectors.toList());
            }
        }
        return bulkDetails.getCaseData().getMultipleCollection();
    }

    private List<MidSearchTypeItem> getAllMultipleCases(List<MultipleTypeItem> multipleTypeItemToSearchBy) {
        List<MidSearchTypeItem> midSearchedList = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : multipleTypeItemToSearchBy) {
            log.info("Case found searching: " + multipleTypeItem.getValue().getEthosCaseReferenceM());
            MidSearchTypeItem midSearchTypeItem = new MidSearchTypeItem();
            midSearchTypeItem.setId(multipleTypeItem.getValue().getEthosCaseReferenceM());
            midSearchTypeItem.setValue(multipleTypeItem.getValue().getEthosCaseReferenceM());
            midSearchedList.add(midSearchTypeItem);
        }
        return midSearchedList;
    }

    public BulkRequestPayload bulkMidSearchLogic(BulkDetails bulkDetails, boolean subMultiple) {
        List<MultipleTypeItem> multipleTypeItemToSearchBy = getMultipleCollectionForFilter(bulkDetails);
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (multipleTypeItemToSearchBy != null) {
            List<MidSearchTypeItem> midSearchedList;
            if (bulkDetails.getCaseData().getSelectAll() != null && bulkDetails.getCaseData().getSelectAll().equals("Yes")) {
                midSearchedList = getAllMultipleCases(multipleTypeItemToSearchBy);
            } else {
                midSearchedList = midSearchCasesByFieldsRequest(multipleTypeItemToSearchBy, bulkDetails, subMultiple);
            }
            bulkDetails.getCaseData().setMidSearchCollection(midSearchedList);
        } else {
            errors.add("No cases have been found in this multiple");
            bulkRequestPayload.setErrors(errors);
        }
        bulkDetails.getCaseData().setSubMultipleDynamicList(null);
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    public BulkRequestPayload bulkSearchLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getMidSearchCollection() != null) {
            List<SearchTypeItem> searchTypeItemList = new ArrayList<>();
            for (MidSearchTypeItem refNumbersFiltered : bulkDetails.getCaseData().getMidSearchCollection()) {
                List<MultipleTypeItem> multipleTypeItemToSearchBy = getMultipleCollectionForFilter(bulkDetails);
                Optional<MultipleTypeItem> multipleTypeItem = multipleTypeItemToSearchBy
                        .stream()
                        .filter(multipleValue -> multipleValue.getValue().getEthosCaseReferenceM().equals(refNumbersFiltered.getValue()))
                        .findFirst();
                if (multipleTypeItem.isPresent()) {
                    SearchTypeItem searchTypeItem = new SearchTypeItem();
                    searchTypeItem.setId(multipleTypeItem.get().getId());
                    searchTypeItem.setValue(BulkHelper.getSearchTypeFromMultipleType(multipleTypeItem.get().getValue()));
                    searchTypeItemList.add(searchTypeItem);
                }
            }
            bulkDetails.getCaseData().setSearchCollectionCount(String.valueOf(searchTypeItemList.size()));
            bulkDetails.getCaseData().setSearchCollection(searchTypeItemList);
            bulkDetails.setCaseData(clearUpFields(bulkDetails.getCaseData()));
        } else {
            errors.add("No cases have been found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkDetails.getCaseData().setSubMultipleDynamicList(null);
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData clearUpFields(BulkData bulkData) {
        bulkData.setEthosCaseReference(null);
        bulkData.setClaimantSurname(null);
        bulkData.setRespondentSurname(null);
        bulkData.setClaimantRep(null);
        bulkData.setRespondentRep(null);
        bulkData.setMidSearchCollection(null);
        bulkData.setSelectAll("No");
        bulkData.setClaimantOrg(null);
        bulkData.setRespondentOrg(null);
        bulkData.setPositionType(null);
        if (bulkData.getSearchCollection() == null || bulkData.getSearchCollection().isEmpty()) {
            bulkData.setJurCodesCollection(null);
        }
        bulkData.setFlag1(null);
        bulkData.setFlag2(null);
        bulkData.setEQP(null);
        bulkData.setSubmissionRef(null);
        bulkData.setState(null);
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

    List<MidSearchTypeItem> midSearchCasesByFieldsRequest(List<MultipleTypeItem> multipleTypeItemToSearchBy, BulkDetails bulkDetails, boolean subMultiple) {
        try {
            BulkData bulkData = bulkDetails.getCaseData();
            String claimantFilter = bulkData.getClaimantSurname();
            String respondentFilter = bulkData.getRespondentSurname();
            String claimantRepFilter = bulkData.getClaimantRep();
            String respondentRepFilter = bulkData.getRespondentRep();
            String positionTypeFilter = bulkData.getPositionType();
            String flag1Filter = bulkData.getFlag1();
            String flag2Filter = bulkData.getFlag2();
            String EQPFilter = bulkData.getEQP();
            String claimantRepOrgFilter = bulkData.getClaimantOrg();
            String respondentRepOrgFilter = bulkData.getRespondentOrg();
            String submissionRefFilter = bulkData.getSubmissionRef();
            String stateFilter = bulkData.getState();
            List<JurCodesTypeItem> jurCodesTypeItemsFilter = bulkData.getJurCodesCollection();
            List<MidSearchTypeItem> midSearchedList = new ArrayList<>();
            if (multipleTypeItemToSearchBy != null && !multipleTypeItemToSearchBy.isEmpty()) {
                Predicate<MultipleTypeItem> subMultipleDuplicatePredicate = d-> d.getValue() != null && (d.getValue().getSubMultipleM() == null
                        || d.getValue().getSubMultipleM().equals(" "));
                Predicate<MultipleTypeItem> claimantPredicate = d-> d.getValue() != null && claimantFilter.equals(d.getValue().getClaimantSurnameM());
                Predicate<MultipleTypeItem> respondentPredicate = d-> d.getValue() != null && respondentFilter.equals(d.getValue().getRespondentSurnameM());
                Predicate<MultipleTypeItem> claimantRepPredicate = d-> d.getValue() != null && claimantRepFilter.equals(d.getValue().getClaimantRepM());
                Predicate<MultipleTypeItem> respondentRepPredicate = d-> d.getValue() != null && respondentRepFilter.equals(d.getValue().getRespondentRepM());
                Predicate<MultipleTypeItem> positionTypePredicate = d-> d.getValue() != null && positionTypeFilter.equals(d.getValue().getPositionTypeM());
                Predicate<MultipleTypeItem> flag1Predicate = d-> d.getValue() != null && flag1Filter.equals(d.getValue().getFlag1M());
                Predicate<MultipleTypeItem> flag2Predicate = d-> d.getValue() != null && flag2Filter.equals(d.getValue().getFlag2M());
                Predicate<MultipleTypeItem> EQPPredicate = d-> d.getValue() != null && EQPFilter.equals(d.getValue().getEQPM());
                Predicate<MultipleTypeItem> submissionRefPredicate = d-> d.getValue() != null && submissionRefFilter.equals(d.getValue().getFeeGroupReferenceM());
                Predicate<MultipleTypeItem> claimantRepOrgPredicate = d-> d.getValue() != null && claimantRepOrgFilter.equals(d.getValue().getClaimantRepOrgM());
                Predicate<MultipleTypeItem> respondentRepOrgPredicate = d-> d.getValue() != null && respondentRepOrgFilter.equals(d.getValue().getRespondentRepOrgM());
                Predicate<MultipleTypeItem> statePredicate = d-> d.getValue() != null && stateFilter.equals(d.getValue().getStateM());
                Predicate<MultipleTypeItem> jurCodesTypeItemsPredicate = d-> d.getValue() != null &&
                        BulkHelper.containsAllJurCodes(jurCodesTypeItemsFilter, BulkHelper.getJurCodesListFromString(d.getValue().getJurCodesCollectionM()));
                List<MultipleTypeItem> searchedList = new ArrayList<>();
                boolean filtered = false;
                if (subMultiple) {
                    searchedList = filterByField(multipleTypeItemToSearchBy, subMultipleDuplicatePredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(claimantFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, claimantPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(respondentFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, respondentPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(claimantRepFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, claimantRepPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(respondentRepFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, respondentRepPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(positionTypeFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, positionTypePredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(flag1Filter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, flag1Predicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(flag2Filter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, flag2Predicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(EQPFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, EQPPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(submissionRefFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, submissionRefPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(claimantRepOrgFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, claimantRepOrgPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(respondentRepOrgFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, respondentRepOrgPredicate);
                    filtered = true;
                }
                if (!isNullOrEmpty(stateFilter)) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, statePredicate);
                    filtered = true;
                }
                if (jurCodesTypeItemsFilter != null && !jurCodesTypeItemsFilter.isEmpty()) {
                    searchedList = filterByField(filtered ? searchedList : multipleTypeItemToSearchBy, jurCodesTypeItemsPredicate);
                }
                midSearchedList.addAll(getAllMultipleCases(searchedList));
            }
            return midSearchedList;

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
