package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Helper {

    private static DateTimeFormatter OLD_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static DateTimeFormatter NEW_DATE_PATTERN = DateTimeFormatter.ofPattern("E, d MMM yyyy");
    private static DateTimeFormatter NEW_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss");
    //private static DateTimeFormatter NEW_PATTERN_TIME = DateTimeFormatter.ofPattern("hh:mm a");
    private static String NEW_LINE = "\",\n";
    public static final String OUTPUT_FILE_NAME = "document.docx";

    private static String formatLocalDate(String date) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, OLD_DATE_TIME_PATTERN).format(NEW_DATE_PATTERN) : "";
    }

    private static String formatLocalDateTime(String date) {
        return !isNullOrEmpty(date) ? LocalDateTime.parse(date, OLD_DATE_TIME_PATTERN).format(NEW_DATE_TIME_PATTERN) : "";
    }

    static String formatCurrentDatePlusDays(LocalDate date, long days) {
        return !isNullOrEmpty(date.toString()) ? date.plusDays(days).format(NEW_DATE_PATTERN) : "";
    }

    static String formatCurrentDate(LocalDate date) {
        return !isNullOrEmpty(date.toString()) ? date.format(NEW_DATE_PATTERN) : "";
    }

    public static StringBuilder buildDocumentContent(CaseDetails caseDetails, String accessKey) {
        String FILE_EXTENSION = ".docx";
        StringBuilder sb = new StringBuilder();
        CaseData caseData = caseDetails.getCaseData();
        String templateName = getTemplateName(caseData);

        // Start building the instruction
        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(FILE_EXTENSION).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        // Building the document data
        sb.append("\"data\":{\n");
        sb.append(getClaimantData(caseData));
        sb.append(getRespondentData(caseData));
        sb.append(getHearingData(caseData));
        sb.append(getCorrespondenceData(caseData));
        sb.append(getCorrespondenceScotData(caseData));
        sb.append(getCourtData());

        sb.append("\"i").append(getSectionName(caseData).replace(".", "_")).append("_enhmcts\":\"")
                .append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        sb.append("\"iScot").append(getScotSectionName(caseData).replace(".", "_")).append("_schmcts\":\"")
                .append("[userImage:").append("schmcts.png]").append(NEW_LINE);

        sb.append("\"Clerk\":\"").append(caseData.getClerkResponsible()).append(NEW_LINE);
        sb.append("\"TODAY_DATE\":\"").append(formatCurrentDate(LocalDate.now())).append(NEW_LINE);
        sb.append("\"TodayPlus28Days\":\"").append(formatCurrentDatePlusDays(LocalDate.now(), 28)).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(caseDetails.getCaseData().getEthosCaseReference()).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }

    private static StringBuilder getClaimantData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        RepresentedTypeC representedTypeC = caseData.getRepresentativeClaimantType();
        Optional<ClaimantIndType> claimantIndType = Optional.ofNullable(caseData.getClaimantIndType());
        if (representedTypeC != null) {
            sb.append("\"claimant_full_name\":\"").append(representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"claimant_rep_full_name\":\"").append(representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"Claimant_name\":\"").append(representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"claimant_addressUK\":\"").append(representedTypeC.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"claimant_rep_addressUK\":\"").append(representedTypeC.getRepresentativeAddress()).append(NEW_LINE);
            //sb.append("\"claimant_email_address\":\"").append(representedTypeC.getRepresentativeEmailAddress()).append(NEW_LINE);
            //sb.append("\"claimant_rep_email_address\":\"").append(representedTypeC.getRepresentativeEmailAddress()).append(NEW_LINE);
            sb.append("\"representative_reference\":\"").append(representedTypeC.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"claimant_rep_reference\":\"").append(representedTypeC.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"claimant_reference\":\"").append(representedTypeC.getRepresentativeReference()).append(NEW_LINE);
        } else {
            Optional<ClaimantType> claimantType = Optional.ofNullable(caseData.getClaimantType());
            if (claimantType.isPresent()) {
                sb.append("\"claimant_addressUK\":\"").append(claimantType.get().getClaimantAddressUK()).append(NEW_LINE);
                sb.append("\"claimant_rep_addressUK\":\"").append(claimantType.get().getClaimantAddressUK()).append(NEW_LINE);
                //sb.append("\"claimant_email_address\":\"").append(claimantType.get().getClaimantEmailAddress()).append(NEW_LINE);
            }
            if (claimantIndType.isPresent()) {
                sb.append("\"Claimant_name\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
                sb.append("\"claimant_full_name\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
                sb.append("\"claimant_rep_full_name\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
            }
        }
        claimantIndType.ifPresent(claimantIndType1 -> sb.append("\"Claimant\":\"").append(claimantIndType1.claimantFullName()).append(NEW_LINE));
        return sb;
    }

    private static StringBuilder getRespondentData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        RepresentedTypeR representedTypeR = caseData.getRepresentativeRespondentType();
        Optional<RespondentSumType> respondentType = Optional.ofNullable(caseData.getRespondentSumType());
        if (representedTypeR != null) {
            sb.append("\"Respondent_name\":\"").append(representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_full_name\":\"").append(representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_representative\":\"").append(representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_rep_full_name\":\"").append(representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_addressUK\":\"").append(representedTypeR.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"respondent_rep_addressUK\":\"").append(representedTypeR.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"respondent_reference\":\"").append(representedTypeR.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"respondent_rep_reference\":\"").append(representedTypeR.getRepresentativeReference()).append(NEW_LINE);
            //sb.append("\"respondent_email_address\":\"").append(representedTypeR.getRepresentativeEmailAddress()).append(NEW_LINE);
            //sb.append("\"respondent_rep_email_address\":\"").append(representedTypeR.getRepresentativeEmailAddress()).append(NEW_LINE);
        } else {
            if (respondentType.isPresent()) {
                sb.append("\"Respondent_name\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_full_name\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_rep_full_name\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_addressUK\":\"").append(respondentType.get().getRespondentAddress()).append(NEW_LINE);
                sb.append("\"respondent_rep_addressUK\":\"").append(respondentType.get().getRespondentAddress()).append(NEW_LINE);
            }
        }
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<String> respOthers = caseData.getRespondentCollection()
                    .stream()
                    .map(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName())
                    .collect(Collectors.toList());
            sb.append("\"resp_others\":\"").append(String.join(", ", respOthers)).append(NEW_LINE);
        }
        respondentType.ifPresent(respondentSumType -> sb.append("\"Respondent\":\"").append(respondentSumType.getRespondentName()).append(NEW_LINE));
        return sb;
    }

    private static StringBuilder getHearingData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        //Currently checking collection not the HearingType
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            HearingType hearingType = caseData.getHearingCollection().get(0).getValue();
            sb.append("\"hearing_date\":\"").append(formatLocalDate(hearingType.getHearingDateStart())).append(NEW_LINE);
            sb.append("\"Hearing_Date\":\"").append(formatLocalDate(hearingType.getHearingDateStart())).append(NEW_LINE);
            sb.append("\"Hearing_date_time\":\"").append(formatLocalDateTime(hearingType.getHearingDateStart())).append(NEW_LINE);
            sb.append("\"Hearing_Date_Time\":\"").append(formatLocalDateTime(hearingType.getHearingDateStart())).append(NEW_LINE);
            sb.append("\"hearing_date_time\":\"").append(formatLocalDateTime(hearingType.getHearingDateStart())).append(NEW_LINE);
            sb.append("\"Hearing_venue\":\"").append(hearingType.getHearingVenue()).append(NEW_LINE);
            sb.append("\"hearing_address\":\"").append(hearingType.getHearingVenue()).append(NEW_LINE);
            sb.append("\"Hearing_Address\":\"").append(hearingType.getHearingVenue()).append(NEW_LINE);
            if (hearingType.getEstHearing() != null) {
                sb.append("\"EstLengthOfHearing\":\"").append(hearingType.getEstHearing()).append(NEW_LINE);
                sb.append("\"Hearing_Duration\":\"").append(hearingType.getEstHearing()).append(NEW_LINE);
                sb.append("\"hearing_duration\":\"").append(hearingType.getEstHearing()).append(NEW_LINE);
                sb.append("\"hearing_length\":\"").append(hearingType.getEstHearing()).append(NEW_LINE);
            }
        }
        return sb;
    }

    public static String getDocumentName(CaseData caseData) {
        String ewSection = getSectionName(caseData);
        String sectionName = ewSection.equals("") ? getScotSectionName(caseData) : ewSection;
        return getTemplateName(caseData) + "_" + sectionName;
    }

    private static String getTemplateName(CaseData caseData) {
        Optional<CorrespondenceType> correspondenceType = Optional.ofNullable(caseData.getCorrespondenceType());
        if (correspondenceType.isPresent()) {
            return correspondenceType.get().getTopLevelDocuments();
        } else {
            Optional<CorrespondenceScotType> correspondenceScotType = Optional.ofNullable(caseData.getCorrespondenceScotType());
            if (correspondenceScotType.isPresent()) {
                return correspondenceScotType.get().getTopLevelScotDocuments();
            } else {
                return "";
            }
        }
    }

    private static String getSectionName(CaseData caseData) {
        Optional<CorrespondenceType> correspondenceType = Optional.ofNullable(caseData.getCorrespondenceType());
        if (correspondenceType.isPresent()) {
            CorrespondenceType correspondence = correspondenceType.get();
            if (correspondence.getPart1Documents() != null) return correspondence.getPart1Documents();
            if (correspondence.getPart2Documents() != null) return correspondence.getPart2Documents();
            if (correspondence.getPart3Documents() != null) return correspondence.getPart3Documents();
            if (correspondence.getPart4Documents() != null) return correspondence.getPart4Documents();
            if (correspondence.getPart5Documents() != null) return correspondence.getPart5Documents();
            if (correspondence.getPart6Documents() != null) return correspondence.getPart6Documents();
            if (correspondence.getPart7Documents() != null) return correspondence.getPart7Documents();
            if (correspondence.getPart9Documents() != null) return correspondence.getPart9Documents();
            if (correspondence.getPart10Documents() != null) return correspondence.getPart10Documents();
            if (correspondence.getPart11Documents() != null) return correspondence.getPart11Documents();
            if (correspondence.getPart12Documents() != null) return correspondence.getPart12Documents();
            if (correspondence.getPart13Documents() != null) return correspondence.getPart13Documents();
            if (correspondence.getPart14Documents() != null) return correspondence.getPart14Documents();
            if (correspondence.getPart15Documents() != null) return correspondence.getPart15Documents();
            if (correspondence.getPart16Documents() != null) return correspondence.getPart16Documents();
            if (correspondence.getPart17Documents() != null) return correspondence.getPart17Documents();
        }
        return "";
    }

    private static String getScotSectionName(CaseData caseData) {
        Optional<CorrespondenceScotType> correspondenceScotTypeOptional = Optional.ofNullable(caseData.getCorrespondenceScotType());
        if (correspondenceScotTypeOptional.isPresent()) {
            CorrespondenceScotType correspondenceScotType = correspondenceScotTypeOptional.get();
            if (correspondenceScotType.getPart1ScotDocuments() != null) return correspondenceScotType.getPart1ScotDocuments();
            if (correspondenceScotType.getPart2ScotDocuments() != null) return correspondenceScotType.getPart2ScotDocuments();
            if (correspondenceScotType.getPart3ScotDocuments() != null) return correspondenceScotType.getPart3ScotDocuments();
            if (correspondenceScotType.getPart4ScotDocuments() != null) return correspondenceScotType.getPart4ScotDocuments();
            if (correspondenceScotType.getPart5ScotDocuments() != null) return correspondenceScotType.getPart5ScotDocuments();
            if (correspondenceScotType.getPart6ScotDocuments() != null) return correspondenceScotType.getPart6ScotDocuments();
            if (correspondenceScotType.getPart7ScotDocuments() != null) return correspondenceScotType.getPart7ScotDocuments();
            if (correspondenceScotType.getPart8ScotDocuments() != null) return correspondenceScotType.getPart8ScotDocuments();
            if (correspondenceScotType.getPart10ScotDocuments() != null) return correspondenceScotType.getPart10ScotDocuments();
            if (correspondenceScotType.getPart11ScotDocuments() != null) return correspondenceScotType.getPart11ScotDocuments();
            if (correspondenceScotType.getPart12ScotDocuments() != null) return correspondenceScotType.getPart12ScotDocuments();
            if (correspondenceScotType.getPart13ScotDocuments() != null) return correspondenceScotType.getPart13ScotDocuments();
            if (correspondenceScotType.getPart14ScotDocuments() != null) return correspondenceScotType.getPart14ScotDocuments();
            if (correspondenceScotType.getPart15ScotDocuments() != null) return correspondenceScotType.getPart15ScotDocuments();
            if (correspondenceScotType.getPart16ScotDocuments() != null) return correspondenceScotType.getPart16ScotDocuments();
        }
        return "";
    }

    private static StringBuilder getCorrespondenceData(CaseData caseData) {
        String sectionName = getSectionName(caseData);
        StringBuilder sb = new StringBuilder();
        if (!sectionName.equals("")) {
            sb.append("\"").append("t").append(sectionName.replace(".", "_")).append("\":\"").append("true").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getCorrespondenceScotData(CaseData caseData) {
        String scotSectionName = getScotSectionName(caseData);
        StringBuilder sb = new StringBuilder();
        if (!scotSectionName.equals("")) {
            sb.append("\"").append("t_Scot_").append(scotSectionName.replace(".", "_")).append("\":\"").append("true").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getCourtData() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"Court_Address\":\"").append("13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU").append(NEW_LINE);
        sb.append("\"Court_Telephone\":\"").append("0121 600 7780").append(NEW_LINE);
        sb.append("\"Court_Fax\":\"").append("01264 347 999").append(NEW_LINE);
        sb.append("\"Court_DX\":\"").append("123456789").append(NEW_LINE);
        sb.append("\"Court_Email\":\"").append("ManchesterOfficeET@hmcts.gov.uk").append(NEW_LINE);
        return sb;
    }
}
