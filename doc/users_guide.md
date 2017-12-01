# Utility Tools for JanusGraph

Utility tools that can be used with JanusGraph, including:

- [JanusGraphSchemaImporter](#janusgraphschemaimporter): a groovy script that imports a graph schema definition, a JSON representation of the JanusGraph schema, into JanusGraph
- [Synthetic data generator](#synthetic-data-generator): a tool for generating synthetic data into CSV files
- [Data importer](#import-csv-file-to-janusgraph): a tool to import data into JanusGraph from CSV files

## Download and Build

Download the files from git repo and get into the directory:
```
git clone https://github.com/IBM/janusgraph-utils.git
cd janusgraph-utils
```

Use maven to build this project:

`mvn package`

### JanusGraphSchemaImporter

This utility reads the graph schema definition and writes to JanusGraph. You can
run JanusGraphSchemaImporter in two ways:
- Using groovy script in JanusGraph gremlin console:
  After you build the project. You can see the groovy script under `target/groovy`
  named `JanusGraphSchemaImporter.groovy`

  Usage:
  ```
  gremlin> graph = JanusGraphFactory.open('conf/janusgraph-cassandra-embedded-es.properties')
  ==>standardjanusgraph[embeddedcassandra:[127.0.0.1]]
  gremlin> :load JanusGraphSchemaImporter.groovy
  ......
  ......
  ==>true
  ==>true
  gremlin> writeGraphSONSchema(graph, 'schema.json')
  ```
  You can find the sample graph schema definition under `test` directory.

- Using `run.sh` with `loadsch` option to load the schema via JanusGraph Java API.

  Usage:
  ```
  ./run.sh loadsch <janusgraph-config-file> <schema-file>
  ```

#### How to load the groovy script in gremlin console
Use the following command to load the utility groovy script into gremlin console:

```
gremlin> :load <JanusGraphSchemaImporter.groovy>
```

Use the `:load` command and specify the groovy script location to load the groovy script.

#### How to load the groovy script into gremlin server
Modify the gremlin server configuration file `gremlin-server.yaml` under `janusgraph/conf/gremlin-server` directory. Add the groovy script into
`scriptEngines.gremlin-groovy.scripts`. Its value is an array and contains
all groovy scripts that would be loaded into gremlin-groovy script engine when
gremlin server starts. Here is a sample configuration of `gremlin-server.yaml` (partial):

```(YAML)
scriptEngines: {
  gremlin-groovy: {
    imports: [java.lang.Math],
    staticImports: [java.lang.Math.PI],
    scripts: [scripts/empty-sample.groovy, scripts/JanusGraphSchemaImporter.groovy]}}
```
#### APIs
Once you load the JanusGraphSchemaImporter.groovy into gremlin console, you can use the following APIs:
- **JanusGraphSONSchema.parse(file)**:
  -  file: a string which points to the graph schema definition

  Parses the graph schema definition and returns a schema bean object which contains the settings of the GSON schema

- **writeGraphSONSchema(janusgraph, file)**:
  - janusgraph: a JanusGraph instance
  - file: a string which points to the graph schema definition

  Parses the graph schema definition and writes the definitions of properties, vertices and edges into the JanusGraph.

- **updateCompositeIndexState(janusgraph, indexName, newState)**:
  - janusgraph: a JanusGraph instance
  - indexName: the composite index name
  - newState: the new index state, could be:
    - SchemaAction.DISABLE_INDEX
    - SchemaAction.ENABLE_INDEX
    - SchemaAction.REGISTER_INDEX
    - SchemaAction.REINDEX
    - SchemaAction.REMOVE_INDEX

   Changes the index from its original state into new state if the state transition is valid.

#### The graph schema definition
Contains the definitions of properties, vertices and edges:

```
{
    "propertyKeys": [.....],
    "vertexLabels": [.....],
    "edgeLabels": [.....],
    "vertexIndexes": [.....],
    "edgeIndexes": [.....],
    "vertexCentricIndexes": [.....]
}
```

Each property contains an array of objects. See detailed information for each
kind of object below.

**propertyKeys**

An array that contains the definition of the properties. Each property is defined in an object with the following format:
```
{
    "name": "<propertyName>",
    "dataType": "<String | Long | Character | Boolean | Byte | Short | Integer | Long | Float | Geoshape | UUID | Date>",
    "cardinality": "<SINGLE|LIST|SET>"
}
```

**vertexLabels**

An array that contains the definition of the vertices. Each vertex is
defined in an object with the following format:

```
{
    "name": "<vertex label>",
    "partition": false|true,    //optional
    "useStatic": false|true     //optional
}
```

**edgeLabels**

An array that contains the definition of the edges. Each edge is defined
in an object with the following format:

```
{
    "name": "<edge label>",
    "multiplicity": "<MULTI | SIMPLE | ONE2MANY | MANY2ONE | ONE2ONE>"
    "signatures": [ "<property key name>" ]
    "unidirected" : true|false    //default value is false, means directed
}
```

**vertexIndexes**

An array that contains the definition of vertex indices. Each vertex index
is defined in an object with the following format:

```
{
    "name": "<index name>",
    "propertyKeys": ["<property key name>"],
    "composite": true|false,
    "unique": true|false,
    "indexOnly": "<vertex label>"
}
```

**edgeIndexes**

An array that contains the definition of edge indices. Each edge index
is defined in an object with the following format:

```
{
    "name": "<index name>",
    "propertyKeys": ["<property key name>"],
    "composite": true|false,
    "indexOnly": "<edge label>"
}
```

**vertexCentricIndexes**

An array that contains the definition of vertex-centric indices. Each
vertex-centric index is defined in an object with the following format:

```
{
    "name": "<index name>",
    "propertyKeys": ["<property key>"],
    "edge": "<edge label>",
    "direction": "BOTH|IN|OUT",
    "order": "incr|decr"
}
```

### Synthetic Data Generator
#### Generate CSV files, schema, and datamapper
Generate customized CSV for importing to JanusGraph.
- Usage:
  `JanusGraphBench <csv-config-file> <mapper-schema-output-directory>`

Example:
      `./run.sh gencsv csv-conf/tiny_config.json /tmp`
#### CSV config file options:
- VertexTypes: contains any number of different vertex labels
  - name: change the name of vertex label
  - columns: may contain any number of columns(property keys)
    - [String]: Name of column
    - dataType: String, Long, Integer,Date
    - dateRange: default from 1970 to current time in default "dd-MMM-yyyy" format
      unless specified in the dateFormat
    - dateFormat: support JAVA SimpleDateFormat patterns. Default: "dd-MMM-yyyy"
    - dataSubType: options are "Name" or "shakespear". This generates some fake data
    - composit: create composit index or not
    - row: change number of vertex of the named label
- EdgeTypes: contains any number of different vertex labels
  - name: change the first edgelabel name
  - columns: may contain any number of columns(edge property keys)
    - [String]: Name of column
    - dataType: String, Long, Integer, and Date.
    - dateRange: default from 1970 to current time in default "dd-MMM-yyyy" format
      unless specified in the date Format
    - dateFormat: support JAVA SimpleDateFormat patterns. Default: "dd-MMM-yyyy"
    - composit: true or false
    - mixedIndex: any String (default should be "search")
- relations: Defines how the named edge relations can happen and number of occurrences.
  - left: the outV of an edge
  - right: the inV of an edge
  - supernode: nodes have many edges
    - vertices: n where n is the first n nodes of left
    - edges: additional edges are added to each of the n nodes

### Import CSV file to JanusGraph
JanusGraphBatchImporter is a tool that imports bulk data into JanusGraph using multiple CSV files for the data and json config files to define the schema and mapping between schema and data. It uses multiple workers in order to achieve full speed of import of the data.

JanusGraphBatchImporter depends on JanusGraph libraries and also commons-csv-1.4.jar

In order to use under Linux:
- Edit conf/batch_import.properties file to configure the importer
- Usage:
  `run.sh import <janusgraph-config-file> <data-files-directory> <schema.json> <data-mapping.json>`
  - `<janusgraph-config-file>`: Properties file with the JanuGraph configuration
  - `<data-files-directory>:` Relative directory where the data files are located
  - `<schema.json>:` JSON file defining the schema of the graph
  - `<data-mapping.json>`: mapping file defining the relationship between the CSV/s fields and the graph

- Example:
  ```
  ./run.sh import /root/janusgraph-v0.1.1/conf/janusgraph-cassandra-es.properties \
      /tmp /tmp/schema.json /tmp/datamapper.json
  ```
- Using different JanusGraph lib
  ```
  export JANUSGRAPH_HOME=/path-to-your-janusgraph-home
  ./run.sh import /root/path-to-your-janusgraph-home/conf/janusgraph-cassandra-es.properties \
      /tmp /tmp/schema.json /tmp/datamapper.json
  ```
