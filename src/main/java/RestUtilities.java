import java.util.concurrent.TimeUnit;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class RestUtilities {

    public static String                ENDPOINT;
    public static RequestSpecBuilder    REQUEST_SPEC_BUILDER;
    public static RequestSpecification  REQUEST_SPECIFICATION;
    public static ResponseSpecBuilder   RESPONSE_SPEC_BUILDER;
    public static ResponseSpecification RESRPONSE_SPECIFICATIONS;


    public static void setEndpoint (String endpoint){
        ENDPOINT = endpoint;
    }

    public static RequestSpecification getRequestSpecification(String path){
        REQUEST_SPEC_BUILDER = new RequestSpecBuilder();
        REQUEST_SPEC_BUILDER.setBaseUri(MyConstants.BASE_URL);
        REQUEST_SPEC_BUILDER.setBasePath(path);
        REQUEST_SPECIFICATION = REQUEST_SPEC_BUILDER.build();
        return REQUEST_SPECIFICATION;
    }

    public static ResponseSpecification getResponseSpecifications(){
        RESPONSE_SPEC_BUILDER = new ResponseSpecBuilder();
        RESPONSE_SPEC_BUILDER.expectStatusCode(200);
        RESPONSE_SPEC_BUILDER.expectBody("status", equalTo("available"));
        RESPONSE_SPEC_BUILDER.expectResponseTime(lessThan(TimeUnit.SECONDS.toMillis(1)));
        RESRPONSE_SPECIFICATIONS = RESPONSE_SPEC_BUILDER.build();

        return RESRPONSE_SPECIFICATIONS;
    }

    public static RequestSpecification createPathParam(RequestSpecification reSpec, String param, String value){
        return reSpec.pathParam(param, value);
    }

    public static Response getResponse(RequestSpecification requestSpecification, String type){
        REQUEST_SPECIFICATION.spec(requestSpecification);
        Response response = null;

        if(type.equalsIgnoreCase("get")){
            response = given().spec(REQUEST_SPECIFICATION).when().get(ENDPOINT);
        }else if(type.equalsIgnoreCase("post")){
            response = given().spec(REQUEST_SPECIFICATION).post(ENDPOINT);

        }else if(type.equalsIgnoreCase("put")) {
            response = given().spec(REQUEST_SPECIFICATION).put(ENDPOINT);
        }else if(type.equalsIgnoreCase("delete")) {
            response = given().spec(REQUEST_SPECIFICATION).delete(ENDPOINT);
        }else {
            System.out.println("Type is not supported");
        }

        response.then().log().all();
        return response;
    }

    public static Response postPet(RequestSpecification requestSpecification, AddPetRequetBody petBody){
        Response resp = given().spec(requestSpecification).contentType(ContentType.JSON).body(petBody).when().post();
        resp.then().log().all();
        return  resp;
    }

    public static JsonPath getJsonPath(Response response){
        String jsonPath = response.asString();
        return new JsonPath(jsonPath);
    }

    public static XmlPath getXmlPath(Response response){
        String jsonPath = response.asString();
        return new XmlPath(jsonPath);
    }

    public static void resetBasePath(){
        RestAssured.basePath = null;
    }

    public static void setCotentType(ContentType type){
        given().contentType(type);
    }

    public static void deletePet(RequestSpecification requestSpecification){
        given().spec(requestSpecification).delete(ENDPOINT);
        System.out.println("The pet is deleted");
    }
}
