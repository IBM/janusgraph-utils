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
gremlin> importer = new JanusgraphGSONSchema(graph)
==>JanusgraphGSONSchema@e0e22f5
gremlin> importer.readFile('schema.json')
```

