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
    private UsersRequest users;

    @BeforeMethod
    void before() {
        users = new UsersRequest();
    }

    @Test(description = "GET list users")
    void getListUsers() {
        response = listUsers = users.getListUsers(null, null);

        // Assertions
        users.verifyStatusCode(200);
        this.doAssertGetList(1, 6);
    }

    @Test(description = "GET list users with custom 'page' and 'per_page'")
    void getListUsersWithCustomPageAndPerPage() {
        response = users.getListUsers(2, 4);

        // Assertions
        users.verifyStatusCode(200);
        this.doAssertGetList(2, 4);
    }

    @Test(description = "GET detail user with valid User ID", dependsOnMethods = "getListUsers")
    void getDetailUserWithValidUserId() {
        response = users.getDetailUser("4");

        // Get expected values from the listUsers response
        JsonPath list = JsonPath.from(listUsers.toString());
        String email = list.get("data.find { it.id == 4 }.email");
        String firstName = list.get("data.find { it.id == 4 }.first_name");
        String lastName = list.get("data.find { it.id == 4 }.last_name");
        String avatar = list.get("data.find { it.id == 4 }.avatar");

        // Assertions
        users.verifyStatusCode(200);
        JSONObject dataObject = response.getJSONObject("data");
        assertEquals(dataObject.get("email"), email);
        assertEquals(dataObject.get("first_name"), firstName);
        assertEquals(dataObject.get("last_name"), lastName);
        assertEquals(dataObject.get("avatar"), avatar);
    }

    @Test(description = "GET detail user with non-existent User ID")
    void getDetailUserWithNonExistentUserId() {
        int nonExistentUserId = listUsers.getInt("total") + 1;
        response = users.getDetailUser(String.valueOf(nonExistentUserId));

        // Assertions
        users.verifyStatusCode(404);
        assertTrue(response.isEmpty());
    }

    @Test(description = "GET detail user with invalid numeric value as User ID")
    void getDetailUserWithInvalidNumericValueAsUserId() {
        response = users.getDetailUser("-1");

        // Assertions
        users.verifyStatusCode(404);
        assertTrue(response.isEmpty());
    }

    @Test(description = "GET detail user with non-numeric value as User ID")
    void getDetailUserWithNonNumericValueAsUserId() {
        response = users.getDetailUser("invalid");

        // Assertions
        users.verifyStatusCode(404);
        assertTrue(response.isEmpty());
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
        response = users.getDetailUser(userId);

        // Assertions
        users.verifyStatusCode(404);
        assertTrue(response.isEmpty());
    }

    private void doAssertGetList(int page, int perPage) {
        users.verifyStatusCode(200);
        assertEquals(response.getInt("page"), page);
        assertEquals(response.getInt("per_page"), perPage);
        assertTrue(response.getInt("total") > 0);
        assertEquals(response.getInt("total_pages"), (int) Math.ceil(response.getInt("total") / response.getInt("per_page")));
        assertEquals(response.getJSONArray("data").length(), perPage);
    }
}
