package utils;

import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static org.testng.AssertJUnit.assertEquals;

public class BaseRequest {
    protected Response baseResponse;
    protected RequestSpecification baseRequest;
    RestAssuredConfig timeoutConfig;

    public BaseRequest() {
        timeoutConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", 30000));

        RestAssured.baseURI = "http://reqres.in/api";
        baseRequest = RestAssured
                .given()
                .config(timeoutConfig);
    }

    /**
     * Verifies the status code of the base response matches the expected status code.
     * It also adds attachments to Allure report for the status code and response body.
     *
     * @param expectedStatusCode The expected status code to compare against.
     */
    public void verifyStatusCode(int expectedStatusCode) {
        Allure.addAttachment("Status Code", String.valueOf(baseResponse.getStatusCode()));
        Allure.addAttachment("Response Body", baseResponse.getBody().asString());
        assertEquals(expectedStatusCode, baseResponse.getStatusCode());
    }
}
