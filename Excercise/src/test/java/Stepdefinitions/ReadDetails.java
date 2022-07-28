package Stepdefinitions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

public class ReadDetails {
	WebDriver driver;

	public ReadDetails(WebDriver driver) {
		this.driver = driver;
	}

	public static String getProperty(String key) {
		String propValue = null;
		try {
			FileInputStream fis = new FileInputStream("src\\test\\java\\Locators.properties");
			Properties prop = new Properties();
			prop.load(fis);
			propValue = prop.getProperty(key);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propValue;
	}

	public static String getProperty(String key, String filePath) {
		String propValue = null;
		try {
			FileInputStream fis = new FileInputStream(filePath);
			Properties prop = new Properties();
			prop.load(fis);
			propValue = prop.getProperty(key);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return propValue;
	}

	public WebElement getElementInfo(String key) {
		WebElement element = null;
		String elementInfo = null;
		String[] elementStr;
		elementInfo = getProperty(key);
		elementStr = elementInfo.split(":");
		switch (elementStr[0].trim().toLowerCase()) {
		case "id":
			element = driver.findElement(By.id(elementStr[1].trim()));
			break;
		case "name":
			element = driver.findElement(By.name(elementStr[1].trim()));
			break;
		case "tagname":
			element = driver.findElement(By.tagName(elementStr[1].trim()));
			break;
		case "xpath": {
			String xpath = "";
			xpath += elementStr[1].trim();
			if (elementStr.length > 2) {
				for (int i = 2; i < elementStr.length; i++) {
					if (!elementStr[i].trim().isBlank()) {
						xpath += "::";
						xpath += elementStr[i].trim();
					}
				}
			}
			element = driver.findElement(By.xpath(xpath));
			break;
		}
		case "cssselector":
			element = driver.findElement(By.cssSelector(elementStr[1].trim()));
			break;
		case "classname":
			element = driver.findElement(By.className(elementStr[1].trim()));
			break;
		case "linktext":
			element = driver.findElement(By.linkText(elementStr[1].trim()));
			break;
		case "partiallinktest":
			element = driver.findElement(By.partialLinkText(elementStr[1].trim()));
			break;
		default:
			System.out.println("Invalid keyword - Method : getElementInfo!!!");
		}

		return element;
	}

	public static List<String[]> readCSVFile(String filePath) {
		List<String[]> empList = null;
		FileReader fileR = null;
		try {
			fileR = new FileReader(filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		@SuppressWarnings("resource")
		CSVReader csvReader = new CSVReader(fileR);
		// String[] nextRecord;
		// we are going to read data line by line
		try {
			empList = csvReader.readAll();
			// while ((nextRecord = csvReader.readNext()) != null) {
			// empList.add(nextRecord);
			// }
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CsvException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return empList;
	}

	public static String getValueByHeader(List<String[]> eList, String header, int index) {
		String value = "";
		if (eList != null) {
			String[] headRow = eList.get(0);
			String[] record = eList.get(index);
			for (int i = 0; i < record.length; i++) {
				if (headRow[i].equalsIgnoreCase(header)) {
					value = record[i];
					break;
				}

			}
		}
		return value;
	}

	public static String getXpath(String[] elementStr) {
		String xpath = "";
		xpath += elementStr[1].trim();
		if (elementStr.length > 2) {
			for (int i = 2; i < elementStr.length; i++) {
				if (!elementStr[i].trim().isBlank()) {
					xpath += "::";
					xpath += elementStr[i].trim();
				}
			}
		}
		return xpath;
	}

	public static String getMonthName(int monthIndex) {
		// since this is zero based, 11 = December
		if (monthIndex < 0 || monthIndex > 11) {
			throw new IllegalArgumentException(monthIndex + " is not a valid month index.");
		}
		return new DateFormatSymbols().getMonths()[monthIndex].toString();
	}

	public static int getMonthNumber(String monthStr) {
		List<String> list1 = Arrays.asList("january", "february", "march", "april", "may", "june", "july", "august",
				"september", "october", "november", "december");
		return list1.indexOf(monthStr.toLowerCase()) + 1;
	}

	public void selectOption(String optionSel, String select_Control) {
		String[] opProp = ReadDetails.getProperty(select_Control).split(":");
		String xpath = "";
		if (opProp[0].equalsIgnoreCase("xpath"))
			xpath = ReadDetails.getXpath(opProp);
		List<WebElement> options = driver.findElements(By.xpath(xpath));
		for (WebElement option : options) {
			if (option.getText().equalsIgnoreCase(optionSel))
				option.click();
		}

	}

	public void selectCalendar(String header, String selectDate) throws InterruptedException {
		String[] dateStr = selectDate.split("/");
		// 0 - month,1 - date, 2 - year

		WebElement Cal_year = driver.findElement(By.xpath("//div[@class='MuiPickersBasePicker-container']//h6"));
		// Select year
		if (!Cal_year.getText().equals(dateStr[2])) {
			Cal_year.click();
			driver.findElement(By.xpath("//div[@class='MuiPickersYearSelection-container']/div[@role='button'][text()='"
					+ dateStr[2] + "']")).click();
			Thread.sleep(2000);
		}
		int monthInt = Integer.parseInt(dateStr[0]);
		String monthStr = getMonthName(monthInt - 1);
		String month_year = monthStr + " " + dateStr[2];
		int index = 0;
		WebElement Cal_header = driver.findElement(By.xpath("//div[@class='MuiPickersCalendarHeader-switchHeader']"));
		// Select month
		while (!Cal_header.findElement(By.tagName("p")).getText().equalsIgnoreCase(month_year)) {
			index = getMonthNumber(Cal_header.findElement(By.tagName("p")).getText().split(" ")[0]);
			if (index > 0 && index <= 12) {
				if (index > monthInt)
					while (index != monthInt) {
						Cal_header.findElements(By.tagName("button")).get(0).click();
						index--;
					}
				else
					while (index != monthInt) {
						Cal_header.findElements(By.tagName("button")).get(1).click();
						index++;
					}
			}
		}
		WebElement date_Container = Cal_header.findElement(By.xpath("//parent::div/following-sibling::div"));
		List<WebElement> dates = date_Container.findElements(By.xpath("//div[@role='presentation']//p"));
		int recDate = Integer.parseInt(dateStr[1]);
		// Select Date
		for (WebElement e : dates) {
			if (e.isDisplayed() && !e.getText().isBlank()) {
				if (Integer.parseInt(e.getText()) == recDate) {
					e.click();
					break;
				}

			}
		}
		
	}

}
