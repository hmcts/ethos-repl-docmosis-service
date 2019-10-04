package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.items.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.types.*;
import uk.gov.hmcts.ethos.replacement.docmosis.model.helper.BulkRequestPayload;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service("subMultipleService")
public class SubMultipleService {

    private final SubMultipleReferenceService subMultipleReferenceService;

    @Autowired
    public SubMultipleService(SubMultipleReferenceService subMultipleReferenceService) {
        this.subMultipleReferenceService = subMultipleReferenceService;
    }

    public BulkRequestPayload createSubMultipleLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getMidSearchCollection() != null) {
            List<String> subMultiplesList = new ArrayList<>();
            for (MidSearchTypeItem refNumbersFiltered : bulkDetails.getCaseData().getMidSearchCollection()) {
                Optional<MultipleTypeItem> multipleTypeItem = bulkDetails.getCaseData().getMultipleCollection()
                        .stream()
                        .filter(multipleValue -> multipleValue.getValue().getEthosCaseReferenceM().equals(refNumbersFiltered.getValue()) &&
                                (multipleValue.getValue().getSubMultipleM() == null || multipleValue.getValue().getSubMultipleM().equals(" ")))
                        .findFirst();
                multipleTypeItem.ifPresent(typeItem -> subMultiplesList.add(typeItem.getValue().getEthosCaseReferenceM()));
            }
            String subMultipleRefNumber = subMultipleReferenceService.createReference(bulkDetails.getCaseTypeId(), bulkDetails.getCaseData().getMultipleReference());
            if (!subMultiplesList.isEmpty()) {
                List<MultipleTypeItem> multipleTypeItems = new ArrayList<>();
                for (MultipleTypeItem multipleTypeItem : bulkDetails.getCaseData().getMultipleCollection()) {
                    if (subMultiplesList.contains(multipleTypeItem.getValue().getEthosCaseReferenceM())) {
                        multipleTypeItem.getValue().setSubMultipleM(subMultipleRefNumber);
                    }
                    multipleTypeItems.add(multipleTypeItem);
                }
                bulkDetails.getCaseData().setMultipleCollection(multipleTypeItems);
            }
            bulkDetails.setCaseData(addSubMultipleTypeToCase(bulkDetails.getCaseData(), subMultipleRefNumber));
            bulkDetails.setCaseData(clearUpFields(bulkDetails.getCaseData()));
        } else {
            errors.add("There are not cases found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData addSubMultipleTypeToCase(BulkData bulkData, String subMultipleRefNumber) {
        SubMultipleType subMultipleType = new SubMultipleType();
        subMultipleType.setSubMultipleNameT(bulkData.getSubMultipleName());
        subMultipleType.setSubMultipleRefT(subMultipleRefNumber);
        SubMultipleTypeItem subMultipleTypeItem = new SubMultipleTypeItem();
        subMultipleTypeItem.setId(subMultipleRefNumber);
        subMultipleTypeItem.setValue(subMultipleType);
        List<SubMultipleTypeItem> subMultipleTypeItems = bulkData.getSubMultipleCollection();
        if (subMultipleTypeItems != null) {
            subMultipleTypeItems.add(subMultipleTypeItem);
        } else {
            subMultipleTypeItems = new ArrayList<>(Collections.singletonList(subMultipleTypeItem));
        }
        bulkData.setSubMultipleCollection(subMultipleTypeItems);
        return bulkData;
    }

    public BulkRequestPayload populateSubMultipleDynamicListLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getSubMultipleCollection() != null) {
            List<DynamicValueType> listItems = new ArrayList<>();
            for (SubMultipleTypeItem subMultipleTypeItem : bulkDetails.getCaseData().getSubMultipleCollection()) {
                log.info("SubMultipleTypeItem: " + subMultipleTypeItem);
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(subMultipleTypeItem.getValue().getSubMultipleRefT());
                dynamicValueType.setLabel(subMultipleTypeItem.getValue().getSubMultipleNameT());
                listItems.add(dynamicValueType);
            }
            bulkDetails.setCaseData(createSubMultipleDynamicList(bulkDetails.getCaseData(), listItems));
        } else {
            errors.add("There are not sub multiples found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData createSubMultipleDynamicList(BulkData bulkData, List<DynamicValueType> listItems) {
        if (bulkData.getSubMultipleDynamicList() != null) {
            bulkData.getSubMultipleDynamicList().setListItems(listItems);
        } else {
            DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            bulkData.setSubMultipleDynamicList(dynamicFixedListType);
        }
        //Default dynamic list
        bulkData.getSubMultipleDynamicList().setValue(listItems.get(0));
        return bulkData;
    }

    public BulkRequestPayload deleteSubMultipleLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getSubMultipleDynamicList() != null) {
            String refSelected = bulkDetails.getCaseData().getSubMultipleDynamicList().getValue().getCode();
            bulkDetails.getCaseData().getSubMultipleCollection()
                    .removeIf(subMultipleTypeItem -> subMultipleTypeItem.getValue().getSubMultipleRefT().equals(refSelected));
            bulkDetails.setCaseData(removeSubMultipleRefFromMultiplesCollection(bulkDetails.getCaseData(), refSelected));
        } else {
            errors.add("There are not sub multiples found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData removeSubMultipleRefFromMultiplesCollection(BulkData bulkData, String refSelected) {
        List<MultipleTypeItem> auxList = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : bulkData.getMultipleCollection()) {
            if (multipleTypeItem.getValue().getSubMultipleM().equals(refSelected)) {
                multipleTypeItem.getValue().setSubMultipleM(" ");
            }
            auxList.add(multipleTypeItem);
        }
        bulkData.setMultipleCollection(auxList);
        return bulkData;
    }

    public BulkRequestPayload bulkMidUpdateLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getSubMultipleDynamicList() != null) {
            String refSelected = bulkDetails.getCaseData().getSubMultipleDynamicList().getValue().getCode();
            Optional<SubMultipleTypeItem> subMultipleOptional = bulkDetails.getCaseData().getSubMultipleCollection().stream()
                    .filter(subMultipleTypeItem -> subMultipleTypeItem.getValue().getSubMultipleRefT().equals(refSelected))
                    .findFirst();
            bulkDetails.getCaseData().setSubMultipleName(subMultipleOptional.isPresent() ? subMultipleOptional.get().getValue().getSubMultipleNameT() : "");
            bulkDetails.getCaseData().setSubMultipleRef(refSelected);
            bulkDetails.getCaseData().setMidSearchCollection(
                    retrieveMidSearchCollectionBySubMultipleRef(bulkDetails.getCaseData(), refSelected));
        } else {
            errors.add("There are not sub multiples found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkDetails.getCaseData().setSubMultipleDynamicList(null);
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private List<MidSearchTypeItem> retrieveMidSearchCollectionBySubMultipleRef(BulkData bulkData, String refSelected) {
        List<MidSearchTypeItem> midSearchTypeItems = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : bulkData.getMultipleCollection()) {
            if (multipleTypeItem.getValue().getSubMultipleM().equals(refSelected)) {
                MidSearchTypeItem midSearchTypeItem = new MidSearchTypeItem();
                midSearchTypeItem.setId(multipleTypeItem.getId());
                midSearchTypeItem.setValue(multipleTypeItem.getValue().getEthosCaseReferenceM());
                midSearchTypeItems.add(midSearchTypeItem);
            }
        }
        return midSearchTypeItems;
    }

    public BulkRequestPayload updateSubMultipleLogic(BulkDetails bulkDetails) {
        BulkData bulkData = bulkDetails.getCaseData();
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        String subMultipleRefNumber = bulkData.getSubMultipleRef();
        bulkData.setSubMultipleCollection(amendSubMultipleDetails(bulkData, subMultipleRefNumber));
        log.info("SubMultiple name updated");
        if (bulkData.getMidSearchCollection() != null) {
            List<String> midSearchCollection = bulkData.getMidSearchCollection().stream()
                    .map(MidSearchTypeItem::getValue)
                    .collect(Collectors.toList());
            List<MultipleTypeItem> auxMultiplesList = new ArrayList<>();
            for (MultipleTypeItem multipleTypeItem : bulkData.getMultipleCollection()) {
                String caseRefNumber = multipleTypeItem.getValue().getEthosCaseReferenceM();
                String subMultipleRef = multipleTypeItem.getValue().getSubMultipleM();
                if (subMultipleRef.equals(subMultipleRefNumber) && !midSearchCollection.contains(caseRefNumber)) {
                    multipleTypeItem.getValue().setSubMultipleM(" ");
                } else if (subMultipleRef.equals(" ") && midSearchCollection.contains(caseRefNumber)) {
                    multipleTypeItem.getValue().setSubMultipleM(subMultipleRefNumber);
                }
                auxMultiplesList.add(multipleTypeItem);
            }
            bulkData.setMultipleCollection(auxMultiplesList);
        }
        bulkDetails.setCaseData(clearUpFields(bulkData));
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private List<SubMultipleTypeItem> amendSubMultipleDetails(BulkData bulkData, String subMultipleRefNumber) {
        String subMultipleName = bulkData.getSubMultipleName();
        log.info("Checking...: " + subMultipleName + " " + subMultipleRefNumber);
        List<SubMultipleTypeItem> subMultipleTypeItems = new ArrayList<>();
        for (SubMultipleTypeItem subMultipleTypeItem : bulkData.getSubMultipleCollection()) {
            if (subMultipleTypeItem.getValue().getSubMultipleRefT().equals(subMultipleRefNumber)) {
                subMultipleTypeItem.getValue().setSubMultipleNameT(subMultipleName);
            }
            subMultipleTypeItems.add(subMultipleTypeItem);
        }
        return subMultipleTypeItems;
    }

    private BulkData clearUpFields(BulkData bulkData) {
        bulkData.setEthosCaseReference(null);
        bulkData.setClaimantSurname(null);
        bulkData.setRespondentSurname(null);
        bulkData.setClaimantRep(null);
        bulkData.setRespondentRep(null);
        bulkData.setMidSearchCollection(null);
        bulkData.setSubMultipleName(null);
        bulkData.setSubMultipleRef(null);
        bulkData.setSubMultipleDynamicList(null);
        return bulkData;
    }

}