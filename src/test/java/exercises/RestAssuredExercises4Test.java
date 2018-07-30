package exercises;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class RestAssuredExercises4Test {

    private static RequestSpecification requestSpec;


    @BeforeAll
    static void setUp() {
        createRequestSpecification();
        retrieveOAuthToken();
    }


    static void createRequestSpecification() {

        requestSpec = new RequestSpecBuilder().
                setBaseUri("http://localhost").
                setPort(9876).
                setBasePath("/api/f1").
                build();
    }

    /*******************************************************
     * Request an authentication token through the API
     * and extract the value of the access_token field in
     * the response to a String variable.
     * Use preemptive Basic authentication:
     * username = oauth
     * password = gimmeatoken
     * Use /oauth2/token
     ******************************************************/

    private static String accessToken;

    public static void retrieveOAuthToken() {
       accessToken=given().spec(requestSpec)
                .auth()
                .preemptive()
                .basic("oauth","gimmeatoken")
                .when()
                .get("/oauth2/token")
               .then()
               .extract()
               .path("access_token");
    }

    /*******************************************************
     * Request a list of payments for this account and check
     * that the number of payments made equals 4.
     * Use OAuth2 authentication with the previously retrieved
     * authentication token.
     * Use /payments
     * Value to be retrieved is in the paymentsCount field
     ******************************************************/

    @Test
    public void checkNumberOfPayments() {

        given().spec(requestSpec)
                .auth()
                .oauth2(accessToken)
                .when().log().uri()
                .get("/payments")
                .then().log().body()
                .assertThat()
                .statusCode(200)
                .and()
                .body("paymentsCount",equalTo(4));
    }

    /*******************************************************
     * Request the list of all circuits that hosted a
     * Formula 1 race in 2014 and check that this request is
     * answered within 100 ms
     * Use /2014/circuits.json
     ******************************************************/

    @Test
    public void checkResponseTimeFor2014CircuitList() {

        given().spec(requestSpec)
                .when().log().uri()
                .get("/2014/circuits.json")
                .then().log().body()
                .assertThat()
                .statusCode(200)
                .and()
                .time(lessThan(100L),TimeUnit.MILLISECONDS);
    }
}
