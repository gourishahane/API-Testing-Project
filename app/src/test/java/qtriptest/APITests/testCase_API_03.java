package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class testCase_API_03 {
    private static final String BASE_URL = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";

    @Test(groups = {"API Tests"})
    public void verifyReservationCreation() {
        // 1. Create a new user and login
        String email = "user" + System.currentTimeMillis() + "@gmail.com";
        String password = "password1234";
        String name = "testuser"; // You can also generate this dynamically if needed
        Map<String, String> loginData = registerAndLogin(email, password);
        
        if (loginData.isEmpty()) {
            Assert.fail("Registration or login failed. Unable to retrieve token and user ID.");
            return;
        }

        String token = loginData.get("token");
        String userId = loginData.get("userId");

        // 2. Perform a booking using POST call
        String adventureId = "2447910730"; // Assuming a valid adventure ID
        String reservationId = makeReservation(token, userId, name, adventureId);

        // 3. Validate that the reservation exists using GET /reservations API
        verifyReservationListed(token, userId);
    }

    private Map<String, String> registerAndLogin(String email, String password) {
        Map<String, String> loginData = new HashMap<>();

        // Register user
        Map<String, String> registrationBody = new HashMap<>();
        registrationBody.put("email", email);
        registrationBody.put("password", password);
        registrationBody.put("confirmpassword", password);

        Response registrationResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registrationBody)
                .post(BASE_URL + "/register");

        System.out.println("Registration Response: " + registrationResponse.getBody().asString());
        Assert.assertEquals(registrationResponse.statusCode(), 201, "Expected status code 201 for successful registration");

        // Login user
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("email", email);
        loginBody.put("password", password);

        Response loginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginBody)
                .post(BASE_URL + "/login");
        
        System.out.println("Login Response: " + loginResponse.getBody().asString());

        int actualStatusCode = loginResponse.statusCode();
        Assert.assertTrue(actualStatusCode == 200 || actualStatusCode == 201, 
                "Expected status code 200 or 201 for successful login, but found: " + actualStatusCode);

        // Extract token and userId from login response
        String token = loginResponse.jsonPath().getString("data.token");
        String userId = loginResponse.jsonPath().getString("data.id");

        if (token != null && userId != null) {
            loginData.put("token", token);
            loginData.put("userId", userId);
        }
        
        return loginData;
    }

    private String makeReservation(String token, String userId, String name, String adventureId) {
        // Generate a future date for booking
        String futureDate = LocalDate.now().plusDays(7).toString();

        // Prepare reservation request body
        Map<String, Object> reservationBody = new HashMap<>();
        reservationBody.put("userId", userId);  
        reservationBody.put("name", name);
        reservationBody.put("date", futureDate); 
        reservationBody.put("person", 1);
        reservationBody.put("adventure", adventureId);

        // Send reservation POST request
        Response reservationResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(reservationBody)
                .post(BASE_URL + "/reservations/new");

        System.out.println("Reservation Response: " + reservationResponse.getBody().asString());
        Assert.assertEquals(reservationResponse.statusCode(), 200, "Expected status code 200 for successful reservation");

        // Extract reservation ID from response
        return reservationResponse.jsonPath().getString("id"); // Assuming the ID is returned in the response
    }

    private void verifyReservationListed(String token, String userId) {
        // Send GET request to retrieve reservations for the user
        Response reservationsResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .get(BASE_URL + "/reservations?id=" + userId);

        System.out.println("Reservations Response: " + reservationsResponse.getBody().asString());
        Assert.assertEquals(reservationsResponse.statusCode(), 200, "Expected status code 200 for retrieving reservations");

        // Check if there is at least one reservation in the response
        Assert.assertFalse(reservationsResponse.jsonPath().getList("id").isEmpty(), "No reservations found for the user");
  
    }
}
