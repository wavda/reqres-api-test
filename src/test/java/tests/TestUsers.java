package tests;

import io.restassured.path.json.JsonPath;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import services.UsersRequest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestUsers {
    private JSONObject listUsers;
    private JSONObject response;
    private UsersRequest usersRequest;

    @BeforeMethod
    void before() {
        usersRequest = new UsersRequest();
    }

    @Test(description = "GET list users")
    void getListUsers() {
        response = listUsers = usersRequest.getListUsers(null, null);
        this.assertGetList(1, 6, 6);
    }

    @Test(description = "GET list users with custom 'page' and 'per_page'")
    void getListUsersWithCustomPageAndPerPage() {
        response = usersRequest.getListUsers(2, 4);
        this.assertGetList(2, 4, 4);
    }

    @Test(description = "GET detail user with valid User ID", dependsOnMethods = "getListUsers")
    void getDetailUserWithValidUserId() {
        response = usersRequest.getDetailUser("4");

        // Get expected values from the listUsers response
        JsonPath list = JsonPath.from(listUsers.toString());
        String email = list.get("data.find { it.id == 4 }.email");
        String firstName = list.get("data.find { it.id == 4 }.first_name");
        String lastName = list.get("data.find { it.id == 4 }.last_name");
        String avatar = list.get("data.find { it.id == 4 }.avatar");

        // Assertions
        usersRequest.verifyStatusCode(200);
        JSONObject dataObject = response.getJSONObject("data");
        assertEquals(dataObject.get("email"), email);
        assertEquals(dataObject.get("first_name"), firstName);
        assertEquals(dataObject.get("last_name"), lastName);
        assertEquals(dataObject.get("avatar"), avatar);
    }

    @DataProvider(name = "InvalidUserId")
    public Object[][] testData() {
        return new Object[][]{
                {"-1"},
                {"invalid"},
                {"999"}
        };
    }

    @Test(dataProvider = "InvalidUserId", description = "GET detail user with invalid User ID")
    public void getDetailUserWithInvalidUserId(String userId) {
        response = usersRequest.getDetailUser(userId);
        usersRequest.verifyStatusCode(404);
        assertTrue(response.isEmpty());
    }

    /**
     * Asserts the response of a GET list request by verifying the status code, page number, per page count,
     * total count, total pages, and length of the data array.
     *
     * @param page        The expected page number.
     * @param perPage     The expected number of items per page.
     * @param dataLength  The expected length of the data array in the response.
     */
    private void assertGetList(int page, int perPage, int dataLength) {
        usersRequest.verifyStatusCode(200);
        assertEquals(response.getInt("page"), page);
        assertEquals(response.getInt("per_page"), perPage);
        assertTrue(response.getInt("total") > 0);
        assertEquals(response.getInt("total_pages"), response.getInt("total") / response.getInt("per_page"));
        assertEquals(response.getJSONArray("data").length(), dataLength);
    }
}
