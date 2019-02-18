package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Helper {

    private static DateTimeFormatter OLD_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter NEW_PATTERN_DATE = DateTimeFormatter.ofPattern("E, d MMM yyyy");
    private static DateTimeFormatter NEW_PATTERN_TIME = DateTimeFormatter.ofPattern("hh:mm a");

    public static final String OUTPUT_FILE_NAME = "myWelcome.doc";

    private static String formatLocalDate(String date) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, OLD_PATTERN).format(NEW_PATTERN_DATE) : "";
    }

    private static String formatLocalDatePlusDays(String date, long days) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, OLD_PATTERN).plusDays(days).format(NEW_PATTERN_DATE) : "";

    }

    private static String formatLocalDateTimeToDate(String date) {
        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(NEW_PATTERN_DATE) : "";
    }

    private static String formatLocalDateTimeToTime(String date) {
        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(NEW_PATTERN_TIME) : "";
    }

    public static StringBuilder buildDocumentContent(CaseDetails caseDetails, String templateName, String accessKey) {
        StringBuilder sb = new StringBuilder();

        // Start building the instruction
        sb.append("{\n");
        String NEW_LINE = "\",\n";
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        // Building the document data
        sb.append("\"data\":{\n");

        CaseData caseData = caseDetails.getCaseData();
        if (caseData.getIfCRepresented() != null) {
            if (caseData.getIfCRepresented().equals("Yes")) {
                RepresentedType representedType = caseData.getCRepresentedType();
                sb.append("\"add_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
                sb.append("\"add_add1\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
                sb.append("\"app_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
            } else {
                ClaimantType claimantType = caseData.getClaimantType();
                sb.append("\"add_name\":\"").append(claimantType.getClaimantName()).append(NEW_LINE);
                sb.append("\"add_add1\":\"").append(claimantType.getClaimantAddressUK()).append(NEW_LINE);
                sb.append("\"app_name\":\"").append(claimantType.getClaimantName()).append(NEW_LINE);
            }
        }
        if (caseData.getIfRRepresented() != null) {
            if (caseData.getIfRRepresented().equals("Yes")) {
                RepresentedType representedType = caseData.getRRepresentedType();
                sb.append("\"resp_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
                sb.append("\"opp_name\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
                sb.append("\"opp_add1\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
            } else {
                RespondentSumType respondentType = caseData.getRespondentSumType();
                sb.append("\"resp_name\":\"").append(respondentType.getRespondentName()).append(NEW_LINE);
                sb.append("\"opp_name\":\"").append(respondentType.getRespondentName()).append(NEW_LINE);
                sb.append("\"opp_add1\":\"").append(respondentType.getRespondentAddress()).append(NEW_LINE);
            }
        }
        if (caseData.getHearingType() != null) {
            HearingType hearingType = caseData.getHearingType();
            sb.append("\"hearing_date\":\"").append(formatLocalDateTimeToDate(hearingType.getHearingDate())).append(NEW_LINE);
            sb.append("\"hearing_time\":\"").append(formatLocalDateTimeToTime(hearingType.getHearingDate())).append(NEW_LINE);
            //sb.append("\"hearing_venue\":\"").append(hearingType.getEstHearing().).append(NEW_LINE);
            if (hearingType.getEstHearing() != null) {
                sb.append("\"EstLengthOfHearing\":\"").append(hearingType.getEstHearing().getFromHours()).append(NEW_LINE);
            }

        }
        sb.append("\"user_name\":\"").append(caseData.getClerkResponsible()).append(NEW_LINE);
        sb.append("\"curr_date\":\"").append(formatLocalDate(caseData.getReceiptDate())).append(NEW_LINE);
        sb.append("\"todayPlus28Days\":\"").append(formatLocalDatePlusDays(caseData.getReceiptDate(), 28)).append(NEW_LINE);
        sb.append("\"case_no_year\":\"").append(caseDetails.getCaseId()).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }
}
