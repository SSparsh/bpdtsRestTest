package cucumberRestAssured;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    plugin = {"pretty", "json:target/cucumber/jsonReports/cucumber.json"},
    glue = "cucumberRestAssured/glue",
    features = "classpath:features",
    dryRun = false,
    snippets = SnippetType.CAMELCASE,
    tags = "@stage1",
    strict = true
)

public final class CucumberRunner {

}
