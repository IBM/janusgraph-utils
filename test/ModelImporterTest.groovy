:load files/src/JanusGraphModelImporter.groovy

graph = JanusGraphFactory.open('conf/janusgraph-berkeleyje.properties')
writeGraphSONModel(graph, 'files/samples/schema.json')
