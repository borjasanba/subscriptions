# Email service
Springboot microservice to consume Kafka topic and send e-email according to the information received.

It is integrated with Spring Cloud Kafaka Streams to consume the topic `adidas_subscription` and send an email asynchronously. If during this proccess something goes wrong, i.e. an e-mail server failure, the information will be send to `dlq_adidas_mail` topic

### Building
```
mvn clean install
```

### Running

Email Service needs the following containers to be running 
- kafka
- zookeeper

So, before continue running the email microservice, it is needed to execute this command in order to start them:
```
docker-compose up -d 
```

#### Intellij idea 
Choose run on EmailSrvApp.class

#### Maven
```
mvn spring-boot:run
```

#### Java
```
java -jar target/adidas-email-service.jar
```

#### Docker
```
../docker-compose up -d
```
Note: parent directory contains the docker compose file configured

### Playing around

#### Sending messages to adidas_subscription topic
Find below useful commands to initiate a producer for the kafka topic where the microservice consume messages:
```
docker exec -it -u root subscriptions-kafka /bin/bash
>$KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic=adidas_subscription
>{"id":"829a4ce7-3859-47eb-bb7b-c14eeb7baa64","email":"twineduardo@yahoo.com","firstName":"Myra Wielock","gender":"Male","dateOfBirth":"1988-01-09","consent":false,"createdAt":"2020-09-26T11:24:02.696"}
```
#### Consuming messages from dlq_adidas_email topic
Find below useful commands to initiate a consumer for the kafka topic where the microservice send messages:
```
docker exec -it -u root subscriptions-kafka /bin/bash
>$KAFKA_HOME/bin/kafka-console-consumer.sh --from-beginning --bootstrap-server localhost:9092 --topic=dlq_adidas_email
```

### Frameworks/Libraries included
| Framework/Library | Used to |
| ------------- | ------------- |
| spring-boot-starter-web | Build the RESTful API with Spring MVC  |
| spring-boot-starter-actuator | Expose operational information about the running application i.e. health, metrics, info  |
| spring-cloud-stream-binder-kafka-streams | Produce messages using the Apache Kafka implementation of Spring Cloud Stream |
| spring-boot-starter-mail | Integrate the spring application with Java Mail API and send e-mails |
| spring-boot-starter-thymeleaf | Integrate the Thymeleaf template engine while sending newsletter templates |
| lombok | Reduce boilerplate code writing less repetitive code |
| vavr | Enhance functional programming features for Java8 and increase readability and robustness of code |
| spring-boot-starter-test | Implement unit testing |
| kafka-streams-test-utils | Generate Kafka Topology and test consumers |
| mockneat | Generate complex mock arbitrary data in unit testing |

### Unit testing
100% classes, 95% lines coverage

### Improvements
#### Retry logic
Implement a retry logic for all failed e-mail. Currently it is only been sent a message to `dlq_adidas_mail` but we can improve this solution consuming this topic with a certain delay in order to retry the operation later on. For instance, we can generate multiple delays in case the external server is down for a certain period, like `dlq_adidas_mail_min_5`, `dlq_adidas_mail_min_15`, `dlq_adidas_mail_min_30` and provide more graceful reprocessing.