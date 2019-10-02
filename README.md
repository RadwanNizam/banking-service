# Banking Service

A simple service to demonstrate how to user Java Lock to make safe transfer between accounts with support for multi-threading

# Transfer Process
```mermaid
sequenceDiagram
AccountResource ->> AccountService: transfer (from, to, amount)
AccountService-->>AccountRepository: getAccount(from)
AccountService-->>AccountRepository: getAccount(to)
AccountService-->>Account: transfer(to, amount)
Note right of Account: method transfer is called on the 'from' account
```
## Used Stack

Java 1.8
dropwizard
guice: In this release, dependency injection is used in the test code only

## Build & Run the project
 \> mvn package
 \>cd target 
 \>java -jar banking-1.0-SNAPSHOT.jar server /banking-service.yml

## Swagger URL
http://localhost:8080/swagger