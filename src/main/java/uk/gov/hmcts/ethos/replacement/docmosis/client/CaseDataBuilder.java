package uk.gov.hmcts.ethos.replacement.docmosis.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ethos.replacement.docmosis.model.bulk.BulkData;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.util.Map;

@Slf4j
@Component
public class CaseDataBuilder {

    private final ObjectMapper objectMapper;
    private static final Boolean IGNORE_WARNING = Boolean.FALSE;

    @Autowired
    public CaseDataBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    CaseDataContent buildCaseDataContent(CaseData caseData, CCDRequest req, String eventSummary) {
        return getCaseDataContent(req, objectMapper.convertValue(caseData, new TypeReference<Map<String, JsonNode>>(){}), eventSummary);
    }

    CaseDataContent buildBulkDataContent(BulkData bulkData, CCDRequest req, String eventSummary) {
        return getCaseDataContent(req, objectMapper.convertValue(bulkData, new TypeReference<Map<String, JsonNode>>(){}), eventSummary);
    }

    private CaseDataContent getCaseDataContent(CCDRequest req, Map<String, JsonNode> data, String eventSummary) {
        return CaseDataContent.builder()
                .event(Event.builder().eventId(req.getEventId()).summary(eventSummary).build())
                .data(data)
                .token(req.getToken())
                .ignoreWarning(IGNORE_WARNING)
                .build();
    }
}
