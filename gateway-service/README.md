# API Gateway
Springboot microservice to receive all requests coming from the consumers and then delegates them to the DNS with a Load Balancer for each internal microservice.

This service acts a single endpoint for authentication, so all requests must sent a JWT that it is been validated against an organizational authentication server (out of scope).

It is implemented the Fallback and Circuit Breaker pattern by configuring Ribbon as a Load Balancer handling failure recovery with the list of Servers.

### Building
```
mvn clean install
```

### Running

Subscription Gateway needs the adidas-subscription-service container and all its dependencies to be running.

So, before continue running the subscription gateway microservice, it is needed to execute this command in order to start them:
```
docker-compose up -d 
```

#### Intellij idea 
Choose run on SubscriptionPubApp.class

#### Maven
```
mvn spring-boot:run
```

#### Java
```
java -jar target/subscription-public-service.jar
```

#### Docker
```
../docker-compose up -d
```
Note: parent directory contains the docker compose file configured

### Playing around

#### Request-response samples
Subscription successfully created:
```
curl -X POST "http://localhost:8080/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -H "Authorization: 1234567890" -d "{ \"consent\": true, \"dateOfBirth\": \"1983-08-15\", \"email\": \"my@email.com\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```json
{ 
  "id":"f58ad26a-aa18-4121-a363-9bda86989d57"
}
```
Access Denied error:
```
curl -X POST "http://localhost:8080/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -H -d "{ \"consent\": true, \"dateOfBirth\": \"1983-08-15\", \"email\": \"my@email.com\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```json
{ 
  "timestamp":"2020-09-26T18:41:41.334+0000","status":403,"error":"Forbidden","message":"Access Denied","path":"/api/subscription"
}
```
Internal server error (when subscription internal service is down)
```
curl -X POST "http://localhost:8080/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -H "Authorization: 1234567890" -d "{ \"consent\": true, \"dateOfBirth\": \"1983-08-15\", \"email\": \"my@email.com\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```
Subscription service is not available
```
```
DynamicServerListLoadBalancer for client subscription-service initialized: DynamicServerListLoadBalancer:{NFLoadBalancer:name=subscription-service,current list of Servers=[subscription:8081],Load balancer stats=Zone stats: {unknown=[Zone:unknown;	Instance count:1;	Active connections count: 0;	Circuit breaker tripped count: 0;	Active connections per server: 0.0;]
},Server stats: [[Server:subscription:8081;	Zone:UNKNOWN;	Total Requests:0;	Successive connection failure:0;	Total blackout seconds:0;	Last connection made:Wed Dec 31 19:00:00 EST 1969;	First connection made: Wed Dec 31 19:00:00 EST 1969;	Active Connections:0;	total failure count in last (1000) msecs:0;	average resp time:0.0;	90 percentile resp time:0.0;	95 percentile resp time:0.0;	min resp time:0.0;	max resp time:0.0;	stddev resp time:0.0]
```
Validation error:
```
curl -X POST "http://localhost:8080/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -H "Authorization: 1234567890" -d "{ \"consent\": false, \"dateOfBirth\": \"1983-08-15\", \"email\": \"ivanlidemail\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```json
{
  "message":"consent must be checked,ivanlidemail e-mail format is invalid"
}
```
#### Operating DB
Find below useful commands to check database and query records added throw the microservice:
```
docker exec -it subscriptions-mysql bash;
mysql -uadmin -p
>use ADIDAS;
>show tables;
+-----------------------+
| Tables_in_ADIDAS      |
+-----------------------+
| flyway_schema_history |
| subscription          |
+-----------------------+
2 rows in set (0.00 sec)
mysql> describe subscription;
+---------------+--------------+------+-----+---------+-------+
| Field         | Type         | Null | Key | Default | Extra |
+---------------+--------------+------+-----+---------+-------+
| id            | varchar(36)  | NO   | PRI | NULL    |       |
| email         | varchar(254) | NO   |     | NULL    |       |
| first_name    | varchar(50)  | YES  |     | NULL    |       |
| gender        | varchar(18)  | YES  |     | NULL    |       |
| date_of_birth | date         | NO   |     | NULL    |       |
| consent       | tinyint(1)   | NO   |     | NULL    |       |
| created_at    | datetime     | NO   |     | NULL    |       |
+---------------+--------------+------+-----+---------+-------+
7 rows in set (0.04 sec)
>mysql> select * from subscription;
 +--------------------------------------+--------------+------------+-------------------+---------------+---------+---------------------+
 | id                                   | email        | first_name | gender            | date_of_birth | consent | created_at          |
 +--------------------------------------+--------------+------------+-------------------+---------------+---------+---------------------+
 | f58ad26a-aa18-4121-a363-9bda86989d57 | my@email.com | Borja      | PREFER_NOT_TO_SAY | 1983-08-15    |       1 | 2020-09-25 20:13:13 |
 +--------------------------------------+--------------+------------+-------------------+---------------+---------+---------------------+
 1 row in set (0.01 sec)
```
#### Operating Kafka
Find below useful commands to initiate a consumer for the kafka topic where the microservice send messages:
```
docker exec -it -u root subscriptions-kafka /bin/bash
>$KAFKA_HOME/bin/kafka-console-consumer.sh --from-beginning --bootstrap-server localhost:9092 --topic=adidas_subscription
```

### Frameworks/Libraries included
| Framework/Library | Used to |
| ------------- | ------------- |
| spring-boot-starter-web | Build the RESTful API with Spring MVC  |
| spring-boot-starter-actuator | Expose operational information about the running application i.e. health, metrics, info  |
| spring-cloud-starter-netflix-zuul | Integrate Zuul as an edge service that proxies requests to our backing services |
| spring-boot-starter-security | Provide a filter that intercepts that request are from authenticated users |
| lombok | Reduce boilerplate code writing less repetitive code |
| spring-boot-starter-test | Implement unit testing |

### Unit testing
100% classes, 91% lines coverage