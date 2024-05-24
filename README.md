### Generic TodoList App to learn more about Spring Boot

The App has a connection to a mysql Instance using docker and 
provides a Rest Api for working with todo list items.

### Start the application
You will need **Docker** running on your machine. \
The startup script is written for **zsh**.

Execute **./startUp.sh** and choose your:
- DB-name
- username 
- password 
- root-password

### Use the application

All Requestbodies are of type: application/json.\
The server runs on port 8080 -> all requests begin with **ip-address:8080** or localhost:8080.

A todo Item has 2 properties:
- content: can be any String
- priority: can be "VERY_HIGH","HIGH","MEDIUM" or "LOW"

###### Save a new todo item
**POST** /todoitems

Requestbody:

{\
"content":"finish this project",\
"priority": "HIGH"\
}

###### Get all todo items
**GET** /todoitems

###### Get a todo items by its id
**GET** /todoitems/**{id}**

example:
**GET** /todoitems/**1**\
will return the todo item with the id of 1.