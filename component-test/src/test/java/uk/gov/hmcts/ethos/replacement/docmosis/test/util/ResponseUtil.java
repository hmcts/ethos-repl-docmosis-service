package uk.gov.hmcts.ethos.replacement.docmosis.test.util;

import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseUtil {

    private static Properties properties;

    public static String getUrlFromResponse(Response response) {
        String urlContent =  response.body().jsonPath().get("confirmation_header").toString();
        Pattern pattern = Pattern.compile(".*href=(.*/binary).*");
        Matcher matcher = pattern.matcher(urlContent);
        if (matcher.matches()) return matcher.group(1).replace("\"", "");
        else return null;
    }

    public static String getAuthToken(String environment) throws IOException {

        if (environment.equalsIgnoreCase("local")) {
            String tidamUrl = getProperty("local.tidam.token.url");
            return getAuthTokenFromLocal(tidamUrl);
        } else {
            String authorizationUrl = getProperty(environment.toLowerCase() + ".idam.auth.url");
            String username = getProperty(environment.toLowerCase() + ".ccd.username");
            String password = getProperty(environment.toLowerCase() + ".ccd.password");

            //Generate Auth token using code
            RestAssured.config = RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig().allowAllHostnames());
            RequestSpecification httpRequest = SerenityRest.given().relaxedHTTPSValidation().config(RestAssured.config);
            httpRequest.header("Accept", "application/json");
            httpRequest.header("Content-Type", "application/x-www-form-urlencoded");
            httpRequest.formParam("username", username);
            httpRequest.formParam("password", password);
            Response response = httpRequest.post(authorizationUrl);

            Assert.assertEquals(200, response.getStatusCode());

            return response.body().jsonPath().getString("access_token");
        }
    }

    public static String getAuthTokenFromLocal(String tidamUrl) {
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.post(tidamUrl + Constants.TOKEN_URI);
        return "Bearer " + response.body().asString();
    }

    public static String getProperty(String name) throws IOException {

        if (properties == null) {
            try (InputStream inputStream = new FileInputStream("src/test/resources/config.properties")) {
                properties = new Properties();
                properties.load(inputStream);
            }
        }

        return properties.getProperty(name);
    }
}
