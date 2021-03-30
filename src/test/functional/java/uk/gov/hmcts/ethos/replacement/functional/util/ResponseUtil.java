package uk.gov.hmcts.ethos.replacement.functional.util;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseUtil {

    private static Properties properties;

    private ResponseUtil() {
    }

    public static String getUrlFromResponse(Response response) {
        String urlContent =  response.body().jsonPath().get("confirmation_header").toString();
        Pattern pattern = Pattern.compile(".*href=(.*/binary).*");
        Matcher matcher = pattern.matcher(urlContent);
        if (matcher.matches()) {
            return matcher.group(1).replace("\"", "");
        } else {
            return null;
        }
    }

    public static String getAuthToken(String environment) throws IOException {

        //Generate Auth token using code
        RestAssured.useRelaxedHTTPSValidation();
        //RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
        RequestSpecification httpRequest = SerenityRest.given().relaxedHTTPSValidation().config(RestAssured.config);
        httpRequest.header("Accept", "application/json");
        httpRequest.header("Content-Type", "application/x-www-form-urlencoded");
        httpRequest.formParam("username", getProperty(environment.toLowerCase() + ".ccd.username"));
        httpRequest.formParam("password",  getProperty(environment.toLowerCase() + ".ccd.password"));
        Response response = httpRequest.post(getProperty(environment.toLowerCase() + ".idam.auth.url"));

        Assert.assertEquals(200, response.getStatusCode());

        return response.body().jsonPath().getString("access_token");
    }

    public static String getAuthTokenFromLocal(String tidamUrl) {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.post(tidamUrl + Constants.TOKEN_URI);
        return "Bearer " + response.body().asString();
    }

    public static String getProperty(String name) throws IOException {

        if (properties == null) {
            try (InputStream inputStream = new FileInputStream(Constants.CONFIG_PROPERTIES)) {
                properties = new Properties();
                properties.load(inputStream);
            }
        }

        return properties.getProperty(name);
    }
}
