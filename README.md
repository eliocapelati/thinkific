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

### Date

TBI

### Location of deployed application

TBD

### Time spent

TBI

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

### Instructions to run assignment locally
If applicable, please provide us with the necessary instructions to run your solution.

### What did you not include in your solution that you want us to know about?

- Full Oauth2 support
- unit tests
- Full text search tool like Elasticsearch for a better search support in API.
- In memory datastore like Redis to speed up the application responses


### Other information about your submission that you feel it's important that we know if applicable.

- Due to my scenario of the project I'm working on, I had to implement over the days and little by little the challenge. And with that I ended up spending more hours than initially necessary if I had done from beginning to end in one go.

### Your feedback on this technical challenge

The test was a lot of fun, but it takes a lot of work to do.
I would just like to recommend decreasing the size of the challenge a little.
