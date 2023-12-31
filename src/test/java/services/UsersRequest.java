package services;

import org.json.JSONObject;
import utils.BaseRequest;

public class UsersRequest extends BaseRequest {
    /**
     * Retrieves a list of users.
     *
     * @param page    The page number to retrieve. Can be null.
     * @param perPage The number of users per page. Can be null.
     * @return A JSONObject representing the list of users.
     */
    public JSONObject getListUsers(Integer page, Integer perPage) {
        if (page != null) {
            baseRequest.queryParam("page", page);
        }

        if (perPage != null) {
            baseRequest.queryParam("per_page", perPage);
        }

        baseResponse = baseRequest
                .get("/users")
                .then()
                .extract()
                .response();

        return new JSONObject(baseResponse.getBody().asString());
    }

    /**
     * Retrieves the details of a user from the API based on the specified user ID.
     *
     * @param userId The ID of the user to retrieve details for.
     * @return A JSONObject representing the details of the user.
     */
    public JSONObject getDetailUser(String userId) {
        baseResponse = baseRequest
                .get("/users/" + userId)
                .then()
                .extract()
                .response();
        return new JSONObject(baseResponse.getBody().asString());
    }
}
