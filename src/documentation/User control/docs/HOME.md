# Overview

![diagram](https://www.plantuml.com/plantuml/svg/0/PP7DRi8m48JlaV8EMqwf1Ccbfvv00PLM5518L752RhA09Vz4zhgKjwzjg0JYP6js_StiUco96N9IWBDeHgrMXB-K9iH983HUWz9hHejunjeJLwrm7SAHgBUlHM7OAJzmEhhl08aCHNchPS4wmvdw4QvtHVaoMWkcgFbS588HMlBIxahDUvyXJT9aCTckcdhtzLxFTulPssmvpPw3laPZjSfqZuQHDaCOhAvNA9qbBT4Cl3W1UxQ44luRuUI3nU60gxqsaX7NAXzuw2PsPgpduM7FxyUzL2DJ27so89bY1wuE3p38oggPFaTccbGeRb9UuLqt1AaxlZyFeKE1X95YSzXg1orJm8JLu5TddI2W8zuPw7sGB8Oq7vlLifZNzJhO1RDeSREN2iaP1HNQtkFmie1BFR0C1WlxW4gJCQek_i8V)

**Level 1: System Context diagram**

This is a basic Rest API test development to demonstrate knowledge. This application allows you to add, update, delete and consult user information. 
The API is developed in Java with Spring Boot, using JPA, and is relationally persisted with Postgres SQL. 
The tests have been carried out using Postman and the user requests are attached. 
Docker images have been added for the application and for the database. 
Added entity mapping process to be able to update one or several fields at once. 
When adding a user, a check is made to only allow people over 18 years of age to register and their CPF must be valid. 
Unit tests performed with JUnit/Mockito are added.

**Scope**: A single API software system.
