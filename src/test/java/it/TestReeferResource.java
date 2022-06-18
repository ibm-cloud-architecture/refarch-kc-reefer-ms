package it;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ibm.eda.kc.freezerms.domain.Reefer;
import ibm.eda.kc.freezerms.infra.api.dto.OrderDTO;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class TestReeferResource {
    private String path = "/api/v1/reefers";
    private String sanJuanReeferID = null;

    private OrderDTO buildOrder(){
        OrderDTO order = new OrderDTO();
        order.orderID = "Test_O1";
        order.quantity = 80;
        order.pickupCity = "San Francisco";
        order.destinationCity = "New york";
        return order;
    }

    @Test
    @Order(1)
    public void testGetExistingListOfFreezer() {
        Response resp = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when().get(path).then()
        .statusCode(200)
        .contentType(ContentType.JSON)
            .extract()
            .response();
            System.out.println(resp.jsonPath().prettyPrint());
            Reefer[] freezers = resp.body().as(Reefer[].class);
            Assertions.assertTrue(freezers.length >= 2);
    }
    
    @Test
    @Order(2)
    public void createFreezerShouldReturnAFreezerWithID() {
        Reefer freezerRequest = new Reefer();
        freezerRequest.brand = "MARENOSTRUM";
        freezerRequest.capacity = 120;
        freezerRequest.currentFreeCapacity = 120;
        freezerRequest.location = "San Juan";
        freezerRequest.type = Reefer.MODEL_40;
        Response resp = with().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
          .body(freezerRequest)
          .when().post(path)
          .then()
             .statusCode(200)
             .contentType(ContentType.JSON)
        .extract()
        .response();
        Reefer freezerResp= resp.body().as(Reefer.class);
        Assertions.assertTrue(freezerResp.reeferID != null);
        System.out.println(freezerResp.reeferID);
        sanJuanReeferID = freezerResp.reeferID;
    }

    @Test
    @Order(3)
    public void shouldHaveOneMoreReefer() {
        Response resp = given().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .when().get(path).then()
        .statusCode(200)
        .contentType(ContentType.JSON)
            .extract()
            .response();
            System.out.println(resp.jsonPath().prettyPrint());
            Reefer[] freezers = resp.body().as(Reefer[].class);
            Assertions.assertTrue(freezers.length >= 3);
    }

    @Test
    @Order(4)
    public void processOrderShouldReturnAllocatedReefer(){
        OrderDTO order = buildOrder();
        Response resp = with().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .body(order)
        .when().post(path + "/assignOrder")
        .then()
           .statusCode(200)
           .contentType(ContentType.JSON)
      .extract()
      .response();
      OrderDTO out = resp.body().as(OrderDTO.class);
      Assertions.assertNotNull(out);
      Assertions.assertEquals("C01",out.containerID);
    }


    @Test
    @Order(5)
    public void whenComposateContainerIDshouldBeEmpty(){
        OrderDTO order = buildOrder();
        Response resp = with().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .body(order)
        .when().post(path + "/assignOrder")
        .then()
           .statusCode(200)
           .contentType(ContentType.JSON)
      .extract()
      .response();

      resp = with().headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
        .body(order)
        .when().put(path + "/compensateOrder")
        .then()
           .statusCode(200)
           .contentType(ContentType.JSON)
      .extract()
      .response();
      OrderDTO out = resp.body().as(OrderDTO.class);
      Assertions.assertNotNull(out);
      Assertions.assertEquals("",out.containerID);
    }
}
