@test
Feature: Check that the data returned by the API is valid and valid operation can be successfully called for each endpoint


@stage1
  Scenario: 1. List all the cities from user api and write them to cities file that can be consumed by users endpoint with city scenario
    Given a parameter '' with a value ''
    When a GET request is made to 'user' endpoint for all 1000 users to get their 'city'
    Then duplicate cities should be deleted

@stage2 #pre-req run @stage1 to generate cities.txt that is consumed by this scenario
  Scenario: 2. List of Users in specific City (city from cities.txt written when executing scenario 1
    Given a parameter '' with a value ''
    When a GET request is made to 'users' endpoint with city
    Then status code 200 is returned
    And a response body should be returned


  Scenario: 3. List all users in users endpoint (1000 users)
    Given a parameter '' with a value ''
    When a GET request is made to 'users' endpoint
    Then status code 200 is returned
    And a response body should be returned


  Scenario Outline: 4. List five users and verify data matches the json file
    Given a parameter '' with a value ''
    When a GET request is made to 'user' endpoint with id '<id>'
    Then status code 200 is returned
    And a response body should be returned
    And the response should match the json <jsonOutput>
  Examples:
  |id  |jsonOutput|
  |1   |testFile1.json|
  |2   |testFile2.json|
  |3   |testFile3.json|
  |4   |testFile4.json|
  |5   |testFile5.json|


  Scenario: 5. GET User endpoint and verify the output for user 1
        # This can be alertered to validate any field, of any user
    Given a parameter '' with a value ''
    When a GET request is made to 'user' endpoint with id '1'
    Then status code 200 is returned
    And a response body should be returned
    And the following values should be returned
      | field           | value                      |
      | id              | 1                            |
      | first_name      | Maurise                      |
      | last_name       | Shieldon                     |
      | email           | mshieldon0@squidoo.com       |
      | ip_address      | 192.57.232.111               |
      | latitude        | 34.003136                    |
      | longitude       | -117.72286                   |
      | city            | Kax                          |


  @test1
  Scenario: 6. Check status code returned is 404 when wrong endpoint is provided on user endpoint
    Given a parameter '' with a value ''
    When a GET request is made to 'test' endpoint
    Then status code 404 is returned
