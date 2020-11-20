package cucumberRestAssured.glue;

import static io.restassured.RestAssured.given;
import static io.restassured.config.RestAssuredConfig.config;
import static io.restassured.filter.log.RequestLoggingFilter.logRequestTo;
import static io.restassured.filter.log.ResponseLoggingFilter.logResponseTo;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;

import groovy.util.logging.Slf4j;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.internal.util.IOUtils;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


import net.minidev.json.JSONObject;
import org.eclipse.jetty.io.WriterOutputStream;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;

@Slf4j
public class StepDefs {

  private Scenario scenario;
  private Response response;
  private RequestSpecification request;
  private String baseURL = "http://bpdts-test-app-v2.herokuapp.com";


  final StringWriter writeRequest = new StringWriter();
  final PrintStream requestCaptor = new PrintStream(new WriterOutputStream(writeRequest), true);
  final StringWriter writeResponse = new StringWriter();
  final PrintStream responseCaptor = new PrintStream(new WriterOutputStream(writeResponse), true);


  @Before
  public void before(Scenario scenario) {
    this.scenario = scenario;
  }

  @Before
  public void setup() {
    RestAssured.config = config().logConfig(LogConfig.logConfig().enablePrettyPrinting(true));
  }

  @Given("a parameter {string} with a value {string}")
  public void aParameterNameWithAValue(String parameterKey, String parameterVal) {
    request = given().param(parameterKey, parameterVal);
  }


  @When("a GET request is made")
  public Response aGETRequestIsMade() {
    response = request.when().get(baseURL);
    scenario.log(writeRequest.toString());
    return response;
  }


  @When("a GET request is made to {string} endpoint with city {string}")
  public Response aCity(String endpoint, String city) {
    String finalURL = baseURL+"/city/"+city+"/"+endpoint;
    System.out.println(finalURL);
    response = request.when().get(finalURL);
    ResponseBody body = response.getBody();
    System.out.println(body.asString());
    scenario.log(writeRequest.toString());
    return response;
  }

  @When("a GET request is made to {string} endpoint with city")
  public Response aallCities(String endpoint) throws IOException {

    String user_dir = "src/test/resources/responseArtifact/";
    int id = 967;
    for (int i=0 ; i<id; i++) {
      Path filePath = Paths.get(user_dir+"cities.txt");
      String line = Files.readAllLines(Paths.get(user_dir+"cities.txt")).get(i);
      String finalURL = baseURL + "/city/" + line + "/" + endpoint;
      response = request.when().get(finalURL);
      ResponseBody body = response.getBody();
      System.out.println("Users in the City: "+line);
      System.out.println(body.asString());
      scenario.log(writeRequest.toString());
    }
    return response;
  }

  @When("a GET request is made to {string} endpoint")
  public Response aCity(String endpoint) {
    String finalURL = baseURL+"/"+endpoint;
    System.out.println(finalURL);
    response = request.when().get(finalURL);
    ResponseBody body = response.getBody();
    System.out.println(body.asString());
    scenario.log(writeRequest.toString());
    return response;
  }

  @Then("test")
  public void bodyodeIsReturned() {
    ResponseBody actualResponse = response.body();

    System.out.println(actualResponse);
    scenario.log(writeResponse.toString());
  }

  @Given("a GET request is made to {string} endpoint for all {int} users to get their {string}")
  public Response allUsers(String user, int id, String city) throws IOException {
    for (int i=0 ; i<id; i++){
    String finalURL = baseURL+"/"+user+"/"+i;
    response = request.when().get(finalURL);
      JsonPath jsonPathEvaluator = response.jsonPath();
      String cityOfUser = jsonPathEvaluator.get(city);
      System.out.println(cityOfUser);


      String user_dir = "src/test/resources/responseArtifact/";
      String contentToAppend = cityOfUser+"\r\n";
      Files.write(
              Paths.get(user_dir+"file.txt"),
              contentToAppend.getBytes(),
              StandardOpenOption.APPEND);
       }
    return response;
  }

  @Then("duplicate cities should be deleted")
  public Response deleteDuplicateCities() throws PendingException, IOException {
    String filePath = "src/test/resources/responseArtifact/file.txt";
    String input = null;
    //Instantiating the Scanner class
    Scanner sc = new Scanner(new File(filePath));
    //Instantiating the FileWriter class
    FileWriter writer = new FileWriter("src/test/resources/responseArtifact/cities.txt");
    //Instantiating the Set class
    Set set = new HashSet();
    while (sc.hasNextLine()) {
      input = sc.nextLine();
      if (set.add(input)) {
        writer.append(input + "\n");
      }
    }
    writer.flush();
    System.out.println("Contents added............");
    return response;
  }

    @Given("a GET request is made to {string} endpoint with id {string}")
  public Response aUser(String user, String id) {
     String finalURL = baseURL+"/"+user+"/"+id;
       System.out.println(finalURL);
    response = request.when().get(finalURL);
    scenario.log(writeRequest.toString());
    return response;
  }


  @Then("status code {int} is returned")
  public void statusCodeIsReturned(int statusCode) {
    response.then().statusCode(statusCode);
    scenario.log(writeResponse.toString());
  }



  @And("^a response body should be returned$")
  public void i_should_have_a_response_body() {
    response.then().assertThat().body(is(notNullValue()));
    response.then().assertThat().body(is(not("")));
  }

  @Given("a JSON body with the following:")
  public void a_JSON_body_with_the_following(DataTable jsonTable) {
    List<List<String>> rows = jsonTable.asLists(String.class);
    JSONObject requestBody = new JSONObject();
    for (List<String> columns : rows) {
      requestBody.put(columns.get(0), columns.get(1));
    }
    request = given()
            .filter(logRequestTo(requestCaptor))
            .filter(logResponseTo(responseCaptor))
            .header("Content-Type", "application/json")
            .body(requestBody.toString());
  }


  @Then("^the response should match the json (.+)$")
  public void the_response_should_match_the_json(String jsonFilename) throws Exception {
    String user_dir = "src/test/resources/responseArtifact/expectedJSON/";
    InputStream is = new FileInputStream(user_dir + jsonFilename);
    String expectedResponse = new String(IOUtils.toByteArray(is));
    String actualResponse = response.body().asString();
    assertThatJson(actualResponse).isEqualTo(expectedResponse);
  }



  @Then("I should get status code {int} or a status code of {int}")
  public void i_should_get_status_code_or_a_status_code_of(Integer status1, Integer status2) {
    response.then().statusCode(anyOf(is(status1), is(status2)));
    scenario.log(writeResponse.toString());
  }


  @Then("I should receive a response body")
  public void i_should_receive_a_positive_response() {
    response.then().assertThat().body(is(notNullValue()));
    response.then().assertThat().body(is(not("")));
  }


  @Then("I should receive an empty response body")
  public void i_should_receive_an_empty_response_body() {
    response.then().assertThat().body(is(""));
  }


  @Then("the following values should be returned")
  public void i_should_have_the_following_values(@NotNull DataTable jsonTable) {
    JsonPath jsonPath = new JsonPath(response.body().asInputStream());
    List<Map<String, String>> list = jsonTable.asMaps(String.class, String.class);
    for (Map<String, String> stringStringMap : list) {
      String field = stringStringMap.get("field");
      String actualValue = jsonPath.getString(field);
      String value = stringStringMap.get("value");
      Assert.assertEquals(value, actualValue);
    }
  }

}
