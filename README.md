# Order Service

## Overview

Order Service is a Spring Boot microservice responsible for managing customer orders in the webshop system.

It creates and stores orders, communicates with Product Service to decrease product stock, calculates order totals, and publishes order-related messages to RabbitMQ.

The service also handles order retrieval based on the authenticated user's role.

## Responsibilities

Order Service handles:

* Creating new orders.
* Retrieving orders based on the authenticated user's role.
* Allowing administrators to retrieve all orders.
* Allowing customers to retrieve only their own orders.
* Storing orders and order items in a database.
* Calculating order totals.
* Communicating with Product Service to decrease product stock.
* Forwarding the authenticated customer's JWT bearer token to Product Service.
* Publishing order confirmation messages to RabbitMQ.

## Technologies

* Java 17
* Spring Boot 4.0.6
* Spring Web / Spring MVC
* Spring Data JPA
* Hibernate
* PostgreSQL
* H2 Database
* Spring Security
* OAuth2 Resource Server
* OAuth2 Client
* JWT
* Spring AMQP
* RabbitMQ
* Jackson JSON message conversion
* Springdoc OpenAPI 3.0.2
* Lombok
* Maven
* JUnit 5

## Database

The application uses Spring Data JPA and Hibernate to persist orders and order items.

The database connection is configured through environment variables:

| Variable      | Description             |
| ------------- | ----------------------- |
| `DB_URL`      | Database connection URL |
| `DB_USERNAME` | Database username       |
| `DB_PASSWORD` | Database password       |

PostgreSQL and H2 are included as runtime dependencies. The actual database used depends on the configured connection URL.

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`, which updates the database schema based on the JPA entities.

## Port

The default port is `9000`.

The port can be changed using the `SERVER_PORT` environment variable.

## Environment Variables

Configure the required environment variables before starting the application.

| Variable                   | Description                      | Default     |
| -------------------------- | -------------------------------- | ----------- |
| `SERVER_PORT`              | Port used by the application     | `9000`      |
| `PRODUCT_SERVICE_BASE_URL` | Base URL of Product Service      | No default  |
| `AUTH_SERVER_URL`          | URL of the authentication server | No default  |
| `DB_URL`                   | Database connection URL          | No default  |
| `DB_USERNAME`              | Database username                | No default  |
| `DB_PASSWORD`              | Database password                | No default  |
| `RABBITMQ_HOST`            | RabbitMQ server hostname         | `localhost` |
| `RABBITMQ_PORT`            | RabbitMQ server port             | `5672`      |
| `RABBITMQ_USERNAME`        | RabbitMQ username                | `guest`     |
| `RABBITMQ_PASSWORD`        | RabbitMQ password                | `guest`     |
| `RABBITMQ_VHOST`           | RabbitMQ virtual host            | `/`         |

RabbitMQ SSL is enabled in the application configuration. The RabbitMQ server must support the configured SSL/TLS connection.

Do not commit database credentials, passwords, or other secrets to the repository.

## API Endpoints

The `OrderController` exposes the following endpoints:

| Method | Endpoint  | Description                                              |
| ------ | --------- | -------------------------------------------------------- |
| `POST` | `/orders` | Creates an order and returns the created order.          |
| `GET`  | `/orders` | Retrieves orders based on the authenticated user's role. |

Creating an order requires the customer's JWT to be available to the controller.

The `POST /orders` endpoint returns HTTP `201 Created` when the controller successfully creates the order.

The `GET /orders` endpoint returns HTTP `200 OK` and applies role-based filtering:

* Customers with the `USER` role can only retrieve their own orders.
* Users with the `ADMIN` role can retrieve all orders.

## Authentication and Authorization

Order Service uses Spring Security OAuth2 Resource Server to authenticate incoming requests using JWT tokens.

The authenticated user's identity is retrieved from the JWT `subject` claim.

The service uses the JWT roles claim to determine which orders the authenticated user is allowed to retrieve.

Order retrieval follows these rules:

* `USER`: Retrieves orders belonging to the authenticated customer.
* `ADMIN`: Retrieves all orders.

The customer's identity is used when creating new orders, ensuring that each order is associated with the authenticated customer.

## Communication with Product Service

Order Service communicates with Product Service using Spring's `RestClient`.

When an order is created, Order Service sends the requested product IDs and quantities to:

`POST /products/stock/decrease`

The request includes the customer's bearer token in the `Authorization` header.

Product Service decreases the requested stock and returns product information. Order Service uses this information to construct the order items and calculate the total price.

The Product Service base URL is configured using `PRODUCT_SERVICE_BASE_URL`.

## RabbitMQ and Messaging

Order Service uses RabbitMQ for asynchronous messaging.

The application declares a durable queue named `email-queue` and configures Jackson JSON message conversion.

When an order is created and saved, `OrderService` publishes an order confirmation message to the queue. The message contains the customer's identifier, ordered items, and total price.

The project also contains a `MessagePublisher` component that can publish order information including the order ID, customer-related information, order date, total price, and order items.

A reachable RabbitMQ server is required for the messaging functionality to work.

## API Documentation

The project uses Springdoc OpenAPI to document the REST API.

The OpenAPI configuration defines the API title as `Order Service API`, version `1.0`, and HTTP Bearer Authentication using JWT.

When the application is running, the standard Springdoc endpoints are:

* Swagger UI: `/swagger-ui/index.html`
* OpenAPI specification: `/v3/api-docs`

## Running Locally

### Prerequisites

* Java 17 or a compatible configured Java environment.
* The Maven Wrapper included in the repository.
* A database configured through the environment variables.
* A reachable Product Service instance.
* A reachable authentication server when authentication is required.
* A reachable RabbitMQ server supporting the configured SSL/TLS connection.

### Windows

Set the required environment variables, open a terminal in the project root, and run:

```powershell
mvnw.cmd spring-boot:run
```

### Linux/macOS

Set the required environment variables, open a terminal in the project root, and run:

```bash
./mvnw spring-boot:run
```

The application starts on port `9000` by default.

## Related Repositories

* [Product Service](https://github.com/philipwmitilinos-max/Product_Service)
* [Auth Service](https://github.com/conniponni/Auth-server)
* [Gateway](https://github.com/conniponni/webshop-gateway)
* [Frontend](https://github.com/Stefanvill/agil-frontend)
* [Email Service](https://github.com/Stefanvill/email-service)
