# JanusGraph Utilities
Benchmark tools for generating and importing customized JanusGraph performance workload.
# How to Use:
## Download and Build
    git clone git@github.ibm.com:htchang/JanusGraphBench.git
    cd JanusGraphBench;mvn package
## Generate CSV files, schema, and datamapper
    Generate customized CSV for importing to JanusGraph.
	Usage: 
	  JanusGraphBench <csv-config-file> <mapper-schema-output-directory>
	Example:
	  ./run.sh gencsv csv-conf/tiny_config.json /tmp
#### CSV config file options:
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
## Import CSV file to JanusGraph
    JanusGraphBatchImporter is a tool that imports bulk data into JanusGraph using multiple CSV files for the data and json config files to define the schema and mapping between schema and data. It uses multiple workers in order to achieve full speed of import of the data.
    JanusGraphBatchImporter depends on JanusGraph libraries and also commons-csv-1.4.jar

    In order to use under Linux:

    Edit conf/batch_import.properties file to configure the importer

    Usage:
      run.sh import <janusgraph-config-file> <data-files-directory> <schema.json> <data-mapping.json>

      <janusgraph-config-file>: Properties file with the JanuGraph configuration
      <data-files-directory>: Relative directory where the data files are located
      <schema.json>: JSON file defining the schema of the graph
      <data-mapping.json>: mapping file defining the relationship between the CSV/s fields and the graph
    Example:
    ./run.sh import /root/janusgraph-v0.1.1/conf/janusgraph-cassandra-es.properties /tmp /tmp/schema.json /tmp/datamapper.json
