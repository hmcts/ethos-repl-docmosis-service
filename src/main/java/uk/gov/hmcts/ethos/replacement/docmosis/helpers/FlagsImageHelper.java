package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

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

    public static void buildFlagsImageFileName(CaseData caseData) {
        StringBuilder flagsImageFileName = new StringBuilder();
        StringBuilder flagsImageAltText = new StringBuilder();

        flagsImageFileName.append(IMAGE_FILE_PRECEDING);
        setFlagImageFor(FLAG_WITH_OUTSTATION, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_DO_NOT_POSTPONE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_LIVE_APPEAL, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RULE_503B, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_REPORTING, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_SENSITIVE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RESERVED, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_ECC, flagsImageFileName, flagsImageAltText, caseData);
        flagsImageFileName.append(IMAGE_FILE_EXTENSION);

        caseData.setFlagsImageAltText(flagsImageAltText.toString());
        caseData.setFlagsImageFileName(flagsImageFileName.toString());
    }

    private static void setFlagImageFor(String flagName, StringBuilder flagsImageFileName, StringBuilder flagsImageAltText, CaseData caseData) {
        boolean flagRequired;
        String flagColor;

        switch (flagName) {
            case FLAG_WITH_OUTSTATION:
                flagRequired = withOutstation(caseData);
                flagColor = COLOR_DEEP_PINK;
                break;
            case FLAG_DO_NOT_POSTPONE:
                flagRequired = doNotPostpone(caseData);
                flagColor = COLOR_DARK_RED;
                break;
            case FLAG_LIVE_APPEAL:
                flagRequired = liveAppeal(caseData);
                flagColor = COLOR_GREEN;
                break;
            case FLAG_RULE_503B:
                flagRequired = rule503bApplies(caseData);
                flagColor = COLOR_RED;
                break;
            case FLAG_REPORTING:
                flagRequired = rule503dApplies(caseData);
                flagColor = COLOR_LIGHT_BLACK;
                break;
            case FLAG_SENSITIVE:
                flagRequired = sensitiveCase(caseData);
                flagColor = COLOR_ORANGE;
                break;
            case FLAG_RESERVED:
                flagRequired = reservedJudgement(caseData);
                flagColor = COLOR_PURPLE;
                break;
            case FLAG_ECC:
                flagRequired = counterClaimMade(caseData);
                flagColor = COLOR_OLIVE;
                break;
            default:
                flagRequired = false;
                flagColor = COLOR_WHITE;
        }
        flagsImageFileName.append(flagRequired ? ONE : ZERO);
        flagsImageAltText.append(flagRequired && flagsImageAltText.length() > 0 ? " - " : "");
        flagsImageAltText.append(flagRequired ? "<font color='" + flagColor + "' size='5'> " + flagName + " </font>" : "");
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

    private static boolean rule503dApplies(CaseData caseData) {
        if (caseData.getRestrictedReporting() != null) {
            if (!isNullOrEmpty(caseData.getRestrictedReporting().getImposed())) {
                return caseData.getRestrictedReporting().getImposed().equals(YES);
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    private static boolean rule503bApplies(CaseData caseData) {
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
                if (hearingTypeItem.getValue().getHearingDateCollection() != null && !hearingTypeItem.getValue().getHearingDateCollection().isEmpty()) {
                    for (DateListedTypeItem dateListedTypeItem : hearingTypeItem.getValue().getHearingDateCollection()) {
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
        return !isNullOrEmpty(caseData.getCounterClaim());
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
            if (!isNullOrEmpty(caseData.getAdditionalCaseInfoType().getDoNotPostpone() )) {
                return caseData.getAdditionalCaseInfoType().getDoNotPostpone().equals(YES);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean withOutstation(CaseData caseData) {
        return caseData.getManagingOffice() != null && !caseData.getManagingOffice().equals(GLASGOW_OFFICE);
    }

}
