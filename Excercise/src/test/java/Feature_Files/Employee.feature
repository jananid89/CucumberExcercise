@Smoke
Feature: Create Employee profiles in VITSHR site

  @Smoke_1
  Scenario Outline: Creating Employee profiles with data
    Given Navigated to VITSHR Login page
    When Login to VITSHR site with company login and move to Employee List tab
    Then Create Employee profiles with EmpDetails file <FilePath>

    Examples: 
      | FilePath                        |
      | src\\test\\java\\EmpDetails.csv |

  @Smoke_1
  Scenario Outline: Employee login and Enter Timesheet
    Given Navigated to VITSHR Login page
    When Login with Employee "LoginID" "Password"for <Index> from "src\\test\\java\\EmpDetails_TimeSheet.csv"
    Then Move to Timesheet enter data "TaskName" "TaskDescription" "TaskStartDate" "TaskEndDate" "TaskDuration" "FilePath"

    Examples: 
      | Index |
      |     1 |
      |     2 |
