**Level 3: Component diagram**

The component diagram shows how a container is made up of a number of "components", what each of those components is, their responsibilities, and the technology/implementation details.

In this diagram, you can visualize the relationship of the various components of the API and their interaction. An operator makes a request to consult, create, delete or update user information. The controller takes the request and forwards it to the service layer. The service is in charge of performing the operation in the database (persistence) or applying some business rule. Subsequently, the information of the operation and the result is returned to the controller, who in turn sends back a response.

**Scope**: A single container.

