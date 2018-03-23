# Notes about data persistance within PeerPub

PeerPub uses MongoDB for its storage of documents and surrounding
metadata, etc. For process state and other process related data, it uses
primarily a H2 database to keep requirements on external data services.

## Schema migrations and initial dataset
Using [Mongobee](https://github.com/mongobee/mongobee), schema migrations
on the otherwise schemaless database are executed.

This is used for importing an initial dataset, too. This is primarily
used for the demo, but also acts as a starting point for new production
instances.

Please consult [MongoChangelog.java](
../src/main/java/de/fzj/peerpub/config/MongoChangelog.java) and the
[migrations](../src/main/resources/migrations) folder for examples and more
informations.

## Configuring and using external data persistance services
Except for MongoDB you can switch to another SQL database by providing
an appropriate configuration.

For your convenience, this project contains some `docker-compose` files,
which allow for an easy setup with Docker, which also creates a MongoDB
container. You can anyway opt for using a central MongoDB server, etc.

Please follow the guide on [External Configuration for Spring Boot](
https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html)
to provide your custom `application.properties` or other format based
configuration.

## Notes about Docker based database services
When using the default `docker-compose.yml`, the MongoDB in the
`mongodb` container will expose its port to your host.
**As this is intended for demo and development purposes, no security is
 in place, be aware of this for production use.**