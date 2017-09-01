# Develop a scalable graph database app using JanusGraph [![Build Status](https://travis-ci.org/IBM/janusgraph-utils.svg?branch=master)](https://travis-ci.org/IBM/janusgraph-utils)

This journey contains sample data and code for running a Twitter-like application in JanusGraph. The utility code illustrates how to use OLTP APIs to define schema, ingest data, and query graph. Developers can use or modify the code to build and operate their custom graph applications, or create similar java and groovy files to interact with JanusGraph.

When the reader has completed this journey, they will understand how to:
* Generate a synthetic graph dataset
* Load a graph schema from json
* Import graph data in csv files into JanusGraph database
* Query and update graph data using Console and REST API
* Setup and configure a scalable JanusGraph system

![](doc/source/images/architecture.png)

## Flow
Prerequisites:
Install and configure JanusGraph, Cassandra, ElasticSearch, janusgraph-utils

1. The user generates Twitter sample schema and data using JanusGraph utilities
2. The user loads schema and imports data in backend servers using JanusGraph utilities
3. The user makes search and update requests in a REST/custom client
4. The client app sends the REST requests to JanusGraph server
5. The JanusGraph server interacts with backend to process and return graph data

## Included components

* [Apache Cassandra](http://cassandra.apache.org/): An open source, scalable, high availability database.
* [JanusGraph](http://janusgraph.org/): A highly scalable graph database optimized for storing and querying large graphs.

## Featured technologies
* [Databases](https://en.wikipedia.org/wiki/IBM_Information_Management_System#.22Full_Function.22_databases): Repository for storing and managing collections of data.
* [Java](https://java.com/en/): A secure, object-oriented programming language for creating applications.

# Watch the Video

![Todo]

# Steps
## Run locally
1. [Install prerequisites](#1-install-prerequisites)
2. [Clone the repo](#2-clone-the-repo)
3. [Generate the graph sample](#3-generate-the-graph-sample)
4. [Load schema and import data](#4-load-schema-and-import-data)
5. [Run interactive remote queries](#5-run-interactive-remote-queries)

### 1. Install prerequisites
> NOTE: These prerequisites can be installed on one server.

Install Cassandra 3.10 on the storage server. Make the following changes in `/etc/cassandra/cassandra.yaml` and restart Cassandra.

```
start_rpc: true
rpc_address: 0.0.0.0
rpc_port: 9160
broadcast_rpc_address: x.x.x.x (your storage server ip)
```

Install ElasticSearch 5.3.0 on the index server. Make the following changes in `/etc/elasticsearch/elasticsearch.yml` and restart ElasticSearch.

```
network.host: x.x.x.x (your index server ip)
```

Install JanusGraph on the graph server
* Install java, maven, git
* Run `git clone https://github.com/JanusGraph/janusgraph.git`
* Run the following in `janusgraph` folder
```
git checkout 4609b6731a01116e96e554140b37ad589f0ae0ca
mvn clean install -DskipTests=true
cp conf/janusgraph-cassandra-es.properties conf/janusgraph-cql-es.properties
vi conf/janusgraph-cql-es.properties
storage.backend=cql
storage.hostname=x.x.x.x (your storage server ip)
index.search.hostname=x.x.x.x (your index server ip)
```

Install a REST client, such as RESTClient add-on for Firefox, on the client machine

### 2. Clone the repo

Clone the `janusgraph-utils` on the graph server.

```
git clone https://github.com/IBM/janusgraph-utils.git
cd janusgraph-utils/
mvn package
```

### 3. Generate the graph sample

Run the command in `janusgraph-utils` folder to generate data into `/tmp` folder.
```
./run.sh gencsv csv-conf/twitter-like-w-date.json /tmp
```
 Modify generated user file under `/tmp`
```
sed -i '2s/.*/1,Indiana Jones/' /tmp/User.csv
```
### 4. Load schema and import data

Run the command in `janusgraph-utils` folder to load schema and import data.
```
export JANUSGRAPH_HOME=~/janusgraph
./run.sh import ~/janusgraph/conf/janusgraph-cql-es.properties /tmp /tmp/schema.json /tmp/datamapper.json
```

### 5. Run interactive remote queries
Configure and start JanusGraph server by running the following in `~/janusgraph/conf/gremlin-server` folder.

```
cp ~/janusgraph-utils/samples/date-helper.groovy ../../scripts 
cp ../janusgraph-cql-es.properties janusgraph-cql-es-server.properties
Add a line to janusgraph-cql-es-server.properties, gremlin.graph=org.janusgraph.core.JanusGraphFactory
cp gremlin-server.yaml rest-gremlin-server.yaml
Change rest-gremlin-server.yaml
host: x.x.x.x (your server ip)
channelizer: org.apache.tinkerpop.gremlin.server.channel.HttpChannelizer
graph: conf/gremlin-server/janusgraph-cql-es-server.properties}
scripts: [scripts/empty-sample.groovy,scripts/date-helper.groovy]}}
cd ~/janusgraph; ./bin/gremlin-server.sh ./conf/gremlin-server/rest-gremlin-server.yaml
```
Query and update graph data using REST. Send REST requests using RESTClient in browser with following:
```
Method: POST
URL: http://x.x.x.x:8182
Body: {"gremlin":“query_to_run"}
```
Find sample search and insert queries in `samples/twitter-like-queries.txt`

# Sample output

![](doc/source/images/sample_output.png)
