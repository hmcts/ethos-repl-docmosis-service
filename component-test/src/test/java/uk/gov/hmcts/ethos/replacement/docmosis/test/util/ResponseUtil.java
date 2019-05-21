package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class ResponseUtil {

    public static String getUrlFromResponse(Response response) {
        return response.body().jsonPath().getMap("significant_item").get("url").toString();
    }

    public static String getAuthToken() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.post(Constants.URL_GEN_TOKEN);
        return "Bearer " + response.body().asString();
    }
}
