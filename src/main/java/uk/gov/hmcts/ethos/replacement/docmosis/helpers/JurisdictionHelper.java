package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.model.ccd.items.JurCodesTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.JurCodesType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JurisdictionHelper {

    private static final String JURISDICTION_OUTCOME_ACAS_CONCILIATED_SETTLEMENT = "Acas conciliated settlement";
    private static final String JURISDICTION_OUTCOME_WITHDRAWN_OR_PRIVATE_SETTLEMENT =
            "Withdrawn or private settlement";
    public static final String JURISDICTION_OUTCOME_INPUT_IN_ERROR = "Input in error";
    private static final String JURISDICTION_OUTCOME_DISMISSED_ON_WITHDRAWAL = "Dismissed on withdrawal";
    static final List<String> HIDE_JURISDICTION_OUTCOME = Arrays.asList(
            JURISDICTION_OUTCOME_ACAS_CONCILIATED_SETTLEMENT,
            JURISDICTION_OUTCOME_WITHDRAWN_OR_PRIVATE_SETTLEMENT,
            JURISDICTION_OUTCOME_INPUT_IN_ERROR,
            JURISDICTION_OUTCOME_DISMISSED_ON_WITHDRAWAL);

    private JurisdictionHelper() {
    }

    static String getJurCodesCollection(List<JurCodesTypeItem> jurCodesTypeItems) {
        if (jurCodesTypeItems != null) {
            return jurCodesTypeItems.stream()
                    .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                    .distinct()
                    .collect(Collectors.joining(", "));
        } else {
            return " ";
        }
    }

    static String getJurCodesCollectionWithHide(List<JurCodesTypeItem> jurCodesTypeItems) {
        if (CollectionUtils.isNotEmpty(jurCodesTypeItems)) {
            return StringUtils.defaultIfEmpty(
                    jurCodesTypeItems.stream()
                        .filter(jurCodesTypeItem ->
                                !HIDE_JURISDICTION_OUTCOME.contains(jurCodesTypeItem.getValue().getJudgmentOutcome()))
                        .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                        .distinct()
                        .collect(Collectors.joining(", ")),
                    " ");
        } else {
            return " ";
        }
    }

    private static List<String> getJurCodesValues(List<JurCodesTypeItem> jurCodesTypeItems) {
        return jurCodesTypeItems != null && !jurCodesTypeItems.isEmpty()
                ? jurCodesTypeItems.stream()
                .map(jurCodesTypeItem -> jurCodesTypeItem.getValue().getJuridictionCodesList())
                .distinct()
                .toList()
                : new ArrayList<>();
    }

    public static boolean containsAllJurCodes(List<JurCodesTypeItem> jurCodesTypeItems1,
                                              List<JurCodesTypeItem> jurCodesTypeItems2) {
        if (jurCodesTypeItems1 != null && !jurCodesTypeItems1.isEmpty()) {
            return getJurCodesValues(jurCodesTypeItems2).containsAll(getJurCodesValues(jurCodesTypeItems1));
        }
        return false;
    }

    public static List<JurCodesTypeItem> getJurCodesListFromString(String jurCodesStringList) {
        List<JurCodesTypeItem> jurCodesTypeItems = new ArrayList<>();
        if (jurCodesStringList != null && !jurCodesStringList.trim().isEmpty()) {
            List<String> codes = new ArrayList<>(Arrays.asList(jurCodesStringList.split(", ")));
            jurCodesTypeItems = codes.stream()
                    .map(code -> {
                        var jurCodesType = new JurCodesType();
                        jurCodesType.setJuridictionCodesList(code);
                        var jurCodesTypeItem = new JurCodesTypeItem();
                        jurCodesTypeItem.setValue(jurCodesType);
                        jurCodesTypeItem.setId(code);
                        return jurCodesTypeItem;
                    })
                    .toList();
        }
        return jurCodesTypeItems;
    }

}
