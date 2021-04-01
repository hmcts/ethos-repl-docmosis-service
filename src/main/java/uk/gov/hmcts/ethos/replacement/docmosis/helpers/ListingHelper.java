package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.types.DateListedType;
import uk.gov.hmcts.ecm.common.model.ccd.types.HearingType;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.items.ListingTypeItem;
import uk.gov.hmcts.ecm.common.model.listing.types.ListingType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_ABERDEEN_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_DUNDEE_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_EDINBURGH_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_GLASGOW_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_IT56;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_IT57;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PRESS_LIST;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PUBLIC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IT56_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IT57_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PUBLIC_CASE_CAUSE_LIST_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RULE_50_APPLIES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.STAFF_CASE_CAUSE_LIST_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

@Slf4j
public class ListingHelper {

    private static final String ROOM_NOT_ALLOCATED = "* Not Allocated";

    private ListingHelper() {

    }

    public static ListingType getListingTypeFromCaseData(ListingData listingData, CaseData caseData,
                                                         HearingType hearingType, DateListedType dateListedType,
                                                         int index, int hearingCollectionSize) {
        ListingType listingType = new ListingType();

        try {
            log.info("started getListingTypeFromCaseData");
            listingType.setElmoCaseReference(caseData.getEthosCaseReference());
            String listedDate = dateListedType.getListedDate();
            listingType.setCauseListDate(!isNullOrEmpty(listedDate) ? UtilHelper.formatLocalDate(listedDate) : " ");
            listingType.setCauseListTime(!isNullOrEmpty(listedDate) ? UtilHelper.formatLocalTime(listedDate) : " ");
            log.info("getJurCodesCollection");
            listingType.setJurisdictionCodesList(BulkHelper.getJurCodesCollection(caseData.getJurCodesCollection()));
            listingType
                    .setHearingType(!isNullOrEmpty(hearingType.getHearingType()) ? hearingType.getHearingType() : " ");
            listingType.setPositionType(!isNullOrEmpty(caseData.getPositionType()) ? caseData.getPositionType() : " ");
            listingType.setHearingJudgeName(!isNullOrEmpty(hearingType.getJudge()) ? hearingType.getJudge() : " ");
            listingType.setHearingEEMember(!isNullOrEmpty(hearingType.getHearingEEMember())
                    ? hearingType.getHearingEEMember()
                    : " ");
            listingType.setHearingERMember(!isNullOrEmpty(hearingType.getHearingERMember())
                    ? hearingType.getHearingERMember()
                    : " ");
            listingType.setHearingClerk(!isNullOrEmpty(dateListedType.getHearingClerk())
                    ? dateListedType.getHearingClerk()
                    : " ");
            listingType.setHearingPanel(!isNullOrEmpty(hearingType.getHearingSitAlone())
                    ? hearingType.getHearingSitAlone()
                    : " ");

            log.info("getVenueFromDateListedType");
            listingType.setCauseListVenue(getVenueFromDateListedType(dateListedType));

            log.info("getHearingRoom");
            listingType.setHearingRoom(getHearingRoom(dateListedType));

            log.info("getHearingNotes");
            listingType.setHearingNotes(!isNullOrEmpty(hearingType.getHearingNotes())
                    ? hearingType.getHearingNotes()
                    : " ");

            log.info("getHearingDuration");
            listingType.setHearingDay(index + 1 + " of " + hearingCollectionSize);
            listingType.setEstHearingLength(!isNullOrEmpty(DocumentHelper.getHearingDuration(hearingType))
                    ? DocumentHelper.getHearingDuration(hearingType)
                    : " ");

            log.info("End getListingTypeFromCaseData");
            return getClaimantRespondentDetails(listingType, listingData, caseData);

        } catch (Exception ex) {
            log.error("ListingData: " + listingData);
            log.error("CaseData: " + caseData);
            log.error("HearingType: " + hearingType);
            log.error("DateListedType: " + dateListedType);
            log.error("index: " + index);
            log.error("hearingCollectionSize: " + hearingCollectionSize);
            return listingType;
        }
    }

    private static String getHearingRoom(DateListedType dateListedType) {
        for (Method m: dateListedType.getClass().getDeclaredMethods()) {
            if (m.getName().startsWith("getHearingRoom")) {
                try {
                    String room = (String)m.invoke(dateListedType);
                    if (!isNullOrEmpty(room)) {
                        return room;
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Error getting hearing room:", e);
                }
            }
        }
        return " ";
    }

    private static ListingType getClaimantRespondentDetails(ListingType listingType, ListingData listingData,
                                                            CaseData caseData) {
        listingType.setClaimantTown(" ");
        listingType.setRespondentTown(" ");
        listingType.setRespondentOthers(" ");
        listingType.setClaimantRepresentative(" ");
        listingType.setRespondentRepresentative(" ");
        boolean isPublicType = listingData.getHearingDocType() != null
                && listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PUBLIC);
        boolean isPressListType = listingData.getHearingDocType() != null
                && listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PRESS_LIST);
        boolean rule50d = caseData.getRestrictedReporting() != null
                && caseData.getRestrictedReporting().getImposed() != null
                && caseData.getRestrictedReporting().getImposed().equals(YES);
        boolean rule50b = caseData.getRestrictedReporting() != null
                && caseData.getRestrictedReporting().getRule503b() != null
                && caseData.getRestrictedReporting().getRule503b().equals(YES);
        if ((rule50b && isPublicType) || (rule50d && isPublicType)) {
            listingType.setClaimantName(" ");
            listingType.setRespondent(" ");
        } else if ((rule50b && isPressListType) || (rule50d && isPressListType)) {
            listingType.setClaimantName(RULE_50_APPLIES);
            listingType.setRespondent(RULE_50_APPLIES);
        } else {
            if (!isNullOrEmpty(caseData.getClaimantCompany())) {
                log.info("Company claimant");
                listingType.setClaimantName(caseData.getClaimantCompany());
            } else {
                log.info("Claimant name");
                listingType.setClaimantName(caseData.getClaimantIndType() != null
                        && caseData.getClaimantIndType().getClaimantLastName() != null
                        ? caseData.getClaimantIndType().claimantFullName()
                        : " ");
            }
            log.info("Claimant address");
            listingType.setClaimantTown(caseData.getClaimantType() != null
                    && caseData.getClaimantType().getClaimantAddressUK() != null
                    && caseData.getClaimantType().getClaimantAddressUK().getPostTown() != null
                    ? caseData.getClaimantType().getClaimantAddressUK().getPostTown()
                    : " ");
            listingType.setRespondent(caseData.getRespondentCollection() != null
                    && !caseData.getRespondentCollection().isEmpty()
                    && caseData.getRespondentCollection().get(0).getValue() != null
                    ? caseData.getRespondentCollection().get(0).getValue().getRespondentName()
                    : " ");
            listingType.setRespondentTown(caseData.getRespondentCollection() != null
                    && !caseData.getRespondentCollection().isEmpty()
                    && caseData.getRespondentCollection().get(0).getValue() != null
                    && DocumentHelper.getRespondentAddressET3(
                            caseData.getRespondentCollection().get(0).getValue()) != null
                    && DocumentHelper.getRespondentAddressET3(
                            caseData.getRespondentCollection().get(0).getValue()).getPostTown() != null
                    ? DocumentHelper.getRespondentAddressET3(
                            caseData.getRespondentCollection().get(0).getValue()).getPostTown()
                    : " ");
            log.info("getRespOthersName");
            listingType.setRespondentOthers(!isNullOrEmpty(getRespOthersName(caseData))
                    ? getRespOthersName(caseData)
                    : " ");
            listingType.setClaimantRepresentative(caseData.getRepresentativeClaimantType() != null
                    && caseData.getRepresentativeClaimantType().getNameOfOrganisation() != null
                    ? caseData.getRepresentativeClaimantType().getNameOfOrganisation()
                    : " ");
            listingType.setRespondentRepresentative(caseData.getRepCollection() != null
                    && !caseData.getRepCollection().isEmpty()
                    && caseData.getRepCollection().get(0).getValue() != null
                    && caseData.getRepCollection().get(0).getValue().getNameOfOrganisation() != null
                    ? caseData.getRepCollection().get(0).getValue().getNameOfOrganisation()
                    : " ");
        }
        return listingType;
    }

    public static StringBuilder buildListingDocumentContent(ListingData listingData, String accessKey,
                                                            String templateName, UserDetails userDetails,
                                                            String caseType) {
        StringBuilder sb = new StringBuilder();

        // Start building the instruction
        sb.append("{\n");
        sb.append("\"accessKey\":\"").append(accessKey).append(NEW_LINE);
        sb.append("\"templateName\":\"").append(templateName).append(FILE_EXTENSION).append(NEW_LINE);
        sb.append("\"outputName\":\"").append(OUTPUT_FILE_NAME).append(NEW_LINE);

        log.info("Building document data");
        // Building the document data
        sb.append("\"data\":{\n");
        log.info("Getting court listing");
        sb.append(getCourtListingData(listingData));
        log.info("Getting logo");
        sb.append(getLogo(caseType));
        if (listingData.getListingCollection() != null && !listingData.getListingCollection().isEmpty()) {
            sb.append("\"Listed_date\":\"").append(listingData.getListingCollection().get(0).getValue()
                    .getCauseListDate()).append(NEW_LINE);
            sb.append("\"Hearing_location\":\"").append(!listingData.getListingVenue().equals(ALL_VENUES)
                    ? listingData.getListingCollection().get(0).getValue().getCauseListVenue()
                    : ALL_VENUES).append(NEW_LINE);
        }
        log.info("Listings range dates");
        sb.append(getListingRangeDates(listingData));
        log.info("Clerk");
        String userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        log.info("Clerk Username: " + userName);
        sb.append("\"Clerk\":\"").append(nullCheck(userName)).append(NEW_LINE);

        if (listingData.getListingCollection() != null && !listingData.getListingCollection().isEmpty()) {
            sortListingCollectionByDateAndVenue(listingData);
            sb.append(getDocumentData(listingData, templateName, caseType));
        }

        log.info("Document data ends");
        sb.append("\"case_total\":\"").append(getCaseTotal(listingData.getListingCollection())).append(NEW_LINE);
        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append("\"\n");
        sb.append("}\n");
        sb.append("}\n");
        return sb;
    }

    private static StringBuilder getLogo(String caseType) {
        StringBuilder sb = new StringBuilder();
        if (caseType.equals(SCOTLAND_LISTING_CASE_TYPE_ID)) {
            sb.append("\"listing_logo\":\"").append("[userImage:").append("schmcts.png]").append(NEW_LINE);
        } else {
            sb.append("\"listing_logo\":\"").append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getDocumentData(ListingData listingData, String templateName, String caseType) {
        if (Arrays.asList(IT56_TEMPLATE, IT57_TEMPLATE, PUBLIC_CASE_CAUSE_LIST_TEMPLATE, STAFF_CASE_CAUSE_LIST_TEMPLATE,
                PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE, PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE).contains(templateName)) {
            return getCaseCauseList(listingData, caseType);
        } else if (Arrays.asList(PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE)
                .contains(templateName)) {
            return getCaseCauseListByRoom(listingData, caseType);
        } else {
            return new StringBuilder();
        }
    }

    public static StringBuilder getListingRangeDates(ListingData listingData) {
        StringBuilder sb = new StringBuilder();
        if (listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE)) {
            sb.append("\"Listed_date_from\":\"").append(
                    UtilHelper.listingFormatLocalDate(listingData.getListingDateFrom())).append(NEW_LINE);
            sb.append("\"Listed_date_to\":\"").append(
                    UtilHelper.listingFormatLocalDate(listingData.getListingDateTo())).append(NEW_LINE);
        }
        return sb;
    }

    private static boolean isEmptyHearingRoom(ListingType listingType) {
        if (listingType.getHearingRoom() != null) {
            return listingType.getHearingRoom().trim().isEmpty();
        }
        return true;
    }

    private static Map<String, List<ListingTypeItem>> getListHearingsByRoomWithNotAllocated(ListingData listingData) {
        Map<String, List<ListingTypeItem>> unsortedMap = listingData.getListingCollection()
                .stream()
                .filter(listingTypeItem -> !isEmptyHearingRoom(listingTypeItem.getValue()))
                .collect(Collectors.groupingBy(listingTypeItem -> listingTypeItem.getValue().getHearingRoom()));
        List<ListingTypeItem> notAllocated = listingData.getListingCollection()
                .stream()
                .filter(listingTypeItem -> isEmptyHearingRoom(listingTypeItem.getValue()))
                .collect(Collectors.toList());
        if (!notAllocated.isEmpty()){
            unsortedMap.computeIfAbsent(ROOM_NOT_ALLOCATED, k -> new ArrayList<>()).addAll(notAllocated);
        }
        return unsortedMap;
    }

    private static StringBuilder getCaseCauseListByRoom(ListingData listingData, String caseType) {
        StringBuilder sb = new StringBuilder();
        Map<String, List<ListingTypeItem>> unsortedMap = getListHearingsByRoomWithNotAllocated(listingData);
        sb.append("\"location\":[\n");
        Iterator<Map.Entry<String, List<ListingTypeItem>>> entries = new TreeMap<>(unsortedMap).entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<ListingTypeItem>> listingEntry = entries.next();
            sb.append("{\"Hearing_room\":\"").append(listingEntry.getKey()).append(NEW_LINE);
            //sb.append("\"Floor\":\"").append("6th Floor").append(NEW_LINE);
            sb.append("\"listing\":[\n");
            for (int i = 0; i < listingEntry.getValue().size(); i++) {
                sb.append(getListingTypeRow(listingEntry.getValue().get(i).getValue(), caseType, listingData));
                if (i != listingEntry.getValue().size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("]\n");
            if (entries.hasNext()) {
                sb.append("},\n");
            } else {
                sb.append("}],\n");
            }
        }
        return sb;
    }

    private static StringBuilder getCaseCauseList(ListingData listingData, String caseType) {
        List<ListingTypeItem> listingTypeItems = listingData.getListingCollection();
        StringBuilder sb = new StringBuilder();
        sb.append("\"listing\":[\n");
        for (int i = 0; i < listingTypeItems.size(); i++) {
            sb.append(getListingTypeRow(listingTypeItems.get(i).getValue(), caseType, listingData));
            if (i != listingTypeItems.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("],\n");
        return sb;
    }

    private static StringBuilder getListingTypeRow(ListingType listingType, String caseType, ListingData listingData) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"Judge\":\"").append(nullCheck(extractHearingJudgeName(listingType))).append(NEW_LINE);
        sb.append(getCourtListingData(listingData));
        log.info("Court listing data");
        sb.append(getLogo(caseType));
        log.info("Logo listing data");
        sb.append("\"ERMember\":\"").append(nullCheck(listingType.getHearingERMember())).append(NEW_LINE);
        sb.append("\"EEMember\":\"").append(nullCheck(listingType.getHearingEEMember())).append(NEW_LINE);
        sb.append("\"Case_No\":\"").append(nullCheck(listingType.getElmoCaseReference())).append(NEW_LINE);
        sb.append("\"Hearing_type\":\"").append(nullCheck(listingType.getHearingType())).append(NEW_LINE);
        sb.append("\"Jurisdictions\":\"").append(nullCheck(listingType.getJurisdictionCodesList())).append(NEW_LINE);
        sb.append("\"Hearing_date\":\"").append(nullCheck(listingType.getCauseListDate())).append(NEW_LINE);
        sb.append("\"Hearing_date_time\":\"").append(nullCheck(listingType.getCauseListDate())).append(" at ").append(
                nullCheck(listingType.getCauseListTime())).append(NEW_LINE);
        sb.append("\"Hearing_time\":\"").append(nullCheck(listingType.getCauseListTime())).append(NEW_LINE);
        sb.append("\"Hearing_duration\":\"").append(nullCheck(listingType.getEstHearingLength())).append(NEW_LINE);
        log.info("Hearing clerk");
        sb.append("\"Hearing_clerk\":\"").append(nullCheck(listingType.getHearingClerk())).append(NEW_LINE);
        log.info("Hearing clerk ends");
        sb.append("\"Claimant\":\"").append(nullCheck(listingType.getClaimantName())).append(NEW_LINE);
        sb.append("\"claimant_town\":\"").append(nullCheck(listingType.getClaimantTown())).append(NEW_LINE);
        sb.append("\"claimant_representative\":\"").append(
                nullCheck(listingType.getClaimantRepresentative())).append(NEW_LINE);
        sb.append("\"Respondent\":\"").append(
                nullCheck(listingType.getRespondent())).append(NEW_LINE);
        log.info("Resp others");
        sb.append("\"resp_others\":\"").append(
                nullCheck(getRespondentOthersWithLineBreaks(listingType))).append(NEW_LINE);
        log.info("End Resp others");
        sb.append("\"respondent_town\":\"").append(nullCheck(listingType.getRespondentTown())).append(NEW_LINE);
        sb.append("\"Hearing_location\":\"").append(nullCheck(listingType.getCauseListVenue())).append(NEW_LINE);
        sb.append("\"Hearing_room\":\"").append(nullCheck(listingType.getHearingRoom())).append(NEW_LINE);
        sb.append("\"Hearing_dayofdays\":\"").append(nullCheck(listingType.getHearingDay())).append(NEW_LINE);
        sb.append("\"Hearing_panel\":\"").append(nullCheck(listingType.getHearingPanel())).append(NEW_LINE);
        sb.append("\"Hearing_notes\":\"").append(extractHearingNotes(listingType)).append(NEW_LINE);
        sb.append("\"respondent_representative\":\"").append(nullCheck(listingType.getRespondentRepresentative())).append("\"}");
        return sb;
    }

    private static String extractHearingNotes(ListingType listingType) {
        if (!isNullOrEmpty(listingType.getHearingNotes())) {
            return listingType.getHearingNotes().replaceAll("\n", " - ");
        }
        return "";
    }

    private static String extractHearingJudgeName(ListingType listingType) {
        if (listingType.getHearingJudgeName() != null) {
            return listingType.getHearingJudgeName().substring(listingType.getHearingJudgeName().indexOf('_') + 1);
        }
        return "";
    }

    public static String getRespondentOthersWithLineBreaks(ListingType listingType) {
        return nullCheck(listingType.getRespondentOthers()).replace(", ", "\\n");
    }

    private static StringBuilder getCourtListingData(ListingData listingData) {
        StringBuilder sb = new StringBuilder();
        if (listingData.getTribunalCorrespondenceAddress() != null) {
            sb.append("\"Court_addressLine1\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getAddressLine1())).append(NEW_LINE);
            sb.append("\"Court_addressLine2\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getAddressLine2())).append(NEW_LINE);
            sb.append("\"Court_addressLine3\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getAddressLine3())).append(NEW_LINE);
            sb.append("\"Court_town\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getPostTown())).append(NEW_LINE);
            sb.append("\"Court_county\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getCounty())).append(NEW_LINE);
            sb.append("\"Court_postCode\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().getPostCode())).append(NEW_LINE);
            sb.append("\"Court_fullAddress\":\"").append(
                    nullCheck(listingData.getTribunalCorrespondenceAddress().toString())).append(NEW_LINE);
        }
        sb.append("\"Court_telephone\":\"").append(
                nullCheck(listingData.getTribunalCorrespondenceTelephone())).append(NEW_LINE);
        sb.append("\"Court_fax\":\"").append(
                nullCheck(listingData.getTribunalCorrespondenceFax())).append(NEW_LINE);
        sb.append("\"Court_DX\":\"").append(
                nullCheck(listingData.getTribunalCorrespondenceDX())).append(NEW_LINE);
        sb.append("\"Court_Email\":\"").append(
                nullCheck(listingData.getTribunalCorrespondenceEmail())).append(NEW_LINE);
        return sb;
    }

    public static String getListingDocName(ListingData listingData) {
        String roomOrNoRoom = !isNullOrEmpty(listingData.getRoomOrNoRoom()) ? listingData.getRoomOrNoRoom() : "";
        if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_STAFF)
                && roomOrNoRoom.equals(NO)) {
            return STAFF_CASE_CAUSE_LIST_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_STAFF)
                && roomOrNoRoom.equals(YES)) {
            return STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PUBLIC)
                && roomOrNoRoom.equals(NO)) {
            return PUBLIC_CASE_CAUSE_LIST_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PUBLIC)
                && roomOrNoRoom.equals(YES)) {
            return PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PRESS_LIST)
                && listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE)) {
            return PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_ETCL)
                && listingData.getHearingDocETCL().equals(HEARING_ETCL_PRESS_LIST)
                && !listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE)) {
            return PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_IT56)) {
            return IT56_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(HEARING_DOC_IT57)) {
            return IT57_TEMPLATE;
        } else if (listingData.getHearingDocType().equals(BROUGHT_FORWARD_REPORT)) {
            return "EM-TRB-SCO-ENG-00218";
        } else if (listingData.getHearingDocType().equals(CLAIMS_ACCEPTED_REPORT)) {
            return "EM-TRB-SCO-ENG-00219";
        } else if (listingData.getHearingDocType().equals(LIVE_CASELOAD_REPORT)) {
            return "EM-TRB-SCO-ENG-00220";
        } else if (listingData.getHearingDocType().equals(CASES_COMPLETED_REPORT)) {
            return "EM-TRB-SCO-ENG-00221";
        }
        //TODO
        return "No document found";
    }

    public static Map<String, String> createMap(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static Map<String, String> getVenueToSearch(ListingData listingData) {
        if (!isNullOrEmpty(listingData.getVenueGlasgow())) {
            return createMap(LISTING_GLASGOW_VENUE_FIELD_NAME, listingData.getVenueGlasgow());
        } else if (!isNullOrEmpty(listingData.getVenueAberdeen())) {
            return createMap(LISTING_ABERDEEN_VENUE_FIELD_NAME, listingData.getVenueAberdeen());
        } else if (!isNullOrEmpty(listingData.getVenueDundee())) {
            return createMap(LISTING_DUNDEE_VENUE_FIELD_NAME, listingData.getVenueDundee());
        } else if (!isNullOrEmpty(listingData.getVenueEdinburgh())) {
            return createMap(LISTING_EDINBURGH_VENUE_FIELD_NAME, listingData.getVenueEdinburgh());
        }
        return !isNullOrEmpty(listingData.getListingVenue())
                ? createMap(LISTING_VENUE_FIELD_NAME, listingData.getListingVenue())
                : createMap("", "");
    }

    public static String getVenueFromDateListedType(DateListedType dateListedType) {
        if (dateListedType.getHearingVenueDay() != null) {
            switch (dateListedType.getHearingVenueDay()) {
                case GLASGOW_OFFICE:
                    return dateListedType.getHearingGlasgow();
                case DUNDEE_OFFICE:
                    return dateListedType.getHearingDundee();
                case EDINBURGH_OFFICE:
                    return dateListedType.getHearingEdinburgh();
                case ABERDEEN_OFFICE:
                    return dateListedType.getHearingAberdeen();
                default:
                    return dateListedType.getHearingVenueDay();
            }
        }
        return " ";
    }

    private static String getRespOthersName(CaseData caseData) {
        if (caseData.getRespondentCollection() != null) {
            List<String> respOthers = caseData.getRespondentCollection()
                    .stream()
                    .skip(1)
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO))
                    .map(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName())
                    .collect(Collectors.toList());
            return String.join(", ", respOthers);
        }
        return " ";
    }

    private static String getCaseTotal(List<ListingTypeItem> listingTypeItems) {
        return String.valueOf(listingTypeItems
                .stream()
                .map(listingTypeItem -> listingTypeItem.getValue().getElmoCaseReference())
                .distinct()
                .count());
    }

    public static boolean getListingDateBetween(String dateToSearchFrom, String dateToSearchTo, String dateToSearch) {
        LocalDate localDateFrom = LocalDate.parse(dateToSearchFrom, OLD_DATE_TIME_PATTERN2);
        LocalDate localDate = LocalDate.parse(dateToSearch, OLD_DATE_TIME_PATTERN);
        if (dateToSearchTo.equals("")) {
            return localDateFrom.isEqual(localDate);
        } else {
            LocalDate localDateTo = LocalDate.parse(dateToSearchTo, OLD_DATE_TIME_PATTERN2);
            return (!localDate.isBefore(localDateFrom)) && (!localDate.isAfter(localDateTo));
        }
    }

    public static boolean getMatchingDateBetween(String dateToSearchFrom, String dateToSearchTo,
                                                 String dateToSearch, boolean dateRange) {
        LocalDate localDate = LocalDate.parse(dateToSearch, OLD_DATE_TIME_PATTERN2);
        LocalDate localDateFrom = LocalDate.parse(dateToSearchFrom, OLD_DATE_TIME_PATTERN2);
        if (!dateRange) {
            return localDateFrom.isEqual(localDate);
        } else {
            LocalDate localDateTo = LocalDate.parse(dateToSearchTo, OLD_DATE_TIME_PATTERN2);
            return (!localDate.isBefore(localDateFrom)) && (!localDate.isAfter(localDateTo));
        }
    }

    private static void sortListingCollectionByDateAndVenue(ListingData listingData) {
        log.info("Sorting hearings");
        Comparator<ListingTypeItem> venueComparator =
                Comparator.comparing(s -> s.getValue().getCauseListVenue(),
                        Comparator.nullsLast(Comparator.naturalOrder()));

        listingData.getListingCollection()
                .sort(venueComparator.thenComparing(s -> s.getValue().getCauseListDate() != null
                                ? LocalDate.parse(s.getValue().getCauseListDate(), NEW_DATE_PATTERN)
                                : null,
                        Comparator.nullsLast(Comparator.naturalOrder())));
    }
}
