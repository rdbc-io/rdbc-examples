[![Travis](https://img.shields.io/travis/rdbc-io/rdbc-pool/master.svg?style=flat-square)](https://travis-ci.org/rdbc-io/rdbc-pool/branches)
[![Gitter](https://img.shields.io/gitter/room/rdbc-io/rdbc.svg?style=flat-square)](https://gitter.im/rdbc-io/rdbc)
[![license](https://img.shields.io/github/license/rdbc-io/rdbc-pool.svg?style=flat-square)](https://github.com/rdbc-io/rdbc-pool/blob/master/LICENSE)

## This repository

This repository contains a collection of example projects using 
[rdbc](https://github.com/rdbc-io/rdbc#what-is-rdbc) database connectivity API.
Chapters below describe the implemented examples.

## Examples

* [Simple](#simple)
* [Play Framework](#play-framework)

### Simple

You can find the code of this example in [simple-java](https://github.com/rdbc-io/rdbc-examples/tree/master/simple-java)
directory. As the name suggest it's just a simple piece of code implemented in
Java that inserts a couple of records and queries them afterwards.

The projects use the API with the PostgreSQL driver. Before you run the project,
start PostgreSQL server on the local machine and configure it to accept
connections with password authentication method using "postgres" username
and "postgres" password.

To run the project, enter the rdbc-examples directory and start the application
with SBT using `sbt simple-java/run`.

### Play Framework

You can find a Scala version of the example in [play-scala](https://github.com/rdbc-io/rdbc-examples/tree/master/play-scala)
directory and Java version in  
[play-java](https://github.com/rdbc-io/rdbc-examples/tree/master/play-java).

The projects use the API with the PostgreSQL driver. Before you run the project,
start PostgreSQL server on the local machine and configure it to accept
connections with password authentication method using "postgres" username
and "postgres" password. Alternatively, configure connection parameters
in `conf/application.conf` file.

To run the project, enter the rdbc-examples directory and start the application
with SBT using `sbt play-scala/run` or `sbt play-java/run` command. After starting the app, 
a sample database table will be created.

The application defines a number of routes:

* [`/list`](http://localhost:9000/list) - a HTML page allowing you to add and list records
defined in the sample table.
* [`/stream`](http://localhost:9000/stream) - endpoint streaming table contents as JSONs.

See `Module` class 
([Scala](https://github.com/rdbc-io/rdbc-examples/blob/master/play-scala/app/io/rdbc/examples/playscala/Module.scala),
[Java](https://github.com/rdbc-io/rdbc-examples/blob/master/play-java/app/io/rdbc/examples/playjava/Module.java))
for the code that configures connection pool and creates a sample table. Inspect
`ApplicationController`
([Scala](https://github.com/rdbc-io/rdbc-examples/blob/master/play-scala/app/io/rdbc/examples/playscala/controllers/ApplicationController.scala),
[Java](https://github.com/rdbc-io/rdbc-examples/blob/master/play-java/app/io/rdbc/examples/playjava/controllers/ApplicationController.java))
class for the implementation of selecting and inserting values and also for the 
streaming. It may be interesting for you to add many records to the table, say,
100 000 and compare behavior of the application when:

- requesting the data with streaming: `wget http://localhost:9000/stream`
- requesting the data with no streaming: `wget http://localhost:9000/list`

Requesting the data with no streaming is likely to cause `OutOfMemoryError` and
force you to kill the application.
