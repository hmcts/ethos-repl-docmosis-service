package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Helper {

    private static DateTimeFormatter OLD_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter NEW_PATTERN_DATE = DateTimeFormatter.ofPattern("E, d MMM yyyy");
    //private static DateTimeFormatter NEW_PATTERN_TIME = DateTimeFormatter.ofPattern("hh:mm a");
    private static String NEW_LINE = "\",\n";
    public static final String OUTPUT_FILE_NAME = "document.docx";

    private static String formatLocalDate(String date) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, OLD_PATTERN).format(NEW_PATTERN_DATE) : "";
    }

    static String formatCurrentDatePlusDays(LocalDate date, long days) {
        return !isNullOrEmpty(date.toString()) ? date.plusDays(days).format(NEW_PATTERN_DATE) : "";
    }

    static String formatCurrentDate(LocalDate date) {
        return !isNullOrEmpty(date.toString()) ? date.format(NEW_PATTERN_DATE) : "";
    }

//    private static String formatLocalDateTimeToDate(String date) {
//        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(NEW_PATTERN_DATE) : "";
//    }
//
//    private static String formatLocalDateTimeToTime(String date) {
//        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(NEW_PATTERN_TIME) : "";
//    }

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
        sb.append(getCourtData(caseData));

        //Add judge_surname and app_date curr_date should be now!!!
        sb.append("\"Clerk\":\"").append(caseData.getClerkResponsible()).append(NEW_LINE);
        //sb.append("\"app_date\":\"").append(formatLocalDate(caseData.getReceiptDate())).append(NEW_LINE);
        sb.append("\"TODAY_DATE\":\"").append(formatCurrentDate(LocalDate.now())).append(NEW_LINE);
        //sb.append("\"todayPlus28Days\":\"").append(formatCurrentDatePlusDays(LocalDate.now(), 28)).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(caseDetails.getCaseId()).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }

    private static StringBuilder getClaimantData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        if (caseData.getIfCRepresented() != null) {
            if (caseData.getIfCRepresented().equals("Yes")) {
                RepresentedType representedType = caseData.getCRepresentedType();
                sb.append("\"claimant_full_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
                sb.append("\"claimant_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
                sb.append("\"claimant_email_address\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
                sb.append("\"representative_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
            } else {
                ClaimantType claimantType = caseData.getClaimantType();
                ClaimantIndType claimantIndType = caseData.getClaimantIndType();
                sb.append("\"claimant_full_name\":\"").append(claimantIndType.claimantFullName()).append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(claimantIndType.claimantFullName()).append(NEW_LINE);
                sb.append("\"claimant_addressUK\":\"").append(claimantType.getClaimantAddressUK()).append(NEW_LINE);
                sb.append("\"claimant_email_address\":\"").append(claimantType.getClaimantEmailAddress()).append(NEW_LINE);
            }
        }
        return sb;
    }

    private static StringBuilder getRespondentData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        if (caseData.getIfRRepresented() != null && caseData.getIfRRepresented().equals("Yes")) {
            RepresentedType representedType = caseData.getRRepresentedType();
            sb.append("\"Respondent\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
        } else {
            Optional<RespondentSumType> respondentType = Optional.ofNullable(caseData.getRespondentSumType());
            if (respondentType.isPresent()) {
                sb.append("\"Respondent\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_addressUK\":\"").append(respondentType.get().getRespondentAddress()).append(NEW_LINE);
            }
        }
        if (caseData.getRespondentCollection() != null && caseData.getRespondentCollection().size() > 0) {
            List<String> respOthers = new ArrayList<>();
            for (RespondentSumTypeItem respondentSumTypeItem : caseData.getRespondentCollection()) {
                respOthers.add(respondentSumTypeItem.getValue().getRespondentName());
            }
            sb.append("\"resp_others\":\"").append(String.join(", ", respOthers)).append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getHearingData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        if (caseData.getHearingCollection() != null && caseData.getHearingCollection().size() > 0) {
            HearingType hearingType = caseData.getHearingCollection().get(0).getValue();
            sb.append("\"hearing_date\":\"").append(formatLocalDate(hearingType.getHearingDate())).append(NEW_LINE);
            sb.append("\"hearing_time\":\"").append("11:00 AM").append(NEW_LINE);
            sb.append("\"hearing_venue\":\"").append("Manchester").append(NEW_LINE);
            //sb.append("\"hearing_time\":\"").append(formatLocalDateTimeToTime(hearingType.getHearingDate())).append(NEW_LINE);
            //sb.append("\"hearing_venue\":\"").append(hearingType.getEstHearing().).append(NEW_LINE);
            if (hearingType.getEstHearing() != null) {
                sb.append("\"EstLengthOfHearing\":\"").append(hearingType.getEstHearing().getEstHearingLengthNumber()).append(NEW_LINE);
            }
        }
        return sb;
    }

    public static String getDocumentName(CaseData caseData) {
        return getTemplateName(caseData) + "_" + getSectionName(caseData);
    }

    private static String getTemplateName(CaseData caseData) {
        Optional<CorrespondenceType> correspondenceType = Optional.ofNullable(caseData.getCorrespondenceType());
        if (correspondenceType.isPresent()) {
            return correspondenceType.get().getTopLevelDocuments();
        } else {
            return "";
        }
    }

    private static String getSectionName(CaseData caseData) {
        String sectionName = "";
        Optional<CorrespondenceType> correspondenceType = Optional.ofNullable(caseData.getCorrespondenceType());
        if (correspondenceType.isPresent()) {
            if (correspondenceType.get().getPart1Documents() != null) sectionName = correspondenceType.get().getPart1Documents();
            if (correspondenceType.get().getPart2Documents() != null) sectionName = correspondenceType.get().getPart2Documents();
            if (correspondenceType.get().getPart3Documents() != null) sectionName = correspondenceType.get().getPart3Documents();
            if (correspondenceType.get().getPart4Documents() != null) sectionName = correspondenceType.get().getPart4Documents();
            if (correspondenceType.get().getPart5Documents() != null) sectionName = correspondenceType.get().getPart5Documents();
            if (correspondenceType.get().getPart6Documents() != null) sectionName = correspondenceType.get().getPart6Documents();
            if (correspondenceType.get().getPart7Documents() != null) sectionName = correspondenceType.get().getPart7Documents();
            if (correspondenceType.get().getPart8Documents() != null) sectionName = correspondenceType.get().getPart8Documents();
            if (correspondenceType.get().getPart9Documents() != null) sectionName = correspondenceType.get().getPart9Documents();
            if (correspondenceType.get().getPart10Documents() != null) sectionName = correspondenceType.get().getPart10Documents();
            if (correspondenceType.get().getPart11Documents() != null) sectionName = correspondenceType.get().getPart11Documents();
            if (correspondenceType.get().getPart12Documents() != null) sectionName = correspondenceType.get().getPart12Documents();
            if (correspondenceType.get().getPart13Documents() != null) sectionName = correspondenceType.get().getPart13Documents();
            if (correspondenceType.get().getPart14Documents() != null) sectionName = correspondenceType.get().getPart14Documents();
            if (correspondenceType.get().getPart15Documents() != null) sectionName = correspondenceType.get().getPart15Documents();
            if (correspondenceType.get().getPart16Documents() != null) sectionName = correspondenceType.get().getPart16Documents();
            if (correspondenceType.get().getPart17Documents() != null) sectionName = correspondenceType.get().getPart17Documents();
        }
        return sectionName;
    }

    private static StringBuilder getCorrespondenceData(CaseData caseData) {
        String sectionName = getSectionName(caseData);
        StringBuilder sb = new StringBuilder();
        if (!sectionName.equals("")) {
            sb.append("\"").append("t").append(sectionName.replace(".", "_")).append("\":\"").append("true").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getCourtData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"Court_Address\":\"").append("13th floor, Centre City Tower, 5-7 Hill Street, Manchester, M5 4UU").append(NEW_LINE);
        sb.append("\"Court_Telephone\":\"").append("0121 600 7780").append(NEW_LINE);
        sb.append("\"Court_Fax\":\"").append("01264 347 999").append(NEW_LINE);
        sb.append("\"Court_DX\":\"").append("123456789").append(NEW_LINE);
        sb.append("\"Court_Email\":\"").append("ManchesterOfficeET@hmcts.gov.uk").append(NEW_LINE);
        return sb;
    }
}
