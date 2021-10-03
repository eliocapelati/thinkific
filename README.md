## Notes

- The first step was reading the document and extract some functional and non-functional requirements
- The project structure was created using [Spring Boot and Java](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.5.5&packaging=jar&jvmVersion=11&groupId=com.thinkific&artifactId=sportsapi&name=Sports%20Management%20API&description=Demo%20project%20for%20Spring%20Boot&packageName=com.thinkific.sportsapi&dependencies=devtools,testcontainers,actuator,cloud-starter-sleuth,data-mongodb,webflux)
- First step was a creation of the contract of signup and login 
- Then an implementation of the process using Auth0 with tests

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
If applicable, use this area to tell us what stretch goals you attempted. What went well? What do you wish you could have done better? If you didn't attempt any of the stretch goals, feel free to let us know why.

### Instructions to run assignment locally
If applicable, please provide us with the necessary instructions to run your solution.

### What did you not include in your solution that you want us to know about?

- Full Oauth2 support
- unit tests
- Full text search tool like Elasticsearch for a better search support in API.


### Other information about your submission that you feel it's important that we know if applicable.

### Your feedback on this technical challenge
Have feedback for how we could make this assignment better? Please let us know.
