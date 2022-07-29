package Stepdefinitions;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.opencsv.CSVReader;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Employee_StepDefn {
	WebDriver driver;
	ReadDetails read;
	CSVReader csvReader;
	String dataFile = "src\\test\\java\\dataValue.properties";
	List<String[]> empTSList, empList;
	int index_int;
	WebDriverWait wait;

	@Given("^Navigated to VITSHR Login page$")
	public void data_given_in_csvtext_files() throws Throwable {

		System.setProperty("webdriver.chrome.driver",
				"C:\\Users\\janan\\eclipse-workspace\\Excercise\\Drivers\\chromedriver.exe");

		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		read = new ReadDetails(driver);
		driver.manage().window().maximize();
		String url = ReadDetails.getProperty("vitshr_login");
		driver.get(url);
	}

	@When("^Login to VITSHR site with company login and move to Employee List tab$")
	public void login_to_vitshr_site_with_company_login_and_move_to_employee_list_tab() throws Throwable {
		String loginID = ReadDetails.getProperty("loginID", dataFile);
		String password = ReadDetails.getProperty("password", dataFile);

		read.getElementInfo("userName").sendKeys(loginID);
		read.getElementInfo("userPassword").sendKeys(password);
		read.getElementInfo("signIN").click();

		Thread.sleep(7000);
	}

	@Then("^Create Employee profiles with EmpDetails file (.+)$")
	public void create_employee_profiles_with_empdetails_file(String filepath) throws Throwable {
		empList = ReadDetails.readCSVFile(filepath);
		String[] header = empList.get(0);
		String type;
		read.getElementInfo("EmployeeList").click();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//span[text()='Add New Employees']//parent::button")));
		for (int i = 1; i < empList.size(); i++) {
			read.getElementInfo("AddNewEmployee").click();

			String[] record = empList.get(i);
			for (int x = 0; x < record.length; x++) {
				if (!record[x].isBlank()) {
					type = ReadDetails.getProperty(header[x], dataFile);
					switch (type.toLowerCase()) {
					case "text":
						read.getElementInfo(header[x]).sendKeys(record[x]);
						break;
					case "radio":
						String[] props = ReadDetails.getProperty(header[x]).split(":");
						List<WebElement> elements = driver.findElements(By.name(props[1]));
						for (WebElement e : elements) {
							if (e.getAttribute("value").equalsIgnoreCase(record[x]))
								if (!e.isSelected())
									e.click();
						}
						break;
					case "select":
						read.getElementInfo(header[x]).click();
						read.selectOption(record[x], "Select_options");
						break;
					case "address":
						read.getElementInfo(header[x]).click();
						wait.until(ExpectedConditions
								.visibilityOfElementLocated(By.xpath("//input[@class='pac-target-input']")));
						read.getElementInfo("Address").sendKeys(record[x]);
						Thread.sleep(2000);
						WebElement ele = read.getElementInfo("ItemLocation");
						Actions act = new Actions(driver);
						if (ele != null) {
							act.moveToElement(ele).click().build().perform();
						}
						Thread.sleep(2000);
						read.getElementInfo("AddrDone").click();
						break;
					case "calendar":
						read.getElementInfo(header[x] + "picker").click();
						Thread.sleep(2000);
						read.selectCalendar(header[x], record[x]);
						driver.findElement(
								By.xpath("//div[@class='MuiDialogActions-root MuiDialogActions-spacing']/button/span[text()='OK']"))
								.click();

						break;
					}
				}
			}
			read.getElementInfo("doneButton").click();
			wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[@class='p-4']")));
		}

	}

	@When("^Login with Employee \"([^\"]*)\" \"([^\"]*)\"for (.+) from \"([^\"]*)\"$")
	public void login_with_employee_something_something_from_something(String loginid, String password, String index,
			String empTSFile) throws Throwable {
		index_int = Integer.parseInt(index);
		empTSList = ReadDetails.readCSVFile(empTSFile);

		read.getElementInfo("userName").sendKeys(ReadDetails.getValueByHeader(empTSList, loginid, index_int));
		read.getElementInfo("userPassword").sendKeys(ReadDetails.getValueByHeader(empTSList, password, index_int));
		read.getElementInfo("signIN").click();
		wait.until(
				ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='root']//div[@class='navigation']")));
	}

	@Then("^Move to Timesheet enter data \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"$")
	public void move_to_timesheet_enter_data_something_something_something_something_something_something(
			String TaskName, String TaskDescription, String TaskStartDate, String TaskEndDate, String TaskDuration,
			String FilePath) throws Throwable {
		read.getElementInfo("TimeSheet").click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class,'timesheet')]")));
		// Task id
		read.getElementInfo("TaskName").click();
		read.selectOption(ReadDetails.getValueByHeader(empTSList, TaskName, index_int), "TaskName_Options");
		// Task Desc
		read.getElementInfo("TaskDesscription")
				.sendKeys(ReadDetails.getValueByHeader(empTSList, TaskDescription, index_int));
		// Task Dates
		read.getElementInfo("TaskDates").click();
		read.selectCalendar(TaskStartDate, ReadDetails.getValueByHeader(empTSList, TaskStartDate, index_int));
		read.selectCalendar(TaskEndDate, ReadDetails.getValueByHeader(empTSList, TaskEndDate, index_int));
		driver.findElement(
				By.xpath("//div[@class='MuiDialogActions-root MuiDialogActions-spacing']/button/span[text()='OK']"))
				.click();

		// Task Duration
		read.getElementInfo("TaskDuration").click();
		read.selectOption(ReadDetails.getValueByHeader(empTSList, TaskDuration, index_int), "TaskDuration_Options");
		read.getElementInfo("doneButton").click();
		wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//label[@for='upload-single-file']/span")));
		//read.getElementInfo("ChooseFile").click();
		WebElement inputFileElement = read.getElementInfo("InputFile");
		inputFileElement.sendKeys(ReadDetails.getValueByHeader(empTSList, FilePath, index_int));
		read.getElementInfo("Submit").click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'MuiAvatar')]")));
	}
	
}
