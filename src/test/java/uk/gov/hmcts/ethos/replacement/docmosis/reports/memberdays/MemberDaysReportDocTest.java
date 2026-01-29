package uk.gov.hmcts.ethos.replacement.docmosis.reports.memberdays;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.ecm.common.model.listing.ListingData;

import java.text.DecimalFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.NEW_LINE;
import static uk.gov.hmcts.ecm.common.model.helper.Constants.SINGLE_HEARING_DATE_TYPE;
import static uk.gov.hmcts.ethos.replacement.docmosis.helpers.Helper.nullCheck;

public class MemberDaysReportDocTest {

    MemberDaysReportDoc memberDaysReportDoc;
    MemberDaysReportData listingData;
    MemberDaysReport memberDaysReport;
    MemberDaysReportDetail detailItem;

    @Before
    public void setUp() {
        memberDaysReport = new MemberDaysReport();
        detailItem = new MemberDaysReportDetail();
        listingData = new MemberDaysReportData();
        memberDaysReportDoc = new MemberDaysReportDoc();
    }

    @Test()
    public void shouldThrowException() throws IllegalStateException {
        var nonMemberDaysReportDocListingData = new ListingData();
        assertThrows(IllegalStateException.class,
            () -> memberDaysReportDoc.getReportDocPart(nonMemberDaysReportDocListingData));
    }

    @Test
    public void shouldReturnCorrectReportPartialWithDetails() {

        detailItem = new MemberDaysReportDetail();
        detailItem.setHearingDate("15 September 2021");
        detailItem.setEmployeeMember("EE Member");
        detailItem.setEmployeeMember("ER Member");
        detailItem.setCaseReference("1800003/2021");
        detailItem.setHearingNumber("33");
        detailItem.setHearingType("Preliminary Hearing");
        detailItem.setHearingClerk("Tester Clerk");
        detailItem.setHearingDuration("420");
        listingData.getReportDetails().add(detailItem);

        var expectedDetailRowContent = new StringBuilder();

        expectedDetailRowContent.append("\"Listed_date\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Report_Office\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Full_Days\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Half_Days\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Days\":\"").append(NEW_LINE);

        expectedDetailRowContent.append("\"memberDaySummaryItems\":[").append("\n");
        expectedDetailRowContent.append("],").append("\n");

        expectedDetailRowContent.append("\"reportDetails\":[").append("\n");
        expectedDetailRowContent.append("{").append("\n");
        expectedDetailRowContent.append("\"Detail_Hearing_Date\":\"")
            .append(nullCheck(detailItem.getHearingDate())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employee_Member\":\"")
            .append(nullCheck(detailItem.getEmployeeMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employer_Member\":\"")
            .append(nullCheck(detailItem.getEmployerMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Case_Reference\":\"")
            .append(nullCheck(detailItem.getCaseReference())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Number\":\"")
            .append(nullCheck(detailItem.getHearingNumber())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Type\":\"")
            .append(nullCheck(detailItem.getHearingType())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Clerk\":\"")
            .append(nullCheck(detailItem.getHearingClerk())).append(NEW_LINE);
        var durationInMinutes = Double.parseDouble(detailItem.getHearingDuration());
        expectedDetailRowContent.append("\"Hearing_Duration\":\"")
            .append(nullCheck(String.valueOf(new DecimalFormat("#").format(durationInMinutes))))
            .append("\"\n");
        expectedDetailRowContent.append("}]").append(",\n");
        var resultListingData = memberDaysReportDoc.getReportDocPart(listingData);
        assertFalse(resultListingData.toString().isEmpty());
        assertEquals(expectedDetailRowContent.toString(), resultListingData.toString());
    }

    @Test
    public void shouldReturnCorrectReportPartialWithSummary() {

        detailItem = new MemberDaysReportDetail();
        detailItem.setHearingDate("15 September 2021");
        detailItem.setEmployeeMember("EE Member");
        detailItem.setEmployeeMember("ER Member");
        detailItem.setCaseReference("1800003/2021");
        detailItem.setHearingNumber("33");
        detailItem.setHearingType("Preliminary Hearing");
        detailItem.setHearingClerk("Tester Clerk");
        detailItem.setHearingDuration("420");
        listingData.getReportDetails().add(detailItem);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingDate("2021-09-15");

        var expectedDetailRowContent = new StringBuilder();
        expectedDetailRowContent.append("\"Listed_date\":\"15 September 2021").append(NEW_LINE);
        expectedDetailRowContent.append("\"Report_Office\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Full_Days\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Half_Days\":\"").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Days\":\"").append(NEW_LINE);

        var memberDaySummaryItem = new MemberDaySummaryItem();
        memberDaySummaryItem.setHearingDate("15 September 2021");
        memberDaySummaryItem.setFullDays("2");
        memberDaySummaryItem.setHalfDays("0");
        memberDaySummaryItem.setTotalDays("2");

        listingData.getMemberDaySummaryItems().add(memberDaySummaryItem);

        expectedDetailRowContent.append("\"memberDaySummaryItems\":[").append("\n");
        expectedDetailRowContent.append("{").append("\n");
        expectedDetailRowContent.append("\"Hearing_Date\":\"15 September 2021").append(NEW_LINE);
        expectedDetailRowContent.append("\"Full_Days\":\"2").append(NEW_LINE);
        expectedDetailRowContent.append("\"Half_Days\":\"0").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Days\":\"2").append("\"\n");
        expectedDetailRowContent.append("}]").append(",\n");

        expectedDetailRowContent.append("\"reportDetails\":[").append("\n");
        expectedDetailRowContent.append("{").append("\n");
        expectedDetailRowContent.append("\"Detail_Hearing_Date\":\"")
            .append(nullCheck(detailItem.getHearingDate())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employee_Member\":\"")
            .append(nullCheck(detailItem.getEmployeeMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employer_Member\":\"")
            .append(nullCheck(detailItem.getEmployerMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Case_Reference\":\"")
            .append(nullCheck(detailItem.getCaseReference())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Number\":\"")
            .append(nullCheck(detailItem.getHearingNumber())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Type\":\"")
            .append(nullCheck(detailItem.getHearingType())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Clerk\":\"")
            .append(nullCheck(detailItem.getHearingClerk())).append(NEW_LINE);
        var durationInMinutes = Double.parseDouble(detailItem.getHearingDuration());
        expectedDetailRowContent.append("\"Hearing_Duration\":\"")
            .append(nullCheck(String.valueOf(new DecimalFormat("#").format(durationInMinutes))))
            .append("\"\n");
        expectedDetailRowContent.append("}]").append(",\n");
        var resultListingData = memberDaysReportDoc.getReportDocPart(listingData);
        assertFalse(resultListingData.toString().isEmpty());
        assertEquals(expectedDetailRowContent.toString(), resultListingData.toString());
    }

    @Test
    public void shouldReturnCorrectReportPartialWithSummaryHeader() {
        detailItem = new MemberDaysReportDetail();
        detailItem.setHearingDate("15 September 2021");
        detailItem.setEmployeeMember("EE Member");
        detailItem.setEmployeeMember("ER Member");
        detailItem.setCaseReference("1800003/2021");
        detailItem.setHearingNumber("33");
        detailItem.setHearingType("Preliminary Hearing");
        detailItem.setHearingClerk("Tester Clerk");
        detailItem.setHearingDuration("420");

        var memberDaySummaryItem = new MemberDaySummaryItem();
        memberDaySummaryItem.setHearingDate("15 September 2021");
        memberDaySummaryItem.setFullDays("2");
        memberDaySummaryItem.setHalfDays("0");
        memberDaySummaryItem.setTotalDays("2");

        listingData.getReportDetails().add(detailItem);
        listingData.setHearingDateType(SINGLE_HEARING_DATE_TYPE);
        listingData.setListingDate("2021-09-18");
        listingData.setOffice("MukeraCity");
        listingData.setHalfDaysTotal("0");
        listingData.setFullDaysTotal("2");
        listingData.setTotalDays("2.0");
        listingData.getMemberDaySummaryItems().add(memberDaySummaryItem);

        var expectedDetailRowContent = new StringBuilder();
        expectedDetailRowContent.append("\"Listed_date\":\"18 September 2021").append(NEW_LINE);
        expectedDetailRowContent.append("\"Report_Office\":\"MukeraCity").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Full_Days\":\"2").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Half_Days\":\"0").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Days\":\"2.0").append(NEW_LINE);

        expectedDetailRowContent.append("\"memberDaySummaryItems\":[").append("\n");
        expectedDetailRowContent.append("{").append("\n");
        expectedDetailRowContent.append("\"Hearing_Date\":\"15 September 2021").append(NEW_LINE);
        expectedDetailRowContent.append("\"Full_Days\":\"2").append(NEW_LINE);
        expectedDetailRowContent.append("\"Half_Days\":\"0").append(NEW_LINE);
        expectedDetailRowContent.append("\"Total_Days\":\"2").append("\"\n");
        expectedDetailRowContent.append("}]").append(",\n");

        expectedDetailRowContent.append("\"reportDetails\":[").append("\n");
        expectedDetailRowContent.append("{").append("\n");
        expectedDetailRowContent.append("\"Detail_Hearing_Date\":\"")
            .append(nullCheck(detailItem.getHearingDate())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employee_Member\":\"")
            .append(nullCheck(detailItem.getEmployeeMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Employer_Member\":\"")
            .append(nullCheck(detailItem.getEmployerMember())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Case_Reference\":\"")
            .append(nullCheck(detailItem.getCaseReference())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Number\":\"")
            .append(nullCheck(detailItem.getHearingNumber())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Type\":\"")
            .append(nullCheck(detailItem.getHearingType())).append(NEW_LINE);
        expectedDetailRowContent.append("\"Hearing_Clerk\":\"")
            .append(nullCheck(detailItem.getHearingClerk())).append(NEW_LINE);
        var durationInMinutes = Double.parseDouble(detailItem.getHearingDuration());
        expectedDetailRowContent.append("\"Hearing_Duration\":\"")
            .append(nullCheck(String.valueOf(new DecimalFormat("#").format(durationInMinutes))))
            .append("\"\n");
        expectedDetailRowContent.append("}]").append(",\n");
        var resultListingData = memberDaysReportDoc.getReportDocPart(listingData);
        assertFalse(resultListingData.toString().isEmpty());
        assertEquals(expectedDetailRowContent.toString(), resultListingData.toString());
    }
}
