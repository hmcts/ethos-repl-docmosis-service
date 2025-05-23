package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsAttributesType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_COPIES_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_COPIES_LESS_10_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_LABELS_LIMIT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_SELECT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_ADDRESS_LABEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_REP_ADDRESS_LABEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.INDIVIDUAL_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MULTIPLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_ADDRESS__LABEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENTS_REPS_ADDRESS__LABEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_CASE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper.ADDRESS_LABELS_RESULT_SELECTION_ERROR;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper.MAX_NUMBER_LABELS;

public class LabelsHelperTest {

    private MultipleDetails multipleDetails;
    private List<LabelPayloadEvent> labelPayloadEvents;
    private AddressLabelsAttributesType addressLabelsAttributesType;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        labelPayloadEvents = MultipleUtil.getLabelPayloadEvents();
        addressLabelsAttributesType = new AddressLabelsAttributesType();
    }

    @Test
    public void customiseSelectedAddressesMultiplesEmptySelectedAddresses() {
        assertNull(LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents,
                multipleDetails.getCaseData()));
    }

    @Test
    public void customiseSelectedAddressesMultiples() {
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(2, Objects.requireNonNull(
                LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleDetails.getCaseData())).size());
    }

    @Test
    public void customiseSelectedAddressesMultiplesClaimant() {
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantTypeOfClaimant(INDIVIDUAL_TYPE_CLAIMANT);
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantType(null);
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(2,
                Objects.requireNonNull(
                        LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleDetails.getCaseData())).size());
    }

    @Test
    public void customiseSelectedAddressesMultiplesClaimantRep() {
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantRepresentedQuestion(YES);
        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative("Name");
        representedTypeC.setRepresentativeReference("1234");
        labelPayloadEvents.get(0).getLabelPayloadES().setRepresentativeClaimantType(representedTypeC);
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(3,
                Objects.requireNonNull(
                        LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleDetails.getCaseData())).size());
    }

    @Test
    public void customiseSelectedAddressesMultiplesRespondent() {
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Collections.singletonList(RESPONDENTS_ADDRESS__LABEL)));
        assertEquals(2,
                Objects.requireNonNull(
                        LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleDetails.getCaseData())).size());
    }

    @Test
    public void customiseSelectedAddressesMultiplesRespondentRep() {
        RepresentedTypeR representedTypeR = new RepresentedTypeR();
        representedTypeR.setNameOfRepresentative("Name");
        representedTypeR.setRepresentativeReference("1234");
        RepresentedTypeRItem representedTypeRItem = new RepresentedTypeRItem();
        representedTypeRItem.setId("12345");
        representedTypeRItem.setValue(representedTypeR);
        List<RepresentedTypeRItem> repCollection = new ArrayList<>();
        repCollection.add(representedTypeRItem);
        labelPayloadEvents.get(0).getLabelPayloadES().setRepCollection(repCollection);
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, RESPONDENTS_REPS_ADDRESS__LABEL)));
        assertEquals(3,
                Objects.requireNonNull(
                        LabelsHelper.customiseSelectedAddressesMultiples(labelPayloadEvents, multipleDetails.getCaseData())).size());
    }

    @Test
    public void validateNumberOfSelectedLabels() {
        List<String> errors = new ArrayList<>();
        CorrespondenceType correspondenceType = new CorrespondenceType();
        correspondenceType.setTopLevelDocuments(ADDRESS_LABELS_TEMPLATE);
        multipleDetails.getCaseData().setCorrespondenceType(correspondenceType);
        addressLabelsAttributesType.setNumberOfSelectedLabels("0");
        multipleDetails.getCaseData().setAddressLabelsAttributesType(addressLabelsAttributesType);
        LabelsHelper.validateNumberOfSelectedLabels(multipleDetails.getCaseData(), errors);
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_LABELS_RESULT_SELECTION_ERROR, errors.get(0));
    }

    @Test
    public void midValidateAddressLabelsMultiple() {
        addressLabelsAttributesType.setNumberOfSelectedLabels("2");
        addressLabelsAttributesType.setNumberOfCopies("1");
        List<String> errors = LabelsHelper.midValidateAddressLabelsErrors(addressLabelsAttributesType, MULTIPLE_CASE_TYPE);
        assertEquals(0, errors.size());
    }

    @Test
    public void midValidateAddressLabelsMultipleErrors() {
        addressLabelsAttributesType.setNumberOfSelectedLabels("20000");
        addressLabelsAttributesType.setNumberOfCopies("3");
        List<String> errors = LabelsHelper.midValidateAddressLabelsErrors(addressLabelsAttributesType, MULTIPLE_CASE_TYPE);
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_LABELS_LABELS_LIMIT_ERROR + " of " + MAX_NUMBER_LABELS, errors.get(0));
    }

    @Test
    public void midValidateAddressLabelsSelectErrors() {
        addressLabelsAttributesType.setNumberOfSelectedLabels("0");
        List<String> errors = LabelsHelper.midValidateAddressLabelsErrors(addressLabelsAttributesType, SINGLE_CASE_TYPE);
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_LABELS_SELECT_ERROR, errors.get(0));
    }

    @Test
    public void midValidateAddressLabelsCopiesErrors() {
        addressLabelsAttributesType.setNumberOfSelectedLabels("2");
        addressLabelsAttributesType.setNumberOfCopies(".");
        List<String> errors = LabelsHelper.midValidateAddressLabelsErrors(addressLabelsAttributesType, SINGLE_CASE_TYPE);
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_LABELS_COPIES_ERROR, errors.get(0));
    }

    @Test
    public void midValidateAddressLabelsLimitNumberCopiesErrors() {
        addressLabelsAttributesType.setNumberOfSelectedLabels("2");
        addressLabelsAttributesType.setNumberOfCopies("11");
        List<String> errors = LabelsHelper.midValidateAddressLabelsErrors(addressLabelsAttributesType, SINGLE_CASE_TYPE);
        assertEquals(1, errors.size());
        assertEquals(ADDRESS_LABELS_COPIES_LESS_10_ERROR, errors.get(0));
    }
}
