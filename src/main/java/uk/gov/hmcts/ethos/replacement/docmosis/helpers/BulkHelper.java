package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.CaseIdTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.MultipleType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.SearchType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.SubmitEvent;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.JurCodesTypeItem;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import static uk.gov.hmcts.ethos.replacement.docmosis.model.helper.Constants.*;
import static com.google.common.base.Strings.isNullOrEmpty;

public class BulkHelper {

    public static BulkDetails setMultipleCollection(BulkDetails bulkDetails, List<MultipleTypeItem> multipleTypeItemList) {
        if (multipleTypeItemList != null && !multipleTypeItemList.isEmpty()) {
            bulkDetails.getCaseData().setMultipleCollectionCount(String.valueOf(multipleTypeItemList.size()));
            bulkDetails.getCaseData().setMultipleCollection(multipleTypeItemList);
        } else {
            bulkDetails.getCaseData().setMultipleCollection(new ArrayList<>());
            bulkDetails.getCaseData().setMultipleCollectionCount(null);
        }
        bulkDetails.getCaseData().setCaseIdCollection(BulkHelper.getCaseIdTypeItems(bulkDetails));
        return bulkDetails;
    }

    public static BulkDetails clearSearchCollection(BulkDetails bulkDetails) {
        bulkDetails.getCaseData().setSearchCollection(new ArrayList<>());
        bulkDetails.getCaseData().setSearchCollectionCount(null);
        return bulkDetails;
    }

    public static List<MultipleTypeItem> getMultipleTypeListBySubmitEventList(List<SubmitEvent> submitEvents, String multipleReference) {
        List<MultipleTypeItem> multipleTypeItemList = new ArrayList<>();
        for (SubmitEvent submitEvent : submitEvents) {
            CaseData caseData = submitEvent.getCaseData();
            MultipleType multipleType = new MultipleType();
            multipleType.setCaseIDM(String.valueOf(submitEvent.getCaseId()));
            multipleType.setEthosCaseReferenceM(Optional.ofNullable(caseData.getEthosCaseReference()).orElse(""));
            multipleType.setMultipleReferenceM(Optional.ofNullable(multipleReference).orElse(" "));
            multipleType.setClerkRespM(Optional.ofNullable(caseData.getClerkResponsible()).orElse(" "));
            if (caseData.getClaimantIndType() != null && caseData.getClaimantIndType().getClaimantLastName() != null) {
                multipleType.setClaimantSurnameM(caseData.getClaimantIndType().getClaimantLastName());
            } else {
                multipleType.setClaimantSurnameM(" ");
            }
            if (caseData.getRespondentSumType() != null && caseData.getRespondentSumType().getRespondentName() != null) {
                multipleType.setRespondentSurnameM(caseData.getRespondentSumType().getRespondentName());
            } else {
                multipleType.setRespondentSurnameM(" ");
            }
            if (caseData.getRepresentativeClaimantType() != null && caseData.getRepresentativeClaimantType().getNameOfRepresentative() != null) {
                multipleType.setClaimantRepM(caseData.getRepresentativeClaimantType().getNameOfRepresentative());
            } else {
                multipleType.setClaimantRepM(" ");
            }
            if (caseData.getRepCollection() != null && !caseData.getRepCollection().isEmpty() && caseData.getRepCollection().get(0).getValue() != null) {
                multipleType.setRespondentRepM(caseData.getRepCollection().get(0).getValue().getNameOfRepresentative());
            } else {
                multipleType.setRespondentRepM(" ");
            }
            multipleType.setFileLocM(Optional.ofNullable(caseData.getFileLocation()).orElse(" "));
            multipleType.setReceiptDateM(Optional.ofNullable(caseData.getReceiptDate()).orElse(" "));
            multipleType.setAcasOfficeM(Optional.ofNullable(caseData.getAcasOffice()).orElse(" "));
            multipleType.setPositionTypeM(Optional.ofNullable(caseData.getPositionType()).orElse(" "));
            multipleType.setFeeGroupReferenceM(Optional.ofNullable(caseData.getFeeGroupReference()).orElse(" "));
            multipleType.setJurCodesCollectionM(getJurCodesCollection(caseData.getJurCodesCollection()));
            multipleType.setStateM(Optional.ofNullable(submitEvent.getState()).orElse(" "));
            MultipleTypeItem multipleTypeItem = new MultipleTypeItem();
            multipleTypeItem.setId(String.valueOf(submitEvent.getCaseId()));
            multipleTypeItem.setValue(multipleType);
            multipleTypeItemList.add(multipleTypeItem);
        }
        return multipleTypeItemList;
    }

    public static SearchType getSearchTypeFromMultipleType(MultipleType multipleType) {
        SearchType searchType = new SearchType();
        searchType.setCaseIDS(multipleType.getCaseIDM());
        searchType.setEthosCaseReferenceS(multipleType.getEthosCaseReferenceM());
        searchType.setLeadClaimantS(multipleType.getLeadClaimantM());
        searchType.setClerkRespS(multipleType.getClerkRespM());
        searchType.setClaimantSurnameS(multipleType.getClaimantSurnameM());
        searchType.setRespondentSurnameS(multipleType.getRespondentSurnameM());
        searchType.setClaimantRepS(multipleType.getClaimantRepM());
        searchType.setRespondentRepS(multipleType.getRespondentRepM());
        searchType.setFileLocS(multipleType.getFileLocM());
        searchType.setReceiptDateS(multipleType.getReceiptDateM());
        searchType.setAcasOfficeS(multipleType.getAcasOfficeM());
        searchType.setPositionTypeS(multipleType.getPositionTypeM());
        searchType.setFeeGroupReferenceS(multipleType.getFeeGroupReferenceM());
        searchType.setJurCodesCollectionS(multipleType.getJurCodesCollectionM());
        searchType.setStateS(multipleType.getStateM());
        return searchType;
    }

    public static String getCaseTypeId(String caseTypeId) {
        if (caseTypeId.equals(MANCHESTER_BULK_CASE_TYPE_ID)) {
            return MANCHESTER_CASE_TYPE_ID;
        }
        return GLASGOW_CASE_TYPE_ID;
    }

    public static MultipleType getMultipleTypeFromSubmitEvent(SubmitEvent submitEvent) {
        CaseData caseData = submitEvent.getCaseData();
        MultipleType multipleType = new MultipleType();
        multipleType.setCaseIDM(String.valueOf(submitEvent.getCaseId()));
        multipleType.setClerkRespM(!isNullOrEmpty(caseData.getClerkResponsible()) ? caseData.getClerkResponsible() : " ");
        multipleType.setClaimantSurnameM(caseData.getClaimantIndType()!=null && caseData.getClaimantIndType().getClaimantLastName()!=null ?
                caseData.getClaimantIndType().getClaimantLastName() : " ");
        multipleType.setRespondentSurnameM(caseData.getRespondentSumType()!=null && caseData.getRespondentSumType().getRespondentName()!=null ?
                caseData.getRespondentSumType().getRespondentName() : " ");
        multipleType.setClaimantRepM(caseData.getRepresentativeClaimantType()!=null && caseData.getRepresentativeClaimantType().getNameOfRepresentative()!=null ?
                caseData.getRepresentativeClaimantType().getNameOfRepresentative() : " ");
        multipleType.setRespondentRepM(caseData.getRepCollection()!=null && !caseData.getRepCollection().isEmpty() &&
                caseData.getRepCollection().get(0).getValue()!=null ?
                caseData.getRepCollection().get(0).getValue().getNameOfRepresentative() : " ");
        multipleType.setEthosCaseReferenceM(caseData.getEthosCaseReference()!=null ? caseData.getEthosCaseReference() : " ");
        multipleType.setFileLocM(!isNullOrEmpty(caseData.getFileLocation()) ? caseData.getFileLocation() : " ");
        multipleType.setReceiptDateM(!isNullOrEmpty(caseData.getReceiptDate()) ? caseData.getReceiptDate() : " ");
        multipleType.setAcasOfficeM(!isNullOrEmpty(caseData.getAcasOffice()) ? caseData.getAcasOffice() : " ");
        multipleType.setPositionTypeM(!isNullOrEmpty(caseData.getPositionType()) ? caseData.getPositionType() : " ");
        multipleType.setFeeGroupReferenceM(!isNullOrEmpty(caseData.getFeeGroupReference()) ? caseData.getFeeGroupReference() : " ");
        multipleType.setJurCodesCollectionM(getJurCodesCollection(caseData.getJurCodesCollection()));
        multipleType.setStateM(!isNullOrEmpty(submitEvent.getState()) ? submitEvent.getState() : " ");
        multipleType.setMultipleReferenceM(!isNullOrEmpty(caseData.getMultipleReference()) ? caseData.getMultipleReference() : " ");
        return multipleType;
    }

    private static String getJurCodesCollection(List<JurCodesTypeItem> jurCodesTypeItems) {
        if (jurCodesTypeItems != null) {
            return jurCodesTypeItems.stream()
                    .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                    .distinct()
                    .collect(Collectors.joining(", "));
        } else {
            return " ";
        }
    }

    public static List<String> getCaseIds(BulkDetails bulkDetails) {
        return bulkDetails.getCaseData().getCaseIdCollection().stream()
                .filter(key -> key.getId() != null)
                .map(caseId -> caseId.getValue().getEthosCaseReference())
                .distinct()
                .collect(Collectors.toList());
    }

    static List<CaseIdTypeItem> getCaseIdTypeItems(BulkDetails bulkDetails) {
        System.out.println("Cases: " + bulkDetails.getCaseData().getCaseIdCollection());
        return bulkDetails.getCaseData().getCaseIdCollection() != null ?
                bulkDetails.getCaseData().getCaseIdCollection().stream()
                        .filter(p -> p.getValue().getEthosCaseReference() != null)
                        .filter(distinctByKey(p -> p.getValue().getEthosCaseReference()))
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }

    public static List<String> getMultipleCaseIds(BulkDetails bulkDetails) {
        return bulkDetails.getCaseData().getMultipleCollection() != null ?
                bulkDetails.getCaseData().getMultipleCollection().stream()
                    .map(caseId -> caseId.getValue().getEthosCaseReferenceM())
                    .distinct()
                    .collect(Collectors.toList()) :
                new ArrayList<>();
    }

//    private static List<String> getJurCodesValues(List<JurCodesTypeItem> jurCodesTypeItems) {
//        return jurCodesTypeItems != null ?
//                jurCodesTypeItems.stream()
//                        .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
//                        .distinct()
//                        .collect(Collectors.toList()) :
//                new ArrayList<>();
//    }

//    public static boolean containsAllJurCodes(List<JurCodesTypeItem> jurCodesTypeItems1, List<JurCodesTypeItem> jurCodesTypeItems2) {
//        return getJurCodesValues(jurCodesTypeItems1).containsAll(getJurCodesValues(jurCodesTypeItems2));
//    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static String getLeadId(BulkDetails bulkDetails) {
        List<CaseIdTypeItem> list = bulkDetails.getCaseData().getCaseIdCollection().stream()
                .filter(key -> !key.getValue().getEthosCaseReference().equals(""))
                .collect(Collectors.toList());
        return !list.isEmpty() ? list.get(0).getValue().getEthosCaseReference() : "";
    }

}
