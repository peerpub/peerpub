# PeerPub

This software can be used for internal peer review clearance processes of
scientific or other publications. It is primarily targeted to a scientific
audience and reflects common workflows in science.

As this software is open source under Gnu AGPLv3, please go ahead and use it.
Please respect the license obligations and restrictions. You should have received
a copy of the AGPLv3 license with this code.

## WARNING

This software is still in early stages of development. Please don't use it
for anything real yet. Expect sudden and disruptive changes, unhandled exceptions
and the end of the world due to null pointers.

Anyway: feel free to try it out.

## Development

This project uses [Maven](https://maven.apache.org) for all stages of
development and [Docker](https://www.docker.com) to run tests and demos.

To run a demo instance, please
1. Install Docker and [docker-compose](https://docs.docker.com/compose/overview)
2. Install Maven
3. Run `mvn package` within the cloned peerpub repository.
4. Run `docker-compose up` in the same directory.
5. Point your browser to http://localhost:8080.
