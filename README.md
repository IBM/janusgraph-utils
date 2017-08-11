# JanusGraphBench
Benchmark tool for generating customized JanusGraph performance workload.
# How to Use:
#### Step 1 Download, Build, and Run from source with Maven
    git clone git@github.ibm.com:htchang/JanusGraphBench.git
    cd JanusGraphBench;mvn package
    java -jar target/JanusGraphBench-0.0.1-SNAPSHOT.jar config/tiny_config.json /tmp
#### Step 2 Populate the database with the [JanusGraphBatchImport] (https://github.com/sdmonov/JanusGraphBatchImporter)
#### How to Customize database size
	VertexTypes: contains any number of different vertex labels
		- name: change the name of vertex label
		- columns: may contain any number of columns(property keys). 
			- Key: Name of column
			- dataType: String, Long, Integer
			- dataSubType: options are "Name" or "shakespear". This generate some fake data
			- composit: create composit index or not
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
