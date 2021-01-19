package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeC;
import uk.gov.hmcts.ecm.common.model.ccd.types.RepresentedTypeR;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultipleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class MultipleDocGenerationServiceTest {

    @Mock
    private MultipleLetterService multipleLetterService;

    @InjectMocks
    private MultipleDocGenerationService multipleDocGenerationService;

    private MultipleDetails multipleDetails;
    private String userToken;
    private List<LabelPayloadEvent> labelPayloadEvents;
    private List<String> errors;

    @Before
    public void setUp() {
        multipleDetails = new MultipleDetails();
        multipleDetails.setCaseData(MultipleUtil.getMultipleData());
        multipleDetails.setCaseTypeId("Leeds_Multiple");
        labelPayloadEvents = MultipleUtil.getLabelPayloadEvents();
        userToken = "authString";
        errors = new ArrayList<>();
    }

    @Test
    public void customiseSelectedAddressesEmptySelectedAddresses() {
        assertNull(multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents,
                multipleDetails.getCaseData()));
    }

    @Test
    public void customiseSelectedAddresses() {
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(2,
                multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents, multipleDetails.getCaseData()).size());
    }

    @Test
    public void customiseSelectedAddressesClaimant() {
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantTypeOfClaimant(INDIVIDUAL_TYPE_CLAIMANT);
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantType(null);
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(2,
                multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents, multipleDetails.getCaseData()).size());
    }

    @Test
    public void customiseSelectedAddressesClaimantRep() {
        labelPayloadEvents.get(0).getLabelPayloadES().setClaimantRepresentedQuestion(YES);
        RepresentedTypeC representedTypeC = new RepresentedTypeC();
        representedTypeC.setNameOfRepresentative("Name");
        representedTypeC.setRepresentativeReference("1234");
        labelPayloadEvents.get(0).getLabelPayloadES().setRepresentativeClaimantType(representedTypeC);
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Arrays.asList(CLAIMANT_ADDRESS_LABEL, CLAIMANT_REP_ADDRESS_LABEL)));
        assertEquals(3,
                multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents, multipleDetails.getCaseData()).size());
    }

    @Test
    public void customiseSelectedAddressesRespondent() {
        multipleDetails.getCaseData().setAddressLabelsSelectionTypeMSL(
                new ArrayList<>(Collections.singletonList(RESPONDENTS_ADDRESS__LABEL)));
        assertEquals(2,
                multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents, multipleDetails.getCaseData()).size());
    }

    @Test
    public void customiseSelectedAddressesRespondentRep() {
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
                multipleDocGenerationService.customiseSelectedAddresses(labelPayloadEvents, multipleDetails.getCaseData()).size());
    }

    @Test
    public void midSelectedAddressLabelsMultiple() {
        multipleDetails.getCaseData().setAddressLabelCollection(MultipleUtil.getAddressLabelTypeItemList());
        multipleDocGenerationService.midSelectedAddressLabelsMultiple(userToken, multipleDetails, errors);
        verify(multipleLetterService, times(1)).bulkLetterLogic(
                userToken,
                multipleDetails,
                errors,
                true);
        verifyNoMoreInteractions(multipleLetterService);
        assertNull(multipleDetails.getCaseData().getAddressLabelCollection());
    }

}