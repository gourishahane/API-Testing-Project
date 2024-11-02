package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class testCase_API_02 {
    private static final String BASE_URL = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1";

    @Test(groups = {"API Tests"})
    public void verifyCitySearchResults() {
        String searchQuery = "beng";
        String expectedDescription = "100+ Places";

        // Send GET request to cities search API
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(BASE_URL + "/cities?q=" + searchQuery);

        // Log response for debugging
        System.out.println("Search Response: " + response.getBody().asString());

        // 1. Assert that the status code is 200
        Assert.assertEquals(response.statusCode(), 200, "Expected status code 200");

        // 2. Verify the result is an array of length 1
        Assert.assertEquals(response.jsonPath().getList("$").size(), 1, "Expected result array of length 1");

        // 3. Verify the description contains "100+ Places"
        String actualDescription = response.jsonPath().getString("[0].description");
        Assert.assertTrue(actualDescription.contains(expectedDescription), 
                          "Description does not contain expected text: " + expectedDescription);

        // 4. Verify that specific fields are present and contain expected values
        String actualId = response.jsonPath().getString("[0].id");
        String actualCity = response.jsonPath().getString("[0].city");
        String actualImage = response.jsonPath().getString("[0].image");

        // Validate field values
        Assert.assertEquals(actualId, "bengaluru", "Expected id to be 'bengaluru'");
        Assert.assertEquals(actualCity, "Bengaluru", "Expected city to be 'Bengaluru'");
        Assert.assertNotNull(actualImage, "Expected image URL to be present");

        System.out.println("City search test passed successfully.");
    }
}
