Feature: Materialui

  Scenario: Start
    Given I open MaterialUI and add "react-button/" path

  Scenario: Verify button
    And I verify if button is present
    And I verify if "Disabled" button is disabled
    And I verify if "Primary" button is enabled

  Scenario: Click on button with icon
    And in MaterialUI I click on button identify by icon
    And I stop