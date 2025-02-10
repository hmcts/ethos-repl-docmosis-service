package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_ABERDEEN_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_DUNDEE_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_EDINBURGH_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_GLASGOW_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.helpers.ESHelper.LISTING_VENUE_FIELD_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ABERDEEN_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.ALL_VENUES;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.BROUGHT_FORWARD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_AWAITING_JUDGMENT_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASES_COMPLETED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CASE_SOURCE_LOCAL_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_ACCEPTED_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.CLAIMS_BY_HEARING_VENUE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.DUNDEE_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.EDINBURGH_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.FILE_EXTENSION;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.GLASGOW_OFFICE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_BY_HEARING_TYPE_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARINGS_TO_JUDGEMENTS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_ETCL;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_IT56;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_DOC_IT57;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PRESS_LIST;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_PUBLIC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.HEARING_ETCL_STAFF;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IT56_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.IT57_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LISTINGS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LISTINGS_DEV;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LISTINGS_USER;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.LIVE_CASELOAD_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.MEMBER_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFCTC;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEWCASTLE_CFT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_DATE_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NO;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OLD_DATE_TIME_PATTERN2;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.OUTPUT_FILE_NAME;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.PUBLIC_CASE_CAUSE_LIST_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.RANGE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SCOTLAND_LISTING_CASE_TYPE_ID;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SERVING_CLAIMS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SESSION_DAYS_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.STAFF_CASE_CAUSE_LIST_TEMPLATE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_JUSTICE_CENTRE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TEESSIDE_MAGS;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.TIME_TO_FIRST_HEARING_REPORT;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.YES;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.MultiplesScheduleHelper.NOT_ALLOCATED;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.ECC_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.NO_CHANGE_IN_CURRENT_POSITION_REPORT;
import static uk.gov.hmcts.ethos.replacement.docmosis.reports.Constants.RESPONDENTS_REPORT;

@Slf4j
public class ListingHelper {

    private static final String ROOM_NOT_ALLOCATED = "* Not Allocated";
    private static final String NO_DOCUMENT_FOUND = "No document found";
    private static final int NUMBER_CHAR_PARSING_DATE = 20;
    private static final String LISTING_NEWLINE = "\"listing\":[\n";
    private static final String ARRAY_ELEMENT_CLOSING_NEWLINE = "}],\n";
    static final List<String> REPORTS = Arrays.asList(BROUGHT_FORWARD_REPORT, CLAIMS_ACCEPTED_REPORT,
        LIVE_CASELOAD_REPORT, CASES_COMPLETED_REPORT, CASES_AWAITING_JUDGMENT_REPORT, TIME_TO_FIRST_HEARING_REPORT,
        SERVING_CLAIMS_REPORT, CASE_SOURCE_LOCAL_REPORT, HEARINGS_TO_JUDGEMENTS_REPORT,
            HEARINGS_BY_HEARING_TYPE_REPORT, NO_CHANGE_IN_CURRENT_POSITION_REPORT,
            MEMBER_DAYS_REPORT, RESPONDENTS_REPORT, SESSION_DAYS_REPORT, ECC_REPORT, CLAIMS_BY_HEARING_VENUE_REPORT);
    private static final List<String> SCOTLAND_HEARING_LIST = List.of("Reading Day", "Deliberation Day",
            "Members meeting", "In Chambers");
    public static final DateTimeFormatter CAUSE_LIST_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    public static final String RULE_49_APPLIES = "Order made pursuant to Rule 49";

    private ListingHelper() {
    }

    public static ListingType getListingTypeFromCaseData(ListingData listingData, CaseData caseData,
                                                         HearingType hearingType, DateListedType dateListedType,
                                                         int index, int hearingCollectionSize) {
        var listingType = new ListingType();

        try {
            log.info("started getListingTypeFromCaseData");
            listingType.setElmoCaseReference(caseData.getEthosCaseReference());
            String listedDate = dateListedType.getListedDate();
            listingType.setCauseListDate(!isNullOrEmpty(listedDate)
                    ? LocalDate.parse(listedDate, OLD_DATE_TIME_PATTERN).format(CAUSE_LIST_DATE_TIME_PATTERN) : " ");
            listingType.setCauseListTime(!isNullOrEmpty(listedDate) ? UtilHelper.formatLocalTime(listedDate) : " ");

            listingType.setJurisdictionCodesList(BulkHelper.getJurCodesCollectionWithHide(
                    caseData.getJurCodesCollection()));
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
            listingType.setHearingFormat(CollectionUtils.isNotEmpty(hearingType.getHearingFormat())
                    ? String.join(", ", hearingType.getHearingFormat())
                    : " ");
            listingType.setJudicialMediation(
                    isNullOrEmpty(hearingType.getJudicialMediation()) || NO.equals(hearingType.getJudicialMediation())
                    ? " "
                    : hearingType.getJudicialMediation());

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

            listingType.setHearingReadingDeliberationMembersChambers(
                    !isNullOrEmpty(dateListedType.getHearingTypeReadingDeliberation())
                    && SCOTLAND_HEARING_LIST.contains(dateListedType.getHearingTypeReadingDeliberation())
                    ? dateListedType.getHearingTypeReadingDeliberation()
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

    public static String getHearingRoom(DateListedType dateListedType) {
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
            listingType.setClaimantName(RULE_49_APPLIES);
            listingType.setRespondent(RULE_49_APPLIES);
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
        var sb = new StringBuilder();

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
        sb.append("\"Office_name\":\"").append(getOfficeName(getListingCaseTypeSingleOrListings(caseType)))
                .append(NEW_LINE);
        log.info("Hearing location");
        sb.append("\"Hearing_location\":\"").append(getListingVenue(listingData)).append(NEW_LINE);
        log.info("Listings dates");
        sb.append(getListingDate(listingData));
        log.info("Clerk");
        String userName = nullCheck(userDetails.getFirstName() + " " + userDetails.getLastName());
        log.info("Clerk Username: " + userName);
        sb.append("\"Clerk\":\"").append(nullCheck(userName)).append(NEW_LINE);

        if (listingData.getListingCollection() != null && !listingData.getListingCollection().isEmpty()) {
            sortListingCollection(listingData, templateName);
            sb.append(getDocumentData(listingData, templateName, caseType));
        }

        log.info("Document data ends");
        sb.append("\"Today_date\":\"").append(UtilHelper.formatCurrentDate(LocalDate.now())).append("\"\n");
        sb.append("}\n");
        sb.append("}\n");
        return sb;
    }

    private static String getListingCaseTypeSingleOrListings(String caseType) {
        if (caseType.endsWith(LISTINGS) || caseType.endsWith(LISTINGS_DEV) || caseType.endsWith(LISTINGS_USER)) {
            return UtilHelper.getListingCaseTypeId(caseType);
        } else {
            return caseType;
        }
    }

    private static String getOfficeName(String caseType) {
        int index = findOfficeNameInUpperCase(caseType);
        if (index != 0) {
            var upperCaseLetter = Character.toString(caseType.charAt(index));
            return caseType.replace(upperCaseLetter, " " + upperCaseLetter);
        } else {
            return caseType;
        }
    }

    private static int findOfficeNameInUpperCase(String caseType) {
        var count = 0;
        for (var i = 0; i < caseType.length(); i++) {
            if (Character.isUpperCase(caseType.charAt(i))) {
                count++;
            }
            if (count == 2) {
                return i;
            }
        }
        return 0;
    }

    private static StringBuilder getLogo(String caseType) {
        var sb = new StringBuilder();
        if (caseType.equals(SCOTLAND_LISTING_CASE_TYPE_ID)) {
            sb.append("\"listing_logo\":\"").append("[userImage:").append("schmcts.png]").append(NEW_LINE);
        } else {
            sb.append("\"listing_logo\":\"").append("[userImage:").append("enhmcts.png]").append(NEW_LINE);
        }
        return sb;
    }

    private static StringBuilder getDocumentData(ListingData listingData, String templateName, String caseType) {
        if (Arrays.asList(IT56_TEMPLATE, IT57_TEMPLATE)
                .contains(templateName)) {
            return getCaseCauseList(listingData, caseType);
        } else if (Arrays.asList(PUBLIC_CASE_CAUSE_LIST_TEMPLATE, STAFF_CASE_CAUSE_LIST_TEMPLATE)
                .contains(templateName)) {
            return getCaseCauseListByDate(listingData, caseType);
        } else if (Arrays.asList(PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE, PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE)
                .contains(templateName)) {
            return getListByRoomOrVenue(new ArrayList<>(), listingData, caseType, false);
        } else if (Arrays.asList(PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE)
                .contains(templateName)) {
            return getCaseCauseListByRoom(listingData, caseType);
        } else {
            return new StringBuilder();
        }
    }

    private static boolean isEmptyHearingDate(ListingType listingType) {
        if (listingType.getCauseListDate() != null) {
            return listingType.getCauseListDate().trim().isEmpty();
        }
        return true;
    }

    private static TreeMap<String, List<ListingTypeItem>> getListHearingsByDate(ListingData listingData) {
        return listingData.getListingCollection()
                .stream()
                .filter(listingTypeItem -> !isEmptyHearingDate(listingTypeItem.getValue()))
                .collect(Collectors.groupingBy(listingTypeItem -> listingTypeItem.getValue().getCauseListDate(),
                    () -> new TreeMap<>(getDateComparator()), Collectors.toList()));
    }

    private static Iterator<Map.Entry<String, List<ListingTypeItem>>> getEntriesByDate(StringBuilder sb,
                                                                                       ListingData listingData) {
        TreeMap<String, List<ListingTypeItem>> sortedMap = getListHearingsByDate(listingData);
        sb.append("\"listing_date\":[\n");
        return new TreeMap<>(sortedMap).entrySet().iterator();
    }

    private static StringBuilder getCaseCauseListByDate(ListingData listingData, String caseType) {
        var sb = new StringBuilder();
        Iterator<Map.Entry<String, List<ListingTypeItem>>> entries = getEntriesByDate(sb, listingData);
        while (entries.hasNext()) {
            Map.Entry<String, List<ListingTypeItem>> listingEntry = entries.next();
            sb.append("{\"date\":\"").append(listingEntry.getKey()).append(NEW_LINE);
            sb.append("\"case_total\":\"").append(listingEntry.getValue().size()).append(NEW_LINE);
            sb.append(LISTING_NEWLINE);
            for (var i = 0; i < listingEntry.getValue().size(); i++) {
                sb.append(getListingTypeRow(listingEntry.getValue().get(i).getValue(), caseType, listingData));
                if (i != listingEntry.getValue().size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("]\n");
            if (entries.hasNext()) {
                sb.append("},\n");
            } else {
                sb.append(ARRAY_ELEMENT_CLOSING_NEWLINE);
            }
        }
        return sb;
    }

    private static StringBuilder getCaseCauseListByRoom(ListingData listingData, String caseType) {
        var sb = new StringBuilder();
        Iterator<Map.Entry<String, List<ListingTypeItem>>> entries = getEntriesByDate(sb, listingData);
        while (entries.hasNext()) {
            Map.Entry<String, List<ListingTypeItem>> listingEntry = entries.next();
            sb.append("{\"date\":\"").append(listingEntry.getKey()).append(NEW_LINE);
            sb.append(getListByRoomOrVenue(listingEntry.getValue(), listingData, caseType, true));
            if (entries.hasNext()) {
                sb.append("},\n");
            } else {
                sb.append(ARRAY_ELEMENT_CLOSING_NEWLINE);
            }
        }
        return sb;
    }

    public static StringBuilder getListingDate(ListingData listingData) {
        var sb = new StringBuilder();
        if (RANGE_HEARING_DATE_TYPE.equals(listingData.getHearingDateType())) {
            sb.append("\"Listed_date_from\":\"")
                    .append(UtilHelper.listingFormatLocalDate(listingData.getListingDateFrom())).append(NEW_LINE);
            sb.append("\"Listed_date_to\":\"")
                    .append(UtilHelper.listingFormatLocalDate(listingData.getListingDateTo())).append(NEW_LINE);
        } else {
            sb.append("\"Listed_date\":\"")
                    .append(UtilHelper.listingFormatLocalDate(listingData.getListingDate())).append(NEW_LINE);
        }
        return sb;
    }

    private static boolean isEmptyHearingRoom(ListingType listingType) {
        if (listingType.getHearingRoom() != null) {
            return listingType.getHearingRoom().trim().isEmpty();
        }
        return true;
    }

    private static TreeMap<String, List<ListingTypeItem>> getListHearingsByRoomWithNotAllocated(
            List<ListingTypeItem> listingSubCollection) {
        TreeMap<String, List<ListingTypeItem>> sortedMap = listingSubCollection
                .stream()
                .filter(listingTypeItem -> !isEmptyHearingRoom(listingTypeItem.getValue()))
                .collect(Collectors.groupingBy(listingTypeItem -> listingTypeItem.getValue().getHearingRoom(),
                    () -> new TreeMap<>(getVenueComparator()), Collectors.toList()));
        List<ListingTypeItem> notAllocated = listingSubCollection
                .stream()
                .filter(listingTypeItem -> isEmptyHearingRoom(listingTypeItem.getValue()))
                .sorted(getVenueComparatorListingTypeItem())
                .collect(toList());
        if (!notAllocated.isEmpty()) {
            sortedMap.computeIfAbsent(ROOM_NOT_ALLOCATED, k -> new ArrayList<>()).addAll(notAllocated);
        }
        return sortedMap;
    }

    private static boolean isEmptyHearingVenue(ListingType listingType) {
        if (listingType.getCauseListVenue() != null) {
            return listingType.getCauseListVenue().trim().isEmpty();
        }
        return true;
    }

    private static TreeMap<String, List<ListingTypeItem>> getListHearingsByVenueWithNotAllocated(
            ListingData listingData) {
        TreeMap<String, List<ListingTypeItem>> sortedMap = listingData.getListingCollection()
                .stream()
                .filter(listingTypeItem -> !isEmptyHearingVenue(listingTypeItem.getValue()))
                .collect(Collectors.groupingBy(listingTypeItem -> listingTypeItem.getValue().getCauseListVenue(),
                    () -> new TreeMap<>(getVenueComparator()), Collectors.toList()));
        List<ListingTypeItem> notAllocated = listingData.getListingCollection()
                .stream()
                .filter(listingTypeItem -> isEmptyHearingVenue(listingTypeItem.getValue()))
                .sorted(getDateComparatorListingTypeItem().thenComparing(getTimeComparatorListingTypeItem()))
                .collect(toList());
        if (!notAllocated.isEmpty()) {
            sortedMap.computeIfAbsent(NOT_ALLOCATED, k -> new ArrayList<>()).addAll(notAllocated);
        }
        return sortedMap;
    }

    private static StringBuilder getListByRoomOrVenue(List<ListingTypeItem> collection, ListingData listingData,
                                                      String caseType, boolean byRoom) {
        var sb = new StringBuilder();
        TreeMap<String, List<ListingTypeItem>> sortedMap = byRoom
                ? getListHearingsByRoomWithNotAllocated(collection)
                : getListHearingsByVenueWithNotAllocated(listingData);
        sb.append("\"location\":[\n");
        Iterator<Map.Entry<String, List<ListingTypeItem>>> entries = new TreeMap<>(sortedMap).entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<ListingTypeItem>> listingEntry = entries.next();
            String hearingRoomOrVenue = byRoom ? "Hearing_room" : "Hearing_venue";
            sb.append("{\"").append(hearingRoomOrVenue).append("\":\"").append(listingEntry.getKey()).append(NEW_LINE);
            sb.append(LISTING_NEWLINE);
            for (var i = 0; i < listingEntry.getValue().size(); i++) {
                sb.append(getListingTypeRow(listingEntry.getValue().get(i).getValue(), caseType, listingData));
                if (i != listingEntry.getValue().size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("]\n");
            if (entries.hasNext()) {
                sb.append("},\n");
            } else {
                sb.append(ARRAY_ELEMENT_CLOSING_NEWLINE);
            }
        }
        return sb;
    }

    private static StringBuilder getCaseCauseList(ListingData listingData, String caseType) {
        List<ListingTypeItem> listingTypeItems = listingData.getListingCollection();
        var sb = new StringBuilder();
        sb.append(LISTING_NEWLINE);
        for (var i = 0; i < listingTypeItems.size(); i++) {
            sb.append(getListingTypeRow(listingTypeItems.get(i).getValue(), caseType, listingData));
            if (i != listingTypeItems.size() - 1) {
                sb.append(",\n");
            }
        }
        sb.append("],\n");
        return sb;
    }

    private static StringBuilder getListingTypeRow(ListingType listingType, String caseType, ListingData listingData) {
        var sb = new StringBuilder();
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
        sb.append("\"Hearing_date_time\":\"").append(nullCheck(listingType.getCauseListDate())).append(" at ")
                .append(nullCheck(listingType.getCauseListTime())).append(NEW_LINE);
        sb.append("\"Hearing_time\":\"").append(nullCheck(listingType.getCauseListTime())).append(NEW_LINE);
        sb.append("\"Hearing_duration\":\"").append(nullCheck(listingType.getEstHearingLength())).append(NEW_LINE);
        log.info("Hearing clerk");
        sb.append("\"Hearing_clerk\":\"").append(nullCheck(listingType.getHearingClerk())).append(NEW_LINE);
        log.info("Hearing clerk ends");
        sb.append("\"Claimant\":\"").append(nullCheck(listingType.getClaimantName())).append(NEW_LINE);
        sb.append("\"claimant_town\":\"").append(nullCheck(listingType.getClaimantTown())).append(NEW_LINE);
        sb.append("\"claimant_representative\":\"")
                .append(nullCheck(listingType.getClaimantRepresentative())).append(NEW_LINE);
        sb.append("\"Respondent\":\"").append(nullCheck(listingType.getRespondent())).append(NEW_LINE);
        log.info("Resp others");
        sb.append("\"resp_others\":\"")
                .append(nullCheck(getRespondentOthersWithLineBreaks(listingType))).append(NEW_LINE);
        log.info("End Resp others");
        sb.append("\"respondent_town\":\"").append(nullCheck(listingType.getRespondentTown())).append(NEW_LINE);
        sb.append("\"Hearing_location\":\"").append(nullCheck(listingType.getCauseListVenue())).append(NEW_LINE);
        sb.append("\"Hearing_room\":\"").append(nullCheck(listingType.getHearingRoom())).append(NEW_LINE);
        sb.append("\"Hearing_dayofdays\":\"").append(nullCheck(listingType.getHearingDay())).append(NEW_LINE);
        sb.append("\"Hearing_panel\":\"").append(nullCheck(listingType.getHearingPanel())).append(NEW_LINE);
        sb.append("\"Hearing_notes\":\"").append(nullCheck(extractHearingNotes(listingType))).append(NEW_LINE);
        sb.append("\"Judicial_mediation\":\"").append(nullCheck(listingType.getJudicialMediation())).append(NEW_LINE);
        sb.append("\"Reading_deliberation_day\":\"")
                .append(nullCheck(listingType.getHearingReadingDeliberationMembersChambers())).append(NEW_LINE);
        sb.append("\"Hearing_format\":\"").append(nullCheck(listingType.getHearingFormat())).append(NEW_LINE);
        sb.append("\"respondent_representative\":\"")
                .append(nullCheck(listingType.getRespondentRepresentative())).append("\"}");
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
        var sb = new StringBuilder();
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
        if (listingData.getHearingDocType() != null) {
            return getHearingDocTemplateName(listingData);
        } else if (listingData.getReportType() != null) {
            return getReportDocTemplateName(listingData.getReportType());
        }
        return NO_DOCUMENT_FOUND;
    }

    public static Map<String, String> createMap(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    public static String getListingVenue(ListingData listingData) {
        Map<String, String> venueToSearchMap = getListingVenueToSearch(listingData);
        return venueToSearchMap.entrySet().iterator().next().getValue();
    }

    public static Map<String, String> getListingVenueToSearch(ListingData listingData) {
        boolean allLocations = listingData.getListingVenue().equals(ALL_VENUES);
        if (allLocations) {
            log.info("All locations");
            return createMap(ALL_VENUES, ALL_VENUES);
        } else {
            log.info("Specific venue");
            return getVenueToSearch(listingData);
        }
    }

    public static Map<String, String> getVenueToSearch(ListingData listingData) {
        if (!isNullOrEmpty(listingData.getVenueGlasgow())
                && !listingData.getVenueGlasgow().equals(ALL_VENUES)) {
            return createMap(LISTING_GLASGOW_VENUE_FIELD_NAME, listingData.getVenueGlasgow());
        } else if (!isNullOrEmpty(listingData.getVenueAberdeen())
                && !listingData.getVenueAberdeen().equals(ALL_VENUES)) {
            return createMap(LISTING_ABERDEEN_VENUE_FIELD_NAME, listingData.getVenueAberdeen());
        } else if (!isNullOrEmpty(listingData.getVenueDundee())
                && !listingData.getVenueDundee().equals(ALL_VENUES)) {
            return createMap(LISTING_DUNDEE_VENUE_FIELD_NAME, listingData.getVenueDundee());
        } else if (!isNullOrEmpty(listingData.getVenueEdinburgh())
                && !listingData.getVenueEdinburgh().equals(ALL_VENUES)) {
            return createMap(LISTING_EDINBURGH_VENUE_FIELD_NAME, listingData.getVenueEdinburgh());
        } else if (!isNullOrEmpty(listingData.getListingVenue())
            && listingData.getListingVenue().equals(NEWCASTLE_CFT)) {
            return createMap(LISTING_VENUE_FIELD_NAME, NEWCASTLE_CFCTC);
        } else if (!isNullOrEmpty(listingData.getListingVenue())
            && listingData.getListingVenue().equals(TEESSIDE_MAGS)) {
            return createMap(LISTING_VENUE_FIELD_NAME, TEESSIDE_JUSTICE_CENTRE);
        }

        return !isNullOrEmpty(listingData.getListingVenue())
                ? createMap(LISTING_VENUE_FIELD_NAME, listingData.getListingVenue())
                : createMap("", "");
    }

    public static boolean isAllScottishVenues(ListingData listingData) {
        boolean allVenuesGlasgow = !isNullOrEmpty(listingData.getVenueGlasgow())
                && listingData.getVenueGlasgow().equals(ALL_VENUES);
        boolean allVenuesAberdeen = !isNullOrEmpty(listingData.getVenueAberdeen())
                && listingData.getVenueAberdeen().equals(ALL_VENUES);
        boolean allVenuesDundee = !isNullOrEmpty(listingData.getVenueDundee())
                && listingData.getVenueDundee().equals(ALL_VENUES);
        boolean allVenuesEdinburgh = !isNullOrEmpty(listingData.getVenueEdinburgh())
                && listingData.getVenueEdinburgh().equals(ALL_VENUES);
        return allVenuesGlasgow || allVenuesAberdeen || allVenuesDundee || allVenuesEdinburgh;
    }

    public static String getVenueFromDateListedType(DateListedType dateListedType) {
        if (dateListedType.getHearingVenueDay() == null) {
            return " ";
        }

        switch (dateListedType.getHearingVenueDay()) {
            case GLASGOW_OFFICE:
                return dateListedType.getHearingGlasgow() != null ? dateListedType.getHearingGlasgow() : " ";
            case DUNDEE_OFFICE:
                return dateListedType.getHearingDundee() != null ? dateListedType.getHearingDundee() : " ";
            case EDINBURGH_OFFICE:
                return dateListedType.getHearingEdinburgh() != null ? dateListedType.getHearingEdinburgh() : " ";
            case ABERDEEN_OFFICE:
                return dateListedType.getHearingAberdeen() != null ? dateListedType.getHearingAberdeen() : " ";
            case NEWCASTLE_CFT:
                return NEWCASTLE_CFCTC;
            case TEESSIDE_MAGS:
                return TEESSIDE_JUSTICE_CENTRE;
            default:
                return dateListedType.getHearingVenueDay() != null ? dateListedType.getHearingVenueDay() : " ";
        }
    }

    private static String getRespOthersName(CaseData caseData) {
        if (caseData.getRespondentCollection() != null) {
            List<String> respOthers = caseData.getRespondentCollection()
                    .stream()
                    .skip(1)
                    .filter(respondentSumTypeItem -> respondentSumTypeItem.getValue().getResponseStruckOut().equals(NO))
                    .map(respondentSumTypeItem -> respondentSumTypeItem.getValue().getRespondentName())
                    .collect(toList());
            return String.join(", ", respOthers);
        }
        return " ";
    }

    public static String addMillisToDateToSearch(String dateToSearch) {
        if (dateToSearch.length() < NUMBER_CHAR_PARSING_DATE) {
            return dateToSearch.concat(".000");
        }
        return dateToSearch;
    }

    public static boolean getListingDateBetween(String dateToSearchFrom, String dateToSearchTo, String dateToSearch) {
        var localDateFrom = LocalDate.parse(dateToSearchFrom, OLD_DATE_TIME_PATTERN2);
        var localDate = LocalDate.parse(addMillisToDateToSearch(dateToSearch), OLD_DATE_TIME_PATTERN);
        if (dateToSearchTo.equals("")) {
            return localDateFrom.isEqual(localDate);
        } else {
            var localDateTo = LocalDate.parse(dateToSearchTo, OLD_DATE_TIME_PATTERN2);
            return (!localDate.isBefore(localDateFrom)) && (!localDate.isAfter(localDateTo));
        }
    }

    public static boolean getMatchingDateBetween(String dateToSearchFrom, String dateToSearchTo,
                                                 String dateToSearch, boolean dateRange) {
        var localDate = LocalDate.parse(dateToSearch, OLD_DATE_TIME_PATTERN2);
        var localDateFrom = LocalDate.parse(dateToSearchFrom, OLD_DATE_TIME_PATTERN2);
        if (!dateRange) {
            return localDateFrom.isEqual(localDate);
        } else {
            var localDateTo = LocalDate.parse(dateToSearchTo, OLD_DATE_TIME_PATTERN2);
            return (!localDate.isBefore(localDateFrom)) && (!localDate.isAfter(localDateTo));
        }
    }

    public static boolean isListingRangeValid(ListingData listingData, List<String> errors) {
        if (listingData.getHearingDateType().equals(RANGE_HEARING_DATE_TYPE)) {
            var localDateFrom = LocalDate.parse(listingData.getListingDateFrom(), OLD_DATE_TIME_PATTERN2);
            var localDateTo = LocalDate.parse(listingData.getListingDateTo(), OLD_DATE_TIME_PATTERN2);
            long noOfDaysBetween = ChronoUnit.DAYS.between(localDateFrom, localDateTo);
            if (localDateFrom.isBefore(localDateTo) && noOfDaysBetween <= 31) {
                return true;
            } else {
                errors.add("Date range is limited to a max of 31 days");
                return false;
            }
        }
        return true;
    }

    private static Comparator<String> getDateComparator() {
        return Comparator.comparing(causeListDate -> causeListDate != null
                        ? LocalDate.parse(causeListDate, NEW_DATE_PATTERN)
                        : null,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static Comparator<String> getVenueComparator() {
        return Comparator.comparing(causeListVenue -> causeListVenue,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static Comparator<ListingTypeItem> getVenueComparatorListingTypeItem() {
        return Comparator.comparing(s -> s.getValue().getCauseListVenue(),
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static Comparator<ListingTypeItem> getDateComparatorListingTypeItem() {
        return Comparator.comparing(s -> s.getValue().getCauseListDate() != null
                        ? LocalDate.parse(s.getValue().getCauseListDate(), NEW_DATE_PATTERN)
                        : null,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static Comparator<ListingTypeItem> getTimeComparatorListingTypeItem() {
        return Comparator.comparing(s -> s.getValue().getCauseListTime() != null
                        ? LocalTime.parse(s.getValue().getCauseListTime(), NEW_TIME_PATTERN)
                        : null,
                Comparator.nullsLast(Comparator.naturalOrder()));
    }

    private static void sortListingCollection(ListingData listingData, String templateName) {

        log.info("Sorting hearings");
        if (Arrays.asList(PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE, PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE)
                .contains(templateName)) {

            listingData.getListingCollection()
                    .sort(getVenueComparatorListingTypeItem()
                            .thenComparing(getDateComparatorListingTypeItem())
                            .thenComparing(getTimeComparatorListingTypeItem()));
        } else {

            listingData.getListingCollection()
                    .sort(getDateComparatorListingTypeItem()
                            .thenComparing(getVenueComparatorListingTypeItem())
                            .thenComparing(getTimeComparatorListingTypeItem()));
        }
    }

    public static boolean isReportType(String reportType) {
        return REPORTS.contains(reportType);
    }

    private static String getReportDocTemplateName(String reportType) {
        switch (reportType) {
            case BROUGHT_FORWARD_REPORT:
                return "EM-TRB-SCO-ENG-00218";
            case CLAIMS_ACCEPTED_REPORT:
                return "EM-TRB-SCO-ENG-00219";
            case LIVE_CASELOAD_REPORT:
                return "EM-TRB-SCO-ENG-00220";
            case CASES_COMPLETED_REPORT:
                return "EM-TRB-SCO-ENG-00221";
            case CASES_AWAITING_JUDGMENT_REPORT:
                return "EM-TRB-SCO-ENG-00749";
            case TIME_TO_FIRST_HEARING_REPORT:
                return "EM-TRB-SCO-ENG-00751";
            case SERVING_CLAIMS_REPORT:
                return "EM-TRB-SCO-ENG-00781";
            case CASE_SOURCE_LOCAL_REPORT:
                return "EM-TRB-SCO-ENG-00783";
            case HEARINGS_BY_HEARING_TYPE_REPORT:
                return "EM-TRB-SCO-ENG-00785";
            case HEARINGS_TO_JUDGEMENTS_REPORT:
                return "EM-TRB-SCO-ENG-00786";
            case NO_CHANGE_IN_CURRENT_POSITION_REPORT:
                return "EM-TRB-SCO-ENG-00794";
            case MEMBER_DAYS_REPORT:
                return "EM-TRB-SCO-ENG-00800";
            case RESPONDENTS_REPORT:
                return "EM-TRB-SCO-ENG-00815";
            case SESSION_DAYS_REPORT:
                return "EM-TRB-SCO-ENG-00817";
            case ECC_REPORT:
                return "EM-TRB-SCO-ENG-00818";
            default:
                return NO_DOCUMENT_FOUND;
        }
    }

    private static String getHearingDocTemplateName(ListingData listingData) {
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
        }
        return NO_DOCUMENT_FOUND;
    }
}

