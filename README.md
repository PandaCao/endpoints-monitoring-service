# Endpoints monitoring service

### About service
The EndpointsMonitoringService is responsible for managing and monitoring various endpoints within the application.
It provides various functionalities to create, update, delete, and retrieve monitored endpoints, as well as to log and view monitoring results.
The service is designed to handle authentication and authorization for users, ensuring that they can only manage their own endpoints.

### Tasks
- ✅ Design REST endpoints for <b>MonitoredEndpoints</b>.
- ✅ Monitor endpoints in the background and create <b>MonitoringResult</b>.
- ✅ Implement an endpoint for getting <b>MonitoringResult</b>.
- ✅ Implement a microservice created in Java, written in <b>Spring Boot</b>. Use
  <b>MySQL</b> for the database.
- ✅ Use <b>Spring MVC</b> as a REST framework.
- ❓ Authentication in HTTP header, you will get accessToken in it.
- ✅ A <b>User</b> can see only <b>MonitoredEndpoints</b> and <b>MonitoringResult</b> for him/herself only (according to <b>accessToken</b>).
- ✅ Model validations.
- ✅ Write basic tests in <b>JUnit</b> or <b>TestNG</b>.
- ✅ Create a readme file where you explain how to start and use the service
- ✅ Create a <b>Dockerfile</b>, add docker-compose and describe how to start and run it in <b>Docker</b>

### Reasons for not done tasks
The task related to authentication in the HTTP header is marked with a question mark because I encountered difficulties with implementing proper functionality.
While pattern matching for `"/monitored-endpoints/**"` was set up, users could only successfully access the `GET /monitored-endpoints` endpoint.

### Build and run the application
- `mvn clean install`
- `mvn spring-boot:run`

alternatively:
- `mvn clean install`
- `java -jar target/demo-0.0.1-SNAPSHOT.jar`

### Docker Compose support
This project contains a Docker Compose file named `docker-compose.yaml`.
In this file, the following services have been defined:

* mysql: [`mysql:latest`](https://hub.docker.com/_/mysql)
* demo: `demo:0.0.1-SNAPSHOT`

Use the following <b>command</b> to start the services:: `docker-compose up`

⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️
- <b>There is a slight problem when starting docker compose. The endpoints monitoring service works on second startup.</b>

⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️⚠️

# REST API
I personally use Postman for sending requests but below are some request examples:

### Some examples of requests
`GET /monitored-endpoints`

    curl -X GET --location "http://localhost:8080/monitored-endpoints" \
    -H "Authorization: 93f39e2f-80de-4033-99ee-249d92736a25"

`POST /monitored-endpoints/create`

    curl -X POST --location "http://localhost:8080/monitored-endpoints/create" \
    -H "Authorization: 93f39e2f-80de-4033-99ee-249d92736a25" \
    -H "Content-Type: application/json" \
    -d '{
    "name" : "Pokemon55",
    "url" : "https://pokeapi.co/api/v2/pokemon?limit=1&offset=55",
    "dateOfCreation" : "",
    "dateOfLastCheck" : "",
    "monitoredInterval" : 5,
    "userId" : 1
    }'

`DELETE /monitored-endpoints`

    curl -X DELETE --location "http://localhost:8080/monitored-endpoints/delete/5" \
    -H "Authorization: 93f39e2f-80de-4033-99ee-249d92736a25"

### Example user models that can be used to test the service
```
  {
  userId: 1
  name: "Applifting",
  email: "info@applifting.cz",
  accessToken: "93f39e2f-80de-4033-99ee-249d92736a25"
  },
  {
  userId: 2
  name: "Batman",
  email: "batman@example.com",
  accessToken: "dcb20f8a-5657-4f1b-9f7f-ce65739b359e"
  }
```

### Endpoints

| Method   | URL                                          | Description                                                |
|----------|----------------------------------------------|------------------------------------------------------------|
| `GET`    | `/monitored-endpoints`                       | Retrieve all monitored endpoints.                          |
| `GET`    | `/monitored-endpoints/{id}`                  | Retrieve the monitored endpoint by id.                     |
| `POST`   | `/monitored-endpoints/create`                | Create the monitored endpoint.                             |
| `DELETE` | `/monitored-endpoints/delete/{id}`           | Delete the monitored endpoint by id.                       |
| `GET`    | `/monitored-endpoints/last-ten-results/{id}` | Retrieve last ten results of the monitored endpoint by id. |
| `GET`    | `/monitored-endpoints/results/{id}`          | Retrieve all results of monitored endpoint by id.          |
| `PUT`    | `/monitored-endpoints/update/{id}`           | Update the monitored endpoint by id.                       |
