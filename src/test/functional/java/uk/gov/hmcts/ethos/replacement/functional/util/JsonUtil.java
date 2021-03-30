package uk.gov.hmcts.ethos.replacement.functional.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import uk.gov.hmcts.ecm.common.model.bulk.BulkRequest;
import uk.gov.hmcts.ecm.common.model.ccd.CCDRequest;

import java.io.IOException;

public class JsonUtil {

    private JsonUtil() {
    }

    public static String getValue(String testDataJson, String key) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(testDataJson);

        JsonNode jsonNode = rootNode.findValue(key);

        if (jsonNode != null) {
            return jsonNode.textValue();
        }

        return null;
    }

    public static CCDRequest getCaseDetails(String json, String topLevel, String childLevel,
                                            boolean isScotland) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        int intTopLevel = Integer.parseInt(topLevel);

        if (isScotland) {
            int templateNo = 0;
            if (intTopLevel >= 1 && intTopLevel <= 7) {
                templateNo = 1;
            } else if (intTopLevel >= 8 && intTopLevel <= 14) {
                templateNo = 2;
            } else if (intTopLevel >= 15 && intTopLevel <= 16) {
                templateNo = 3;
            } else if (intTopLevel >= 18 && intTopLevel <= 25) {
                templateNo = 4;
            } else if (intTopLevel >= 26 && intTopLevel <= 33) {
                templateNo = 5;
            } else if (intTopLevel >= 34 && intTopLevel <= 57) {
                templateNo = 6;
            } else if (intTopLevel >= 58 && intTopLevel <= 63) {
                templateNo = 7;
            } else if (intTopLevel >= 64 && intTopLevel <= 69) {
                templateNo = 8;
            } else if (intTopLevel >= 71 && intTopLevel <= 91) {
                templateNo = 10;
            } else if (intTopLevel >= 92 && intTopLevel <= 98) {
                templateNo = 11;
            } else if (intTopLevel >= 99 && intTopLevel <= 103) {
                templateNo = 12;
            } else if (intTopLevel >= 104 && intTopLevel <= 106) {
                templateNo = 13;
            } else if (intTopLevel >= 110 && intTopLevel <= 152) {
                templateNo = 14;
            } else if (intTopLevel >= 159 && intTopLevel <= 172) {
                templateNo = 15;
            } else if (intTopLevel >= 180 && intTopLevel <= 194) {
                templateNo = 16;
            }
            if (templateNo < 10) {
                templateNo += 41;
            } else {
                templateNo += 40;
            }
            json.replace("#TOPLEVEL#", "EM-TRB-SCO-ENG-000" + templateNo);
        } else {
            if (intTopLevel < 9) {
                intTopLevel += 25;
            } else {
                intTopLevel += 24;
            }
            if (topLevel.equals("8")) {
                intTopLevel = 65;
            }
            if (topLevel.equals("18")) {
                intTopLevel = 66;
            }
            json.replace("#TOPLEVEL#", "EM-TRB-EGW-ENG-000" + intTopLevel);
        }

        String templateVersion;

        if (StringUtils.isEmpty(childLevel)) {
            templateVersion = topLevel;
        } else {
            if (topLevel.equalsIgnoreCase("14") && childLevel.equalsIgnoreCase("A")) {
                templateVersion = "14A";
            } else if (topLevel.equalsIgnoreCase("16") && childLevel.equalsIgnoreCase("A")) {
                templateVersion = "16A";
            } else {
                templateVersion = topLevel + "." + childLevel;
            }
        }

        return mapper.readValue(json.replace("#VERSION#", templateVersion), CCDRequest.class);
    }

    public static BulkRequest getBulkDetails(boolean isScotland, String testData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(testData, BulkRequest.class);
    }
}
