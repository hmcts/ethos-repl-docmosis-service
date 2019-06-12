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

    public static CCDRequest getCaseDetails(String json, String topLevel, String childLevel, boolean isScotland) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        int intTopLevel = Integer.parseInt(topLevel);

        if (isScotland) {
            if (intTopLevel < 10) intTopLevel += 41;
            else intTopLevel += 40;
            json = json.replace("#TOPLEVEL#", "EM-TRB-SCO-ENG-000" + intTopLevel);
        } else {
            if (intTopLevel < 9) intTopLevel += 25;
            else intTopLevel += 24;
            json = json.replace("#TOPLEVEL#", "EM-TRB-EGW-ENG-000" + intTopLevel);
        }
        String templateVersion = topLevel + "." + childLevel;
        json = json.replace("#VERSION#", templateVersion);

        return mapper.readValue(json, CCDRequest.class);
    }
}
