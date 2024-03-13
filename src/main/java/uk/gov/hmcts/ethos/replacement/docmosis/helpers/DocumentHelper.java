package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.Address;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.AddressLabelTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.DocumentTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.HearingTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RepresentedTypeRItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.AddressLabelType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantIndType;
import uk.gov.hmcts.ecm.common.model.ccd.types.ClaimantType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceScotType;
import uk.gov.hmcts.ecm.common.model.ccd.types.CorrespondenceType;
import uk.gov.hmcts.ecm.common.model.ccd.types.DocumentType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.ccd.types.RespondentSumType;
import uk.gov.hmcts.ecm.common.model.ccd.types.UploadedDocumentType;
import uk.gov.hmcts.ecm.common.model.helper.DefaultValues;
import uk.gov.hmcts.ecm.common.model.multiples.MultipleData;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_PAGE_SIZE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ADDRESS_LABELS_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.COMPANY_TYPE_CLAIMANT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_STATUS_LISTED;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LABEL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LBL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.VENUE_ADDRESS_VALUES_FILE_PATH;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ACAS_CERTIFICATE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ANONYMITY_ORDER;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_FOR_A_WITNESS_ORDER_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_FOR_A_WITNESS_ORDER_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_AMEND_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_AMEND_RESPONSE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_EXTEND_TIME_TO_PRESENT_A_RESPONSE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_ORDER_THE_C_TO_DO_SOMETHING;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_ORDER_THE_R_TO_DO_SOMETHING;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_POSTPONE_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_POSTPONE_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_RESTRICT_PUBLICITY_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_RESTRICT_PUBLICITY_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_REVOKE_AN_ORDER_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_REVOKE_AN_ORDER_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_RESPONSE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_VARY_AN_ORDER_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_VARY_AN_ORDER_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_VARY_OR_REVOKE_AN_ORDER_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.APP_TO_VARY_OR_REVOKE_AN_ORDER_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CASE_MANAGEMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CERTIFICATE_OF_CORRECTION;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CHANGE_OF_PARTYS_DETAILS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CLAIM_ACCEPTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CLAIM_PART_REJECTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CLAIM_REJECTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CONTACT_THE_TRIBUNAL_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.CONTACT_THE_TRIBUNAL_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.COT3;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.COUNTER_SCHEDULE_OF_LOSS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.C_HAS_NOT_COMPLIED_WITH_AN_ORDER_R;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.DEPOSIT_ORDER;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.DISABILITY_IMPACT_STATEMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET1;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET1_ATTACHMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET1_VETTING;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET3;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET3_ATTACHMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.ET3_PROCESSING;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.EXTRACT_OF_JUDGMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.HEARINGS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.HEARING_BUNDLE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.INITIAL_CONSIDERATION;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.JUDGMENT;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.JUDGMENT_AND_REASONS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.JUDGMENT_WITH_REASONS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.LEGACY_DOCUMENT_NAMES;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.MISC;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.NOTICE_OF_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.NOTICE_OF_HEARING;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.OTHER;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.REASONS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RECONSIDERATION;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.REFERRAL_JUDICIAL_DIRECTION;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RESPONSE_ACCEPTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RESPONSE_REJECTED;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RESPONSE_TO_A_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RULE_27_NOTICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.RULE_28_NOTICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.R_HAS_NOT_COMPLIED_WITH_AN_ORDER_C;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.SCHEDULE_OF_LOSS;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.STARTING_A_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.TRIBUNAL_CASE_FILE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.TRIBUNAL_NOTICE;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.TRIBUNAL_ORDER;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.UNLESS_ORDER;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.WITHDRAWAL_OF_ALL_OR_PART_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.WITHDRAWAL_OF_ENTIRE_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.WITHDRAWAL_OF_PART_OF_CLAIM;
import static uk.gov.hmcts.ethos.replacement.docmosis.constants.DocumentConstants.WITHDRAWAL_SETTLED;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class DocumentHelper {

    private DocumentHelper() {
    }

    private static final String VENUE_ADDRESS_OPENING_PROCESSING_ERROR = "Failed while opening or "
            + "processing the entries for the venueAddressValues.xlsx file : ---> ";

    public static StringBuilder buildDocumentContent(CaseData caseData, String accessKey,
                                                     UserDetails userDetails, String caseTypeId,
                                                     InputStream venueAddressInputStream,
                                                     CorrespondenceType correspondenceType,
                                                     CorrespondenceScotType correspondenceScotType,
                                                     MultipleData multipleData,
                                                     DefaultValues allocatedCourtAddress) {
        var sb = new StringBuilder();
        String templateName = getTemplateName(correspondenceType, correspondenceScotType);

        // Start building the instruction
        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(FILE_EXTENSION).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        // Building the document data
        sb.append("\"data\":{\n");

        if (templateName.equals(ADDRESS_LABELS_TEMPLATE) && multipleData == null) {
            log.info("Getting address labels data for single case:" + caseData.getEthosCaseReference());
            sb.append(getAddressLabelsDataSingleCase(caseData));
        } else if (templateName.equals(ADDRESS_LABELS_TEMPLATE)) {
            log.info("Getting address labels data for multiple reference:" + multipleData.getMultipleReference());
            sb.append(getAddressLabelsDataMultipleCase(multipleData));
        } else {
            log.info("Getting data for single template for case:" + caseData.getEthosCaseReference());
            sb.append(getClaimantData(caseData));
            sb.append(getRespondentData(caseData));
            sb.append(getHearingData(caseData, caseTypeId, venueAddressInputStream, correspondenceType,
                    correspondenceScotType));
            sb.append(getCorrespondenceData(correspondenceType));
            sb.append(getCorrespondenceScotData(correspondenceScotType));
            sb.append(getCourtData(caseData, allocatedCourtAddress));
        }

        sb.append("\"i").append(getEWSectionName(correspondenceType)
                .replace(".", "_"))
                .append("_enhmcts\":\"").append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        sb.append("\"i").append(getEWSectionName(correspondenceType)
                .replace(".", "_"))
                .append("_enhmcts1\":\"").append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        sb.append("\"i").append(getEWSectionName(correspondenceType)
                .replace(".", "_"))
                .append("_enhmcts2\":\"").append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        sb.append("\"iScot").append(getScotSectionName(correspondenceScotType)
                .replace(".", "_"))
                .append("_schmcts\":\"").append("[userImage:").append("schmcts.png]").append(NEW_LINE);
        sb.append("\"iScot").append(getScotSectionName(correspondenceScotType)
                .replace(".", "_"))
                .append("_schmcts1\":\"").append("[userImage:").append("schmcts.png]").append(NEW_LINE);
        sb.append("\"iScot").append(getScotSectionName(correspondenceScotType)
                .replace(".", "_"))
                .append("_schmcts2\":\"").append("[userImage:").append("schmcts.png]").append(NEW_LINE);

        String userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        sb.append("\"Clerk\":\"").append(nullCheck(userName)).append(NEW_LINE);
        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append(NEW_LINE);
        sb.append("\"TodayPlus28Days\":\"").append(UtilHelper.formatCurrentDatePlusDays(LocalDate.now(), 28))
                .append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(nullCheck(caseData.getEthosCaseReference())).append(NEW_LINE);
        sb.append("}\n");
        sb.append("}\n");

        return sb;
    }

    private static StringBuilder getClaimantAddressUK(Address address) {
        var sb = new StringBuilder();
        sb.append("\"claimant_addressLine1\":\"").append(nullCheck(address.getAddressLine1())).append(NEW_LINE);
        sb.append("\"claimant_addressLine2\":\"").append(nullCheck(address.getAddressLine2())).append(NEW_LINE);
        sb.append("\"claimant_addressLine3\":\"").append(nullCheck(address.getAddressLine3())).append(NEW_LINE);
        sb.append("\"claimant_town\":\"").append(nullCheck(address.getPostTown())).append(NEW_LINE);
        sb.append("\"claimant_county\":\"").append(nullCheck(address.getCounty())).append(NEW_LINE);
        sb.append("\"claimant_postCode\":\"").append(nullCheck(address.getPostCode())).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getClaimantOrRepAddressUK(Address address) {
        var sb = new StringBuilder();
        sb.append("\"claimant_or_rep_addressLine1\":\"").append(nullCheck(address.getAddressLine1())).append(NEW_LINE);
        sb.append("\"claimant_or_rep_addressLine2\":\"").append(nullCheck(address.getAddressLine2())).append(NEW_LINE);
        sb.append("\"claimant_or_rep_addressLine3\":\"").append(nullCheck(address.getAddressLine3())).append(NEW_LINE);
        sb.append("\"claimant_or_rep_town\":\"").append(nullCheck(address.getPostTown())).append(NEW_LINE);
        sb.append("\"claimant_or_rep_county\":\"").append(nullCheck(address.getCounty())).append(NEW_LINE);
        sb.append("\"claimant_or_rep_postCode\":\"").append(nullCheck(address.getPostCode())).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getClaimantData(CaseData caseData) {
        log.info("Getting Claimant Data for case: " + caseData.getEthosCaseReference());
        var sb = new StringBuilder();
        var representedTypeC = caseData.getRepresentativeClaimantType();
        Optional<ClaimantIndType> claimantIndType = Optional.ofNullable(caseData.getClaimantIndType());
        if (representedTypeC != null && caseData.getClaimantRepresentedQuestion() != null &&  caseData
                .getClaimantRepresentedQuestion().equals(YES)) {
            log.info("Claimant is represented for case reference: " + caseData.getEthosCaseReference());
            sb.append("\"claimant_or_rep_full_name\":\"").append(nullCheck(representedTypeC.getNameOfRepresentative()))
                    .append(NEW_LINE);
            sb.append("\"claimant_rep_organisation\":\"").append(nullCheck(representedTypeC.getNameOfOrganisation()))
                    .append(NEW_LINE);
            if (representedTypeC.getRepresentativeAddress() != null) {
                sb.append(getClaimantOrRepAddressUK(representedTypeC.getRepresentativeAddress()));
            } else {
                sb.append(getClaimantOrRepAddressUK(new Address()));
            }
            sb.append("\"claimant_reference\":\"").append(nullCheck(representedTypeC.getRepresentativeReference()))
                    .append(NEW_LINE);
            Optional<String> claimantTypeOfClaimant = Optional.ofNullable(caseData.getClaimantTypeOfClaimant());
            if (claimantTypeOfClaimant.isPresent() && caseData.getClaimantTypeOfClaimant()
                    .equals(COMPANY_TYPE_CLAIMANT)) {
                log.info("Claimant is a company for case reference: " + caseData.getEthosCaseReference());
                sb.append("\"claimant_full_name\":\"").append(nullCheck(caseData.getClaimantCompany()))
                        .append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(nullCheck(caseData.getClaimantCompany())).append(NEW_LINE);
            } else if (claimantIndType.isPresent()) {
                sb.append("\"claimant_full_name\":\"").append(nullCheck(claimantIndType.get().claimantFullName()))
                        .append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(nullCheck(claimantIndType.get().claimantFullName()))
                        .append(NEW_LINE);
            } else {
                sb.append("\"claimant_full_name\":\"").append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(NEW_LINE);
            }
        } else {
            log.info("Claimant is not represented for case: " + caseData.getEthosCaseReference());
            Optional<String> claimantTypeOfClaimant = Optional.ofNullable(caseData.getClaimantTypeOfClaimant());
            if (claimantTypeOfClaimant.isPresent() && caseData.getClaimantTypeOfClaimant()
                    .equals(COMPANY_TYPE_CLAIMANT)) {
                log.info("Claimant Company");
                sb.append("\"claimant_or_rep_full_name\":\"").append(nullCheck(caseData.getClaimantCompany()))
                        .append(NEW_LINE);
                sb.append("\"claimant_full_name\":\"").append(nullCheck(caseData.getClaimantCompany()))
                        .append(NEW_LINE);
                sb.append("\"Claimant\":\"").append(nullCheck(caseData.getClaimantCompany())).append(NEW_LINE);
            } else {
                log.info("Claimant data");
                if (claimantIndType.isPresent()) {
                    sb.append("\"claimant_or_rep_full_name\":\"").append(nullCheck(claimantIndType.get()
                            .claimantFullName())).append(NEW_LINE);
                    sb.append("\"claimant_full_name\":\"").append(nullCheck(claimantIndType.get().claimantFullName()))
                            .append(NEW_LINE);
                    sb.append("\"Claimant\":\"").append(nullCheck(claimantIndType.get().claimantFullName()))
                            .append(NEW_LINE);
                } else {
                    sb.append("\"claimant_or_rep_full_name\":\"").append(NEW_LINE);
                    sb.append("\"claimant_full_name\":\"").append(NEW_LINE);
                    sb.append("\"Claimant\":\"").append(NEW_LINE);
                    sb.append("\"claimant_rep_organisation\":\"").append(NEW_LINE);
                }
            }
            Optional<ClaimantType> claimantType = Optional.ofNullable(caseData.getClaimantType());
            if (claimantType.isPresent()) {
                sb.append(getClaimantOrRepAddressUK(claimantType.get().getClaimantAddressUK()));
            } else {
                sb.append(getClaimantOrRepAddressUK(new Address()));
            }
        }
        log.info("Claimant address UK");
        Optional<ClaimantType> claimantType = Optional.ofNullable(caseData.getClaimantType());
        if (claimantType.isPresent()) {
            sb.append(getClaimantAddressUK(claimantType.get().getClaimantAddressUK()));
        } else {
            sb.append(getClaimantAddressUK(new Address()));
        }
        return sb;
    }

    private static StringBuilder getRespondentAddressUK(Address address) {
        var sb = new StringBuilder();
        sb.append("\"respondent_addressLine1\":\"").append(nullCheck(address.getAddressLine1())).append(NEW_LINE);
        sb.append("\"respondent_addressLine2\":\"").append(nullCheck(address.getAddressLine2())).append(NEW_LINE);
        sb.append("\"respondent_addressLine3\":\"").append(nullCheck(address.getAddressLine3())).append(NEW_LINE);
        sb.append("\"respondent_town\":\"").append(nullCheck(address.getPostTown())).append(NEW_LINE);
        sb.append("\"respondent_county\":\"").append(nullCheck(address.getCounty())).append(NEW_LINE);
        sb.append("\"respondent_postCode\":\"").append(nullCheck(address.getPostCode())).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getRespondentOrRepAddressUK(Address address) {
        var sb = new StringBuilder();
        sb.append("\"respondent_or_rep_addressLine1\":\"").append(nullCheck(address.getAddressLine1()))
                .append(NEW_LINE);
        sb.append("\"respondent_or_rep_addressLine2\":\"").append(nullCheck(address.getAddressLine2()))
                .append(NEW_LINE);
        sb.append("\"respondent_or_rep_addressLine3\":\"").append(nullCheck(address.getAddressLine3()))
                .append(NEW_LINE);
        sb.append("\"respondent_or_rep_town\":\"").append(nullCheck(address.getPostTown())).append(NEW_LINE);
        sb.append("\"respondent_or_rep_county\":\"").append(nullCheck(address.getCounty())).append(NEW_LINE);
        sb.append("\"respondent_or_rep_postCode\":\"").append(nullCheck(address.getPostCode())).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getRespondentData(CaseData caseData) {
        log.info("Respondent Data");
        var sb = new StringBuilder();
        List<RespondentSumTypeItem> respondentSumTypeItemList = CollectionUtils.isNotEmpty(
                caseData.getRespondentCollection())
                ? caseData.getRespondentCollection() : new ArrayList<>();

        if (CollectionUtils.isEmpty(respondentSumTypeItemList)) {
            log.error("No respondents present for case: " + caseData.getEthosCaseReference());
        }

        var responseContinue = false;
        var responseNotStruckOut = false;

        var respondentToBeShown = new RespondentSumType();

        for (RespondentSumTypeItem respondentSumTypeItem: respondentSumTypeItemList) {
            responseContinue = Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseContinue())
                    || YES.equals(respondentSumTypeItem.getValue().getResponseContinue());
            responseNotStruckOut = Strings.isNullOrEmpty(respondentSumTypeItem.getValue().getResponseStruckOut())
                    || respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO);

            if (responseContinue && responseNotStruckOut) {
                log.info("Response is continuing and not struck out for case: " + caseData.getEthosCaseReference());
                respondentToBeShown = respondentSumTypeItem.getValue();
                break;
            }
        }

        if (!responseContinue) {
            log.error("Atleast one respondent should have response continuing for case: "
                    + caseData.getEthosCaseReference());
        }

        if (!responseNotStruckOut) {
            log.error("Atleast one respondent should have response not struck out for case: "
                    + caseData.getEthosCaseReference());
        }

        if (respondentToBeShown.equals(new RespondentSumType())) {
            log.error("No respondent found whose response is continuing and is not struck out for case: "
                    + caseData.getEthosCaseReference());
        }

        List<RepresentedTypeRItem> representedTypeRList = caseData.getRepCollection();
        RespondentSumType finalRespondentToBeShown = respondentToBeShown;
        Optional<RepresentedTypeRItem> representedTypeRItem = Optional.empty();

        if (CollectionUtils.isNotEmpty(representedTypeRList) && responseNotStruckOut && responseContinue
                && !finalRespondentToBeShown.equals(new RespondentSumType())) {
            representedTypeRItem = representedTypeRList.stream()
                    .filter(a -> a.getValue().getRespRepName().equals(
                            finalRespondentToBeShown.getRespondentName())).findFirst();
        }

        if (representedTypeRItem.isPresent()) {
            log.info("Respondent represented");
            var representedTypeR = representedTypeRItem.get().getValue();
            sb.append("\"respondent_or_rep_full_name\":\"").append(nullCheck(representedTypeR
                    .getNameOfRepresentative())).append(NEW_LINE);
            if (representedTypeR.getRepresentativeAddress() != null) {
                sb.append(getRespondentOrRepAddressUK(representedTypeR.getRepresentativeAddress()));
            } else {
                sb.append(getRespondentOrRepAddressUK(new Address()));
            }
            sb.append("\"respondent_reference\":\"").append(nullCheck(representedTypeR.getRepresentativeReference()))
                    .append(NEW_LINE);
            sb.append("\"respondent_rep_organisation\":\"").append(nullCheck(representedTypeR.getNameOfOrganisation()))
                    .append(NEW_LINE);

        } else {
            log.info("Respondent not represented");
            if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())
                    && responseNotStruckOut && responseContinue
                    && !finalRespondentToBeShown.equals(new RespondentSumType())) {
                sb.append("\"respondent_or_rep_full_name\":\"").append(nullCheck(finalRespondentToBeShown
                        .getRespondentName())).append(NEW_LINE);
                sb.append(getRespondentOrRepAddressUK(getRespondentAddressET3(finalRespondentToBeShown)));
            } else {
                sb.append("\"respondent_or_rep_full_name\":\"").append(NEW_LINE);
                sb.append("\"respondent_rep_organisation\":\"").append(NEW_LINE);
                sb.append(getRespondentOrRepAddressUK(new Address()));
            }
        }
        if (CollectionUtils.isNotEmpty(caseData.getRespondentCollection())) {
            log.info("Respondent collection");
            sb.append("\"respondent_full_name\":\"").append(
                    nullCheck((Strings.isNullOrEmpty(finalRespondentToBeShown.getResponseContinue())
                            || YES.equals(finalRespondentToBeShown.getResponseContinue()))
                            ? finalRespondentToBeShown.getRespondentName()
                            : ""))
                    .append(NEW_LINE);
            sb.append((Strings.isNullOrEmpty(finalRespondentToBeShown.getResponseContinue())
                    || YES.equals(finalRespondentToBeShown.getResponseContinue()))
                    && !finalRespondentToBeShown.equals(new RespondentSumType())
                    ? getRespondentAddressUK(getRespondentAddressET3(finalRespondentToBeShown)) : "");

            if (Strings.isNullOrEmpty(finalRespondentToBeShown.getResponseContinue())
                    || YES.equals(finalRespondentToBeShown.getResponseContinue())) {
                sb.append("\"Respondent\":\"").append(caseData.getRespondentCollection().size() > 1 ? "1. " : "")
                        .append(nullCheck((finalRespondentToBeShown.getRespondentName())))
                        .append(NEW_LINE);
            }

            sb.append(getRespOthersName(caseData, finalRespondentToBeShown.getRespondentName()));
            sb.append(getRespAddress(caseData));
        } else {
            sb.append("\"respondent_full_name\":\"").append(NEW_LINE);
            sb.append(getRespondentAddressUK(new Address()));
            sb.append("\"Respondent\":\"").append(NEW_LINE);
            sb.append("\"resp_others\":\"").append(NEW_LINE);
            sb.append("\"resp_address\":\"").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getRespOthersName(CaseData caseData, String firstRespondentName) {
        log.info("Respondent Others Name");
        var sb = new StringBuilder();
        var atomicInteger = new AtomicInteger(2);
        List<String> respOthers = caseData.getRespondentCollection()
                .stream()
                .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut() == null
                        || respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO)
                        && (respondentSumTypeItem.getValue().getResponseContinue() == null
                        || respondentSumTypeItem.getValue().getResponseContinue().equals(YES))
                        && !respondentSumTypeItem.getValue().getRespondentName().equals(firstRespondentName))
                .map(respondentSumTypeItem -> atomicInteger.getAndIncrement() + ". "
                        + respondentSumTypeItem.getValue().getRespondentName())
                .collect(Collectors.toList());
        sb.append("\"resp_others\":\"").append(nullCheck(String.join("\\n", respOthers))).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getRespAddress(CaseData caseData) {
        log.info("Get Resp address");
        var sb = new StringBuilder();
        var atomicInteger = new AtomicInteger(1);
        int size = caseData.getRespondentCollection().size();
        List<String> respAddressList = caseData.getRespondentCollection()
                .stream()
                .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut() == null
                        || respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO)
                        && (respondentSumTypeItem.getValue().getResponseContinue() == null
                        || YES.equals(respondentSumTypeItem.getValue().getResponseContinue())))
                .map(respondentSumTypeItem -> (size > 1 ? atomicInteger.getAndIncrement() + ". " : "")
                        + getRespondentAddressET3(respondentSumTypeItem.getValue()))
                .collect(Collectors.toList());
        sb.append("\"resp_address\":\"").append(nullCheck(String.join("\\n", respAddressList)))
                .append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getHearingData(CaseData caseData, String caseTypeId,
                                                InputStream venueAddressInputStream,
                                                CorrespondenceType correspondenceType,
                                                CorrespondenceScotType correspondenceScotType) {
        log.info("Hearing Data");
        var sb = new StringBuilder();
        //Currently checking collection not the HearingType
        if (caseData.getHearingCollection() != null && !caseData.getHearingCollection().isEmpty()) {
            String correspondenceHearingNumber = getCorrespondenceHearingNumber(
                    correspondenceType, correspondenceScotType);
            log.info("Hearing Number: " + correspondenceHearingNumber);
            var hearingType = getHearingByNumber(caseData.getHearingCollection(), correspondenceHearingNumber);
            log.info("Hearing type info by number");
            if (hearingType.getHearingDateCollection() != null && !hearingType.getHearingDateCollection().isEmpty()) {
                log.info("Hearing dates collection");
                sb.append("\"Hearing_date\":\"").append(nullCheck(getHearingDates(hearingType
                        .getHearingDateCollection()))).append(NEW_LINE);
                String hearingDateAndTime = nullCheck(getHearingDatesAndTime(hearingType.getHearingDateCollection()));
                sb.append("\"Hearing_date_time\":\"").append(hearingDateAndTime).append(NEW_LINE);
                sb.append("\"Hearing_time\":\"").append(getHearingTime(hearingDateAndTime)).append(NEW_LINE);
            } else {
                sb.append("\"Hearing_date\":\"").append(NEW_LINE);
                sb.append("\"Hearing_date_time\":\"").append(NEW_LINE);
                sb.append("\"Hearing_time\":\"").append(NEW_LINE);
            }
            log.info("Checking hearing venue and duration");
            sb.append("\"Hearing_venue\":\"").append(nullCheck(getVenueAddress(
                    hearingType, caseTypeId, venueAddressInputStream))).append(NEW_LINE);
            sb.append("\"Hearing_duration\":\"").append(nullCheck(getHearingDuration(hearingType))).append(NEW_LINE);
        } else {
            sb.append("\"Hearing_date\":\"").append(NEW_LINE);
            sb.append("\"Hearing_date_time\":\"").append(NEW_LINE);
            sb.append("\"Hearing_venue\":\"").append(NEW_LINE);
            sb.append("\"Hearing_duration\":\"").append(NEW_LINE);
            sb.append("\"Hearing_time\":\"").append(NEW_LINE);
        }
        return sb;
    }

    public static String getCorrespondenceHearingNumber(CorrespondenceType correspondenceType,
                                                        CorrespondenceScotType correspondenceScotType) {
        if (correspondenceType != null && correspondenceType.getDynamicHearingNumber() != null) {
            return correspondenceType.getDynamicHearingNumber().getValue().getCode();
        } else if (correspondenceScotType != null && correspondenceScotType.getDynamicHearingNumber() != null) {
            return correspondenceScotType.getDynamicHearingNumber().getValue().getCode();
        } else {
            return null;
        }

    }

    public static HearingType getHearingByNumber(List<HearingTypeItem> hearingCollection,
                                                 String correspondenceHearingNumber) {

        var hearingType = new HearingType();

        for (HearingTypeItem hearingTypeItem : hearingCollection) {
            hearingType = hearingTypeItem.getValue();
            if (hearingType.getHearingNumber() != null
                    && hearingType.getHearingNumber().equals(correspondenceHearingNumber)) {
                break;
            }
        }

        return hearingType;
    }

    private static String getHearingTime(String dateTime) {
        return !dateTime.isEmpty() ? dateTime.substring(dateTime.indexOf("at") + 3) : "";
    }

    private static String getHearingDates(List<DateListedTypeItem> hearingDateCollection) {

        var sb = new StringBuilder();

        List<String> dateListedList = new ArrayList<>();
        for (DateListedTypeItem dateListedTypeItem : hearingDateCollection) {
            if (dateListedTypeItem.getValue().getHearingStatus() != null
                    && dateListedTypeItem.getValue().getHearingStatus().equals(HEARING_STATUS_LISTED)) {
                dateListedList.add(UtilHelper.formatLocalDate(dateListedTypeItem.getValue().getListedDate()));
            }
        }
        sb.append(String.join(", ", dateListedList));

        return sb.toString();
    }

    private static String getHearingDatesAndTime(List<DateListedTypeItem> hearingDateCollection) {

        var sb = new StringBuilder(getHearingDates(hearingDateCollection));
        Iterator<DateListedTypeItem> itr = hearingDateCollection.iterator();
        var earliestTime = LocalTime.of(23, 59);
        var isEmpty = true;

        while (itr.hasNext()) {
            var dateListedType = itr.next().getValue();
            if (dateListedType.getHearingStatus() != null && dateListedType.getHearingStatus()
                    .equals(HEARING_STATUS_LISTED)) {
                var listedDate = LocalDateTime.parse(dateListedType.getListedDate());
                var listedTime = LocalTime.of(listedDate.getHour(), listedDate.getMinute());
                earliestTime = listedTime.isBefore(earliestTime) ? listedTime : earliestTime;
                isEmpty = false;
            }
        }
        if (!isEmpty) {
            sb.append(" at ");
            sb.append(earliestTime.toString());
        }

        return sb.toString();
    }

    private static String getVenueAddress(HearingType hearingType, String caseTypeId,
                                          InputStream venueAddressInputStream) {

        String hearingVenue = getHearingVenue(hearingType, caseTypeId);
        log.info("HearingVenue: " + hearingVenue);
        try (Workbook workbook = new XSSFWorkbook(venueAddressInputStream)) {
            var datatypeSheet = workbook.getSheet(caseTypeId);
            if (datatypeSheet != null) {
                log.info("Processing venue addresses for tab : " + caseTypeId + " within file : "
                        + VENUE_ADDRESS_VALUES_FILE_PATH);
                for (Row currentRow : datatypeSheet) {
                    if (currentRow.getRowNum() == 0) {
                        continue;
                    }
                    String excelHearingVenue = getCellValue(currentRow.getCell(0));
                    log.info("ExcelHearingVenue: " + excelHearingVenue);
                    if (!isNullOrEmpty(excelHearingVenue) && excelHearingVenue.equals(hearingVenue)) {
                        return getCellValue(currentRow.getCell(1));
                    }
                }
            }

        } catch (Exception ex) {
            log.error(VENUE_ADDRESS_OPENING_PROCESSING_ERROR + ex.getMessage());
        }

        return hearingVenue;
    }

    private static String getHearingVenue(HearingType hearingType, String caseTypeId) {
        String hearingVenueToSearch = hearingType.getHearingVenue();
        if (caseTypeId.equals(SCOTLAND_CASE_TYPE_ID)) {
            switch (hearingVenueToSearch) {
                case ABERDEEN_OFFICE:
                    return hearingType.getHearingAberdeen();
                case DUNDEE_OFFICE:
                    return hearingType.getHearingDundee();
                case EDINBURGH_OFFICE:
                    return hearingType.getHearingEdinburgh();
                default:
                    return hearingType.getHearingGlasgow();
            }
        } else {
            return hearingVenueToSearch;
        }
    }

    private static String getCellValue(Cell currentCell) {
        if (currentCell.getCellType() == CellType.STRING) {
            return currentCell.getStringCellValue();
        } else {
            return "";
        }
    }

    static String getHearingDuration(HearingType hearingType) {
        String numType = hearingType.getHearingEstLengthNumType();
        try {
            int tmp = Integer.parseInt(hearingType.getHearingEstLengthNum());
            if (tmp == 1) {
                numType = numType.substring(0, numType.length() - 1);
            }
        } catch (NumberFormatException e) {
            log.error(e.toString());
            numType = hearingType.getHearingEstLengthNumType();
        }
        return String.join(" ",
                hearingType.getHearingEstLengthNum(), numType);
    }

    public static String getTemplateName(CorrespondenceType correspondenceType,
                                         CorrespondenceScotType correspondenceScotType) {
        if (correspondenceType != null) {
            return correspondenceType.getTopLevelDocuments();
        } else {
            if (correspondenceScotType != null) {
                return correspondenceScotType.getTopLevelScotDocuments();
            } else {
                return "";
            }
        }
    }

    public static String getEWSectionName(CorrespondenceType correspondenceType) {
        if (correspondenceType != null) {
            return getEWPartDocument(correspondenceType);
        }
        return "";
    }

    public static String getScotSectionName(CorrespondenceScotType correspondenceScotType) {
        if (correspondenceScotType != null) {
            return getScotPartDocument(correspondenceScotType);
        }
        return "";
    }

    private static String getEWPartDocument(CorrespondenceType correspondence) {
        if (correspondence.getPart0Documents() != null) {
            return correspondence.getPart0Documents();
        }
        if (correspondence.getPart1Documents() != null) {
            return correspondence.getPart1Documents();
        }
        if (correspondence.getPart2Documents() != null) {
            return correspondence.getPart2Documents();
        }
        if (correspondence.getPart3Documents() != null) {
            return correspondence.getPart3Documents();
        }
        if (correspondence.getPart4Documents() != null) {
            return correspondence.getPart4Documents();
        }
        if (correspondence.getPart5Documents() != null) {
            return correspondence.getPart5Documents();
        }
        if (correspondence.getPart6Documents() != null) {
            return correspondence.getPart6Documents();
        }
        if (correspondence.getPart7Documents() != null) {
            return correspondence.getPart7Documents();
        }
        if (correspondence.getPart8Documents() != null) {
            return correspondence.getPart8Documents();
        }
        if (correspondence.getPart9Documents() != null) {
            return correspondence.getPart9Documents();
        }
        if (correspondence.getPart10Documents() != null) {
            return correspondence.getPart10Documents();
        }
        if (correspondence.getPart11Documents() != null) {
            return correspondence.getPart11Documents();
        }
        if (correspondence.getPart12Documents() != null) {
            return correspondence.getPart12Documents();
        }
        if (correspondence.getPart13Documents() != null) {
            return correspondence.getPart13Documents();
        }
        if (correspondence.getPart14Documents() != null) {
            return correspondence.getPart14Documents();
        }
        if (correspondence.getPart15Documents() != null) {
            return correspondence.getPart15Documents();
        }
        if (correspondence.getPart16Documents() != null) {
            return correspondence.getPart16Documents();
        }
        if (correspondence.getPart17Documents() != null) {
            return correspondence.getPart17Documents();
        }
        if (correspondence.getPart18Documents() != null) {
            return correspondence.getPart18Documents();
        }
        if (correspondence.getPart20Documents() != null) {
            return correspondence.getPart20Documents();
        }
        return "";
    }

    private static String getScotPartDocument(CorrespondenceScotType correspondenceScotType) {
        if (correspondenceScotType.getPart0ScotDocuments() != null) {
            return correspondenceScotType.getPart0ScotDocuments();
        }
        if (correspondenceScotType.getPart1ScotDocuments() != null) {
            return correspondenceScotType.getPart1ScotDocuments();
        }
        if (correspondenceScotType.getPart2ScotDocuments() != null) {
            return correspondenceScotType.getPart2ScotDocuments();
        }
        if (correspondenceScotType.getPart3ScotDocuments() != null) {
            return correspondenceScotType.getPart3ScotDocuments();
        }
        if (correspondenceScotType.getPart4ScotDocuments() != null) {
            return correspondenceScotType.getPart4ScotDocuments();
        }
        if (correspondenceScotType.getPart5ScotDocuments() != null) {
            return correspondenceScotType.getPart5ScotDocuments();
        }
        if (correspondenceScotType.getPart6ScotDocuments() != null) {
            return correspondenceScotType.getPart6ScotDocuments();
        }
        if (correspondenceScotType.getPart7ScotDocuments() != null) {
            return correspondenceScotType.getPart7ScotDocuments();
        }
        if (correspondenceScotType.getPart8ScotDocuments() != null) {
            return correspondenceScotType.getPart8ScotDocuments();
        }
        if (correspondenceScotType.getPart9ScotDocuments() != null) {
            return correspondenceScotType.getPart9ScotDocuments();
        }
        if (correspondenceScotType.getPart10ScotDocuments() != null) {
            return correspondenceScotType.getPart10ScotDocuments();
        }
        if (correspondenceScotType.getPart11ScotDocuments() != null) {
            return correspondenceScotType.getPart11ScotDocuments();
        }
        if (correspondenceScotType.getPart12ScotDocuments() != null) {
            return correspondenceScotType.getPart12ScotDocuments();
        }
        if (correspondenceScotType.getPart13ScotDocuments() != null) {
            return correspondenceScotType.getPart13ScotDocuments();
        }
        if (correspondenceScotType.getPart14ScotDocuments() != null) {
            return correspondenceScotType.getPart14ScotDocuments();
        }
        if (correspondenceScotType.getPart15ScotDocuments() != null) {
            return correspondenceScotType.getPart15ScotDocuments();
        }
        if (correspondenceScotType.getPart16ScotDocuments() != null) {
            return correspondenceScotType.getPart16ScotDocuments();
        }
        return "";
    }

    private static StringBuilder getCorrespondenceData(CorrespondenceType correspondence) {
        log.info("Correspondence data");
        String sectionName = getEWSectionName(correspondence);
        var sb = new StringBuilder();
        if (!sectionName.equals("")) {
            sb.append("\"").append("t").append(sectionName.replace(".", "_"))
                    .append("\":\"").append("true").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getCorrespondenceScotData(CorrespondenceScotType correspondenceScotType) {
        log.info("Correspondence scot data");
        String scotSectionName = getScotSectionName(correspondenceScotType);
        var sb = new StringBuilder();
        if (!scotSectionName.equals("")) {
            sb.append("\"").append("t_Scot_").append(scotSectionName.replace(".", "_"))
                    .append("\":\"").append("true").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getCourtData(CaseData caseData, DefaultValues allocatedCourtAddress) {
        var sb = new StringBuilder();
        log.info("Court data");
        if (allocatedCourtAddress != null) {
            sb.append("\"Court_addressLine1\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceAddressLine1())).append(NEW_LINE);
            sb.append("\"Court_addressLine2\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceAddressLine2())).append(NEW_LINE);
            sb.append("\"Court_addressLine3\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceAddressLine3())).append(NEW_LINE);
            sb.append("\"Court_town\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceTown())).append(NEW_LINE);
            sb.append("\"Court_county\":\"").append(NEW_LINE);
            sb.append("\"Court_postCode\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondencePostCode())).append(NEW_LINE);
            sb.append("\"Court_telephone\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceTelephone())).append(NEW_LINE);
            sb.append("\"Court_fax\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceFax())).append(NEW_LINE);
            sb.append("\"Court_DX\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceDX())).append(NEW_LINE);
            sb.append("\"Court_Email\":\"").append(nullCheck(allocatedCourtAddress
                    .getTribunalCorrespondenceEmail())).append(NEW_LINE);
        } else {
            if (caseData.getTribunalCorrespondenceAddress() != null) {
                sb.append("\"Court_addressLine1\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getAddressLine1())).append(NEW_LINE);
                sb.append("\"Court_addressLine2\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getAddressLine2())).append(NEW_LINE);
                sb.append("\"Court_addressLine3\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getAddressLine3())).append(NEW_LINE);
                sb.append("\"Court_town\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getPostTown())).append(NEW_LINE);
                sb.append("\"Court_county\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getCounty())).append(NEW_LINE);
                sb.append("\"Court_postCode\":\"").append(nullCheck(caseData
                        .getTribunalCorrespondenceAddress().getPostCode())).append(NEW_LINE);
            }
            sb.append("\"Court_telephone\":\"").append(nullCheck(caseData
                    .getTribunalCorrespondenceTelephone())).append(NEW_LINE);
            sb.append("\"Court_fax\":\"").append(nullCheck(caseData
                    .getTribunalCorrespondenceFax())).append(NEW_LINE);
            sb.append("\"Court_DX\":\"").append(nullCheck(caseData
                    .getTribunalCorrespondenceDX())).append(NEW_LINE);
            sb.append("\"Court_Email\":\"").append(nullCheck(caseData
                    .getTribunalCorrespondenceEmail())).append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getAddressLabelsDataSingleCase(CaseData caseData) {

        var numberOfCopies = Integer.parseInt(caseData.getAddressLabelsAttributesType().getNumberOfCopies());
        var startingLabel = Integer.parseInt(caseData.getAddressLabelsAttributesType().getStartingLabel());
        String showTelFax = caseData.getAddressLabelsAttributesType().getShowTelFax();
        List<AddressLabelTypeItem> addressLabelCollection = caseData.getAddressLabelCollection();

        return getAddressLabelsData(numberOfCopies, startingLabel, showTelFax, addressLabelCollection);

    }

    private static StringBuilder getAddressLabelsDataMultipleCase(MultipleData multipleData) {

        var numberOfCopies = Integer.parseInt(multipleData.getAddressLabelsAttributesType().getNumberOfCopies());
        var startingLabel = Integer.parseInt(multipleData.getAddressLabelsAttributesType().getStartingLabel());
        String showTelFax = multipleData.getAddressLabelsAttributesType().getShowTelFax();
        List<AddressLabelTypeItem> addressLabelCollection = multipleData.getAddressLabelCollection();

        return getAddressLabelsData(numberOfCopies, startingLabel, showTelFax, addressLabelCollection);

    }

    private static StringBuilder getAddressLabelsData(int numberOfCopies, int startingLabel, String showTelFax,
                                                      List<AddressLabelTypeItem> addressLabelCollection) {

        List<AddressLabelTypeItem> selectedAddressLabelCollection = getSelectedAddressLabels(addressLabelCollection);
        List<AddressLabelTypeItem> copiedAddressLabelCollection =
                getCopiedAddressLabels(selectedAddressLabelCollection, numberOfCopies);

        var sb = new StringBuilder();
        sb.append("\"address_labels_page\":[\n");

        var startingLabelAboveOne = true;

        for (var i = 0; i < copiedAddressLabelCollection.size(); i++) {
            int pageLabelNumber = i + 1;

            if (startingLabel > 1) {
                pageLabelNumber += startingLabel - 1;
            }

            if (pageLabelNumber > ADDRESS_LABELS_PAGE_SIZE) {
                int numberOfFullLabelPages = pageLabelNumber / ADDRESS_LABELS_PAGE_SIZE;
                pageLabelNumber = pageLabelNumber % ADDRESS_LABELS_PAGE_SIZE == 0
                        ? ADDRESS_LABELS_PAGE_SIZE
                        : pageLabelNumber - (numberOfFullLabelPages * ADDRESS_LABELS_PAGE_SIZE);
            }

            if (pageLabelNumber == 1 || startingLabelAboveOne) {
                startingLabelAboveOne = false;
                sb.append("{");
            }

            String templateLabelNumber = (pageLabelNumber < 10)
                    ? "0" + pageLabelNumber : String.valueOf(pageLabelNumber);
            sb.append(getAddressLabel(copiedAddressLabelCollection.get(i).getValue(), templateLabelNumber, showTelFax));

            if (pageLabelNumber == ADDRESS_LABELS_PAGE_SIZE || i == copiedAddressLabelCollection.size() - 1) {
                sb.append("}");
            }

            if (i != copiedAddressLabelCollection.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("],\n");
        return sb;
    }

    private static StringBuilder getAddressLabel(AddressLabelType addressLabelType,
                                                 String labelNumber, String showTelFax) {
        var sb = new StringBuilder();
        sb.append("\"").append(LABEL).append(labelNumber).append("_Entity_Name_01\":\"")
                .append(nullCheck(addressLabelType.getLabelEntityName01())).append(NEW_LINE);
        sb.append("\"").append(LABEL).append(labelNumber).append("_Entity_Name_02\":\"")
                .append(nullCheck(addressLabelType.getLabelEntityName02())).append(NEW_LINE);
        sb.append(getAddressLines(addressLabelType, labelNumber));
        sb.append(getTelFaxLine(addressLabelType, labelNumber, showTelFax));
        sb.append("\"").append(LBL).append(labelNumber).append("_Eef\":\"")
                .append(nullCheck(addressLabelType.getLabelEntityReference())).append(NEW_LINE);
        sb.append("\"").append(LBL).append(labelNumber).append("_Cef\":\"")
                .append(nullCheck(addressLabelType.getLabelCaseReference())).append("\"");
        return sb;
    }

    private static StringBuilder getAddressLines(AddressLabelType addressLabelType, String labelNumber) {
        var sb = new StringBuilder();

        var lineNum = 0;
        var addressLine = "";

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine1()))) {
            lineNum++;
            addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine1());
            sb.append(getAddressLine(addressLine, labelNumber, lineNum));
        }

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine2()))) {
            lineNum++;
            addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine2());
            sb.append(getAddressLine(addressLine, labelNumber, lineNum));
        }

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine3()))) {
            lineNum++;
            addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getAddressLine3());
            sb.append(getAddressLine(addressLine, labelNumber, lineNum));
        }

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getPostTown()))) {
            lineNum++;
            addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getPostTown());
            sb.append(getAddressLine(addressLine, labelNumber, lineNum));
        }

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getCounty()))) {
            lineNum++;
            addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getCounty());
            if (lineNum < 5) {
                sb.append(getAddressLine(addressLine, labelNumber, lineNum));
            }
        }

        if (!isNullOrEmpty(nullCheck(addressLabelType.getLabelEntityAddress().getPostCode()))) {
            if (lineNum < 5) {
                lineNum++;
                addressLine = nullCheck(addressLabelType.getLabelEntityAddress().getPostCode());
            } else {
                addressLine += " " + nullCheck(addressLabelType.getLabelEntityAddress().getPostCode());
            }
            sb.append(getAddressLine(addressLine, labelNumber, lineNum));
        }
        return sb;
    }

    private static StringBuilder getAddressLine(String addressLine, String labelNumber, int lineNum) {
        var sb = new StringBuilder();
        String lineNumber = "0" + lineNum;
        sb.append("\"").append(LABEL).append(labelNumber).append("_Address_Line_").append(lineNumber)
                .append("\":\"").append(addressLine).append(NEW_LINE);
        return sb;
    }

    private static StringBuilder getTelFaxLine(AddressLabelType addressLabelType, String labelNumber,
                                               String showTelFax) {
        var sb = new StringBuilder();
        if (showTelFax.equals(YES)) {
            var tel = "";
            var fax = "";

            if (!isNullOrEmpty(addressLabelType.getLabelEntityTelephone())) {
                tel = addressLabelType.getLabelEntityTelephone();
            }

            if (isNullOrEmpty(tel)) {
                if (!isNullOrEmpty(addressLabelType.getLabelEntityFax())) {
                    tel = addressLabelType.getLabelEntityFax();
                }
            } else {
                if (!isNullOrEmpty(addressLabelType.getLabelEntityFax())) {
                    fax = addressLabelType.getLabelEntityFax();
                }
            }

            sb.append("\"").append(LABEL).append(labelNumber).append("_Telephone\":\"").append(tel).append(NEW_LINE);
            sb.append("\"").append(LABEL).append(labelNumber).append("_Fax\":\"").append(fax).append(NEW_LINE);
        }
        return sb;
    }

    public static List<AddressLabelTypeItem> getSelectedAddressLabels(
            List<AddressLabelTypeItem> addressLabelCollection) {

        List<AddressLabelTypeItem> selectedAddressLabels = new ArrayList<>();

        if (addressLabelCollection != null && !addressLabelCollection.isEmpty()) {
            selectedAddressLabels = addressLabelCollection
                    .stream()
                    .filter(addressLabelTypeItem -> addressLabelTypeItem.getValue().getPrintLabel() != null
                            && addressLabelTypeItem.getValue().getPrintLabel().equals(YES))
                    .filter(addressLabelTypeItem -> addressLabelTypeItem.getValue().getFullName() != null
                            || addressLabelTypeItem.getValue().getFullAddress() != null)
                    .collect(Collectors.toList());
        }

        return selectedAddressLabels;
    }

    private static List<AddressLabelTypeItem> getCopiedAddressLabels(List<AddressLabelTypeItem> selectedAddressLabels,
                                                                     int numberOfCopies) {

        List<AddressLabelTypeItem> copiedAddressLabels = new ArrayList<>();
        if (!selectedAddressLabels.isEmpty() && numberOfCopies > 1) {
            for (AddressLabelTypeItem selectedAddressLabel : selectedAddressLabels) {
                var addressLabelType = selectedAddressLabel.getValue();
                for (var i = 0; i < numberOfCopies; i++) {
                    var addressLabelTypeItem = new AddressLabelTypeItem();
                    addressLabelTypeItem.setId(String.valueOf(copiedAddressLabels.size()));
                    addressLabelTypeItem.setValue(addressLabelType);
                    copiedAddressLabels.add(addressLabelTypeItem);
                }
            }
        } else {
            return selectedAddressLabels;
        }

        return copiedAddressLabels;
    }

    public static Address getRespondentAddressET3(RespondentSumType respondentSumType) {

        log.info("Get respondent address ET3");

        return (YES.equals(respondentSumType.getResponseReceived())
                && respondentSumType.getResponseRespondentAddress() != null
                && !Strings.isNullOrEmpty(respondentSumType.getResponseRespondentAddress().toString()))
                ? respondentSumType.getResponseRespondentAddress()
                : respondentSumType.getRespondentAddress();

    }

    public static void setSecondLevelDocumentFromType(DocumentType documentType, String typeOfDocument) {
        switch (typeOfDocument) {
            case ET1, ET1_ATTACHMENT, ACAS_CERTIFICATE, NOTICE_OF_CLAIM, CLAIM_ACCEPTED, CLAIM_REJECTED,
                    CLAIM_PART_REJECTED, ET1_VETTING -> documentType.setStartingClaimDocuments(typeOfDocument);
            case ET3, ET3_ATTACHMENT, RESPONSE_ACCEPTED, RESPONSE_REJECTED, APP_TO_EXTEND_TIME_TO_PRESENT_A_RESPONSE,
                    ET3_PROCESSING -> documentType.setResponseClaimDocuments(typeOfDocument);
            case INITIAL_CONSIDERATION, RULE_27_NOTICE, RULE_28_NOTICE
                    -> documentType.setInitialConsiderationDocuments(typeOfDocument);
            case TRIBUNAL_ORDER, DEPOSIT_ORDER, UNLESS_ORDER, TRIBUNAL_NOTICE, APP_TO_VARY_AN_ORDER_C,
                    APP_TO_VARY_AN_ORDER_R, APP_TO_REVOKE_AN_ORDER_C, APP_TO_REVOKE_AN_ORDER_R,
                    APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_C,
                    APP_TO_EXTEND_TIME_TO_COMPLY_TO_AN_ORDER_DIRECTIONS_R, APP_TO_ORDER_THE_R_TO_DO_SOMETHING,
                    APP_TO_ORDER_THE_C_TO_DO_SOMETHING, APP_TO_AMEND_CLAIM, APP_TO_AMEND_RESPONSE,
                    APP_FOR_A_WITNESS_ORDER_C, DISABILITY_IMPACT_STATEMENT, R_HAS_NOT_COMPLIED_WITH_AN_ORDER_C,
                    C_HAS_NOT_COMPLIED_WITH_AN_ORDER_R, APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_CLAIM,
                    APP_TO_STRIKE_OUT_ALL_OR_PART_OF_THE_RESPONSE, REFERRAL_JUDICIAL_DIRECTION,
                    CHANGE_OF_PARTYS_DETAILS, APP_TO_VARY_OR_REVOKE_AN_ORDER_R, APP_TO_VARY_OR_REVOKE_AN_ORDER_C,
                    CONTACT_THE_TRIBUNAL_C, CONTACT_THE_TRIBUNAL_R, APP_FOR_A_WITNESS_ORDER_R
                    -> documentType.setCaseManagementDocuments(typeOfDocument);
            case WITHDRAWAL_OF_ENTIRE_CLAIM, WITHDRAWAL_OF_PART_OF_CLAIM, COT3, WITHDRAWAL_OF_ALL_OR_PART_CLAIM
                    -> documentType.setWithdrawalSettledDocuments(typeOfDocument);
            case APP_TO_RESTRICT_PUBLICITY_C, APP_TO_RESTRICT_PUBLICITY_R, ANONYMITY_ORDER, NOTICE_OF_HEARING,
                    APP_TO_POSTPONE_C, APP_TO_POSTPONE_R, HEARING_BUNDLE, SCHEDULE_OF_LOSS, COUNTER_SCHEDULE_OF_LOSS
                    -> documentType.setHearingsDocuments(typeOfDocument);
            case JUDGMENT, JUDGMENT_WITH_REASONS, REASONS, EXTRACT_OF_JUDGMENT
                    -> documentType.setJudgmentAndReasonsDocuments(typeOfDocument);
            case APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_C,
                    APP_TO_HAVE_A_LEGAL_OFFICER_DECISION_CONSIDERED_AFRESH_R, APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_C,
                    APP_FOR_A_JUDGMENT_TO_BE_RECONSIDERED_R -> documentType.setReconsiderationDocuments(typeOfDocument);
            case CERTIFICATE_OF_CORRECTION, TRIBUNAL_CASE_FILE, OTHER -> documentType.setMiscDocuments(typeOfDocument);
            default -> documentType.setTypeOfDocument(typeOfDocument);
        }
    }

    /**
     * Add document numbers to each of the docs in the case.
     * @param caseData CaseData
     */
    public static void setDocumentNumbers(uk.gov.hmcts.ecm.common.model.ccd.CaseData caseData) {
        if (CollectionUtils.isEmpty(caseData.getDocumentCollection())) {
            return;
        }
        caseData.getDocumentCollection().forEach(documentTypeItem -> {
            DocumentType documentType = documentTypeItem.getValue();
            documentType.setDocNumber(String.valueOf(caseData.getDocumentCollection()
                    .indexOf(documentTypeItem) + 1));
        });
    }

    /**
     * Create a new DocumentTypeItem, copy from uploadedDocumentType and update TypeOfDocument.
     * @param uploadedDocumentType UploadedDocumentType to be added
     * @param topLevel top level document
     * @param secondLevel second level document
     * @return DocumentTypeItem
     */
    public static DocumentTypeItem createDocumentTypeItemFromTopLevel(UploadedDocumentType uploadedDocumentType,
                                                                      String topLevel,
                                                                      String secondLevel,
                                                                      String shortDescription) {
        DocumentTypeItem documentTypeItem = fromUploadedDocument(uploadedDocumentType);
        DocumentType documentType = documentTypeItem.getValue();
        documentType.setShortDescription(shortDescription);
        documentType.setDateOfCorrespondence(LocalDate.now().toString());
        documentType.setTopLevelDocuments(topLevel);
        setSecondLevelDocumentFromType(documentType, secondLevel);
        return documentTypeItem;
    }

    public static DocumentTypeItem fromUploadedDocument(UploadedDocumentType uploadedDocumentType) {
        DocumentType docType = new DocumentType();
        docType.setUploadedDocument(uploadedDocumentType);
        DocumentTypeItem docTypeItem = new DocumentTypeItem();
        docTypeItem.setValue(docType);
        return docTypeItem;
    }
}
