package uk.gov.hmcts.ethos.replacement.docmosis.reports.bfaction;

import java.time.LocalDate;
import java.util.Comparator;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_PATTERN;
import uk.gov.hmcts.ecm.common.model.listing.items.BFDateTypeItem;

public class BFDateTypeItemComparator implements Comparator<BFDateTypeItem> {
    @Override
    public int compare(BFDateTypeItem firstItem, BFDateTypeItem secondItem) {
        return LocalDate.parse(firstItem.getValue().getBroughtForwardDate(), NEW_DATE_PATTERN)
                .compareTo(LocalDate.parse(secondItem.getValue().getBroughtForwardDate(), NEW_DATE_PATTERN));
    }
}