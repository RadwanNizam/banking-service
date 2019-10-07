# Banking Service

A simple service to demonstrate how to user Java Lock to make safe transfer between accounts with support for multi-threading. 

## NOTE
You can create multiple accounts using the same owner name; however, the generated account id is unique

## Technology Stack
- Java 1.8
- dropwizard
- guice: In this release, dependency injection is used in the test code only

## Build & Run the project
 - mvn package
 - cd target 
 - java -jar banking-1.0-SNAPSHOT.jar server /banking-service.yml

## Swagger URL
http://localhost:8080/swagger


## Test Scenario

####Create Account1: 
#####`curl -X POST "http://localhost:8080/api/v1/account" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"ownerFirstName\": \"Radwan\", \"ownerLastName\": \"Nizam\", \"balance\": 100}"`
##### Response:
'
{
  "id": "1",
  "ownerFirstName": "Radwan",
  "ownerLastName": "Nizam",
  "balance": 100,
  "creationDate": 1570432944587
}
'

####Create Account2: 
#####`curl -X POST "http://localhost:8080/api/v1/account" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"ownerFirstName\": \"George\", \"ownerLastName\": \"Brown\", \"balance\": 100}"`
##### Response:
'
{
  "id": "2",
  "ownerFirstName": "George",
  "ownerLastName": "Brown",
  "balance": 100,
  "creationDate": 1570432945587
}
'

####Transfer Money (success):
##### `curl -X POST "http://localhost:8080/api/v1/account/actions/transfer/invoke" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"source\": \"1\", \"target\": \"2\", \"amount\": 100}"`
##### Response:
code 202

####Transfer Money (failed):
##### `curl -X POST "http://localhost:8080/api/v1/account/actions/transfer/invoke" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"source\": \"1\", \"target\": \"2\", \"amount\": 100}"`
##### Response:
`code 500`
`{
  "code": 5,
  "message": "insufficient funds"
}`
