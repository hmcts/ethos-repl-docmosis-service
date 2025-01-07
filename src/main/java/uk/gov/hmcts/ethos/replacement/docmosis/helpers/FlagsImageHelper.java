package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;

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

    private FlagsImageHelper() {
    }

    public static void buildFlagsImageFileName(CaseData caseData) {
        var flagsImageFileName = new StringBuilder();
        var flagsImageAltText = new StringBuilder();

        flagsImageFileName.append(IMAGE_FILE_PRECEDING);
        setFlagImageFor(FLAG_WITH_OUTSTATION, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_DO_NOT_POSTPONE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_LIVE_APPEAL, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RULE_493B, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_REPORTING, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_SENSITIVE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_RESERVED, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_ECC, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_DIGITAL_FILE, flagsImageFileName, flagsImageAltText, caseData);
        setFlagImageFor(FLAG_REASONABLE_ADJUSTMENT, flagsImageFileName, flagsImageAltText, caseData);
        flagsImageFileName.append(IMAGE_FILE_EXTENSION);

        caseData.setFlagsImageAltText(flagsImageAltText.toString());
        caseData.setFlagsImageFileName(flagsImageFileName.toString());
    }

    private static void setFlagImageFor(String flagName, StringBuilder flagsImageFileName,
                                        StringBuilder flagsImageAltText, CaseData caseData) {
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
            case FLAG_RULE_493B:
                flagRequired = rule493bApplies(caseData);
                flagColor = COLOR_RED;
                break;
            case FLAG_REPORTING:
                flagRequired = rule493dApplies(caseData);
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
            case FLAG_DIGITAL_FILE:
                flagRequired = digitalFile(caseData);
                flagColor = COLOR_SLATE_GRAY;
                break;
            case FLAG_REASONABLE_ADJUSTMENT:
                flagRequired = reasonableAdjustment(caseData);
                flagColor = COLOR_DARK_SLATE_BLUE;
                break;
            default:
                flagRequired = false;
                flagColor = COLOR_WHITE;
        }
        flagsImageFileName.append(flagRequired ? ONE : ZERO);
        flagsImageAltText.append(flagRequired && flagsImageAltText.length() > 0 ? "<font size='5'> - </font>" : "");
        flagsImageAltText.append(flagRequired ? "<font color='"
                + flagColor + "' size='5'> " + flagName + " </font>" : "");
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

}
