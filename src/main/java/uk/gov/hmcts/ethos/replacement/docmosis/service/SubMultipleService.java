package uk.gov.hmcts.ethos.replacement.docmosis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.BulkData;
import uk.gov.hmcts.ecm.common.model.bulk.BulkDetails;
import uk.gov.hmcts.ecm.common.model.bulk.items.MidSearchTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.MultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.items.SubMultipleTypeItem;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.bulk.types.SubMultipleType;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.helper.BulkRequestPayload;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.BulkHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEFAULT_SELECT_ALL_VALUE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SELECT_NONE_VALUE;

@Slf4j
@Service("subMultipleService")
public class SubMultipleService {

    private final SubMultipleReferenceService subMultipleReferenceService;

    @Autowired
    public SubMultipleService(SubMultipleReferenceService subMultipleReferenceService) {
        this.subMultipleReferenceService = subMultipleReferenceService;
    }

    private String generateSubMultipleRef(BulkDetails bulkDetails) {
        if (bulkDetails.getCaseData().getSubMultipleRef() == null
                || bulkDetails.getCaseData().getSubMultipleRef().trim().equals("")) {
            return subMultipleReferenceService.createReference(bulkDetails.getCaseTypeId(),
                    bulkDetails.getCaseData().getMultipleReference(), 1);
        } else {
            return bulkDetails.getCaseData().getSubMultipleRef();
        }
    }

    public BulkRequestPayload createSubMultipleLogic(BulkDetails bulkDetails) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<String> errors = new ArrayList<>();
        if (bulkDetails.getCaseData().getMidSearchCollection() != null) {
            List<String> subMultiplesList = new ArrayList<>();
            for (MidSearchTypeItem refNumbersFiltered : bulkDetails.getCaseData().getMidSearchCollection()) {
                Optional<MultipleTypeItem> multipleTypeItem = bulkDetails.getCaseData().getMultipleCollection()
                        .stream()
                        .filter(multipleValue -> multipleValue.getValue().getEthosCaseReferenceM()
                                .equals(refNumbersFiltered.getValue())
                                && (multipleValue.getValue().getSubMultipleM() == null
                                        || multipleValue.getValue().getSubMultipleM().equals(" ")))
                        .findFirst();
                multipleTypeItem.ifPresent(typeItem ->
                        subMultiplesList.add(typeItem.getValue().getEthosCaseReferenceM()));
            }
            String subMultipleRefNumber = generateSubMultipleRef(bulkDetails);
            if (!subMultiplesList.isEmpty()) {
                List<MultipleTypeItem> multipleTypeItems = new ArrayList<>();
                for (MultipleTypeItem multipleTypeItem : bulkDetails.getCaseData().getMultipleCollection()) {
                    if (subMultiplesList.contains(multipleTypeItem.getValue().getEthosCaseReferenceM())) {
                        multipleTypeItem.getValue().setSubMultipleM(subMultipleRefNumber);
                        multipleTypeItem.getValue().setSubMultipleTitleM(
                                bulkDetails.getCaseData().getSubMultipleName());
                    }
                    multipleTypeItems.add(multipleTypeItem);
                }
                bulkDetails.getCaseData().setMultipleCollection(multipleTypeItems);
            }
            bulkDetails.setCaseData(addSubMultipleTypeToCase(bulkDetails.getCaseData(), subMultipleRefNumber));
            bulkDetails.setCaseData(clearUpFields(bulkDetails.getCaseData()));
        } else {
            errors.add("No cases have been found");
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
            bulkDetails.setCaseData(createSubMultipleDynamicList(bulkDetails.getCaseData(),
                    getSubMultipleListItems(bulkDetails, listItems)));
        } else {
            errors.add("No sub multiples have been found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private List<DynamicValueType> createDynamicFixListWithDefaultValue(String defaultCode, String defaultValue) {
        List<DynamicValueType> listItems = new ArrayList<>();
        DynamicValueType defaultDynamicValueType = new DynamicValueType();
        defaultDynamicValueType.setCode(defaultCode);
        defaultDynamicValueType.setLabel(defaultValue);
        listItems.add(defaultDynamicValueType);
        return listItems;
    }

    public BulkRequestPayload populateFilterDefaultedDynamicListLogic(BulkDetails bulkDetails, String defaultValue) {
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
        List<DynamicValueType> subMultipleItems =
                createDynamicFixListWithDefaultValue(DEFAULT_SELECT_ALL_VALUE, defaultValue);
        bulkDetails.setCaseData(createSubMultipleDynamicList(
                bulkDetails.getCaseData(), getSubMultipleListItems(bulkDetails, subMultipleItems)));
        if (defaultValue.equals(SELECT_NONE_VALUE)) {
            List<DynamicValueType> jurCodesItems = createDynamicFixListWithDefaultValue(defaultValue, defaultValue);
            bulkDetails.setCaseData(createJurCodeDynamicList(
                    bulkDetails.getCaseData(), getJurCodeListItems(bulkDetails, jurCodesItems)));
        }
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private List<DynamicValueType> getJurCodeListItems(BulkDetails bulkDetails, List<DynamicValueType> listItems) {
        if (bulkDetails.getCaseData().getJurCodesCollection() != null
                && bulkDetails.getCaseData().getSearchCollection() != null) {
            for (JurCodesTypeItem jurCodesTypeItem : bulkDetails.getCaseData().getJurCodesCollection()) {
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(jurCodesTypeItem.getValue().getJuridictionCodesList());
                dynamicValueType.setLabel(jurCodesTypeItem.getValue().getJuridictionCodesList());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private List<DynamicValueType> getSubMultipleListItems(BulkDetails bulkDetails, List<DynamicValueType> listItems) {
        if (bulkDetails.getCaseData().getSubMultipleCollection() != null) {
            for (SubMultipleTypeItem subMultipleTypeItem : bulkDetails.getCaseData().getSubMultipleCollection()) {
                DynamicValueType dynamicValueType = new DynamicValueType();
                dynamicValueType.setCode(subMultipleTypeItem.getValue().getSubMultipleRefT());
                dynamicValueType.setLabel(subMultipleTypeItem.getValue().getSubMultipleNameT());
                listItems.add(dynamicValueType);
            }
        }
        return listItems;
    }

    private BulkData createJurCodeDynamicList(BulkData bulkData, List<DynamicValueType> listItems) {
        if (bulkData.getJurCodesDynamicList() != null) {
            bulkData.getJurCodesDynamicList().setListItems(listItems);
        } else {
            DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(listItems);
            bulkData.setJurCodesDynamicList(dynamicFixedListType);
        }
        //Default dynamic list
        bulkData.getJurCodesDynamicList().setValue(listItems.get(0));
        return bulkData;
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
                    .removeIf(subMultipleTypeItem ->
                            subMultipleTypeItem.getValue().getSubMultipleRefT().equals(refSelected));
            bulkDetails.setCaseData(
                    removeSubMultipleRefFromMultiplesCollection(bulkDetails.getCaseData(), refSelected));
        } else {
            errors.add("No sub multiples have been found");
            bulkRequestPayload.setErrors(errors);
        }
        bulkDetails.getCaseData().setSubMultipleDynamicList(null);
        bulkRequestPayload.setBulkDetails(bulkDetails);
        return bulkRequestPayload;
    }

    private BulkData removeSubMultipleRefFromMultiplesCollection(BulkData bulkData, String refSelected) {
        List<MultipleTypeItem> auxList = new ArrayList<>();
        for (MultipleTypeItem multipleTypeItem : bulkData.getMultipleCollection()) {
            if (multipleTypeItem.getValue().getSubMultipleM().equals(refSelected)) {
                multipleTypeItem.getValue().setSubMultipleM(" ");
                multipleTypeItem.getValue().setSubMultipleTitleM(" ");
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
            Optional<SubMultipleTypeItem> subMultipleOptional =
                    bulkDetails.getCaseData().getSubMultipleCollection().stream()
                            .filter(subMultipleTypeItem ->
                            subMultipleTypeItem.getValue().getSubMultipleRefT().equals(refSelected))
                            .findFirst();
            bulkDetails.getCaseData().setSubMultipleName(subMultipleOptional.isPresent()
                    ? subMultipleOptional.get().getValue().getSubMultipleNameT()
                    : "");
            bulkDetails.getCaseData().setSubMultipleRef(refSelected);
            bulkDetails.getCaseData().setMidSearchCollection(
                    retrieveMidSearchCollectionBySubMultipleRef(bulkDetails.getCaseData(), refSelected));
        } else {
            errors.add("No sub multiples have been found");
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
        String subMultipleRefNumber = bulkData.getSubMultipleRef();
        bulkData.setSubMultipleCollection(amendSubMultipleDetails(bulkData, subMultipleRefNumber));
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
                    multipleTypeItem.getValue().setSubMultipleTitleM(" ");
                } else if (subMultipleRef.equals(" ") && midSearchCollection.contains(caseRefNumber)) {
                    multipleTypeItem.getValue().setSubMultipleM(subMultipleRefNumber);
                    multipleTypeItem.getValue().setSubMultipleTitleM(BulkHelper
                            .getSubMultipleTitle(subMultipleRefNumber, bulkData));
                }
                auxMultiplesList.add(multipleTypeItem);
            }
            bulkData.setMultipleCollection(auxMultiplesList);
        }
        bulkDetails.setCaseData(clearUpFields(bulkData));
        BulkRequestPayload bulkRequestPayload = new BulkRequestPayload();
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