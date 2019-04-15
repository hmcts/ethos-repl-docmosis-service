package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        sb.append(getCourtData());

        sb.append("\"Clerk\":\"").append(caseData.getClerkResponsible()).append(NEW_LINE);
        //sb.append("\"app_date\":\"").append(formatLocalDate(caseData.getReceiptDate())).append(NEW_LINE);
        sb.append("\"TODAY_DATE\":\"").append(formatCurrentDate(LocalDate.now())).append(NEW_LINE);
        sb.append("\"TodayPlus28Days\":\"").append(formatCurrentDatePlusDays(LocalDate.now(), 28)).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(caseDetails.getCaseId()).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }

    private static RepresentedType getRepresentation(CaseData caseData, String type) {
        RepresentedType representedType = caseData.getRepSumType();
        if (representedType != null && representedType.getRepType().equals(type)) {
            return representedType;
        }
        List<RepresentedTypeItem> representedTypeCollection = caseData.getRepCollection();
        if (representedTypeCollection != null && !representedTypeCollection.isEmpty()) {
            for (RepresentedTypeItem rep : representedTypeCollection) {
                if (rep.getValue().getRepType().equals(type)) {
                    return rep.getValue();
                }
            }
        }
        return null;
    }

    private static StringBuilder getClaimantData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        RepresentedType representedType = getRepresentation(caseData, "Claimant");
        if (representedType != null) {
            sb.append("\"claimant_full_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"claimant_rep_full_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"Claimant_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"Claimant\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"claimant_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"claimant_rep_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"claimant_email_address\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
            sb.append("\"claimant_rep_email_address\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
            sb.append("\"representative_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"claimant_rep_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"claimant_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
        } else {
            Optional<ClaimantType> claimantType = Optional.ofNullable(caseData.getClaimantType());
            if (claimantType.isPresent()) {
                sb.append("\"claimant_addressUK\":\"").append(claimantType.get().getClaimantAddressUK()).append(NEW_LINE);
                sb.append("\"claimant_email_address\":\"").append(claimantType.get().getClaimantEmailAddress()).append(NEW_LINE);
            }
            Optional<ClaimantIndType> claimantIndType = Optional.ofNullable(caseData.getClaimantIndType());
            if (claimantIndType.isPresent()) {
                sb.append("\"claimant_full_name\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
                sb.append("\"Claimant_name\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(claimantIndType.get().claimantFullName()).append(NEW_LINE);
            }
        }
        return sb;
    }

    private static StringBuilder getRespondentData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        RepresentedType representedType = getRepresentation(caseData, "Respondent");
        if (representedType != null) {
            sb.append("\"Respondent\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"Respondent_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_full_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_representative\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_rep_full_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"respondent_rep_addressUK\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
            sb.append("\"respondent_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"respondent_rep_reference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);
            sb.append("\"respondent_email_address\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
            sb.append("\"respondent_rep_email_address\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
        } else {
            Optional<RespondentSumType> respondentType = Optional.ofNullable(caseData.getRespondentSumType());
            if (respondentType.isPresent()) {
                sb.append("\"Respondent\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"Respondent_name\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_full_name\":\"").append(respondentType.get().getRespondentName()).append(NEW_LINE);
                sb.append("\"respondent_addressUK\":\"").append(respondentType.get().getRespondentAddress()).append(NEW_LINE);
            }
        }
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<String> respOthers = caseData.getRespondentCollection()
                    .stream()
                    .map(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName())
                    .collect(Collectors.toList());
            sb.append("\"resp_others\":\"").append(String.join(", ", respOthers)).append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getHearingData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            HearingType hearingType = caseData.getHearingCollection().get(0).getValue();
            sb.append("\"hearing_date\":\"").append(formatLocalDate(hearingType.getHearingDate())).append(NEW_LINE);
            sb.append("\"hearing_time\":\"").append("11:00 AM").append(NEW_LINE);
            sb.append("\"hearing_venue\":\"").append("Manchester").append(NEW_LINE);
            //sb.append("\"hearing_time\":\"").append(formatLocalDateTimeToTime(hearingType.getHearingDate())).append(NEW_LINE);
            //sb.append("\"hearing_venue\":\"").append(hearingType.getEstHearing().).append(NEW_LINE);
            if (hearingType.getEstHearing() != null) {
                sb.append("\"EstLengthOfHearing\":\"").append(hearingType.getEstHearing()).append(NEW_LINE);
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
            if (correspondence.getPart8Documents() != null) return correspondence.getPart8Documents();
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

    private static StringBuilder getCorrespondenceData(CaseData caseData) {
        String sectionName = getSectionName(caseData);
        StringBuilder sb = new StringBuilder();
        if (!sectionName.equals("")) {
            sb.append("\"").append("t").append(sectionName.replace(".", "_")).append("\":\"").append("true").append(NEW_LINE);
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
