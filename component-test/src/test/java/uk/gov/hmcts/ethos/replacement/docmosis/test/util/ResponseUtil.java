package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import com.jayway.restassured.response.Response;

public class ResponseUtil {

    public static String getUrlFromResponse(Response response) {
        return response.body().jsonPath().getMap("significant_item").get("url").toString();
    }
}
