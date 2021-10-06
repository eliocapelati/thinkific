## Notes

- The first step was reading the document and extract some functional and non-functional requirements
- The project structure was created using [Spring Boot and Java](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.thinkific&artifactId=sportsapi&name=Sports%20Management%20API&description=Demo%20project%20for%20Spring%20Boot&packageName=com.thinkific.sportsapi&dependencies=devtools,testcontainers,actuator,cloud-starter-sleuth,data-mongodb,webflux)
  - The stack is:
    - Java 17
    - SpringBoot
    - WebFlux (Reactive support)
    - Validation
    - MongoDB
    - Monitoring tools (Actuator and Sleuth)
      - Log correlation
      - Readiness and Liveness probe
    - Developer tools
      - Hotswap
      - Livereload
    - Security
      - API security
      - Oauth Resource Server
      - Auth0 library
    - Test stack
      - SpringBoot Starter
      - TestContainers
        - MongoDB
      - Faker
- First step was a creation of the contract of signup and login 
- Then an implementation of the process using Auth0 with tests
- Following the contract first approach, I created the spec and implementation for Team and Player
- I decided to take a lot of care defining the API definition to ensure the contract of the API.
- I decided to use integration test that is close to a real scenario and trying to not mock every thing.
  - I'm using a test container approach with real full mongodb for integration tests.
- For the development process, I decided to use a Trunk based development. But in the real world working with a team, I strongly recommend using gitflow style workflow.
- For pipeline, I've chosen Google Code Build (integrated with Google Cloud Run)
- For deployment, I've chosen to run into Google Cloud Run
- For some reason google isn't verifying my own domain, and I'm using the Google regular domain


### Date

2021-Oct-06

### Location of deployed application

The project is running on Google GCP
- https://thinkific-5tppttbb5q-uc.a.run.app/v1

### Time spent

9 hours (including deployment)

### Assumptions made

- For easy evaluation I decided to not implement the Oauth2 flow, and accept a JWT on authenticated resources.
- For easy evaluation I implemented I've implemented a signup and sign in resource, but today update user information isn't supported
- For tests under authenticated resources I've created a mock JWT decoder under JwtTestSecurityConfig, but for development and production environment there's a decoder who validates through the issuer and audience and getting the jwks from the resource server.
- For tests and the time spent, I decided to create only integration tests.

### Shortcuts/Compromises made

#### Shortcuts:
For tests and easy evaluation during the assessment, I've created a signup and login flow that needs to be better addressed for a real-world application.  


### Stretch goals attempted
 - API spec using OpenAPI, available on:
   - src/main/resources/SportsManagementAPI.yaml
 - Deployed API 
   - https://thinkific-5tppttbb5q-uc.a.run.app/v1

### Instructions to run assignment locally

#### Requirements

  To run for development this application is necessary to have:
  - JDK 17 installed  
  - Docker to run the integration tests

#### Configuration

For security reasons and good practices, all the application secrets are injected at runtime.
But to run into development, I'm sending config file outside the git.


#### Running the application for evaluation using Docker Compose

To use this step, you don't need to have Java installed, just *docker* and *docker-compose*.

If you're using windows or mac, it's done by installing [`Docker Desktop`](https://www.docker.com/products/docker-desktop). Now if you're using linux check your package manager of your distro.

Ok, now it's time to get this project running.

STEPS
1. Get the provided `secrets.env` file and put it at the level folder of this document.
2. Run the following command in your terminal:
   1. ``docker-compose up --build`` (This may take a while, at which point the application is downloading its dependencies.)
3. The application will automatically start after the compilation process.
4. In the end of this process, you'll probably see a log fragment like this:

```
thinkific-api    | 2021-10-06 23:05:57.357  INFO [Sports Management API,,] 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
thinkific-api    | 2021-10-06 23:05:57.399  INFO [Sports Management API,,] 1 --- [           main] com.thinkific.sportsapi.App              : Started App in 2.241 seconds (JVM running for 3.202)
```
5. It's done!

#### Accessing the API

Inside the folder ``src/main/resources/`` you can find a OpenApi spec file `SportsManagementAPI.yaml` to get access to the application. 
Using the [editor.swagger.io](https://editor.swagger.io) you can choose to run locally on server "Local Development", or using the exposed api on server "Production Server".

#### Executing the integration tests

For this you'll need to get Java 17 and Docker installed.
Having this cleared, you can simply run the command:

```
  ./mvnw clean compile verify failsafe:integration-test
```

This can take a while, due to container provisioning process of the step.

In the end of this process, you'll get a log fragment like this:
```
[INFO] Results:
[INFO]
[INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- maven-failsafe-plugin:2.22.2:verify (default) @ sportsapi ---
[INFO]
[INFO] --- maven-failsafe-plugin:2.22.2:integration-test (default-cli) @ sportsapi ---
[INFO] Skipping execution of surefire because it has already been run for this configuration
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  01:30 min
[INFO] Finished at: 2021-10-06T20:32:37-03:00
[INFO] ------------------------------------------------------------------------
```


### What did you not include in your solution that you want us to know about?

- Full Oauth2 support
- unit tests (Only integration tests are included)
- Full text search tool like Elasticsearch for a better search support in API.
- In memory datastore like Redis to speed up the application responses


### Other information about your submission that you feel it's important that we know if applicable.

- Due to my scenario of the project I'm working on, I had to implement over the days and little by little the challenge. And with that I ended up spending more hours than initially necessary if I had done from beginning to end in one go.

### Your feedback on this technical challenge

The test was a lot of fun, but it takes a lot of work to do.
I would just like to recommend decreasing the size of the challenge a little.
