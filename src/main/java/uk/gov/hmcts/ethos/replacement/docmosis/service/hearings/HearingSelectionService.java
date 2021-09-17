package uk.gov.hmcts.ethos.replacement.docmosis.service.hearings;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicFixedListType;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;

import java.util.ArrayList;
import java.util.List;

@Service
public class HearingSelectionService {

    public List<DynamicValueType> getHearingSelection(CaseData caseData) {
        var values = new ArrayList<DynamicValueType>();

        for (var hearing : caseData.getHearingCollection()) {
            for (var listing : hearing.getValue().getHearingDateCollection()) {
                var code = listing.getId();

                String  date = UtilHelper.formatLocalDateTime(listing.getValue().getListedDate());
                var label = String.format("Hearing %s, %s", hearing.getValue().getHearingNumber(), date);
                values.add(DynamicValueType.create(code, label));
            }
        }

        return values;
    }

    public HearingType getSelectedHearing(CaseData caseData, DynamicFixedListType dynamicFixedListType) {
        var id = dynamicFixedListType.getValue().getCode();
        for (var hearing : caseData.getHearingCollection()) {
            for (var listing : hearing.getValue().getHearingDateCollection()) {
                if (listing.getId().equals(id)) {
                    return hearing.getValue();
                }
            }
        }

        throw new IllegalStateException(String.format("Selected hearing %s not found in case %s",
                dynamicFixedListType.getValue().getLabel(), caseData.getEthosCaseReference()));
    }

    public DateListedType getSelectedListing(CaseData caseData, DynamicFixedListType dynamicFixedListType) {
        var id = dynamicFixedListType.getValue().getCode();
        for (var hearing : caseData.getHearingCollection()) {
            for (var listing : hearing.getValue().getHearingDateCollection()) {
                if (listing.getId().equals(id)) {
                    return listing.getValue();
                }
            }
        }

        throw new IllegalStateException(String.format("Selected listing %s not found in case %s",
                dynamicFixedListType.getValue().getLabel(), caseData.getEthosCaseReference()));
    }

}
