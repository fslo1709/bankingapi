# Backend Engineer Hiring Challenge

## Code Organization
This micro-service is divided into three folders:
- auxiliary: Classes that support the microservice
    - CurrencyConverter: 
    - Transaction:
- controller: RESTful API related classes
    - MessageController
    - MessageRequest
- kafkaClasses: All kafka-related classes
    - KafkaConsumerGroup
    - KafkaProducerConfig
    - KafkaTopicConfig
    - Ka

## Requirements Implementation
Below we can see the problem description (each part that belongs to the explanation will be ___highlighted like this___) and assumptions, explaining how they're implemented in the code.
### Summary ###
___For an e-Banking Portal you have been given the task to design and implement a reusable REST API___

The REST API is designed using the web library from spring-boot. There is only one operations designed in the microservice: GET, accessed through /api/v1/synpulse

To do so, the RestController annotation is utilized, along with GetMapping

___for returning the paginated list of money account transactions created in an arbitrary calendar month___

The get entry for our API requires a Request Parameter, date, compliant with DateTime ISO standard. This will be used for the Kafka Consumer to filter out only the data pertinent to the desired month. 

The date can be any date, the backend will fetch the appropriate calendar month associated with it.

The return value of this call will be a json object with all the transactions of the user that belong to the specified month.

___for a given customer who is logged-on in the portal.___

JWT tokens are used to validate that the user is logged in correctly. To implement
this, it is assumed that the login is part of another service, which is outside
the scope of this microservice. That login service would provide the user with a JWT token, which will be validated by our microservice.

___For each transaction ‘page’ return the total credit and debit values at the current exchange rate (from the third-party provider).___

Third party provider picked is Exchange rates from Abstract API, although any API can be used if the url is changed and a valid key is provided. 

The Key is stored in apiKey.txt, located in java.com.synpulsebankapi, although this is never comitted to Github.

The values of the total credit and debit are appended to the json response.

___The list of transactions should be consumed from a Kafka topic.___

To consume the transactions, a Consumer is defined in the kafkaClasses.config.KafkaConsumerConfig

This is implemented by the MessageController, which uses the user id to tell the Consumer which partition to listen to, process the data and close the Consumer.

___Build a Docker image out of the application and prepare the configuration for deploying it to Kubernetes / OpenShift.___

### Assumptions
___Every e-banking client has one or more accounts in different currencies (e.g. GBP, EUR, CHF)___

All transactions will be converted to USD, so when we call the API, we request the rate from all currencies to USD and use that as the total rate.

___There are approximately one hundred thousand e-banking customers, each with a couple thousands of transactions per month.___

By using the user id as the key for adding the event to the topic and specifying k number of partitions, where k is the number of customers, one Kafka partition per customer can be used (the id is still checked to guarantee correctness)

___The transactions cover the last ten years and are stored in Kafka with the key being the transaction ID and the value the JSON representation of the transaction___

Key covered in the previous assumption. To create the JSON representation, a JSON serializer/deserializer is used to convert from and to a Java Object defined in auxiliary/Transaction.java 

Another implementation was thought of, in which more topics could be created and divided per calendar year as to reduce the size of the topics, but not certain if this would be better or worse. For the sake of time, it was not explored further.

___The user is already authenticated and the API client invoking the transaction API will send a JWT token containing the user’s unique identity key (e.g. P-0123456789)___ 

Key used to create partitions. 
TODO: extract username from JWT token

___The exchange rate on any given date is provided by an external API___

The external API related code is defined in auxiliary.CurrencyConverter

### Money transaction structure
___For simplicity reasons, consider a money account transaction composed of the following attributes:___
- ___Unique identifier (e.g. 89d3o179-abcd-465b-o9ee-e2d5f6ofEld46)___
- ___Amount with currency (eg GBP 100-, CHF 75)___
- ___Account IBAN (eg. CH93-0000-0000-0000-0000-0)___
- ___Value date (e.g. 01-10-2020)___
- ___Description (e.g. Online payment CHF)___

Each transaction from the backend perspective is a Java Object, defined in the class Transactions, located inside the auxiliary folder. The structure is as follows:

```
public class Transaction {
    private String id;
    private String amount;
    private String iban;
    private LocalDate date;
    private String description;
}
```
## User Diagram


## Architecture


## Data Modeling
