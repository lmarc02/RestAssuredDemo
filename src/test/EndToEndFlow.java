import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static  org.hamcrest.Matchers.equalTo;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.lessThan;

public class EndToEndFlow {
    Long id;
    static RequestSpecification requestSpecification;
    static ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp(){

        requestSpecification = RestUtilities.getRequestSpecification(MyConstants.PET_PATH);
        responseSpecification = RestUtilities.getResponseSpecifications();

    }

    @Test
    public void addPet(){
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put("id", "0");
        categoryMap.put("name", "doggie");

        String name = "puffy";

        List<String> photoUrls = new ArrayList<String>();
        photoUrls.add("www.google.com");
        photoUrls.add("www.abc.com");

        String status = "available";

        AddPetModal pet = new AddPetModal();
        pet.setCategory(categoryMap);
        pet.setName(name);
        pet.setPhotoUrls(photoUrls);
        pet.setStatus(status);

        Response response = given().spec(requestSpecification).contentType(ContentType.JSON).body(pet)
               .when()
               .post();
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
    public void getPet(){
        Response response = given().spec(requestSpecification).when().get("/" + id);
        JsonPath jsonPath = new JsonPath(response.asString());
        id = jsonPath.get("id");
        System.out.println("Pet info: "+response.asString());

        // for request we log after given, for response we og after then
        given().spec(requestSpecification)//.log()
               //.parameters()
               //.method()
               //.uri()
               //.body()
               //.body(false)
               //.all()
               .get("/" + id).then().statusCode(200).log()
               //.ifValidationFails()
               .ifError();



        //hard assertion
//        given().spec(requestSpecification).when()
//               .get("/" + id)
//               .then()
//               .body("category.id", equalTo(0))
//               .body("category.id", lessThan(1))
//               .body("category.name", equalTo("doggie"))
//                .spec(responseSpecification);

        //softAssertion
                given().spec(requestSpecification).when()
                       .get("/" + id)
                       .then()
                       .statusCode(200)
                       .body("category.id", equalTo(0),
                             "category.id", lessThan(1),
                             "category.name", equalTo("doggie"));
        given()
                .spec(requestSpecification)
                .when()
                .get("/" + id)
                .then()
                .log()
                .body()
                .rootPath("category")
                .body("id", equalTo(0))
                .body("id", lessThan(1))
                .body("name", equalTo("doggie"));// now we can create another rootpath if exist an make other tests



    }



    @Test(dependsOnMethods = {"getPet"})
    public void checkResponseTime(){
        basicCheck();
        long responseTime =
        given().spec(requestSpecification)
            .when()
                .get("/" + id)
            //.time(); // will return time in milliseconds
                .timeIn(TimeUnit.SECONDS);

        System.out.println("response time is: " + responseTime);

        // add an assertion

        given().spec(requestSpecification)
                .when()
                    .get("/"+id)
                .then()
                    .time(lessThan(2L), TimeUnit.SECONDS);

    }

    public void basicCheck(){
        given()
                .spec(requestSpecification)
        .when()
                .get("/"+id)
        .then()
                .body("id", equalTo(id))
                .body("name", equalTo("puffy"));

        System.out.println("basic check successful");

    }

    @Test(dependsOnMethods = {"checkResponseTime"})
    public void deletePet(){
        given().spec(requestSpecification).delete("/"+id);
        System.out.println("The pet is deleted");
    }

}
