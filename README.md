# Spring boot example with REST and spring data JPA 

User control API


### Running
Run docker
- Execute docker-compose up
- To review swagger access: -> http://localhost.com:8080/swagger-ui.html#/user-controller
- Import the requests for postman located in /postman
- Review technical information at: user-control\src\documentation\User control\docs\User control.pdf
- if you want to see the same documentation in the browser, run: user-control\src\documentation\'User control'>c4builder site
    and go to http://localhost:3000


### Endpoints

| Method | Url | Decription |
| ------ | --- | ---------- |
| POST   |/user| Add a user record |
| GET    |/user| Return a list with all users per page |
| GET    |/user/{cpf}| Return a unique user giving his CPF |
| GET    |/user/filter| Returns a list of users where their firstName (firstname) matches the search text (case sensitive) |
| GET    |/user/filter| Returns a list of users where their lastName (lastname) matches the search text (case sensitive) |
| DELETE |/user/{cpf}| Delete a user giving his CPF|
| PUT    |/user/{cpf}     | Modify one or several user fields giving their CPF |
