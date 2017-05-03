# Gremlin groovy utility script

Provide several utility scripts that could be used in gremlin console of
Janusgraph, including:

- JanusgraphGSONSchema: import GraphSON schema document into JanusGraph


### JanusgraphGSONSchema

This utility read GraphSON scheme document and write to JanusGraph.
Please see the sample GraphSON scheme document under `samples` directory.

Usage:
```
gremlin> graph = JanusGraphFactory.open('conf/janusgraph-cassandra-embedded-es.properties')
==>standardjanusgraph[embeddedcassandra:[127.0.0.1]]
gremlin> :load JanusgraphGSONSchema.groovy
......
......
==>true
==>true
gremlin> writeGraphSONSchema(graph, 'schema.json')
```

#### schema GSON
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

#### schema for propertyKey

The schema of the object inside the array of "propertyKeys":
```
{
    "name": "<propertyName>",
    "dataType": "<String | Long | Character | Boolean | Byte | Short | Integer | Long | Float | Geoshape | UUID | Date>",
    "cardinality": "<SINGLE|LIST|SET>"
}
```

#### schema for VertexLabel

The schema of the object inside the array of "vertexLabels":
```
{
    "name": "<vertex label>",
    "partition": false|true,    //optional
    "useStatic": false|true     //optional
}
```

#### schema for EdgeLabel

The schema of the object inside the array of "edgeLabels":
```
{
    "name": "<edge label>",
    "multiplicity": "<MULTI | SIMPLE | ONE2MANY | MANY2ONE | ONE2ONE>"
    "signatures": [ "<property key name>" ]
    "unidirected" : true|false    //default value is false, means directed
}
```

#### schema for vertexIndexes

The schema of the object inside of the array of "vertexIndexes":
```
{
    "name": "<index name>",
    "propertyKeys": ["<property key name>"],
    "composite": true|false,
    "unique": true|false,
    "indexOnly": "<vertex label>"
}
```

#### schema for edgeIndexes

The schema of the object inside of the array of "edgeIndexes":
```
{
    "name": "<index name>",
    "propertyKeys": ["<property key name>"],
    "composite": true|false,
    "indexOnly": "<edge label>"
}
```

#### schema for vertexCentricIndexes

The schema of the object inside of the array of "vertexCentricIndexes":
```
{
    "name": "<index name>",
    "propertyKeys": ["<property key>"],
    "edge": "<edge label>",
    "direction": "BOTH|IN|OUT",
    "order": "incr|decr"
}
```
