package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadES;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.getActiveRespondents;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.getActiveRespondentsLabels;

@Slf4j
public class LabelsHelper {

    public static final int MAX_NUMBER_LABELS = 2000;
    public static final String ADDRESS_LABELS_RESULT_SELECTION_ERROR = "There are no address labels associated with your selection";

    public static AddressLabelTypeItem getClaimantAddressLabelData(LabelPayloadES labelPayloadES, String printClaimantLabel) {

        return getClaimantAddressLabel(labelPayloadES.getClaimantTypeOfClaimant(),
                labelPayloadES.getClaimantIndType(),
                labelPayloadES.getClaimantType(),
                labelPayloadES.getClaimantCompany(),
                labelPayloadES.getEthosCaseReference(),
                printClaimantLabel);

    }

    public static AddressLabelTypeItem getClaimantAddressLabelCaseData(CaseData caseData, String printClaimantLabel) {

        return getClaimantAddressLabel(caseData.getClaimantTypeOfClaimant(),
                caseData.getClaimantIndType(),
                caseData.getClaimantType(),
                caseData.getClaimantCompany(),
                caseData.getEthosCaseReference(),
                printClaimantLabel);

    }

    private static AddressLabelTypeItem getClaimantAddressLabel(String claimantTypeOfClaimant,
                                                               ClaimantIndType claimantIndType,
                                                               ClaimantType claimantType,
                                                               String claimantCompany,
                                                               String ethosCaseReference,
                                                               String printClaimantLabel) {

        AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
        AddressLabelType addressLabelType = new AddressLabelType();

        addressLabelType.setPrintLabel(printClaimantLabel);

        if (claimantTypeOfClaimant != null && claimantTypeOfClaimant.equals(INDIVIDUAL_TYPE_CLAIMANT)) {
            if (claimantIndType != null) {
                addressLabelType.setFullName(CLAIMANT + Helper.nullCheck(claimantIndType.claimantFullName()));
                addressLabelType.setLabelEntityName01(Helper.nullCheck(claimantIndType.claimantFullName()));
                addressLabelType.setLabelEntityName02("");
            }
        } else {
            addressLabelType.setFullName(CLAIMANT + Helper.nullCheck(claimantCompany));
            addressLabelType.setLabelEntityName01("");
            addressLabelType.setLabelEntityName02((Helper.nullCheck(claimantCompany)));
        }

        if (claimantType != null) {
            getEntityAddress(addressLabelType, claimantType.getClaimantAddressUK());
            getEntityTelephone(addressLabelType, claimantType.getClaimantPhoneNumber());
            getEntityFax(addressLabelType, claimantType.getClaimantMobileNumber());
        } else {
            addressLabelType.setFullAddress("");
            addressLabelType.setLabelEntityAddress(new Address());
            addressLabelType.setLabelEntityTelephone("");
            addressLabelType.setLabelEntityFax("");
        }

        addressLabelType.setLabelEntityReference(REF);
        addressLabelType.setLabelCaseReference(ethosCaseReference);

        addressLabelTypeItem.setId(UUID.randomUUID().toString());
        addressLabelTypeItem.setValue(addressLabelType);

        return addressLabelTypeItem;

    }

    public static AddressLabelTypeItem getClaimantRepAddressLabelData(LabelPayloadES labelPayloadES, String printClaimantRepLabel) {

        if (labelPayloadES.getRepresentativeClaimantType() != null && labelPayloadES.getClaimantRepresentedQuestion().equals(YES)) {
            return getClaimantRepAddressLabel(labelPayloadES.getRepresentativeClaimantType(),
                    labelPayloadES.getEthosCaseReference(), printClaimantRepLabel);
        }

        return null;
    }

    public static AddressLabelTypeItem getClaimantRepAddressLabelCaseData(CaseData caseData, String printClaimantRepLabel) {

        if (caseData.getRepresentativeClaimantType() != null && caseData.getClaimantRepresentedQuestion().equals(YES)) {
            return getClaimantRepAddressLabel(caseData.getRepresentativeClaimantType(),
                    caseData.getEthosCaseReference(), printClaimantRepLabel);
        }

        return null;

    }

    private static AddressLabelTypeItem getClaimantRepAddressLabel(RepresentedTypeC representedTypeC,
                                                                  String ethosCaseReference,
                                                                  String printClaimantRepLabel) {

        AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
        AddressLabelType addressLabelType = new AddressLabelType();

        addressLabelType.setPrintLabel(printClaimantRepLabel);

        addressLabelType.setFullName(CLAIMANT_REP + Helper.nullCheck(representedTypeC.getNameOfRepresentative()));
        addressLabelType.setLabelEntityName01(Helper.nullCheck(representedTypeC.getNameOfRepresentative()));
        addressLabelType.setLabelEntityName02(Helper.nullCheck(representedTypeC.getNameOfOrganisation()));
        getEntityAddress(addressLabelType, representedTypeC.getRepresentativeAddress());
        getEntityTelephone(addressLabelType, representedTypeC.getRepresentativePhoneNumber());
        getEntityFax(addressLabelType, representedTypeC.getRepresentativeMobileNumber());

        if (!isNullOrEmpty(Helper.nullCheck(representedTypeC.getRepresentativeReference()))) {
            addressLabelType.setLabelEntityReference(REF + Helper.nullCheck(representedTypeC.getRepresentativeReference()));
        } else {
            addressLabelType.setLabelEntityReference(REF);
        }

        addressLabelType.setLabelCaseReference(ethosCaseReference);

        addressLabelTypeItem.setId(UUID.randomUUID().toString());
        addressLabelTypeItem.setValue(addressLabelType);

        return addressLabelTypeItem;

    }

    public static List<AddressLabelTypeItem> getRespondentsAddressLabelsData(LabelPayloadES labelPayloadES, String printRespondentsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();
        if (labelPayloadES.getRespondentCollection() != null && !labelPayloadES.getRespondentCollection().isEmpty()) {
            List<RespondentSumTypeItem> activeRespondents = getActiveRespondentsLabels(labelPayloadES);
            if (!activeRespondents.isEmpty()) {
                labelTypeItemList = getRespondentsAddressLabels(activeRespondents,
                        labelPayloadES.getEthosCaseReference(), printRespondentsLabels);
            }
        }

        return labelTypeItemList;

    }

    public static List<AddressLabelTypeItem> getRespondentsAddressLabelsCaseData(CaseData caseData, String printRespondentsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<RespondentSumTypeItem> activeRespondents = getActiveRespondents(caseData);
            if (!activeRespondents.isEmpty()) {
                labelTypeItemList = getRespondentsAddressLabels(activeRespondents,
                        caseData.getEthosCaseReference(), printRespondentsLabels);
            }
        }

        return labelTypeItemList;

    }

    private static List<AddressLabelTypeItem> getRespondentsAddressLabels(List<RespondentSumTypeItem> activeRespondents,
                                                                         String ethosCaseReference,
                                                                         String printRespondentsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();

        for (RespondentSumTypeItem activeRespondent : activeRespondents) {

            AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
            AddressLabelType addressLabelType = new AddressLabelType();

            RespondentSumType respondentSumType = activeRespondent.getValue();

            addressLabelType.setPrintLabel(printRespondentsLabels);
            addressLabelType.setFullName(RESPONDENT + Helper.nullCheck(respondentSumType.getRespondentName()));
            addressLabelType.setLabelEntityName01(Helper.nullCheck(respondentSumType.getRespondentName()));
            addressLabelType.setLabelEntityName02("");
            getEntityAddress(addressLabelType, DocumentHelper.getRespondentAddressET3(respondentSumType));
            getEntityTelephone(addressLabelType, respondentSumType.getRespondentPhone1());
            getEntityFax(addressLabelType, respondentSumType.getRespondentPhone2());
            addressLabelType.setLabelEntityReference(REF);
            addressLabelType.setLabelCaseReference(ethosCaseReference);

            addressLabelTypeItem.setId(UUID.randomUUID().toString());
            addressLabelTypeItem.setValue(addressLabelType);

            labelTypeItemList.add(addressLabelTypeItem);

        }

        return labelTypeItemList;

    }

    public static List<AddressLabelTypeItem> getRespondentsRepsAddressLabelsData(LabelPayloadES labelPayloadES, String printRespondentsRepsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();
        if (labelPayloadES.getRepCollection() != null && !labelPayloadES.getRepCollection().isEmpty()) {
            labelTypeItemList = getRespondentsRepsAddressLabels(labelPayloadES.getRepCollection(),
                    labelPayloadES.getEthosCaseReference(), printRespondentsRepsLabels);

        }

        return labelTypeItemList;

    }

    public static List<AddressLabelTypeItem> getRespondentsRepsAddressLabelsCaseData(CaseData caseData, String printRespondentsRepsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();
        if (caseData.getRepCollection() != null && !caseData.getRepCollection().isEmpty()) {
            labelTypeItemList = getRespondentsRepsAddressLabels(caseData.getRepCollection(),
                    caseData.getEthosCaseReference(), printRespondentsRepsLabels);

        }

        return labelTypeItemList;

    }

    private static List<AddressLabelTypeItem> getRespondentsRepsAddressLabels(List<RepresentedTypeRItem> repCollection,
                                                                             String ethosCaseReference,
                                                                             String printRespondentsRepsLabels) {

        List<AddressLabelTypeItem> labelTypeItemList = new ArrayList<>();

        for (RepresentedTypeRItem representedTypeRItem : repCollection) {

            AddressLabelTypeItem addressLabelTypeItem = new AddressLabelTypeItem();
            AddressLabelType addressLabelType = new AddressLabelType();

            RepresentedTypeR representedTypeR = representedTypeRItem.getValue();

            addressLabelType.setPrintLabel(printRespondentsRepsLabels);

            addressLabelType.setFullName(RESPONDENT_REP + Helper.nullCheck(representedTypeR.getNameOfRepresentative()));
            addressLabelType.setLabelEntityName01(Helper.nullCheck(representedTypeR.getNameOfRepresentative()));
            addressLabelType.setLabelEntityName02(Helper.nullCheck(representedTypeR.getNameOfOrganisation()));
            getEntityAddress(addressLabelType, representedTypeR.getRepresentativeAddress());
            getEntityTelephone(addressLabelType, representedTypeR.getRepresentativePhoneNumber());
            getEntityFax(addressLabelType, representedTypeR.getRepresentativeMobileNumber());

            if (!isNullOrEmpty(Helper.nullCheck(representedTypeR.getRepresentativeReference()))) {
                addressLabelType.setLabelEntityReference(REF + Helper.nullCheck(representedTypeR.getRepresentativeReference()));
            } else {
                addressLabelType.setLabelEntityReference(REF);
            }

            addressLabelType.setLabelCaseReference(ethosCaseReference);

            addressLabelTypeItem.setId(UUID.randomUUID().toString());
            addressLabelTypeItem.setValue(addressLabelType);

            labelTypeItemList.add(addressLabelTypeItem);

        }

        return labelTypeItemList;

    }

    private static void getEntityAddress(AddressLabelType addressLabelType, Address entityAddress) {

        if (entityAddress != null) {
            addressLabelType.setFullAddress(getFullAddressOneLine(entityAddress).toString());
            addressLabelType.setLabelEntityAddress(entityAddress);

        } else {
            addressLabelType.setFullAddress("");
            addressLabelType.setLabelEntityAddress(new Address());
        }

    }

    private static void getEntityTelephone(AddressLabelType addressLabelType, String telephone) {

        if (!isNullOrEmpty(Helper.nullCheck(telephone))) {
            addressLabelType.setLabelEntityTelephone(TEL + Helper.nullCheck(telephone));

        } else {
            addressLabelType.setLabelEntityTelephone("");
        }

    }

    private static void getEntityFax(AddressLabelType addressLabelType, String fax) {

        if (!isNullOrEmpty(Helper.nullCheck(fax))) {
            addressLabelType.setLabelEntityFax(TEL + Helper.nullCheck(fax));

        } else {
            addressLabelType.setLabelEntityFax("");
        }

    }


    private static StringBuilder getFullAddressOneLine(Address address) {

        StringBuilder sb = new StringBuilder();
        sb.append(Helper.nullCheck(address.getAddressLine1()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getAddressLine2())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getAddressLine2()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getAddressLine3())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getAddressLine3()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getPostTown())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getPostTown()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getCounty())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getCounty()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getPostCode())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getPostCode()));
        sb.append(!isNullOrEmpty(Helper.nullCheck(address.getCountry())) && sb.length() > 0  ? ", " : "");
        sb.append(Helper.nullCheck(address.getCountry()));
        sb.append(sb.length() > 0  ? "." : "");

        return sb;

    }

    public static List<AddressLabelTypeItem> customiseSelectedAddressesMultiples(List<LabelPayloadEvent> labelPayloadEvents,
                                                                                 MultipleData multipleData) {

        if (multipleData.getAddressLabelsSelectionTypeMSL() != null && !multipleData.getAddressLabelsSelectionTypeMSL().isEmpty()) {

            return new ArrayList<>(getAddressLabelTypeItems(labelPayloadEvents, multipleData.getAddressLabelsSelectionTypeMSL()));

        } else {

            return null;

        }

    }

    private static List<AddressLabelTypeItem> getAddressLabelTypeItems(List<LabelPayloadEvent> labelPayloadEvents,
                                                                List<String> addressLabelsSelectionTypeMSL) {

        List<AddressLabelTypeItem> addressLabelTypeItems = new ArrayList<>();

        for (LabelPayloadEvent labelPayloadEvent : labelPayloadEvents) {

            if (addressLabelsSelectionTypeMSL.contains(CLAIMANT_ADDRESS_LABEL)) {

                log.info("Adding: CLAIMANT_ADDRESS_LABEL");

                addressLabelTypeItems.add(LabelsHelper.getClaimantAddressLabelData(labelPayloadEvent.getLabelPayloadES(), YES));

            }

            if (addressLabelsSelectionTypeMSL.contains(CLAIMANT_REP_ADDRESS_LABEL)) {

                log.info("Adding: CLAIMANT_REP_ADDRESS_LABEL");

                AddressLabelTypeItem addressLabelTypeItem =
                        LabelsHelper.getClaimantRepAddressLabelData(labelPayloadEvent.getLabelPayloadES(), YES);
                if (addressLabelTypeItem != null) {
                    addressLabelTypeItems.add(addressLabelTypeItem);
                }

            }

            if (addressLabelsSelectionTypeMSL.contains(RESPONDENTS_ADDRESS__LABEL)) {

                log.info("Adding: RESPONDENTS_ADDRESS__LABEL");

                List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                        LabelsHelper.getRespondentsAddressLabelsData(labelPayloadEvent.getLabelPayloadES(), YES);
                if (!addressLabelTypeItemsAux.isEmpty()) {
                    addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
                }
            }

            if (addressLabelsSelectionTypeMSL.contains(RESPONDENTS_REPS_ADDRESS__LABEL)) {

                log.info("Adding: RESPONDENTS_REPS_ADDRESS__LABEL");

                List<AddressLabelTypeItem> addressLabelTypeItemsAux =
                        LabelsHelper.getRespondentsRepsAddressLabelsData(labelPayloadEvent.getLabelPayloadES(), YES);
                if (!addressLabelTypeItemsAux.isEmpty()) {
                    addressLabelTypeItems.addAll(addressLabelTypeItemsAux);
                }
            }

        }

        return addressLabelTypeItems;

    }

    public static List<AddressLabelTypeItem> getSelectedAddressLabelsMultiple(MultipleData multipleData) {

        List<AddressLabelTypeItem> selectedAddressLabels = new ArrayList<>();

        if (multipleData.getAddressLabelCollection() != null && !multipleData.getAddressLabelCollection().isEmpty()) {
            selectedAddressLabels = multipleData.getAddressLabelCollection()
                    .stream()
                    .filter(addressLabelTypeItem -> addressLabelTypeItem.getValue().getFullName() != null
                            || addressLabelTypeItem.getValue().getFullAddress() != null)
                    .collect(Collectors.toList());
        }

        return selectedAddressLabels;
    }

    public static void validateNumberOfSelectedLabels(MultipleData multipleData, List<String> errors) {
        String templateName = DocumentHelper.getTemplateName(multipleData.getCorrespondenceType(), multipleData.getCorrespondenceScotType());
        if (errors.isEmpty()
                && templateName.equals(ADDRESS_LABELS_TEMPLATE)
                && Integer.parseInt(multipleData.getAddressLabelsAttributesType().getNumberOfSelectedLabels()) == 0) {
            errors.add(ADDRESS_LABELS_RESULT_SELECTION_ERROR);
        }
    }

    public static List<String> midValidateAddressLabelsErrors(AddressLabelsAttributesType addressLabelsAttributesType, String caseType) {

        List<String> errors = new ArrayList<>();

        if (caseType.equals(SINGLE_CASE_TYPE) && Integer.parseInt(addressLabelsAttributesType.getNumberOfSelectedLabels()) == 0) {
            errors.add(ADDRESS_LABELS_SELECT_ERROR);

        } else if (addressLabelsAttributesType.getNumberOfCopies().contains(".")) {
            errors.add(ADDRESS_LABELS_COPIES_ERROR);

        } else if (caseType.equals(SINGLE_CASE_TYPE) && Integer.parseInt(addressLabelsAttributesType.getNumberOfCopies()) > 10) {
            errors.add(ADDRESS_LABELS_COPIES_LESS_10_ERROR);

        } else if (caseType.equals(MULTIPLE_CASE_TYPE) && reachLimitOfTotalNumberLabels(addressLabelsAttributesType)) {
            errors.add(ADDRESS_LABELS_LABELS_LIMIT_ERROR + " of " + MAX_NUMBER_LABELS);

        }

        return errors;

    }

    private static boolean reachLimitOfTotalNumberLabels(AddressLabelsAttributesType addressLabelsAttributesType) {

        int selectedLabels = Integer.parseInt(addressLabelsAttributesType.getNumberOfSelectedLabels());
        int numberOfCopies = Integer.parseInt(addressLabelsAttributesType.getNumberOfCopies());
        return selectedLabels * numberOfCopies > MAX_NUMBER_LABELS;

    }

}
