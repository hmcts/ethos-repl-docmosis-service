package uk.gov.hmcts.ethos.replacement.docmosis.service.excel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelsAttributesType;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.LabelsHelper;

import java.util.List;

@Slf4j
@Service("multipleDocGenerationService")
public class MultipleDocGenerationService {

    private final MultipleLetterService multipleLetterService;

    @Autowired
    public MultipleDocGenerationService(MultipleLetterService multipleLetterService) {
        this.multipleLetterService = multipleLetterService;
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
