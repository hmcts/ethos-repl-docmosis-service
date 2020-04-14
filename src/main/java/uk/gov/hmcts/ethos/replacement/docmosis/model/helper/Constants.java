package uk.gov.hmcts.ethos.replacement.docmosis.model.helper;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter OLD_DATE_TIME_PATTERN2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter NEW_DATE_PATTERN = DateTimeFormatter.ofPattern("d MMMM yyyy");
    public static final DateTimeFormatter NEW_DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("d MMMM yyyy HH:mm");
    public static final DateTimeFormatter NEW_TIME_PATTERN = DateTimeFormatter.ofPattern("HH:mm");
    public static final String NEW_LINE = "\",\n";
    public static final String OUTPUT_FILE_NAME = "document.docx";

    public static final String PRE_DEFAULT_XLSX_FILE_PATH = "preDefaultValues.xlsx";
    public static final String POST_DEFAULT_XLSX_FILE_PATH = "postDefaultValues.xlsx";

    public static final String MANCHESTER_DEV_BULK_CASE_TYPE_ID = "Manchester_Multiples_Dev";
    public static final String MANCHESTER_DEV_CASE_TYPE_ID = "Manchester_Dev";
    public static final String SCOTLAND_DEV_BULK_CASE_TYPE_ID = "Scotland_Multiples_Dev";
    public static final String SCOTLAND_DEV_CASE_TYPE_ID = "Scotland_Dev";
    public static final String BRISTOL_DEV_BULK_CASE_TYPE_ID = "Bristol_Multiples_Dev";
    public static final String BRISTOL_DEV_CASE_TYPE_ID = "Bristol_Dev";
    public static final String LEEDS_DEV_BULK_CASE_TYPE_ID = "Leeds_Multiples_Dev";
    public static final String LEEDS_DEV_CASE_TYPE_ID = "Leeds_Dev";
    public static final String LONDON_CENTRAL_DEV_BULK_CASE_TYPE_ID = "LondonCentral_Multiples_Dev";
    public static final String LONDON_CENTRAL_DEV_CASE_TYPE_ID = "LondonCentral_Dev";
    public static final String LONDON_EAST_DEV_BULK_CASE_TYPE_ID = "LondonEast_Multiples_Dev";
    public static final String LONDON_EAST_DEV_CASE_TYPE_ID = "LondonEast_Dev";
    public static final String LONDON_SOUTH_DEV_BULK_CASE_TYPE_ID = "LondonSouth_Multiples_Dev";
    public static final String LONDON_SOUTH_DEV_CASE_TYPE_ID = "LondonSouth_Dev";
    public static final String MIDLANDS_EAST_DEV_BULK_CASE_TYPE_ID = "MidlandsEast_Multiples_Dev";
    public static final String MIDLANDS_EAST_DEV_CASE_TYPE_ID = "MidlandsEast_Dev";
    public static final String MIDLANDS_WEST_DEV_BULK_CASE_TYPE_ID = "MidlandsWest_Multiples_Dev";
    public static final String MIDLANDS_WEST_DEV_CASE_TYPE_ID = "MidlandsWest_Dev";
    public static final String NEWCASTLE_DEV_BULK_CASE_TYPE_ID = "Newcastle_Multiples_Dev";
    public static final String NEWCASTLE_DEV_CASE_TYPE_ID = "Newcastle_Dev";
    public static final String WALES_DEV_BULK_CASE_TYPE_ID = "Wales_Multiples_Dev";
    public static final String WALES_DEV_CASE_TYPE_ID = "Wales_Dev";
    public static final String WATFORD_DEV_BULK_CASE_TYPE_ID = "Watford_Multiples_Dev";
    public static final String WATFORD_DEV_CASE_TYPE_ID = "Watford_Dev";

    public static final String MANCHESTER_USERS_BULK_CASE_TYPE_ID = "Manchester_Multiples_User";
    public static final String MANCHESTER_USERS_CASE_TYPE_ID = "Manchester_User";
    public static final String SCOTLAND_USERS_BULK_CASE_TYPE_ID = "Scotland_Multiples_User";
    public static final String SCOTLAND_USERS_CASE_TYPE_ID = "Scotland_User";
    public static final String BRISTOL_USERS_BULK_CASE_TYPE_ID = "Bristol_Multiples_User";
    public static final String BRISTOL_USERS_CASE_TYPE_ID = "Bristol_User";
    public static final String LEEDS_USERS_BULK_CASE_TYPE_ID = "Leeds_Multiples_User";
    public static final String LEEDS_USERS_CASE_TYPE_ID = "Leeds_User";
    public static final String LONDON_CENTRAL_USERS_BULK_CASE_TYPE_ID = "LondonCentral_Multiples_User";
    public static final String LONDON_CENTRAL_USERS_CASE_TYPE_ID = "LondonCentral_User";
    public static final String LONDON_EAST_USERS_BULK_CASE_TYPE_ID = "LondonEast_Multiples_User";
    public static final String LONDON_EAST_USERS_CASE_TYPE_ID = "LondonEast_User";
    public static final String LONDON_SOUTH_USERS_BULK_CASE_TYPE_ID = "LondonSouth_Multiples_User";
    public static final String LONDON_SOUTH_USERS_CASE_TYPE_ID = "LondonSouth_User";
    public static final String MIDLANDS_EAST_USERS_BULK_CASE_TYPE_ID = "MidlandsEast_Multiples_User";
    public static final String MIDLANDS_EAST_USERS_CASE_TYPE_ID = "MidlandsEast_User";
    public static final String MIDLANDS_WEST_USERS_BULK_CASE_TYPE_ID = "MidlandsWest_Multiples_User";
    public static final String MIDLANDS_WEST_USERS_CASE_TYPE_ID = "MidlandsWest_User";
    public static final String NEWCASTLE_USERS_BULK_CASE_TYPE_ID = "Newcastle_Multiples_User";
    public static final String NEWCASTLE_USERS_CASE_TYPE_ID = "Newcastle_User";
    public static final String WALES_USERS_BULK_CASE_TYPE_ID = "Wales_Multiples_User";
    public static final String WALES_USERS_CASE_TYPE_ID = "Wales_User";
    public static final String WATFORD_USERS_BULK_CASE_TYPE_ID = "Watford_Multiples_User";
    public static final String WATFORD_USERS_CASE_TYPE_ID = "Watford_User";

    public static final String MANCHESTER_BULK_CASE_TYPE_ID = "Manchester_Multiples";
    public static final String MANCHESTER_CASE_TYPE_ID = "Manchester";
    public static final String SCOTLAND_BULK_CASE_TYPE_ID = "Scotland_Multiples";
    public static final String SCOTLAND_CASE_TYPE_ID = "Scotland";
    public static final String BRISTOL_BULK_CASE_TYPE_ID = "Bristol_Multiples";
    public static final String BRISTOL_CASE_TYPE_ID = "Bristol";
    public static final String LEEDS_BULK_CASE_TYPE_ID = "Leeds_Multiples";
    public static final String LEEDS_CASE_TYPE_ID = "Leeds";
    public static final String LONDON_CENTRAL_BULK_CASE_TYPE_ID = "LondonCentral_Multiples";
    public static final String LONDON_CENTRAL_CASE_TYPE_ID = "LondonCentral";
    public static final String LONDON_EAST_BULK_CASE_TYPE_ID = "LondonEast_Multiples";
    public static final String LONDON_EAST_CASE_TYPE_ID = "LondonEast";
    public static final String LONDON_SOUTH_BULK_CASE_TYPE_ID = "LondonSouth_Multiples";
    public static final String LONDON_SOUTH_CASE_TYPE_ID = "LondonSouth";
    public static final String MIDLANDS_EAST_BULK_CASE_TYPE_ID = "MidlandsEast_Multiples";
    public static final String MIDLANDS_EAST_CASE_TYPE_ID = "MidlandsEast";
    public static final String MIDLANDS_WEST_BULK_CASE_TYPE_ID = "MidlandsWest_Multiples";
    public static final String MIDLANDS_WEST_CASE_TYPE_ID = "MidlandsWest";
    public static final String NEWCASTLE_BULK_CASE_TYPE_ID = "Newcastle_Multiples";
    public static final String NEWCASTLE_CASE_TYPE_ID = "Newcastle";
    public static final String WALES_BULK_CASE_TYPE_ID = "Wales_Multiples";
    public static final String WALES_CASE_TYPE_ID = "Wales";
    public static final String WATFORD_BULK_CASE_TYPE_ID = "Watford_Multiples";
    public static final String WATFORD_CASE_TYPE_ID = "Watford";

    public static final String PENDING_STATE = "Pending";
    public static final String SUBMITTED_STATE = "Submitted";
    public static final String ACCEPTED_STATE = "Accepted";
    public static final String REJECTED_STATE = "Rejected";
    public static final String CLOSED_STATE = "Closed";

    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final String SINGLE_CASE_TYPE = "Single";
    public static final String INDIVIDUAL_TYPE_CLAIMANT = "Individual";
    public static final String MANUALLY_CREATED_POSITION = "Manually Created";

    public static final String CREATION_EVENT_TRIGGER_ID = "initiateCase";
    public static final String UPDATE_EVENT_TRIGGER_ID = "amendCaseDetails";
    public static final String UPDATE_EVENT_TRIGGER_ID_BULK = "amendCaseDetailsBulk";
    public static final String PRE_ACCEPT_CASE_TRIGGER_ID_BULK = "preAcceptanceCase";
    public static final String UPDATE_BULK_EVENT_TRIGGER_ID = "updateBulkAction";

    public static final String GLASGOW_OFFICE = "Glasgow";
    public static final String ABERDEEN_OFFICE = "Aberdeen";
    public static final String DUNDEE_OFFICE = "Dundee";
    public static final String EDINBURGH_OFFICE = "Edinburgh";

    public static final String MIDLANDS_WEST_OFFICE_NUMBER = "13";
    public static final String BRISTOL_OFFICE_NUMBER = "14";
    public static final String WALES_OFFICE_NUMBER = "16";
    public static final String LEEDS_OFFICE_NUMBER = "18";
    public static final String LONDON_CENTRAL_OFFICE_NUMBER = "22";
    public static final String LONDON_SOUTH_OFFICE_NUMBER = "23";
    public static final String MANCHESTER_OFFICE_NUMBER = "24";
    public static final String NEWCASTLE_OFFICE_NUMBER = "25";
    public static final String MIDLANDS_EAST_OFFICE_NUMBER = "26";
    public static final String LONDON_EAST_OFFICE_NUMBER = "32";
    public static final String WATFORD_OFFICE_NUMBER = "33";
    public static final String GLASGOW_OFFICE_NUMBER = "41";

    public static final String DEFAULT_INIT_REF = "20000";
    public static final int DEFAULT_INIT_SUB_REF = 1;
    public static final String DEFAULT_MAX_REF = "99999";

    public static final String DEFAULT_SELECT_ALL_VALUE = "999999";
    public static final String SELECT_ALL_VALUE = "Select All";
    public static final String SELECT_NONE_VALUE = "None";

    public static final String MANCHESTER_DEV_LISTING_CASE_TYPE_ID = "Manchester_Listings_Dev";
    public static final String SCOTLAND_DEV_LISTING_CASE_TYPE_ID = "Scotland_Listings_Dev";
    public static final String LEEDS_DEV_LISTING_CASE_TYPE_ID = "Leeds_Listings_Dev";
    public static final String BRISTOL_DEV_LISTING_CASE_TYPE_ID = "Bristol_Listings_Dev";
    public static final String LONDON_CENTRAL_DEV_LISTING_CASE_TYPE_ID = "LondonCentral_Listings_Dev";
    public static final String LONDON_EAST_DEV_LISTING_CASE_TYPE_ID = "LondonEast_Listings_Dev";
    public static final String LONDON_SOUTH_DEV_LISTING_CASE_TYPE_ID = "LondonSouth_Listings_Dev";
    public static final String MIDLANDS_EAST_DEV_LISTING_CASE_TYPE_ID = "MidlandsEast_Listings_Dev";
    public static final String MIDLANDS_WEST_DEV_LISTING_CASE_TYPE_ID = "MidlandsWest_Listings_Dev";
    public static final String NEWCASTLE_DEV_LISTING_CASE_TYPE_ID = "Newcastle_Listings_Dev";
    public static final String WALES_DEV_LISTING_CASE_TYPE_ID = "Wales_Listings_Dev";
    public static final String WATFORD_DEV_LISTING_CASE_TYPE_ID = "Watford_Listings_Dev";

    public static final String MANCHESTER_USERS_LISTING_CASE_TYPE_ID = "Manchester_Listings_User";
    public static final String SCOTLAND_USERS_LISTING_CASE_TYPE_ID = "Scotland_Listings_User";
    public static final String LEEDS_USERS_LISTING_CASE_TYPE_ID = "Leeds_Listings_User";
    public static final String BRISTOL_USERS_LISTING_CASE_TYPE_ID = "Bristol_Listings_User";
    public static final String LONDON_CENTRAL_USERS_LISTING_CASE_TYPE_ID = "LondonCentral_Listings_User";
    public static final String LONDON_EAST_USERS_LISTING_CASE_TYPE_ID = "LondonEast_Listings_User";
    public static final String LONDON_SOUTH_USERS_LISTING_CASE_TYPE_ID = "LondonSouth_Listings_User";
    public static final String MIDLANDS_EAST_USERS_LISTING_CASE_TYPE_ID = "MidlandsEast_Listings_User";
    public static final String MIDLANDS_WEST_USERS_LISTING_CASE_TYPE_ID = "MidlandsWest_Listings_User";
    public static final String NEWCASTLE_USERS_LISTING_CASE_TYPE_ID = "Newcastle_Listings_User";
    public static final String WALES_USERS_LISTING_CASE_TYPE_ID = "Wales_Listings_User";
    public static final String WATFORD_USERS_LISTING_CASE_TYPE_ID = "Watford_Listings_User";

    public static final String MANCHESTER_LISTING_CASE_TYPE_ID = "Manchester_Listings";
    public static final String SCOTLAND_LISTING_CASE_TYPE_ID = "Scotland_Listings";
    public static final String LEEDS_LISTING_CASE_TYPE_ID = "Leeds_Listings";
    public static final String BRISTOL_LISTING_CASE_TYPE_ID = "Bristol_Listings";
    public static final String LONDON_CENTRAL_LISTING_CASE_TYPE_ID = "LondonCentral_Listings";
    public static final String LONDON_EAST_LISTING_CASE_TYPE_ID = "LondonEast_Listings";
    public static final String LONDON_SOUTH_LISTING_CASE_TYPE_ID = "LondonSouth_Listings";
    public static final String MIDLANDS_EAST_LISTING_CASE_TYPE_ID = "MidlandsEast_Listings";
    public static final String MIDLANDS_WEST_LISTING_CASE_TYPE_ID = "MidlandsWest_Listings";
    public static final String NEWCASTLE_LISTING_CASE_TYPE_ID = "Newcastle_Listings";
    public static final String WALES_LISTING_CASE_TYPE_ID = "Wales_Listings";
    public static final String WATFORD_LISTING_CASE_TYPE_ID = "Watford_Listings";

    public static final String IT56_TEMPLATE = "EM-TRB-SCO-ENG-00210";
    public static final String IT57_TEMPLATE = "EM-TRB-SCO-ENG-00211";
    public static final String PUBLIC_CASE_CAUSE_LIST_TEMPLATE = "EM-TRB-SCO-ENG-00212";
    public static final String STAFF_CASE_CAUSE_LIST_TEMPLATE = "EM-TRB-SCO-ENG-00213";
    public static final String PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE = "EM-TRB-SCO-ENG-00214";
    public static final String STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE = "EM-TRB-SCO-ENG-00215";
    public static final String PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE = "EM-TRB-SCO-ENG-00216";
    public static final String PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE = "EM-TRB-SCO-ENG-00217";

    public static final String HEARING_DOC_ETCL = "ETCL";
    public static final String HEARING_DOC_IT56 = "IT56";
    public static final String HEARING_DOC_IT57 = "IT57";

    public static final String HEARING_ETCL_PUBLIC = "Public";
    public static final String HEARING_ETCL_STAFF = "Staff";
    public static final String HEARING_ETCL_PRESS_LIST = "Press List";
    public static final String RANGE_HEARING_DATE_TYPE = "Range";
    public static final String SINGLE_HEARING_DATE_TYPE = "Single";

    public static final String ALL_VENUES = "All";
    public static final String RULE_50_APPLIES = "Order made pursuant to Rule 50";

    public static final String LIST_CASES = "EM-TRB-SUM-ENG-00220";
    public static final String MULTIPLE_SCHEDULE = "EM-TRB-SUM-ENG-00221";
    public static final String MULTIPLE_SCHEDULE_DETAILED = "EM-TRB-SUM-ENG-00222";

    public static final String LIST_CASES_CONFIG = "List Cases";
    public static final String MULTIPLE_SCHEDULE_CONFIG = "Multiple Schedule";
    public static final String MULTIPLE_SCHEDULE_DETAILED_CONFIG = "Multiple Schedule (Detailed)";

    public static final String FILE_EXTENSION = ".docx";

    public static final String FUTURE_RECEIPT_DATE_ERROR_MESSAGE = "Receipt date should not be a date in the future";
    public static final String EMPTY_RESPONDENT_COLLECTION_ERROR_MESSAGE = "At least one active respondent is required";
    public static final String EARLY_DATE_RETURNED_FROM_JUDGE_ERROR_MESSAGE = "Date returned from judge can not be earlier than Date referred to judge";
    public static final String EMPTY_HEARING_COLLECTION_ERROR_MESSAGE = "Hearings could not be found for this case, the hearing collection is empty";
    public static final String HEARING_NUMBER_MISMATCH_ERROR_MESSAGE = "The hearing number provided did not match with any of the hearing numbers contained within this case";
    public static final String MISSING_JURISDICTION_OUTCOME_ERROR_MESSAGE = "A Jurisdiction outcome is required before the case can be closed";

    public static final int TARGET_HEARING_DATE_INCREMENT = 181;

    public static final int MAX_ES_SIZE = 10000;

    public static final int NUMBER_THREADS = 20;
}
