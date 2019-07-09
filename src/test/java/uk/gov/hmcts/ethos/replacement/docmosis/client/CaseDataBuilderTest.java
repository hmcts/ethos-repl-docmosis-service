package uk.gov.hmcts.ethos.replacement.docmosis.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkDetails;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient.CREATION_EVENT_SUMMARY;
import static uk.gov.hmcts.ethos.replacement.docmosis.client.CcdClient.UPDATE_BULK_EVENT_SUMMARY;

@RunWith(SpringJUnit4ClassRunner.class)
public class CaseDataBuilderTest {

    @InjectMocks
    private CaseDataBuilder caseDataBuilder;
    @Mock
    private ObjectMapper objectMapper;
    private Map<String, JsonNode> data;
    private CaseDetails caseDetails;
    private CCDRequest ccdRequest;
    private CaseData caseData;
    private BulkDetails bulkDetails;
    private BulkData bulkData;

    @Before
    public void setUp() {
        ccdRequest = new CCDRequest();
        ccdRequest.setEventId("1111");
        ccdRequest.setToken("Token");
        caseDetails = new CaseDetails();
        caseDetails.setJurisdiction("TRIBUNALS");
        caseDetails.setCaseTypeId("Type1");
        caseData = new CaseData();
        caseDetails.setCaseData(caseData);
        data = new HashMap<>();
        bulkDetails = new BulkDetails();
        bulkDetails.setJurisdiction("TRIBUNALS");
        bulkDetails.setCaseTypeId("Type1");
        bulkData = new BulkData();
        bulkDetails.setCaseData(bulkData);
    }

    @Test
    public void buildCaseDataContent() {
        when(objectMapper.convertValue(caseDetails.getCaseData(), new TypeReference<Map<String, JsonNode>>(){})).thenReturn(data);
        CaseDataContent caseDataContent = CaseDataContent.builder()
                .event(Event.builder().eventId(ccdRequest.getEventId()).summary(CREATION_EVENT_SUMMARY).build())
                .ignoreWarning(false)
                .token(ccdRequest.getToken())
                .build();
        assertEquals(caseDataContent, caseDataBuilder.buildCaseDataContent(caseData, ccdRequest, CREATION_EVENT_SUMMARY));
    }

    @Test
    public void buildBulkDataContent() {
        when(objectMapper.convertValue(bulkDetails.getCaseData(), new TypeReference<Map<String, JsonNode>>(){})).thenReturn(data);
        CaseDataContent caseDataContent = CaseDataContent.builder()
                .event(Event.builder().eventId(ccdRequest.getEventId()).summary(UPDATE_BULK_EVENT_SUMMARY).build())
                .ignoreWarning(false)
                .token(ccdRequest.getToken())
                .build();
        assertEquals(caseDataContent, caseDataBuilder.buildBulkDataContent(bulkData, ccdRequest, UPDATE_BULK_EVENT_SUMMARY));
    }
}