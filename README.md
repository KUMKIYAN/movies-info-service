## Streaming Endpoint

Streaming endpoint is a kind of endpoint which continuously send updates to the client as new data arrives

This concept is similar to Server sent events

Easy to implement spring webflux

eg: stocks and sports events updates

####  unit Testing and integration testing 

Spring boot uses recent versions of Junit. So it uses Junit5.

    tasks.named('test') {
	    useJUnitPlatform()
    }

    sourceSets {
        test{
            java.srcDirs = ['src/test/java/unit', 'src/test/java/intg']
        }
    }

    Integration test is for end to end testing

## Integration test

    @WebFluxTest(controllers = FluxAndMonoController.class) on the top of the class - allows to access endpoints available in the controller
    @AutoConfigureWebTestClient helps us to @Autowire the WebTestClient on top of the class

## Unit Testing

    Unit testing is for testing interested classes / methods by mocking the dependancy layer

### @DataMongoTest
    @DataMongoTest  scan the application and look for repository classes and make that class available in your test case. We do not have to instantiate the whole spring application context in order to write an integration test for the database layer. We can test the application faster than starting from scratch.

### @ActiveProfiles(“test”)
    @ActiveProfiles(“test”)  will give connection to embedded mongo db. If we give @ActiveProfiles(“local”) will take the values from the application.yml file as this file contain local profile enusre test is not override

#### Whenever we are interact with reactive repository class it will return always Flux/Mono

block() will block the previous call for asynchronous. it is require for unit or integration test.

standard way of testing anything is through unite or integration testing. Testing using postman is waste of time.

@SpringBootTest will spin up the application for testing. @ActiveProfie(“test”) will use embedded mongo db and @AutoConfigureWebTestClient give webTestClient to connect endpoints.


