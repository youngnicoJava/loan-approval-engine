package com.loanorigination;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
class ProductionReadinessTest {
    @Test
    void healthEndpointsAndMetricsAreAvailable() {
        given().get("/q/health/live").then().statusCode(200);
        given().get("/q/health/ready").then().statusCode(200);
        given().get("/q/metrics").then().statusCode(200).body(containsString("http_server_active_requests"));
    }

    @Test
    void correlationIdIsGeneratedAndPreservedForUnauthorizedApiCalls() {
        given().get("/api/v1/audit/events")
                .then().statusCode(401).header("X-Correlation-ID", notNullValue());

        given().header("X-Correlation-ID", "request-42").get("/api/v1/audit/events")
                .then().statusCode(401).header("X-Correlation-ID", "request-42");

        String invalid = given().header("X-Correlation-ID", "not a valid id").get("/api/v1/audit/events")
                .then().statusCode(401).extract().header("X-Correlation-ID");
        assertNotEquals("not a valid id", invalid);
        org.jboss.logging.MDC.put("correlationId", invalid);
        try {
            assertEquals(invalid, com.loanorigination.loanapplication.adapter.in.rest.error.ApiErrorResponse
                    .of("TEST_ERROR", "Safe message").correlationId());
        } finally {
            org.jboss.logging.MDC.remove("correlationId");
        }
    }
}
