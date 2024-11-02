package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;
import io.restassured.RestAssured;



public class testCase_API_01 {
    private static final String base_Url = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";

    @Test(groups = {"API Tests"})
    public void verifyUserRegistrationAndLogin() {

        String uniqueEmail = "user" + System.currentTimeMillis() + "@gmail.com"; 
        String password = "password1234"; 
         
         System.out.println("Testing with email: " + uniqueEmail + ", password: " + password);

        
        JSONObject registerPayload = new JSONObject();
        registerPayload.put("email", uniqueEmail);
        registerPayload.put("password", password);
        registerPayload.put("confirmpassword", password);
        

        Response registerResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(registerPayload.toString())
                .when()
                .post(base_Url + "/register");

                System.out.println("Registration Response: " + registerResponse.getBody().asString());
      

        Assert.assertEquals(registerResponse.statusCode(), 201, "Registration failed");
        System.out.println("User registered successfully with status code 201");

        JSONObject loginPayload = new JSONObject();
        loginPayload.put("email", uniqueEmail);
        loginPayload.put("password", password);


        Response loginResponse = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(loginPayload.toString())
                .when()
                .post(base_Url + "/login");

                System.out.println("Login Response: " + loginResponse.getBody().asString());


     
        Assert.assertEquals(loginResponse.statusCode(), 201, "Login failed");

        String token = loginResponse.jsonPath().getString("data.token");
        String userId = loginResponse.jsonPath().getString("data.id");

        
         Assert.assertNotNull(token, "Token not returned");
         Assert.assertNotNull(userId, "User ID not returned");

         System.out.println("User login successful with token: " + token + " and userId: " + userId);
    }
}
