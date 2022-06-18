package it;

import io.quarkus.test.junit.QuarkusTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;

import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestVersion {

    @Test
    public void validateGotVersion(){
        given()
        .when().get("/api/v1/version")
        .then()
        .statusCode(200)
        .body(
            containsString("version"));
    }
}
