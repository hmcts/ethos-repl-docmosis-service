package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseUtil {

    public static String getUrlFromResponse(Response response) {
        String urlContent =  response.body().jsonPath().get("confirmation_header").toString();
        Pattern pattern = Pattern.compile(".*href=(.*/binary).*");
        Matcher matcher = pattern.matcher(urlContent);
        if (matcher.matches()) return matcher.group(1).replace("\"", "");
        else return null;
    }

    public static String getAuthToken() {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.post(Constants.URL_GEN_TOKEN);
        return "Bearer " + response.body().asString();
    }
}
