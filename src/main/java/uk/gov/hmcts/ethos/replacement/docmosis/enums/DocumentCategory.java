package uk.gov.hmcts.ethos.replacement.docmosis.enums;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum DocumentCategory {

    STARTING_A_CLAIM("C1", "Starting a claim"),
    RESPONSE_TO_A_CLAIM("C2", "Response to a claim"),
    INITIAL_CONSIDERATION("C3", "Initial consideration"),
    CASE_MANAGEMENT("C4", "Case Management"),
    WITHDRAWAL_SETTLED("C5", "Withdrawal/Settled"),
    HEARINGS("C6", "Hearings"),
    JUDGMENT_AND_REASONS("C7", "Judgment and Reasons"),
    RECONSIDERATION("C8", "Reconsideration"),
    MISC("C9", "Misc"),
    ET1("C11", "ET1", "C1"),
    ET1_ATTACHMENT("C12", "ET1 Attachment", "C1"),
    ACAS_CERTIFICATE("C13", "ACAS Certificate", "C1"),
    NOTICE_OF_CLAIM("C14", "Notice of claim", "C1"),
    CLAIM_ACCEPTED("C15", "Claim accepted", "C1"),
    CLAIM_REJECTED("C16", "Claim rejected", "C1"),
    CLAIM_PART_REJECTED("C17", "Claim part rejected", "C1"),
    ET3("C18", "ET3", "C2"),
    ET3_ATTACHMENT("C19", "ET3 Attachment", "C2"),
    RESPONSE_ACCEPTED("C20", "Response accepted", "C2"),
    RESPONSE_REJECTED("C21", "Response rejected", "C2"),
    APP_TO_EXTEND_TIME_TO_PRESENT_A_RESPONSE("C22",
            "App to extend time to present a response", "C2"),
    INITIAL_CONSIDERATION_SUB("C23", "Initial Consideration", "C3"),
    RULE_27_NOTICE("C24", "Rule 27 Notice", "C3"),
    RULE_28_NOTICE("C25", "Rule 28 Notice", "C3"),
    TRIBUNAL_ORDER("C26", "Tribunal Order", "C4"),
    DEPOSIT_ORDER("C27", "Deposit Order", "C4"),
    UNLESS_ORDER("C28", "Unless Order", "C4"),
    TRIBUNAL_NOTICE("C29", "Tribunal Notice", "C4"),
    APP_TO_VARY_AN_ORDER_C("C30", "App to vary an order – C", "C4"),
    APP_TO_VARY_AN_ORDER_R("C31", "App to vary an order – R", "C4"),
    APP_TO_REVOKE_AN_ORDER_C("C32", "App to revoke an order- C", "C4"),
    APP_TO_REVOKE_AN_ORDER_R("C33", "App to revoke an order – R", "C4"),
    APP_TO_VARY_OR_REVOKE_AN_ORDER_C("C73", "App to vary or revoke an order - C", "C4"),
    APP_TO_VARY_OR_REVOKE_AN_ORDER_R("C74", "App to vary or revoke an order - R", "C4"),
    APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_C("C34",
            "App to extend time to comply to an order/directions – C", "C4"),
    APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_R("C35",
            "App to extend time to comply to an order/directions – R", "C4"),
    APP_TO_ORDER_THE_R_TO_DO_SOMETHING("C36", "App to Order the R to do something", "C4"),
    APP_TO_ORDER_THE_C_TO_DO_SOMETHING("C37", "App to Order the C to do something", "C4"),
    APP_TO_AMEND_CLAIM("C38", "App to amend claim", "C4"),
    APP_TO_AMEND_RESPONSE("C39", "App to amend response", "C4"),
    APP_FOR_A_WITNESS_ORDER_C("C40", "App for a Witness Order - C", "C4"),
    APP_FOR_A_WITNESS_ORDER_R("C75", "App for a Witness Order - R", "C4"),
    DISABILITY_IMPACT_STATEMENT("C41", "Disability Impact statement", "C4"),
    R_HAS_NOT_COMPLIED_WITH_AN_ORDER_C("C42", "R has not complied with an order - C", "C4"),
    C_HAS_NOT_COMPLIED_WITH_AN_ORDER_R("C43", "C has not complied with an order - R", "C4"),
    APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_CLAIM("C44",
            "App to Strike out all or part of the claim", "C4"),
    APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_RESPONSE("C45",
            "App to Strike out all or part of the response", "C4"),
    REFERRAL_JUDICIAL_DIRECTION("C46", "Referral/Judicial Direction", "C4"),
    CHANGE_OF_PARTY_DETAILS("C47", "Change of party's details", "C4"),
    WITHDRAWAL_OF_ENTIRE_CLAIM("C48", "Withdrawal of entire claim", "C5"),
    WITHDRAWAL_OF_PART_OF_CLAIM("C49", "Withdrawal of part of claim", "C5"),
    WITHDRAWAL_OF_ALL_OR_PART_CLAIM("C78", "Withdrawal of all or part of claim", "C5"),
    COT3("C50", "COT3", "C5"),
    APP_TO_RESTRICT_PUBLICITY_C("C51", "App to restrict publicity - C", "C6"),
    APP_TO_RESTRICT_PUBLICITY_R("C52", "App to restrict publicity - R", "C6"),
    ANONYMITY_ORDER("C53", "Anonymity Order", "C6"),
    NOTICE_OF_HEARING("C54", "Notice of Hearing", "C6"),
    APP_TO_POSTPONE_C("C55", "App to postpone - C", "C6"),
    APP_TO_POSTPONE_R("C56", "App to postpone - R", "C6"),
    HEARING_BUNDLE("C57", "Hearing Bundle", "C6"),
    SCHEDULE_OF_LOSS("C58", "Schedule of Loss", "C6"),
    COUNTER_SCHEDULE_OF_LOSS("C59", "Counter Schedule of Loss", "C6"),
    JUDGMENT("C60", "Judgment", "C7"),
    JUDGMENT_WITH_REASONS("C61", "Judgment with Reasons", "C7"),
    REASONS("C62", "Reasons", "C7"),
    EXTRACT_OF_JUDGMENT("C63", "Extract of Judgment", "C7"),
    APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_C("C64",
            "App to have a Legal Officer decision considered afresh - C", "C8"),
    APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_R("C65",
            "App to have a Legal Officer decision considered afresh - R", "C8"),
    APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_C("C66",
            "App for a judgment to be reconsidered - C", "C8"),
    APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_R("C67",
            "App for a judgment to be reconsidered - R", "C8"),
    CERTIFICATE_OF_CORRECTION("C68", "Certificate of Correction", "C9"),
    TRIBUNAL_CASE_FILE("C69", "Tribunal Case File", "C9"),
    OTHER("C70", "Other", "C9"),
    ET1_VETTING("C71", "ET1 Vetting", "C1"),
    ET3_PROCESSING("C72", "ET3 Processing", "C2"),
    CONTACT_THE_TRIBUNAL_C("C76", "Contact the tribunal about something else - C", "C4"),
    CONTACT_THE_TRIBUNAL_R("C77", "Contact the tribunal about something else - R", "C4");

    private final String id;
    private final String category;
    private final String parentId;

    DocumentCategory(String id, String category) {
        this.id = id;
        this.category = category;
        this.parentId = null;
    }

    DocumentCategory(String id, String category, String parentId) {
        this.id = id;
        this.category = category;
        this.parentId = parentId;
    }

    public String getParentCategory() {
        return DocumentCategory.valueOf(parentId).getCategory();
    }

    public static List<String> getSubCategoryNames(String parentId) {
        return Stream.of(DocumentCategory.values())
                .filter(documentCategory -> documentCategory.getParentId().equals(parentId))
                .map(DocumentCategory::getCategory).collect(Collectors.toList());
    }

    public static String getIdFromCategory(String category) {
        return Stream.of(DocumentCategory.values())
                .filter(documentCategory -> documentCategory.getCategory().equals(category))
                .map(DocumentCategory::getId)
                .findFirst()
                .orElse(null);
    }

}
