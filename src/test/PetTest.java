import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;


import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class PetTest {
    Long id;

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp(){
        requestSpecification = RestUtilities.getRequestSpecification( MyConstants.PET_PATH);
        responseSpecification = RestUtilities.getResponseSpecifications();

    }
    @Test
    public void addPet(){
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put("id", "0");
        categoryMap.put("name", "Romanian Mioritic Shepherd Dog");

        String name = "Brutus";

        List<String> photoUrls = new ArrayList<String>();
        photoUrls.add("www.google.com");
        photoUrls.add("www.abc.com");

        String status = "available";

        AddPetRequetBody pet = new AddPetRequetBody(categoryMap, name, photoUrls, status);

        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).body(pet)
                                   .when()
                                   .post();
        response
                .then()
                .log().all()
                .statusCode(200);
        //System.out.println("Json response=" + response.asString());

        response.then()
                .contentType(ContentType.JSON).and()
                .body("name", equalTo(name));
        // System.out.println("The pet is created");
        id = response.path("id");

        JsonPath jsonPath = RestUtilities.getJsonPath(response);//new JsonPath(response.asString());
        String dogName = jsonPath.get("name");
        String category = jsonPath.get("category.name");
        System.out.println("Tha pet name is: " + dogName);
        System.out.println("Pet category is: : " + category);
    }

    @Test(dependsOnMethods = {"addPet"})
    public void getPet1(){
        given()
                .spec(requestSpecification)
        .when()
                .get("/"+id)
        .then()
                .log().all()
                .spec(responseSpecification)
                .body("name", equalTo("Brutus"))
                .body("photoUrls", hasItem("www.abc.com"));// hasItem works only for list
    }

    @Test(dependsOnMethods = {"addPet"})
    public void getPet2(){
        RestUtilities.setEndpoint("/" + id);
        Response resp = RestUtilities.getResponse(requestSpecification, "get");
        ArrayList<String> photoUrlList = resp.path("photoUrls");
        Assert.assertTrue(photoUrlList.contains("www.abc.com"));
    }





















}
