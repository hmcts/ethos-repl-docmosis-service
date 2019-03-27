package uk.gov.hmcts.ethos.replacement.docmosis.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.ethos.replacement.docmosis.model.ccd.*;

import java.util.Map;

@Slf4j
@Component
public class CaseDataBuilder {

    private final ObjectMapper objectMapper;
    static final String EVENT_SUMMARY = "case created automatically";
    private static final Boolean IGNORE_WARNING = Boolean.FALSE;

    @Autowired
    public CaseDataBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    CaseDataContent buildCaseDataContent(CaseDetails caseDetails, CCDRequest req) {
        Map<String, JsonNode> data = objectMapper.convertValue(caseDetails.getCaseData(), new TypeReference<Map<String, JsonNode>>(){});
        return CaseDataContent.builder()
                .event(Event.builder().eventId(req.getEventId()).summary(EVENT_SUMMARY).build())
                .data(data)
                .token(req.getToken())
                .ignoreWarning(IGNORE_WARNING)
                .build();
    }
}
