package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings.allocatehearing;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.tribunaloffice.TribunalOffice;
import uk.gov.hmcts.ethos.replacement.docmosis.service.referencedata.VenueService;

import java.util.List;
import java.util.UUID;

@Service
public class VenueSelectionService {
    private final VenueService venueService;

    public VenueSelectionService(VenueService venueService) {
        this.venueService = venueService;
    }

    public void initHearingCollection(CaseData caseData) {
        var venues = venueService.getVenues(TribunalOffice.valueOf(caseData.getOwningOffice()));
        if (CollectionUtils.isEmpty(caseData.getHearingCollection())) {
            var hearingTypeItem = new HearingTypeItem();
            hearingTypeItem.setId(UUID.randomUUID().toString());
            caseData.setHearingCollection(List.of(hearingTypeItem));

            var hearingType = new HearingType();
            var dynamicFixedListType = new DynamicFixedListType();
            dynamicFixedListType.setListItems(venues);
            hearingType.setHearingVenue(dynamicFixedListType);
            hearingTypeItem.setValue(hearingType);
        } else {
            for (var hearingItemType : caseData.getHearingCollection()) {
                var hearingType = hearingItemType.getValue();
                var dynamicFixedListType = hearingType.getHearingVenue();
                if (dynamicFixedListType == null) {
                    dynamicFixedListType = new DynamicFixedListType();
                    hearingType.setHearingVenue(dynamicFixedListType);
                }
                dynamicFixedListType.setListItems(venues);
            }
        }
    }

    public DynamicFixedListType createVenueSelection(CaseData caseData, DateListedType selectedListing) {
        var dynamicFixedListType = new DynamicFixedListType();
        dynamicFixedListType.setListItems(venueService.getVenues(TribunalOffice.valueOf(caseData.getOwningOffice())));

        if (selectedListing.hasHearingVenue()) {
            dynamicFixedListType.setValue(selectedListing.getHearingVenueDay().getValue());
        }

        return dynamicFixedListType;
    }
}
