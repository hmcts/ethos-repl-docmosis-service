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

    private static DateTimeFormatter oldPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("d MMM yyyy");

    public static final String outputFileName = "myWelcome.doc";

    private static String formatLocalDate(String date) {
        return !isNullOrEmpty(date) ? LocalDate.parse(date, oldPattern).format(newPattern) : "";
    }

    private static String formatLocalDateTime(String date) {
        return !isNullOrEmpty(date) ? LocalDateTime.parse(date).format(newPattern) : "";
    }

    public static StringBuffer buildDocumentContent(CaseDetails caseDetails, String templateName) {
        StringBuffer sb = new StringBuffer();

        // Start building the instruction
        sb.append("{\n");
        //sb.append("\"accessKey\":\"").append(accessKey).append("\",\n");
        sb.append("\"templateName\":\"").append(templateName).append("\",\n");
        sb.append("\"outputName\":\"").append(outputFileName).append("\",\n");

        // Building the document data
        sb.append("\"data\":{\n");

        CaseData caseData = caseDetails.getCaseData();
        RepresentedType representedType = caseData.getRepresentedType();
        String represented = representedType.getIfRepresented().equals("Yes") ? "true" : "false";
        sb.append("\"ifRepresented\":\"").append(represented).append("\",\n");
        sb.append("\"ifRefused\":\"").append("false").append("\",\n");

        sb.append("\"nameOfRepresentative\":\"").append(representedType.getNameOfRepresentative()).append("\",\n");
        sb.append("\"nameOfOrganisation\":\"").append(representedType.getNameOfOrganisation()).append("\",\n");
        sb.append("\"representativeAddress\":\"").append(representedType.getRepresentativeAddress()).append("\",\n");
        sb.append("\"representativePhoneNumber\":\"").append(representedType.getRepresentativePhoneNumber()).append("\",\n");
        sb.append("\"representativeFaxNumber\":\"").append(representedType.getRepresentativeFaxNumber()).append("\",\n");
        sb.append("\"representativeDxNumber\":\"").append(representedType.getRepresentativeDxNumber()).append("\",\n");
        sb.append("\"representativeEmailAddress\":\"").append(representedType.getRepresentativeEmailAddress()).append("\",\n");
        sb.append("\"representativeReference\":\"").append(representedType.getRepresentativeReference()).append("\",\n");

        ScheduleType scheduleType = caseData.getScheduleType();
        sb.append("\"createdDate\":\"").append(formatLocalDate(caseData.getReceiptDate())).append("\",\n");
        sb.append("\"receivedDate\":\"").append(formatLocalDate(caseData.getReceiptDate())).append("\",\n");
        sb.append("\"hearingDate\":\"").append(formatLocalDateTime(scheduleType.getScheduleDateTime())).append("\",\n");
        sb.append("\"caseNo\":\"").append(caseDetails.getCaseId()).append("\",\n");
        sb.append("\"claimant\":\"").append(caseData.getClaimantType().getClaimantLastName()).append("\",\n");
        sb.append("\"respondent\":\"").append(caseData.getRespondentType().getRespondentName()).append("\",\n");
        sb.append("\"clerk\":\"").append(scheduleType.getScheduleClerk()).append("\",\n");
        sb.append("\"judgeSurname\":\"").append(scheduleType.getScheduleJudge()).append("\",\n");

        ClaimantType claimantType = caseData.getClaimantType();
        sb.append("\"claimantTitle\":\"").append(claimantType.getClaimantTitle()).append(" ").append("\",\n");
        sb.append("\"claimantFirstName\":\"").append(claimantType.getClaimantFirstName()).append(" ").append("\",\n");
        sb.append("\"claimantInitials\":\"").append(claimantType.getClaimantInitials()).append(" ").append("\",\n");
        sb.append("\"claimantLastName\":\"").append(claimantType.getClaimantLastName()).append("\",\n");
        sb.append("\"claimantDateOfBirth\":\"").append(formatLocalDate(claimantType.getClaimantDateOfBirth())).append("\",\n");
        sb.append("\"claimantGender\":\"").append(claimantType.getClaimantGender()).append("\",\n");
        sb.append("\"claimantAddressUK\":\"").append(claimantType.getClaimantAddressUK()).append("\",\n");
        sb.append("\"claimantPhoneNumber\":\"").append(claimantType.getClaimantPhoneNumber()).append("\",\n");
        sb.append("\"claimantMobileNumber\":\"").append(claimantType.getClaimantMobileNumber()).append("\",\n");
        sb.append("\"claimantFaxNumber\":\"").append(claimantType.getClaimantFaxNumber()).append("\",\n");
        sb.append("\"claimantEmailAddress\":\"").append(claimantType.getClaimantEmailAddress()).append("\",\n");
        sb.append("\"claimantContactPreference\":\"").append(claimantType.getClaimantContactPreference()).append("\",\n");

        RespondentType respondentType = caseData.getRespondentType();
        sb.append("\"respondentName\":\"").append(respondentType.getRespondentName()).append("\",\n");
        sb.append("\"respondentAddress\":\"").append(respondentType.getRespondentAddress()).append("\",\n");

        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }
}
