package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.hmcts.ethos.replacement.docmosis.test.util.model.CCDRequest;

import java.io.IOException;

public class JsonUtil {

    public static String getValue(String testDataJson, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(testDataJson);

        JsonNode jsonNode = rootNode.findValue(key);

        if (jsonNode != null) return jsonNode.textValue();

        return null;
    }

    public static CCDRequest getCaseDetails(String json, String topLevel, String childLevel) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        json = json.replace("#TOPLEVEL#", "Part_" + topLevel);
        String templateVersion = topLevel + "." + childLevel;
        json = json.replace("#VERSION#", templateVersion);

        return mapper.readValue(json, CCDRequest.class);
    }
}
