package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.helpers.DynamicListHelper;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;

/**
 * Service for handling the 'Allocate Hearing' functionality.
 */
@Service
public class AllocateHearingService {

    /**
     * Initializes the hearing selection dropdown for the Allocate Hearing event.
     * It creates a dynamic list of all hearings associated with the case.
     *
     * @param caseData The case data containing the list of hearings.
     */
    public void initialiseAllocateHearing(CaseData caseData) {
        List<DynamicValueType> dynamicHearingList = DynamicListHelper.createDynamicHearingList(caseData);
        DynamicFixedListType dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(dynamicHearingList);
        caseData.setSelectedHearingNumberForUpdate(dynamicFixedListType);
    }

    /**
     * Populates the hearing details for the selected hearing.
     * This is used to display the details of the hearing that the user has chosen from the dynamic list.
     *
     * @param caseData The case data containing the user's selection.
     * @throws NullPointerException if no hearing has been selected.
     */
    public void populateHearingDetails(CaseData caseData) {
        if (caseData.getSelectedHearingNumberForUpdate() == null) {
            throw new NullPointerException("selectedHearingNumberForUpdate cannot be null for case "
                                           + caseData.getEthosCaseReference());
        }

        Optional<HearingTypeItem> hearingDetails = caseData.getHearingCollection().stream()
            .filter(hearingTypeItem -> hearingTypeItem.getValue().getHearingNumber()
                .equals(caseData.getSelectedHearingNumberForUpdate().getValue().getCode()))
            .findFirst();

        // Set the found hearing into a dedicated collection for the UI to render.
        hearingDetails.ifPresent(hearingTypeItem -> caseData.setHearingsCollectionForUpdate(List.of(hearingTypeItem)));

        if (isNotEmpty(caseData.getHearingsCollectionForUpdate())) {
            caseData.getHearingsCollectionForUpdate().stream()
                .filter(hearingTypeItem ->
                    ObjectUtils.isNotEmpty(hearingTypeItem.getValue().getHearingNotesDocument()))
                .forEach(hearingTypeItem -> hearingTypeItem.getValue().setDoesHearingNotesDocExist(YES));
        }
    }
}
