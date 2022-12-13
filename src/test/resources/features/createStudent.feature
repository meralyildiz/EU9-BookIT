Feature: Create student


  Scenario: Create student a teacher and verify status code 201
    Given I logged Bookit api using "blyst6@si.edu" and "barbabaslyst"
    When I send POST request to "/api/students/student" endpoint with following information
      | first-name      | harold             |
      | last-name       | teeth               |
      | email           | meral125@gmail.com    |
      | password        | abc1234      |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                  |
      | team-name       | Nukes               |
    And status code should be 201
    Then I delete previously added student

  @wip
   Scenario: test config
   Given I get env properties


  Scenario: Create student a teacher and verify status code 201
    Given I logged Bookit api as "teacher"
    When I send POST request to "/api/students/student" endpoint with following information
      | first-name      | harold             |
      | last-name       | teeth               |
      | email           | meral125@gmail.com    |
      | password        | abc1234      |
      | role            | student-team-leader |
      | campus-location | VA                  |
      | batch-number    | 8                  |
      | team-name       | Nukes               |
    Then status code should be 201
    And I delete previously added student

