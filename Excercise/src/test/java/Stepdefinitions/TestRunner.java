package Stepdefinitions;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
			features="/Users/janan/eclipse-workspace/Excercise/src/test/java/Feature_Files",
			glue="Stepdefinitions")
public class TestRunner {

}
