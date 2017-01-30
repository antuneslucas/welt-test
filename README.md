Welt Java Developer Test (Solution)
=======================================
Test for [Welt](https://www.welt.de/)


This solution uses SpringBoot 1.4.3, Java 8, Guava 21.0, Logback and Maven. It's very simple to package or run.

It uses maven 3 so you can:
- run using command-line ```mvn spring-boot:run```
- create a fat jar using ```mvn clean package```
- to run the package you will use ```java -jar welt_test.jar```

This app exposes two endpoints, it's possible to perform the following calls:

- The first endpoint is a **GET** endpoint: http://localhost:8080/api/generic-merger/**_{userId}_**. This request will perform three (hardcoded) different requests and return a single merged json response.
  - http://jsonplaceholder.typicode.com/users/**{userId}**
  - http://jsonplaceholder.typicode.com/posts?userId=**{userId}**
  - http://jsonplaceholder.typicode.com/posts/**{userId}**/comments

    - e.g.: **GET** ```http://localhost:8080/api/generic-merger/3```

- The second (**POST**) endpoint receives a list of urls as json body and produces a merged json with all responses of each received url. 

  - e.g.: **POST** ```http://localhost:8080/api/generic-merger/```
  - ```["http://jsonplaceholder.typicode.com/users/1", "http://jsonplaceholder.typicode.com/users/5"]```

Note: If a request do not produce a valid json as a response, the app response will not produce a valid json as well.

=============================================

**INSTRUCTIONS**:

Your task is to write a short example app: An Asynchronous API Gateway that proxies two different REST end points and outputs the results into a merged (also JSON) response.
- For example you could use these two endpoints:
  - http://jsonplaceholder.typicode.com/users/1 to obtain a user's data
  - http://jsonplaceholder.typicode.com/posts?userId=1 to obtain all comments written by that user
