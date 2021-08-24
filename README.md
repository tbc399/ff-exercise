# Airport Weather

I've build a simple maven Spring Boot API that exposes two endpoints.

```
GET /api/airports/{id}/weather`
```
This will return a single JSON object for the given airport id.

```
GET /api/airports/weather?id={id}`
```
This endpoint is similar except that it can handle a csv list of airport 
ids and will return a corresponding list of JSON objects.

Build and Test
---
```
mvn test
```

Run
---
```
mvn spring-boot:run
```

Improvement Ideas
---
- The biggest thing I can see that would improve this app would be to move
to an asynchronous model for making the api calls to the ForeFlight services
to keep from blocking and making response times really slow when multiple ids 
are passed in.
- more rigorous validation of the json payloads returned from the airport 
and weather services
