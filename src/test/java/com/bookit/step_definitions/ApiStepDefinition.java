package com.bookit.step_definitions;

import com.bookit.pages.SelfPage;
import com.bookit.utilities.BookItApiUtil;
import com.bookit.utilities.ConfigurationReader;
import com.bookit.utilities.DBUtils;
import com.bookit.utilities.Environment;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiStepDefinition {
    String token;
    Response response;
    String emailGlobal;
    String studentEmail;
    String studentPassword;


    @Given("I logged Bookit api using {string} and {string}")
    public void i_logged_Bookit_api_using_and(String email, String password) {
      token = BookItApiUtil.generateToken(email,password);
      emailGlobal= email;
    }

    @When("I get the current user information from api")
    public void i_get_the_current_user_information_from_api() {
        System.out.println("token = " + token);

        //send a GET request "/api/users/me" endpoint to get current user info
        response = given().accept(ContentType.JSON)
                .and()
                .header("Authorization", token)
                .when()
                .get(Environment.BASE_URL +"/api/users/me");
    }
    @Then("status code should be {int}")
    public void status_code_should_be(int statusCode) {
        Assert.assertEquals(statusCode, response.statusCode());
    }


    @Then("the information about current user from api and database should match")
    public void the_information_about_current_user_from_api_and_database_should_match() {
        System.out.println("We will compare database and api in this step");

        //get information from database

        String query = "select firstname, lastname, role from users\n" +
                "where email = '" +emailGlobal +"'";

        Map<String,Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);

        String expectedFirstname = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();

        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");
        System.out.println("actualFirstName = " + actualFirstName);
        System.out.println("actualLastName = " + actualLastName);
        System.out.println("actualRole = " + actualRole);



        //compare database vs api
        Assert.assertEquals(expectedFirstname, actualFirstName);
        Assert.assertEquals(expectedLastName, actualLastName);
        Assert.assertEquals(expectedRole, actualRole);


    }

    @Then("UI,API and Database user information must be match")
    public void ui_API_and_Database_user_information_must_be_match() {
        //get information from database
        //connection is from hooks and it will be ready
        String query = "select firstname, lastname, role from users\n" +
                "where email = '" +emailGlobal +"'";

        Map<String,Object> dbMap = DBUtils.getRowMap(query);
        System.out.println("dbMap = " + dbMap);

        String expectedFirstname = (String) dbMap.get("firstname");
        String expectedLastName = (String) dbMap.get("lastname");
        String expectedRole = (String) dbMap.get("role");

        //get information from api
        JsonPath jsonPath = response.jsonPath();

        //save api info into variables
        String actualFirstName = jsonPath.getString("firstName");
        String actualLastName = jsonPath.getString("lastName");
        String actualRole = jsonPath.getString("role");
        System.out.println("actualFirstName = " + actualFirstName);
        System.out.println("actualLastName = " + actualLastName);
        System.out.println("actualRole = " + actualRole);

        //get information from UI
        SelfPage selfPage = new SelfPage();
        String actualUIName = selfPage.name.getText();
        String actualUIRole = selfPage.role.getText();

        System.out.println("actualUIName = " + actualUIName);
        System.out.println("actualUIRole = " + actualUIRole);

        //UI vs DB
        String expectedFullName = expectedFirstname+ " "+actualLastName;
        Assert.assertEquals(expectedFullName, actualUIName);
        Assert.assertEquals(expectedRole, actualUIRole);

        //UI vs API
        //create a full name for API
        String actualFullName = actualFirstName+ " "+actualLastName;
        Assert.assertEquals(actualFullName, actualUIName);
        Assert.assertEquals(actualRole,actualUIRole);
    }

    @When("I send POST request to {string} endpoint with following information")
    public void i_send_POST_request_to_endpoint_with_following_information(String path, Map<String,String> studentInfo) {
        //why we prefer to get information as a map from feature file ?
        //bc we have queryParams method that takes map and pass to url as query key&value structur

        System.out.println("studentInfo = " + studentInfo);

        //assign email and password value to these variables so that we can use them later for deleting
        studentEmail = studentInfo.get("email");
        studentPassword = studentInfo.get("password");

        response = given().accept(ContentType.JSON)
               .queryParams(studentInfo)
                .and().header("Authorization", token)
                .log().all()
                .when()
                .post(Environment.BASE_URL+path)
                .then().log().all().extract().response();

    }

    @Then("I delete previously added student")
    public void i_delete_previously_added_student() {
        //we have created method to delete student,
        // you pass email and password of the student that you want to delete
    BookItApiUtil.deleteStudent(studentEmail,studentPassword);

    }

    @Given("I logged Bookit api as {string}")
    public void i_logged_Bookit_api_as(String role) {
        token = BookItApiUtil.getTokenByRole(role);

    }





}
