# Event-Driven Order Management System

Microservices project where placing an order triggers a chain of events through Apache Kafka.
Built with Java 17, Spring Boot 3, Spring Kafka, Spring Data JPA, MySQL and Docker.

## How it works

```
POST /api/orders
      |
      v
 order-service ----(order-created)----> inventory-service
   saves order                            checks and reduces stock
   status = PLACED                        saves result in its own DB
      ^                                          |
      |                                  (inventory-updated)
      |                                          |
      +------------------------------------------+---------> notification-service
        updates status to                                      logs the "email"
        CONFIRMED or REJECTED
```

| Service | Port | Responsibility |
|---|---|---|
| order-service | 8081 | Accepts orders, stores them, updates status from the inventory result |
| inventory-service | 8082 | Owns product stock, reserves stock for each order |
| notification-service | 8083 | Sends (simulated) customer notifications |

Each service has its own database and never reads another service's tables. Services only talk through Kafka.

## Kafka design

| Topic | Partitions | Producer | Consumers |
|---|---|---|---|
| `order-created` | 3 | order-service | inventory-service (`inventory-group`) |
| `inventory-updated` | 3 | inventory-service | order-service (`order-group`), notification-service (`notification-group`) |
| `*.DLT` | 1 | error handler | manual inspection |

- The message key is the `orderId`, so events for one order always go to the same partition and stay in order.
- Every consumer retries a failed message 3 times (1 second apart), then sends it to a dead-letter topic (`<topic>.DLT`).
- Inventory is idempotent: it stores processed order ids, so a redelivered event does not reduce stock twice.
- Inventory publishes its result inside the database transaction and waits for Kafka to confirm, so a failed publish rolls back the stock change.
- `@Version` on the inventory item gives optimistic locking when several consumers update the same product.

## Run it

Requirements: Docker, and JDK 17 + Maven if you want to run the services from your IDE.

**Everything in Docker**

```bash
docker compose up --build
```

**Only Kafka and MySQL in Docker, services from your IDE** (easier for debugging)

```bash
docker compose up mysql kafka
```

Then run the three `*Application` classes. MySQL is exposed on port 3307 and Kafka on 9092.

## Try it

Sample products: 1 = Laptop (50), 2 = Mobile Phone (100), 3 = Headphones (200).

```bash
# place an order
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 2, "customerEmail": "anil@example.com"}'

# check the order (status changes from PLACED to CONFIRMED after a moment)
curl http://localhost:8081/api/orders/1

# stock should now be 48
curl http://localhost:8082/api/inventory/1

# ask for more than the stock -> order ends up REJECTED
curl -X POST http://localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "quantity": 500, "customerEmail": "anil@example.com"}'
```

Watch the notification-service log for the "email" lines.
Swagger UI: http://localhost:8081/swagger-ui.html and http://localhost:8082/swagger-ui.html

Run the unit tests inside each service folder with `mvn test`.

## Project structure

```
order-management-system/
  order-service/
  inventory-service/
  notification-service/
  docker/mysql-init.sql
  docker-compose.yml
```

Each service follows the same layout: `controller`, `service`, `repository`, `model`, `dto`, `event`, `messaging`, `config`.
The event classes are copied into each service on purpose, so services do not share code and stay independent.

## Known limitations

- Order-service saves the order and then publishes the event. If the app crashes between the two, the order stays `PLACED`. The usual fix is the transactional outbox pattern.
- No authentication yet.

## Roadmap

- [ ] Eureka service discovery
- [ ] Spring Cloud API Gateway
- [ ] JWT authentication at the gateway
- [ ] Integration tests with Testcontainers
