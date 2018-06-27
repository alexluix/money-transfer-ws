# money-transfer-ws

[![Build Status](https://travis-ci.org/landpro/money-transfer-ws.svg?branch=master)](https://travis-ci.org/landpro/money-transfer-ws)

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
