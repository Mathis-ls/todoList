### Generic TodoList App to learn more about Spring Boot

The App has a connection to a mysql Instance using docker and 
provides a Rest Api for working with todo list items.

### <span style="color:#FF6347">Start the application</span>
You will need **Docker** running on your machine. \
1. Change into the projects root directory.
2. Execute the setup.sh script. It is written for **zsh**. 
   If it does not work just set the values inside the **.env file** manually.
2. Execute: docker compose up. 

### <span style="color:#1E90FF">Use the application</span>

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

###### Change the priority of a existing todo item
**PATCH** /todoitems/**{id}**\
Requestbody:\
{\
"priority": "HIGH"\
}

###### Change the content of a existing todo item
**PATCH** /todoitems/**{id}**\
Requestbody:\
{\
"content":"finish another project"
}

##### Patch multiple fields
It is possible to update multiple fields.\
As of now there are only 2 fields though.

**PATCH** /todoitems/**{id}**\
Requestbody:\
{\
"priority": "HIGH",\
"content":"finish another project"\
}

###### Update an existing todo item
**PUT** /todoitems/**{id}**\
{\
"priority": "HIGH",\
"content":"finish another project"\
}

###### Delete an existing todo item
**DELETE** /todoitems/**{id}**