# Cucumber Rest-assured
This is an example basic project for running [REST-assured](http://rest-assured.io/) tests with a [Cucumber](https://cucumber.io/) framework.
This will include example step definitions to perform basic calls using common http verbs and basic assertions on the responses.
## Running the tests

Run the CucumberRunner to run the cucumber tests
* Right click on the [CucumberRunner](./src/test/CucumberRunner.java) and select Run

Run through maven to generate the Cucumber report
* `mvn verify -Dtest=CucumberRunner`
