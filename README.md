# Gremlin groovy utility script

Provide several utility scripts that could be used in gremlin console of
Janusgraph, including:

- JanusgraphGSONSchema: import GraphSON schema document into JanusGraph


### JanusGraphModelImporter

This utility read GraphSON model document and write to JanusGraph.
Please see the sample GraphSON model document under `samples` directory.

Usage:
```
gremlin> graph = JanusGraphFactory.open('conf/janusgraph-cassandra-embedded-es.properties')
==>standardjanusgraph[embeddedcassandra:[127.0.0.1]]
gremlin> :load JanusGraphModelImporter.groovy
......
......
==>true
==>true
gremlin> writeGraphSONModel(graph, 'schema.json')
```

#### How to load the groovy script in gremlin console
Use the following command to load the utility groovy script into gremlin console:

```
gremlin> :load <JanusGraphModelImporter.groovy>
```

Use the `:load` command and specify the groovy script location to load the groovy script.

#### How to load the groovy scripto into gremlin server
Modify the gremlin server configuration file `gremlin-server.yaml` under `janusgraph/conf/gremlin-server` directory. Add the groovy script into
`scriptEngines.gremlin-groovy.scripts`. Its value is an array and contains
all groovy scripts that would be loaded into gremlin-groovy script engine when
gremlin server starts. Here is a sample configruation of `gremlin-server.yaml` (partial):

```(YAML)
scriptEngines: {
  gremlin-groovy: {
    imports: [java.lang.Math],
    staticImports: [java.lang.Math.PI],
    scripts: [scripts/empty-sample.groovy, scripts/JanusGraphModelImporter.groovy]}}
```
#### APIs
Once you load the JanusgraphGSONSchema.groovy into gremlin console, you can use the following APIs:
- **JanusGraphSONModel.parse(file)**:
  -  file: a string which points to the schema graphSON document  
  
  It parses and retunrs a schema bean object which contains the whole settings of the schema GSON

- **writeGraphSONModel(janusgraph, file)**:
  - janusgraph: a JanusGraph instance
  - file: a string which points to the schema graphSON document
  
  It parses the schema GraphSON document and write the definitions of properties, vertices and edges into the janusgraph database.
  
- **updateCompositeIndexState(janusgraph, indexName, newState)**:
  - janusgraph: a JanusGraph instance
  - indexName: the composite index name
  - newState: the new index state, could be:
    - SchemaAction.DISABLE_INDEX
    - SchemaAction.ENABLE_INDEX
    - SchemaAction.REGISTER_INDEX
    - SchemaAction.REINDEX
    - SchemaAction.REMOVE_INDEX

   It changes the index from its original state into new state if the state transition is valid.

#### The graphSON document
It contains the defintions of properties, vertices and edges:

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

It's an array and contains the definition of the properties. Each property is defined in an object with the following format:
```
{
    "name": "<propertyName>",
    "dataType": "<String | Long | Character | Boolean | Byte | Short | Integer | Long | Float | Geoshape | UUID | Date>",
    "cardinality": "<SINGLE|LIST|SET>"
}
```

**vertexLabels**

It's an array and contains the definition of the vertices. Each vertex is
defined in an object with the following format:

```
{
    "name": "<vertex label>",
    "partition": false|true,    //optional
    "useStatic": false|true     //optional
}
```

**edgeLabels**

It's an array and contains the definition of the edges. Each edge is defined
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

It's an array and contains the definition of vertex indices. Each vertex index
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

It's an array and contains the definition of edge indices. Each edge index
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

It's an array and contains the definition of vertex-centric indices. Each
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
