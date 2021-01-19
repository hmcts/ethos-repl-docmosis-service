package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsAttributesType;
import uk.gov.hmcts.ecm.common.model.labels.LabelPayloadEvent;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper;

import java.util.ArrayList;
import java.util.List;

import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

@Slf4j
@Service("multipleDocGenerationService")
public class MultipleDocGenerationService {

    private final MultipleLetterService multipleLetterService;

    @Autowired
    public MultipleDocGenerationService(MultipleLetterService multipleLetterService) {
        this.multipleLetterService = multipleLetterService;
    }

    public List<AddressLabelTypeItem> customiseSelectedAddresses(List<LabelPayloadEvent> labelPayloadEvents, MultipleData multipleData) {

        if (multipleData.getAddressLabelsSelectionTypeMSL() != null && !multipleData.getAddressLabelsSelectionTypeMSL().isEmpty()) {

            return new ArrayList<>(getAddressLabelTypeItems(labelPayloadEvents, multipleData.getAddressLabelsSelectionTypeMSL()));

        } else {

            return null;

        }

    }

    private List<AddressLabelTypeItem> getAddressLabelTypeItems(List<LabelPayloadEvent> labelPayloadEvents,
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

    public void midSelectedAddressLabelsMultiple(String userToken, MultipleDetails multipleDetails, List<String> errors) {

        multipleDetails.getCaseData().setAddressLabelsAttributesType(new AddressLabelsAttributesType());

        log.info("Populate the address label collection");

        multipleLetterService.bulkLetterLogic(userToken, multipleDetails, errors, true);

        if (errors.isEmpty()) {

            List<AddressLabelTypeItem> selectedAddressLabels =
                    LabelsHelper.getSelectedAddressLabelsMultiple(multipleDetails.getCaseData());

            log.info("Adding number of selected labels");

            multipleDetails.getCaseData().getAddressLabelsAttributesType()
                    .setNumberOfSelectedLabels(String.valueOf(selectedAddressLabels.size()));

            log.info("Reset the address label collection for payload performance");

        }

        multipleDetails.getCaseData().setAddressLabelCollection(null);

    }

}
