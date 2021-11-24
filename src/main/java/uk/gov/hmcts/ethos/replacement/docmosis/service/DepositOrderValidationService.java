package uk.gov.hmcts.ethos.replacement.docmosis.service;

import org.apache.commons.collections4.CollectionUtils;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DepositTypeItem;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMANT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RESPONDENT_TITLE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.UNABLE_TO_FIND_PARTY;

public class DepositOrderValidationService {

    private DepositOrderValidationService() {
    }

    public static List<String> validateDepositOrder(CaseData caseData) {
        List<String> errors = new ArrayList<>();
        if (!CollectionUtils.isEmpty(caseData.getDepositCollection())) {

            for (DepositTypeItem depositTypeItem : caseData.getDepositCollection()) {
                if (!isNullOrEmpty(depositTypeItem.getValue().getDepositAmountRefunded())
                        && (isNullOrEmpty(depositTypeItem.getValue().getDepositAmount())
                        || Integer.parseInt(depositTypeItem.getValue().getDepositAmountRefunded())
                        > Integer.parseInt(depositTypeItem.getValue().getDepositAmount()))) {
                    errors.add(DEPOSIT_REFUNDED_GREATER_DEPOSIT_ERROR);
                }

                validateDepositOrderAgainst(errors, depositTypeItem);
                validateDepositRequestedBy(errors, depositTypeItem);
                validateDepositRefunded(errors, depositTypeItem);
            }
        }
        return errors;
    }

    public static void validateDepositRefunded(List<String> errors, DepositTypeItem depositTypeItem) {
        if (!isNullOrEmpty(depositTypeItem.getValue().getDepositRefund())
                && depositTypeItem.getValue().getDynamicDepositRefundedTo() != null) {
            var dynamicRefundedTo = depositTypeItem.getValue().getDynamicDepositRefundedTo()
                    .getValue().getCode();
            if (dynamicRefundedTo.startsWith("R: ")) {
                depositTypeItem.getValue().setDepositRefundedTo(RESPONDENT_TITLE);
            } else if (dynamicRefundedTo.startsWith("C: ")) {
                depositTypeItem.getValue().setDepositRefundedTo(CLAIMANT_TITLE);
            } else {
                errors.add(UNABLE_TO_FIND_PARTY);
            }
        }
    }

    public static void validateDepositRequestedBy(List<String> errors, DepositTypeItem depositTypeItem) {
        if (depositTypeItem.getValue().getDynamicDepositRequestedBy() != null) {
            var dynamicRequestedBy = depositTypeItem.getValue().getDynamicDepositRequestedBy()
                    .getValue().getCode();
            if (dynamicRequestedBy.startsWith("R:")) {
                depositTypeItem.getValue().setDepositRequestedBy(RESPONDENT_TITLE);
            } else if (dynamicRequestedBy.startsWith("C:")) {
                depositTypeItem.getValue().setDepositRequestedBy(CLAIMANT_TITLE);
            } else if (dynamicRequestedBy.equals("Tribunal")) {
                depositTypeItem.getValue().setDepositRequestedBy("Tribunal");
            } else {
                errors.add(UNABLE_TO_FIND_PARTY);
            }
        }
    }

    public static void validateDepositOrderAgainst(List<String> errors, DepositTypeItem depositTypeItem) {
        if (depositTypeItem.getValue().getDynamicDepositOrderAgainst() != null) {
            var dynamicOrderAgainst = depositTypeItem.getValue().getDynamicDepositOrderAgainst()
                    .getValue().getCode();
            if (dynamicOrderAgainst.startsWith("R:")) {
                depositTypeItem.getValue().setDepositOrderAgainst(RESPONDENT_TITLE);
            } else if (dynamicOrderAgainst.startsWith("C:")) {
                depositTypeItem.getValue().setDepositOrderAgainst(CLAIMANT_TITLE);
            } else {
                errors.add(UNABLE_TO_FIND_PARTY);
            }
        }
    }
}
