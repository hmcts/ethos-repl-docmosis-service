package uk.gov.hmcts.ethos.replacement.docmosis.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.helpers.UtilHelper;
import uk.gov.hmcts.ecm.common.idam.models.UserDetails;
import uk.gov.hmcts.ecm.common.model.ccd.CaseData;
import uk.gov.hmcts.ecm.common.model.ccd.items.DateListedTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.items.RespondentSumTypeItem;
import uk.gov.hmcts.ecm.common.model.ccd.types.*;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;
import uk.gov.hmcts.ecm.common.model.listing.ListingDetails;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static org.junit.Assert.*;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.*;

public class ListingHelperTest {

    private ListingDetails listingDetails;
    private UserDetails userDetails;

    @Before
    public void setUp() throws Exception {
        listingDetails = generateListingDetails("listingDetailsTest1.json");
        userDetails = HelperTest.getUserDetails();
    }

    private ListingDetails generateListingDetails(String jsonFileName) throws Exception {
        String json = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(getClass().getClassLoader()
                .getResource(jsonFileName)).toURI())));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, ListingDetails.class);
    }

    @Test
    public void buildCaseCauseListByRoom() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00214.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"location\":[\n" +
                "{\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Ms AM Aspden\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n" +
                "\"respondent_town\":\"respondentTown\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"1 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes1\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Ms AM Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, DCD\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Andrew Pearl\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown1\",\n" +
                "\"claimant_representative\":\"Rep2\",\n" +
                "\"Respondent\":\"sdf2\",\n" +
                "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n" +
                "\"respondent_town\":\"respondentTown1\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"\",\n" +
                "\"Hearing_notes\":\"Notes2\",\n" +
                "\"respondent_representative\":\"Org2\"}]\n" +
                "},\n" +
                "{\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Judge For Tribunal4\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Minutes\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown2\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"Mark Taylor\",\n" +
                "\"respondent_town\":\"respondentTown2\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"\",\n" +
                "\"Hearing_notes\":\"Notes3\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Another Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"3 Minutes\",\n" +
                "\"Hearing_clerk\":\"Juan Fox\",\n" +
                "\"Claimant\":\"Mr s Dominguez\",\n" +
                "\"claimant_town\":\"\",\n" +
                "\"claimant_representative\":\"Representative\",\n" +
                "\"Respondent\":\"Respondent\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"respondent_town\":\"\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes4\",\n" +
                "\"respondent_representative\":\"Organization\"}]\n" +
                "}],\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocument() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", "", userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseListWithNoDocumentAndRangeAndScotland() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\".docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:schmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Listed_date_from\":\"2 January 2020\",\n" +
                "\"Listed_date_to\":\"1 March 2020\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        listingDetails.getCaseData().setHearingDateType(RANGE_HEARING_DATE_TYPE);
        listingDetails.getCaseData().setListingDateFrom("2020-01-02");
        listingDetails.getCaseData().setListingDateTo("2020-03-01");
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", "", userDetails, SCOTLAND_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void buildCaseCauseList() {
        String expected = "{\n" +
                "\"accessKey\":\"\",\n" +
                "\"templateName\":\"EM-TRB-SCO-ENG-00212.docx\",\n" +
                "\"outputName\":\"document.docx\",\n" +
                "\"data\":{\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"Listed_date\":\"12 October 2020\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Clerk\":\"Mike Jordan\",\n" +
                "\"listing\":[\n" +
                "{\"Judge\":\"Ms AM Aspden\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"Mark Taylor\\nTony Jones\\nSteve Thomas\",\n" +
                "\"respondent_town\":\"respondentTown\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"1 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes1\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Ms AM Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Hearing\",\n" +
                "\"Jurisdictions\":\"ADG, DCD\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Days\",\n" +
                "\"Hearing_clerk\":\"Andrew Pearl\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown1\",\n" +
                "\"claimant_representative\":\"Rep2\",\n" +
                "\"Respondent\":\"sdf2\",\n" +
                "\"resp_others\":\"Mark Taylor\\nTony Jones\",\n" +
                "\"respondent_town\":\"respondentTown1\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 2\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"\",\n" +
                "\"Hearing_notes\":\"Notes2\",\n" +
                "\"respondent_representative\":\"Org2\"},\n" +
                "{\"Judge\":\"Judge For Tribunal4\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"12 Minutes\",\n" +
                "\"Hearing_clerk\":\"Anne Fox\",\n" +
                "\"Claimant\":\"Mr s sdfs\",\n" +
                "\"claimant_town\":\"claimantTown2\",\n" +
                "\"claimant_representative\":\"Rep\",\n" +
                "\"Respondent\":\"sdf\",\n" +
                "\"resp_others\":\"Mark Taylor\",\n" +
                "\"respondent_town\":\"respondentTown2\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"\",\n" +
                "\"Hearing_notes\":\"Notes3\",\n" +
                "\"respondent_representative\":\"Org\"},\n" +
                "{\"Judge\":\"Another Judge\",\n" +
                "\"Court_addressLine1\":\"Manchester Employment Tribunal\",\n" +
                "\"Court_addressLine2\":\"Alexandra House\",\n" +
                "\"Court_addressLine3\":\"14-22 The Parsonage\",\n" +
                "\"Court_town\":\"Manchester\",\n" +
                "\"Court_county\":\"\",\n" +
                "\"Court_postCode\":\"M3 2JA\",\n" +
                "\"Court_fullAddress\":\"Manchester Employment Tribunal, Alexandra House, 14-22 The Parsonage, Manchester, M3 2JA\",\n" +
                "\"Court_telephone\":\"03577131270\",\n" +
                "\"Court_fax\":\"07577126570\",\n" +
                "\"Court_DX\":\"123456\",\n" +
                "\"Court_Email\":\"ManchesterOfficeET@hmcts.gov.uk\",\n" +
                "\"listing_logo\":\"[userImage:enhmcts.png]\",\n" +
                "\"ERMember\":\" \",\n" +
                "\"EEMember\":\" \",\n" +
                "\"Case_No\":\"1112\",\n" +
                "\"Hearing_type\":\"Preliminary Hearing (CM)\",\n" +
                "\"Jurisdictions\":\"ADG, COM\",\n" +
                "\"Hearing_date\":\"12 October 2020\",\n" +
                "\"Hearing_date_time\":\"12 October 2020 at 00:00\",\n" +
                "\"Hearing_time\":\"00:00\",\n" +
                "\"Hearing_duration\":\"3 Minutes\",\n" +
                "\"Hearing_clerk\":\"Juan Fox\",\n" +
                "\"Claimant\":\"Mr s Dominguez\",\n" +
                "\"claimant_town\":\"\",\n" +
                "\"claimant_representative\":\"Representative\",\n" +
                "\"Respondent\":\"Respondent\",\n" +
                "\"resp_others\":\"\",\n" +
                "\"respondent_town\":\"\",\n" +
                "\"Hearing_location\":\"Manchester\",\n" +
                "\"Hearing_room\":\"Tribunal 4\",\n" +
                "\"Hearing_dayofdays\":\"2 of 3\",\n" +
                "\"Hearing_panel\":\"Panel\",\n" +
                "\"Hearing_notes\":\"Notes4\",\n" +
                "\"respondent_representative\":\"Organization\"}],\n" +
                "\"case_total\":\"1\",\n" +
                "\"Today_date\":\"" + UtilHelper.formatCurrentDate(LocalDate.now()) + "\"\n" +
                "}\n" +
                "}\n";
        assertEquals(expected, ListingHelper.buildListingDocumentContent(listingDetails.getCaseData(), "", PUBLIC_CASE_CAUSE_LIST_TEMPLATE, userDetails, MANCHESTER_LISTING_CASE_TYPE_ID).toString());
    }

    @Test
    public void getCaseTypeId() {
        assertEquals(MANCHESTER_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MANCHESTER_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MANCHESTER_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MANCHESTER_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MANCHESTER_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MANCHESTER_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LEEDS_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LEEDS_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LEEDS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LEEDS_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(SCOTLAND_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(SCOTLAND_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(SCOTLAND_CASE_TYPE_ID, ListingHelper.getCaseTypeId("OTHERS"));
        assertEquals(BRISTOL_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(BRISTOL_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(BRISTOL_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(BRISTOL_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(BRISTOL_CASE_TYPE_ID, ListingHelper.getCaseTypeId(BRISTOL_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_CENTRAL_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_CENTRAL_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_CENTRAL_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_CENTRAL_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_EAST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_EAST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_EAST_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_EAST_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_SOUTH_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_SOUTH_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(LONDON_SOUTH_CASE_TYPE_ID, ListingHelper.getCaseTypeId(LONDON_SOUTH_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_EAST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_EAST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_EAST_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_EAST_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_WEST_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_WEST_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(MIDLANDS_WEST_CASE_TYPE_ID, ListingHelper.getCaseTypeId(MIDLANDS_WEST_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(NEWCASTLE_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(NEWCASTLE_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(NEWCASTLE_CASE_TYPE_ID, ListingHelper.getCaseTypeId(NEWCASTLE_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WALES_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WALES_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(WALES_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WALES_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_DEV_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WATFORD_DEV_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_USERS_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WATFORD_USERS_LISTING_CASE_TYPE_ID));
        assertEquals(WATFORD_CASE_TYPE_ID, ListingHelper.getCaseTypeId(WATFORD_LISTING_CASE_TYPE_ID));
    }

    @Test
    public void getListingTypeFromSubmitData() {
        CaseData caseData = new CaseData();
        ClaimantIndType claimantIndType = new ClaimantIndType();
        claimantIndType.setClaimantLastName("Rodriguez");
        caseData.setClaimantIndType(claimantIndType);
        RespondentSumType respondentSumType = new RespondentSumType();
        respondentSumType.setRespondentName("Juan Pedro");
        RespondentSumTypeItem respondentSumTypeItem = new RespondentSumTypeItem();
        respondentSumTypeItem.setValue(respondentSumType);
        caseData.setRespondentCollection(new ArrayList<>(Collections.singleton(respondentSumTypeItem)));
        HearingType hearingType = new HearingType();
        DateListedTypeItem dateListedTypeItem = new DateListedTypeItem();
        DateListedType dateListedType = new DateListedType();
        dateListedType.setHearingClerk("Clerk");
        dateListedType.setHearingRoomKirkawall("Tribunal 4");
        dateListedType.setHearingEdinburgh("Edinburgh");
        dateListedType.setHearingVenueDay("Other");
        dateListedType.setListedDate("2019-12-12T12:11:00.000");
        dateListedTypeItem.setId("123");
        dateListedTypeItem.setValue(dateListedType);
        hearingType.setHearingDateCollection(new ArrayList<>(Collections.singleton(dateListedTypeItem)));
        hearingType.setHearingVenue("Aberdeen");
        hearingType.setHearingEstLengthNum("2");
        hearingType.setHearingEstLengthNumType("hours");
        String expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Edinburgh, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 4, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomStranraer("Tribunal 5");
        dateListedType.setHearingEdinburgh(null);
        dateListedType.setHearingDundee("Dundee");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Dundee, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 5, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 6");
        dateListedType.setHearingDundee(null);
        dateListedType.setHearingGlasgow("Glasgow");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Glasgow, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 6, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingRoomCambeltown("Tribunal 7");
        dateListedType.setHearingGlasgow(null);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Other, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());

        CaseData caseDataRule50 = new CaseData();
        RestrictedReportingType restrictedReportingType = new RestrictedReportingType();
        restrictedReportingType.setRule503b(YES);
        caseDataRule50.setRestrictedReporting(restrictedReportingType);
        ListingData listingDataPublic = listingDetails.getCaseData();
        listingDataPublic.setHearingDocETCL(HEARING_ETCL_PUBLIC);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Other, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName= , claimantTown= , claimantRepresentative= , respondent= , respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDataPublic, caseDataRule50, hearingType, dateListedType, 1, 3).toString());
        ListingData listingDataPressList = listingDetails.getCaseData();
        listingDataPressList.setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Other, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Order made pursuant to Rule 50, claimantTown= , claimantRepresentative= , " +
                "respondent=Order made pursuant to Rule 50, respondentTown= , respondentRepresentative= , estHearingLength=2 hours, " +
                "hearingPanel= , hearingRoom=Tribunal 7, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDataPressList, caseDataRule50, hearingType, dateListedType, 1, 3).toString());

        dateListedType.setHearingVenueDay("Manchester");
        dateListedType.setHearingRoomKirkawall(null);
        dateListedType.setHearingRoomStranraer(null);
        dateListedType.setHearingRoomCambeltown(null);

        dateListedType.setHearingRoomM("Tribunal M");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal M, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomM(null);

        dateListedType.setHearingRoomL("Tribunal L");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal L, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomL(null);

        dateListedType.setHearingRoomCM("Tribunal CM");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal CM, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCM(null);

        dateListedType.setHearingRoomCC("Tribunal CC");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal CC, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCC(null);

        dateListedType.setHearingRoomCrownCourt("Tribunal Crown Court");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Crown Court, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomCrownCourt(null);

        dateListedType.setHearingRoomKendal("Tribunal Kendal");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Kendal, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomKendal(null);

        dateListedType.setHearingRoomMinshullSt("Tribunal Minshull St");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Minshull St, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomMinshullSt(null);

        dateListedType.setHearingRoomMancMagistrate("Tribunal Manc Magistrate");
        expected = "ListingType(causeListDate=12 December 2019, causeListTime=12:11, causeListVenue=Manchester, elmoCaseReference=null, " +
                "jurisdictionCodesList= , hearingType= , positionType= , hearingJudgeName= , hearingEEMember= , hearingERMember= , hearingClerk=Clerk, " +
                "hearingDay=2 of 3, claimantName=Rodriguez, claimantTown= , claimantRepresentative= , respondent=Juan Pedro, respondentTown= , " +
                "respondentRepresentative= , estHearingLength=2 hours, hearingPanel= , hearingRoom=Tribunal Manc Magistrate, respondentOthers= , hearingNotes= )";
        assertEquals(expected, ListingHelper.getListingTypeFromCaseData(listingDetails.getCaseData(), caseData, hearingType, dateListedType, 1, 3).toString());
        dateListedType.setHearingRoomMancMagistrate(null);
    }

    @Test
    public void getListingDateBetween() {
        String dateToSearchFrom = "2020-01-02";
        String dateToSearchTo = "2020-03-01";
        String dateToSearch = "2020-01-02T00:00:00.000";
        boolean isBetween = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertTrue(isBetween);

        dateToSearchFrom = "2020-01-02";
        dateToSearchTo = "";
        dateToSearch = "2020-01-02T10:00:00.000";
        boolean isEqual = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertTrue(isEqual);

        dateToSearchFrom = "2020-01-02";
        dateToSearchTo = "";
        dateToSearch = "2020-01-03T10:00:00.000";
        isEqual = ListingHelper.getListingDateBetween(dateToSearchFrom, dateToSearchTo, dateToSearch);
        assertFalse(isEqual);
    }

    @Test
    public void getListingDocName() {
        ListingData listingData = new ListingData();
        listingData.setHearingDocType(HEARING_DOC_ETCL);
        listingData.setHearingDocETCL(HEARING_ETCL_STAFF);
        listingData.setRoomOrNoRoom(NO);
        assertEquals(STAFF_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom(YES);
        assertEquals(STAFF_CASE_CAUSE_LIST_ROOM_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocETCL(HEARING_ETCL_PUBLIC);
        listingData.setRoomOrNoRoom(NO);
        assertEquals(PUBLIC_CASE_CAUSE_LIST_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setRoomOrNoRoom(YES);
        assertEquals(PUBLIC_CASE_CAUSE_LIST_ROOM_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocETCL(HEARING_ETCL_PRESS_LIST);
        listingData.setHearingDateType(RANGE_HEARING_DATE_TYPE);
        assertEquals(PRESS_LIST_CAUSE_LIST_RANGE_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        assertEquals(PRESS_LIST_CAUSE_LIST_SINGLE_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(HEARING_DOC_IT56);
        assertEquals(IT56_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType(HEARING_DOC_IT57);
        assertEquals(IT57_TEMPLATE, ListingHelper.getListingDocName(listingData));
        listingData.setHearingDocType("");
        assertEquals("No document found", ListingHelper.getListingDocName(listingData));
    }

    @Test
    public void getRespondentOthersWithLineBreaksForMultipleRespondents() {
        String expected = "Mark Taylor\\nTony Jones\\nSteve Thomas";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(0).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForTwoRespondents() {
        String expected = "Mark Taylor\\nTony Jones";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(1).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForSingleRespondent() {
        String expected = "Mark Taylor";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(2).getValue());

        assertEquals(expected, actual);
    }

    @Test
    public void getRespondentOthersWithLineBreaksForNoRespondents() {
        String expected = "";

        String actual = ListingHelper.getRespondentOthersWithLineBreaks(listingDetails.getCaseData().getListingCollection().get(3).getValue());

        assertEquals(expected, actual);
    }
}