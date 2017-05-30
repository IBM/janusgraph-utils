# JanusGraphBench
Benchmark tool for generating customized JanusGraph performance workload.
# How to Use:
#### Step 1 Download, Build, and Run from source with Maven
    git clone git@github.ibm.com:htchang/JanusGraphBench.git
    cd JanusGraphBench;mvn package
    java -jar target/JanusGraphBench-0.0.1-SNAPSHOT.jar tiny_config.json /tmp
#### Step 2 Populate the database with the [JanusGraphBatchImport] (https://github.com/sdmonov/JanusGraphBatchImporter)
#### How to Customize database size
	VertexTypes: contains any number of different vertex labels
		- name: change the name of vertex label
		- columns : may contain any number of columns(property keys). Column names, dataType, and composit are all customizable
		- row: change number of vertex of the named label
	EdgeTypes: contains any number of different vertex labels
		- name: change the first edgelabel name
		- columns: may contain any number of columns(edge property keys). 
			- names: any string 
			- dataType: String, Long, Integer, and Date. 
			- composit: true or false
			- mixedIndex: any String (default should be "search") 
	-	- relations: Defines how the named edge relations can happen and number of occurrences.
			- left: the outV of an edge
			- right: the inV of an edge
			- supernode: nodes have many edges
				- vertices: n where n is the first n nodes of left
				- edges: additional edges are added to each of the n nodes
#### Example config:
```
{
  "VertexTypes": [
    {
      "name": "T1",
      "columns": {
        "T1-P1": {"dataType":"String","composit":true}
      },
      "row": 10
    },
    {
      "name": "T2",
      "columns": {
        "T2-P1": {"dataType":"String","composit":true},
        "T2-P2": {"dataType":"Integer","composit":true}
      },
      "row": 10
    }
  ],
  "EdgeTypes": [
    {
      "name": "E1",
      "columns": {
        "E1-P1": {"dataType":"Long","composit":true}
      },
      "relations": [
        {"left": "T1", "right": "T2", "row": 10 },
        {"left": "T2", "right": "T1", "row": 10 }
      ]
    }
  ]
}
```

# Proposal 
### Populate entire graphdb with a configuration json. 
##### We should provide a default configuration that specify these elements (* is for now):  
    1. Vertices:
        * label names
        * Number of different label types
        * size (small, medium, large)
    2. Edges:
        * label names
        * Number of different label types
        * size (small, medium, large)
    3. Relation(Edge) pattern?
    4. Properties
        * Number of properties for:
          - vertices(look at some existing data)
          - edges(look at some existing data)
        * data types (use String and Integer first)
    5. Index
        * which key should be indexed?
        * types of index (composite or mixed)?
        * start with 2 properites first. 1 compostie and 1 mixed
    
### Performance Metric:
    1. How fast can we do these types of transactions (With pre-defined default or customized transaction scenarios):
        * Importing data
            - Use Simeon's importer
                - one csv for vertices: each line is a unique vertex
                - one csv for edges: each line is a relation
        * Concurrent Query (How to drive? See options below)
            - Define own test queries here. For example:
                - List all the edges of all vertices
                - List all vertices with certain properties
            - Drive using Simeon's despatcher through Gremlin server
        - delete?
        - update?
        - Insert(same as create vertices?)
    2. Metric to measure:
        * Avg. TPS
        * Avg. KB/sec?(A vertex can have any # of properties and edges so its size can vary)
        * Avg. latency
    3. known variables:
        - Transactions/sec VS Threads(users)
        - Number of Transactions for each commit
### Monitor:
    * nmon
    * gclog
    
    
### Hardware
#### Single node:
    * 1 x cassandra + ES (1 composit index + 1 mixed inedex)
#### Multiple nodes:
    * Cluster of 3 cassandra later?
    - Other data store? (hbase)
### Which interface should the driver use to generate data and send queries:
#### Options:
    * Send APIs through Gremlin servers (web socket or REST)?
    * Stand alone Java Application?
### Codename:
    - Hermes?
### Ohter tests:
  1. https://github.com/njeirath/titan-perf-tester 
  2. https://www.slideshare.net/NakulJeirath/addressing-performance-issues-in-titancassandra
  3. https://aws.amazon.com/blogs/big-data/performance-tuning-your-titan-graph-database-on-aws/
  4. https://www.datastax.com/dev/blog/boutique-graph-data-with-titan
  5. http://s3.thinkaurelius.com/docs/titan/0.5.1/hadoop-performance-tuning.html

