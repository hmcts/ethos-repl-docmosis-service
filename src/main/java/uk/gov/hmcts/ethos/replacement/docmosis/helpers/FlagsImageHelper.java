package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_DIGITAL_FILE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_DO_NOT_POSTPONE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_ECC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_LIVE_APPEAL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_REPORTING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_RESERVED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_SENSITIVE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FLAG_WITH_OUTSTATION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IMAGE_FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IMAGE_FILE_PRECEDING;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ONE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ZERO;

@Slf4j
public class FlagsImageHelper {

    private static final String COLOR_ORANGE = "Orange";
    private static final String COLOR_LIGHT_BLACK = "LightBlack";
    private static final String COLOR_RED = "Red";
    private static final String COLOR_PURPLE = "Purple";
    private static final String COLOR_OLIVE = "Olive";
    private static final String COLOR_GREEN = "Green";
    private static final String COLOR_DARK_RED = "DarkRed";
    private static final String COLOR_WHITE = "White";
    private static final String COLOR_DEEP_PINK = "DeepPink";
    private static final String COLOR_SLATE_GRAY = "SlateGray";
    private static final String COLOR_DARK_SLATE_BLUE = "DarkSlateBlue";
    private static final String FLAG_REASONABLE_ADJUSTMENT = "REASONABLE ADJUSTMENT";
    private static final String FLAG_RULE_493B = "RULE 49(3)b";
    private static final String SPEAK_TO_VP = "SPEAK TO VP";
    private static final String SPEAK_TO_REJ = "SPEAK TO REJ";
    private static final String RESERVED_TO_JUDGE = "RESERVED TO JUDGE";

    private static final List<String> FLAGS = List.of(FLAG_WITH_OUTSTATION,
            FLAG_DO_NOT_POSTPONE, FLAG_LIVE_APPEAL, FLAG_RULE_493B, FLAG_REPORTING, FLAG_SENSITIVE, FLAG_RESERVED,
            FLAG_ECC, FLAG_DIGITAL_FILE, FLAG_REASONABLE_ADJUSTMENT, SPEAK_TO_VP, SPEAK_TO_REJ, RESERVED_TO_JUDGE);

    private FlagsImageHelper() {
    }

    public static void buildFlagsImageFileName(CaseData caseData, String caseTypeId) {
        var flagsImageFileName = new StringBuilder();
        var flagsImageAltText = new StringBuilder();

        flagsImageFileName.append(IMAGE_FILE_PRECEDING);
        FLAGS.forEach(flag -> setFlagImageFor(flag, flagsImageFileName, flagsImageAltText, caseData, caseTypeId));
        flagsImageFileName.append(IMAGE_FILE_EXTENSION);

        caseData.setFlagsImageAltText(flagsImageAltText.toString());
        caseData.setFlagsImageFileName(flagsImageFileName.toString());
    }

    private static void setFlagImageFor(String flagName, StringBuilder flagsImageFileName,
                                        StringBuilder flagsImageAltText, CaseData caseData, String caseTypeId) {
        boolean flagRequired;
        String flagColor = switch (flagName) {
            case FLAG_WITH_OUTSTATION -> {
                flagRequired = withOutstation(caseData);
                yield COLOR_DEEP_PINK;
            }
            case FLAG_DO_NOT_POSTPONE -> {
                flagRequired = doNotPostpone(caseData);
                yield COLOR_DARK_RED;
            }
            case FLAG_LIVE_APPEAL -> {
                flagRequired = liveAppeal(caseData);
                yield COLOR_GREEN;
            }
            case FLAG_RULE_493B -> {
                flagRequired = rule493bApplies(caseData);
                yield COLOR_RED;
            }
            case FLAG_REPORTING -> {
                flagRequired = rule493dApplies(caseData);
                yield COLOR_LIGHT_BLACK;
            }
            case FLAG_SENSITIVE -> {
                flagRequired = sensitiveCase(caseData);
                yield COLOR_ORANGE;
            }
            case FLAG_RESERVED -> {
                flagRequired = reservedJudgement(caseData);
                yield COLOR_PURPLE;
            }
            case FLAG_ECC -> {
                flagRequired = counterClaimMade(caseData);
                yield COLOR_OLIVE;
            }
            case FLAG_DIGITAL_FILE -> {
                flagRequired = digitalFile(caseData);
                yield COLOR_SLATE_GRAY;
            }
            case FLAG_REASONABLE_ADJUSTMENT -> {
                flagRequired = reasonableAdjustment(caseData);
                yield COLOR_DARK_SLATE_BLUE;
            }
            case SPEAK_TO_VP -> {
                flagRequired = speakToVp(caseTypeId, caseData);
                yield  "#1D70B8";
            }
            case SPEAK_TO_REJ -> {
                flagRequired = speakToRej(caseTypeId, caseData);
                yield "#1D70B8";
            }
            case RESERVED_TO_JUDGE -> {
                flagRequired = reservedToJudge(caseData);
                yield  "#85994b";
            }
            default -> {
                flagRequired = false;
                yield COLOR_WHITE;
            }
        };

        flagsImageFileName.append(flagRequired ? ONE : ZERO);
        flagsImageAltText.append(flagRequired && !flagsImageAltText.isEmpty() ? "<font size='5'> - </font>" : "");
        flagsImageAltText.append(flagRequired ? "<font color='"
                + flagColor + "' size='5'> " + flagName + " </font>" : "");
    }

    private static boolean reservedToJudge(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (isNullOrEmpty(caseData.getAdditionalCaseInfoType().getReservedToJudge())) {
                return false;
            } else {
                return YES.equals(caseData.getAdditionalCaseInfoType().getReservedToJudge());
            }
        } else {
            return false;
        }
    }

    private static boolean sensitiveCase(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getAdditionalSensitive())) {
                return caseData.getAdditionalCaseInfoType().getAdditionalSensitive().equals(YES);
            } else {
                return  false;
            }
        } else {
            return  false;
        }
    }

    private static boolean rule493dApplies(CaseData caseData) {
        if (caseData.getRestrictedReporting() != null) {
            if (!isNullOrEmpty(caseData.getRestrictedReporting().getImposed())) {
                return caseData.getRestrictedReporting().getImposed().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean rule493bApplies(CaseData caseData) {
        // Note: rule494b was previously rule503b which is why the field is named as such
        if (caseData.getRestrictedReporting() != null) {
            if (!isNullOrEmpty(caseData.getRestrictedReporting().getRule503b())) {
                return caseData.getRestrictedReporting().getRule503b().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean reservedJudgement(CaseData caseData) {
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            for (HearingTypeItem hearingTypeItem : caseData.getHearingCollection()) {
                if (hearingTypeItem.getValue().getHearingDateCollection() != null
                        && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty()) {
                    for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue()
                            .getHearingDateCollection()) {
                        String hearingReservedJudgement = dateListedTypeItem.getValue().getHearingReservedJudgement();
                        if (!isNullOrEmpty(hearingReservedJudgement) && hearingReservedJudgement.equals(YES))  {
                            return true;
                        }
                    }
                }
            }
        } else {
            return false;
        }
        return false;
    }

    private static boolean counterClaimMade(CaseData caseData) {
        return !isNullOrEmpty(caseData.getCounterClaim())
                || (caseData.getEccCases() != null
                && !caseData.getEccCases().isEmpty());
    }

    private static boolean liveAppeal(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getAdditionalLiveAppeal())) {
                return caseData.getAdditionalCaseInfoType().getAdditionalLiveAppeal().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean doNotPostpone(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getDoNotPostpone())) {
                return caseData.getAdditionalCaseInfoType().getDoNotPostpone().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean reasonableAdjustment(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getReasonableAdjustment())) {
                return caseData.getAdditionalCaseInfoType().getReasonableAdjustment().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean digitalFile(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            return YES.equals(caseData.getAdditionalCaseInfoType().getDigitalFile());
        } else {
            return false;
        }
    }

    private static boolean withOutstation(CaseData caseData) {
        return !isNullOrEmpty(caseData.getManagingOffice()) && !caseData.getManagingOffice().equals(GLASGOW_OFFICE);
    }

    private static boolean speakToRej(String caseTypeId, CaseData caseData) {
        if (SCOTLAND_CASE_TYPE_ID.equals(caseTypeId)) {
            return false;
        }

        return isInterventionRequired(caseData);
    }

    private static boolean speakToVp(String caseTypeId, CaseData caseData) {
        if (!SCOTLAND_CASE_TYPE_ID.equals(caseTypeId)) {
            return false;
        }
        return isInterventionRequired(caseData);
    }

    private static boolean isInterventionRequired(CaseData caseData) {
        if (caseData.getAdditionalCaseInfoType() != null) {
            if (isNullOrEmpty(caseData.getAdditionalCaseInfoType().getInterventionRequired())) {
                return false;
            } else {
                return YES.equals(caseData.getAdditionalCaseInfoType().getInterventionRequired());
            }
        } else {
            return false;
        }
    }
}
