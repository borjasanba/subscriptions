# Subscription Internal Service API
RESTful API exposing a single endpoint for new subscriptions creation. 

Once the request is validated, this service is responsible for:
- Persisting a new record in Mysql table `subscription`
- Sending a message to Kafka topic `adidas_subscription`

API-first approach was followed so the design of the application programming interface came before the implementation. This way, the contract of how the API must behave was tackled in first place providing a well-documented interface. Check here the [API design](src/main/resources/api/subscription-service-api.yml)

The Kafka producer is configured as an Idempotent Producer (exactly once). If something goes wrong, like a communication failure, it isssured that the message is delivered without duplicates when the system is recovered.   

### Building
```
mvn clean install
```

### Running

Subscription Internal Service needs the following containers to be running 
- mysql
- kafka
- zookeeper

So, before continue running the subscription internal microservice, it is needed to execute this command in order to start them:
```
docker-compose up -d 
```

#### Intellij idea 
Choose run on SubscriptionSrvApp.class

#### Maven
```
mvn spring-boot:run
```

#### Java
```
java -jar target/subscription-internal-service.jar
```

#### Docker
```
../docker-compose up -d
```
Note: parent directory contains the docker compose file configured

### Playing around
Swagger UI: http://localhost:8081/swagger-ui.html

#### Request-response samples
Subscription successfully created:
```
curl -X POST "http://localhost:8081/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"consent\": true, \"dateOfBirth\": \"1983-08-15\", \"email\": \"my@email.com\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```json
{ 
  "id":"f58ad26a-aa18-4121-a363-9bda86989d57"
}
```
Validation error:
```
curl -X POST "http://localhost:8081/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"consent\": true, \"dateOfBirth\": \"1983-08-15\", \"email\": \"my@email.com\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
```
```json
{
  "message":"SubscriptionDTO already exists for my@email.com"
}
```
Validation error:
```
curl -X POST "http://localhost:8081/api/subscription" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"consent\": false, \"dateOfBirth\": \"1983-08-15\", \"email\": \"ivanlidemail\", \"firstName\": \"Borja\", \"gender\": \"Prefer not to say\"}"
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
| spring-cloud-stream-binder-kafka-streams | Produce messages using the Apache Kafka implementation of Spring Cloud Stream |
| spring-boot-starter-data-jpa | Integrate the spring application with Java Persistent API and persist data in the DB |
| mysql-connector-java | Provide the JDBC driver connection for MySQL |
| flyway-core | Implement automated and version-based database migrations  |
| springfox-swagger2/springfox-swagger-ui | Describe and document the RESTful API |
| lombok | Reduce boilerplate code writing less repetitive code |
| vavr | Enhance functional programming features for Java8 and increase readability and robustness of code |
| mapstruct | Simplify the implementation of mappings between beans |
| spring-boot-starter-test | Implement unit testing |
| mockneat | Generate complex mock arbitrary data in unit testing |
| h2 | Add an in-memory database for testing purposes |

### Unit testing
100% classes, 95% lines coverage

### Improvements
##### Key generation
Currently it is been used an UUID generator for subscription keys. This key is 36 bytes length and can be decrease to 7 bytes if it is replace with a key generator service to hash, i.e. the email using SHA1 and base64 encoding to avoid collisions. With this approach can be generated up to 5 billion of different combinations.
##### Security layer
Given this service will be running inside a private network we rely on the network security layer to allow requests from a certain range of ips, security groups, certificate, etc. We also may consider to add another layer of security providing an API key or token validation for all incoming requests.
##### At least once Producer policy
We may consider to replace the Kafka producer policy with 'At least once'. This way, it is needed to the logic to handle incoming duplicate messages to all consumers but the latency of this service will be improved.