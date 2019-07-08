package uk.gov.hmcts.ethos.replacement.functional.util;

import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.CaseDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.types.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;

public class DocumentUtil {

    private static DateTimeFormatter NEW_DATE_PATTERN = DateTimeFormatter.ofPattern("E, d MMM yyyy");
    private static String NEW_LINE = "\",\n";
    public static final String OUTPUT_FILE_NAME = "document.docx";

    public static String buildDocumentContent(CaseDetails caseDetails, String accessKey) {
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
        sb.append(getCourtData(caseData));

        sb.append("\"i").append(getSectionName(caseData).replace(".", "_")).append("_enhmcts\":\"")
                .append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        sb.append("\"iScot").append(getScotSectionName(caseData).replace(".", "_")).append("_schmcts\":\"")
                .append("[userImage:").append("schmcts.png]").append(NEW_LINE);

        sb.append("\"Clerk\":\"").append(nullCheck(caseData.getClerkResponsible())).append(NEW_LINE);
        sb.append("\"Today_date\":\"").append(formatCurrentDate(LocalDate.now())).append(NEW_LINE);
        sb.append("\"TodayPlus28Days\":\"").append(formatCurrentDatePlusDays(LocalDate.now(), 28)).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(nullCheck(caseDetails.getCaseData().getEthosCaseReference())).append(NEW_LINE);

        sb.append("}\n");
        sb.append("}\n");

        String result = sb.toString();

        //Hack put in due to a bug in the buildDocumentContent() code adding extra comma (,) at the end
        return result.substring(0, result.lastIndexOf(',')) + "}\n}" ;
        //return sb.toString();
    }

    static String formatCurrentDatePlusDays(LocalDate date, long days) {
        return !isNullOrEmpty(date.toString()) ? date.plusDays(days).format(NEW_DATE_PATTERN) : "";
    }

    static String formatCurrentDate(LocalDate date) {
        return !isNullOrEmpty(date.toString()) ? date.format(NEW_DATE_PATTERN) : "";
    }

    private static StringBuilder getClaimantData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        RepresentedTypeC representedTypeC = caseData.getRepresentativeClaimantType();
        if (representedTypeC != null) {
            sb.append("\"claimant_addressLine1\": \"" + representedTypeC.getRepresentativeAddress().getAddressLine1()).append(NEW_LINE);
            sb.append("\"claimant_addressLine2\": \"" + nullCheck(representedTypeC.getRepresentativeAddress().getAddressLine2())).append(NEW_LINE);
            sb.append("\"claimant_addressLine3\": \"" + nullCheck(representedTypeC.getRepresentativeAddress().getAddressLine3())).append(NEW_LINE);
            sb.append("\"claimant_town\": \"" + nullCheck(representedTypeC.getRepresentativeAddress().getPostTown())).append(NEW_LINE);
            sb.append("\"claimant_county\": \"" + nullCheck(representedTypeC.getRepresentativeAddress().getCounty())).append(NEW_LINE);
            sb.append("\"claimant_postCode\": \"" + nullCheck(representedTypeC.getRepresentativeAddress().getPostCode())).append(NEW_LINE);
            //sb.append("\"Claimant_name\": \"" + representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            //sb.append("\"claimant_name\": \"" + representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"claimant_full_name\": \"" + representedTypeC.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"Claimant\": \"" + caseData.getClaimantIndType().claimantFullName()).append(NEW_LINE);
            //sb.append("\"claimant_email_address\": \"").append(NEW_LINE);
        } else {
            ClaimantType claimantType = caseData.getClaimantType();
            ClaimantIndType claimantIndType = caseData.getClaimantIndType();

            sb.append("\"claimant_addressLine1\": \"" + claimantType.getClaimantAddressUK().getAddressLine1()).append(NEW_LINE);
            sb.append("\"claimant_addressLine2\": \"" + nullCheck(claimantType.getClaimantAddressUK().getAddressLine2())).append(NEW_LINE);
            sb.append("\"claimant_addressLine3\": \"" + nullCheck(claimantType.getClaimantAddressUK().getAddressLine3())).append(NEW_LINE);
            sb.append("\"claimant_town\": \"" + nullCheck(claimantType.getClaimantAddressUK().getPostTown())).append(NEW_LINE);
            sb.append("\"claimant_county\": \"" + nullCheck(claimantType.getClaimantAddressUK().getCounty())).append(NEW_LINE);
            sb.append("\"claimant_postCode\": \"" + nullCheck(claimantType.getClaimantAddressUK().getPostCode())).append(NEW_LINE);
            String typeOfClaimant = caseData.getClaimantTypeOfClaimant();
            if (typeOfClaimant.equalsIgnoreCase("individual")) {
                sb.append("\"claimant_full_name\": \"" + claimantIndType.claimantFullName()).append(NEW_LINE);
                sb.append("\"Claimant\": \"" + claimantIndType.claimantFullName()).append(NEW_LINE);
                //sb.append("\"claimant_email_address\": \"").append(NEW_LINE);
            } else if (typeOfClaimant.equalsIgnoreCase("company")) {
                sb.append("\"claimant_full_name\": \"" + caseData.getClaimantCompany()).append(NEW_LINE);
                sb.append("\"Claimant\": \"" + caseData.getClaimantCompany()).append(NEW_LINE);
                //sb.append("\"claimant_email_address\": \"").append(NEW_LINE);
            }
        }
        return sb;
    }

    private static StringBuilder getRespondentData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();

        RespondentSumType respondentType = caseData.getRespondentSumType();

        List<RepresentedTypeRItem> representedTypeRList = caseData.getRepCollection();
        if (representedTypeRList != null && !representedTypeRList.isEmpty()) {
            RepresentedTypeR representedTypeR = representedTypeRList.get(0).getValue();
            //sb.append("\"respondent_email_address\": \"\",");
            sb.append("\"respondent_full_name\": \"" + representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            //sb.append("\"respondent_representative\": \"" + representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            //sb.append("\"respondent_rep_full_name\": \"" + representedTypeR.getNameOfRepresentative()).append(NEW_LINE);
            sb.append("\"respondent_addressLine1\": \"" + representedTypeR.getRepresentativeAddress().getAddressLine1()).append(NEW_LINE);
            sb.append("\"respondent_addressLine2\": \"" + nullCheck(representedTypeR.getRepresentativeAddress().getAddressLine2())).append(NEW_LINE);
            sb.append("\"respondent_addressLine3\": \"" + nullCheck(representedTypeR.getRepresentativeAddress().getAddressLine3())).append(NEW_LINE);
            sb.append("\"respondent_town\": \"" + nullCheck(representedTypeR.getRepresentativeAddress().getPostTown())).append(NEW_LINE);
            sb.append("\"respondent_county\": \"" + nullCheck(representedTypeR.getRepresentativeAddress().getCounty())).append(NEW_LINE);
            sb.append("\"respondent_postCode\": \"" + nullCheck(representedTypeR.getRepresentativeAddress().getPostCode())).append(NEW_LINE);
            sb.append("\"respondent_reference\": \"" + nullCheck(representedTypeR.getRepresentativeReference())).append(NEW_LINE);
            //sb.append("\"respondent_rep_reference\": \"" + nullCheck(representedTypeR.getRepresentativeReference())).append(NEW_LINE);
        } else {

            sb.append("\"respondent_full_name\": \"" + respondentType.getRespondentName()).append(NEW_LINE);
            sb.append("\"respondent_addressLine1\": \"" + nullCheck(respondentType.getRespondentAddress().getAddressLine1())).append(NEW_LINE);
            sb.append("\"respondent_addressLine2\": \"" + nullCheck(respondentType.getRespondentAddress().getAddressLine2())).append(NEW_LINE);
            sb.append("\"respondent_addressLine3\": \"" + nullCheck(respondentType.getRespondentAddress().getAddressLine3())).append(NEW_LINE);
            sb.append("\"respondent_town\": \"" + nullCheck(respondentType.getRespondentAddress().getPostTown())).append(NEW_LINE);
            sb.append("\"respondent_county\": \"" + nullCheck(respondentType.getRespondentAddress().getCounty())).append(NEW_LINE);
            sb.append("\"respondent_postCode\": \"" + nullCheck(respondentType.getRespondentAddress().getPostCode())).append(NEW_LINE);
            //sb.append("\"claimant_email_address\": \"").append(NEW_LINE);
        }
        //Currently not checking caseData.getRepCollection(). Should create a list with names and check if represented or not
        if (caseData.getRespondentCollection() != null && !caseData.getRespondentCollection().isEmpty()) {
            List<String> respOthers = caseData.getRespondentCollection()
                    .stream()
                    .map(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName())
                    .collect(Collectors.toList());
            sb.append("\"resp_others\":\"").append(String.join(", ", respOthers)).append(NEW_LINE);
        }

        if (respondentType != null) sb.append("\"Respondent\": \"" + respondentType.getRespondentName()).append(NEW_LINE);

        return sb;
    }

    private static StringBuilder getHearingData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        //Currently checking collection not the HearingType
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            HearingType hearingType = caseData.getHearingCollection().get(0).getValue();
            if (hearingType.getHearingDateStart() != null) {
                sb.append("\"Hearing_date\": \"" + hearingType.getHearingDateStart()).append(NEW_LINE);
                sb.append("\"Hearing_date_time\": \"" + hearingType.getHearingDateStart()).append(NEW_LINE);
            } else {
                sb.append("\"Hearing_date\": \"").append(NEW_LINE);
                sb.append("\"Hearing_date_time\": \"").append(NEW_LINE);
            }
            sb.append("\"Hearing_venue\": \"" + hearingType.getHearingVenue()).append(NEW_LINE);
            //sb.append("\"hearing_address\": \"" + hearingType.getHearingVenue()).append(NEW_LINE);
            if (hearingType.getEstHearing() != null) {
                sb.append("\"Hearing_duration\": \"" + hearingType.getEstHearing()).append(NEW_LINE);
            }
        }
        return sb;
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

    private static StringBuilder getCourtData(CaseData caseData) {
        StringBuilder sb = new StringBuilder();
        if (caseData.getTribunalCorrespondenceAddress() != null) {
            sb.append("\"Court_addressLine1\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getAddressLine1())).append(NEW_LINE);
            sb.append("\"Court_addressLine2\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getAddressLine2())).append(NEW_LINE);
            sb.append("\"Court_addressLine3\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getAddressLine3())).append(NEW_LINE);
            sb.append("\"Court_town\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getPostTown())).append(NEW_LINE);
            sb.append("\"Court_county\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getCounty())).append(NEW_LINE);
            sb.append("\"Court_postCode\": \"" + nullCheck(caseData.getTribunalCorrespondenceAddress().getPostCode())).append(NEW_LINE);
        }
        sb.append("\"Court_telephone\":\"" + nullCheck(caseData.getTribunalCorrespondenceTelephone())).append(NEW_LINE);
        sb.append("\"Court_fax\":\"" + nullCheck(caseData.getTribunalCorrespondenceFax())).append(NEW_LINE);
        sb.append("\"Court_DX\":\"" + nullCheck(caseData.getTribunalCorrespondenceDX())).append(NEW_LINE);
        sb.append("\"Court_Email\":\"" + nullCheck(caseData.getTribunalCorrespondenceEmail())).append(NEW_LINE);
        return sb;
    }

    private static String nullCheck(String input) {
        return Objects.toString(input, "");
    }
}
