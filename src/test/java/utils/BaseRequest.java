package utils;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.testng.AssertJUnit.assertEquals;

public class BaseRequest {
    protected Response response;
    protected RequestSpecification request;
    RestAssuredConfig timeoutConfig;

    public BaseRequest() {
        timeoutConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000));

        RestAssured.baseURI = "http://reqres.in/api";
        request = RestAssured
                .given()
                .config(timeoutConfig);
    }

    /**
     * Verifies the status code of the API response.
     *
     * @param expectedStatusCode The expected status code to compare against the actual response status code.
     */
    public void verifyStatusCode(int expectedStatusCode) {
        Allure.addAttachment("Status Code", String.valueOf(response.getStatusCode()));
        Allure.addAttachment("Response Body", response.getBody().asString());
        assertEquals(expectedStatusCode, response.getStatusCode());
    }
}
