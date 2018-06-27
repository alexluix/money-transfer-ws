# money-transfer-ws

[![Build Status](https://travis-ci.org/landpro/money-transfer-ws.svg?branch=master)](https://travis-ci.org/landpro/money-transfer-ws)
[![codecov](https://codecov.io/gh/landpro/money-transfer-ws/branch/master/graph/badge.svg)](https://codecov.io/gh/landpro/money-transfer-ws)

Simple money transfer web service.

## Functionality

-   simple account management
-   money transfers between accounts

## API & Limitations

-   REST API
-   authentication unsupported
-   in-memory storage – accounts are forgotten on server restart

## Technologies

-   Java 8
-   Jersey Web Services / JAX-RS
-   Jackson JSON
-   Maven

## Build & Run

-   build: `mvn clean package`
-   run: `mvn exec:java`
-   apply code changes and run: `mvn compile exec:java`

## REST Endpoints

Description of API endpoints with examples of request / response.

Dynamic information about available endpoints is available on running server at `http://localhost:8080/application.wadl`.

### Create Account

POST /accounts

#### Request
```
curl -X "POST" "http://localhost:8080/accounts" \
     -H 'Content-Type: application/json' \
     -d $'{
  "balance": "700"
}'
```
#### Response
```
HTTP/1.1 201 Created
Location: http://localhost:8080/accounts/1
Content-Type: application/json

{
  "id" : 1,
  "balance" : 700
}
```

### Get Account

GET /accounts/{accountId}

#### Request
```
curl "http://localhost:8080/accounts/3"
```
#### Response
```
HTTP/1.1 200 OK
Content-Type: application/json
Connection: close
Content-Length: 33

{
  "id" : 3,
  "balance" : 700
}
```

### Get All Accounts

GET /accounts

#### Request
```
curl "http://localhost:8080/accounts"
```

#### Response
```
HTTP/1.1 200 OK
Content-Type: application/json

[ {
  "id" : 1,
  "balance" : 700
}, {
  "id" : 2,
  "balance" : 700
} ]
```

### Delete Account

DELETE /accounts/{accountId}

#### Request
```
curl -X "DELETE" "http://localhost:8080/accounts/4"
```

#### Response
```
HTTP/1.1 204 No Content
```

### Money Transfer

POST /accounts/transfer

#### Request
```
curl -X "POST" "http://localhost:8080/accounts/transfer" \
     -H 'Content-Type: application/json' \
     -d $'{
  "withdrawalAccountId": 1,
  "depositAccountId": 2,
  "amount": 150
}'
```
#### Response
```
HTTP/1.1 200 OK
Content-Type: application/json

{
  "withdrawalAccount" : {
    "id" : 1,
    "balance" : 550
  },
  "depositAccount" : {
    "id" : 2,
    "balance" : 850
  },
  "amount" : 150
}
```
#### Erroneous Responses

-   400 Bad Request: Invalid transfer parameters
-   404 Not Found: Account not found
-   406 Not Acceptable: Insufficient funds
