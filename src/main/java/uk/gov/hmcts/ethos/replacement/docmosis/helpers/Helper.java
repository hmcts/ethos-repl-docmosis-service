package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RepresentedType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.RespondentType;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.ScheduleType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.google.common.base.Strings.isNullOrEmpty;

public class Helper {

    private static DateTimeFormatter OLD_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter NEW_PATTERN = DateTimeFormatter.ofPattern("d MMM yyyy");
    private static String NEW_LINE = "\",\n";

    public static final String OUTPUT_FILE_NAME = "myWelcome.doc";

    private static String formatLocalDate(String date) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, OLD_PATTERN).format(NEW_PATTERN) : "";
    }

    private static String formatLocalDateTime(String date) {
        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(NEW_PATTERN) : "";
    }

    public static StringBuilder buildDocumentContent(CaseDetails caseDetails, String templateName, String accessKey) {
        StringBuilder sb = new StringBuilder();

        // Start building the instruction
        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        // Building the document data
        sb.append("\"data\":{\n");

        CaseData caseData = caseDetails.getCaseData();
        RepresentedType representedType = caseData.getRepresentedType();
        String represented = representedType.getIfRepresented().equals("Yes") ? "true" : "false";
        sb.append("\"ifRepresented\":\"").append(represented).append(NEW_LINE);
        sb.append("\"ifRefused\":\"").append("false").append(NEW_LINE);

        sb.append("\"nameOfRepresentative\":\"").append(representedType.getNameOfRepresentative()).append(NEW_LINE);
        sb.append("\"nameOfOrganisation\":\"").append(representedType.getNameOfOrganisation()).append(NEW_LINE);
        sb.append("\"representativeAddress\":\"").append(representedType.getRepresentativeAddress()).append(NEW_LINE);
        sb.append("\"representativePhoneNumber\":\"").append(representedType.getRepresentativePhoneNumber()).append(NEW_LINE);
        sb.append("\"representativeFaxNumber\":\"").append(representedType.getRepresentativeFaxNumber()).append(NEW_LINE);
        sb.append("\"representativeDxNumber\":\"").append(representedType.getRepresentativeDxNumber()).append(NEW_LINE);
        sb.append("\"representativeEmailAddress\":\"").append(representedType.getRepresentativeEmailAddress()).append(NEW_LINE);
        sb.append("\"representativeReference\":\"").append(representedType.getRepresentativeReference()).append(NEW_LINE);

        ScheduleType scheduleType = caseData.getScheduleType();
        sb.append("\"createdDate\":\"").append(formatLocalDate(caseData.getReceiptDate())).append(NEW_LINE);
        sb.append("\"receivedDate\":\"").append(formatLocalDate(caseData.getReceiptDate())).append(NEW_LINE);
        sb.append("\"hearingDate\":\"").append(formatLocalDateTime(scheduleType.getScheduleDateTime())).append(NEW_LINE);
        sb.append("\"caseNo\":\"").append(caseDetails.getCaseId()).append(NEW_LINE);
        sb.append("\"claimant\":\"").append(caseData.getClaimantType().getClaimantLastName()).append(NEW_LINE);
        sb.append("\"respondent\":\"").append(caseData.getRespondentType().getRespondentName()).append(NEW_LINE);
        sb.append("\"clerk\":\"").append(scheduleType.getScheduleClerk()).append(NEW_LINE);
        sb.append("\"judgeSurname\":\"").append(scheduleType.getScheduleJudge()).append(NEW_LINE);

        ClaimantType claimantType = caseData.getClaimantType();
        sb.append("\"claimantTitle\":\"").append(claimantType.getClaimantTitle()).append(" ").append(NEW_LINE);
        sb.append("\"claimantFirstName\":\"").append(claimantType.getClaimantFirstName()).append(" ").append(NEW_LINE);
        sb.append("\"claimantInitials\":\"").append(claimantType.getClaimantInitials()).append(" ").append(NEW_LINE);
        sb.append("\"claimantLastName\":\"").append(claimantType.getClaimantLastName()).append(NEW_LINE);
        sb.append("\"claimantDateOfBirth\":\"").append(formatLocalDate(claimantType.getClaimantDateOfBirth())).append(NEW_LINE);
        sb.append("\"claimantGender\":\"").append(claimantType.getClaimantGender()).append(NEW_LINE);
        sb.append("\"claimantAddressUK\":\"").append(claimantType.getClaimantAddressUK()).append(NEW_LINE);
        sb.append("\"claimantPhoneNumber\":\"").append(claimantType.getClaimantPhoneNumber()).append(NEW_LINE);
        sb.append("\"claimantMobileNumber\":\"").append(claimantType.getClaimantMobileNumber()).append(NEW_LINE);
        sb.append("\"claimantFaxNumber\":\"").append(claimantType.getClaimantFaxNumber()).append(NEW_LINE);
        sb.append("\"claimantEmailAddress\":\"").append(claimantType.getClaimantEmailAddress()).append(NEW_LINE);
        sb.append("\"claimantContactPreference\":\"").append(claimantType.getClaimantContactPreference()).append(NEW_LINE);

        RespondentType respondentType = caseData.getRespondentType();
        sb.append("\"respondentName\":\"").append(respondentType.getRespondentName()).append(NEW_LINE);
        sb.append("\"respondentAddress\":\"").append(respondentType.getRespondentAddress()).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }
}
