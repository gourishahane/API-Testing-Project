package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class testCase_API_04 {
    private static final String BASE_URL = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";

    @Test(groups = {"API Tests"})
    public void verifyDuplicateUserRegistration() {
        // Step 1: Create a new user
        String email = "duplicateUser" + System.currentTimeMillis() + "@gmail.com"; // Unique email
        String password = "password1234";
        registerUser(email, password); // First registration should succeed

        // Step 2: Try to register the same user again
        Response duplicateRegistrationResponse = registerUser(email, password); // Second registration should fail

        // Step 3: Validate that the registration fails with status code 400
        Assert.assertEquals(duplicateRegistrationResponse.statusCode(), 400, "Expected status code 400 for duplicate registration");

        // Step 4: Validate the error message in the response
        String expectedMessage = "Email already exists";
        String actualMessage = duplicateRegistrationResponse.jsonPath().getString("message");
        Assert.assertEquals(actualMessage, expectedMessage, "Expected error message for duplicate email not found");
    }

    private Response registerUser(String email, String password) {
        // Prepare registration request body
        Map<String, String> registrationBody = new HashMap<>();
        registrationBody.put("email", email);
        registrationBody.put("password", password);
        registrationBody.put("confirmpassword", password);

        // Send registration POST request
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registrationBody)
                .post(BASE_URL + "/register");
    }
}
