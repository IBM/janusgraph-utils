import org.apache.tinkerpop.gremlin.process.traversal.Order
import org.apache.tinkerpop.gremlin.structure.Direction
import org.apache.tinkerpop.gremlin.structure.Edge
import org.apache.tinkerpop.gremlin.structure.Vertex
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper
import org.apache.tinkerpop.shaded.jackson.databind.node.ObjectNode
import org.apache.tinkerpop.shaded.jackson.databind.node.TextNode
import org.janusgraph.core.Cardinality
import org.janusgraph.core.EdgeLabel
import org.janusgraph.core.JanusGraph
import org.janusgraph.core.JanusGraphTransaction
import org.janusgraph.core.Multiplicity
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.EdgeLabelMaker
import org.janusgraph.core.schema.JanusGraphIndex
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.JanusGraphSchemaType
import org.janusgraph.core.schema.SchemaAction
import org.janusgraph.core.schema.VertexLabelMaker
import org.janusgraph.core.schema.JanusGraphManagement.IndexBuilder
import org.janusgraph.graphdb.database.StandardJanusGraph

/**
 * Janusgraph datatype mapping table
 */
class TypeMap {
    public static HashMap<String, Class> MAP = new HashMap<String, Class>()
    /**
     * Create the data type mapping table here
     * note: can't find the corresponding classes for:
     *       Decimal and Precision
     */
    static {
        MAP.put("String", String.class)
        MAP.put("Character", Character.class)
        MAP.put("Boolean", Boolean.class)
        MAP.put("Byte", Byte.class)
        MAP.put("Short", Short.class)
        MAP.put("Integer", Integer.class)
        MAP.put("Long", Long.class)
        MAP.put("Float", Float.class)
        MAP.put("Geoshape", Geoshape.class)
        MAP.put("UUID", UUID.class)
        MAP.put("Date", Date.class)
    }
}
/**
 * JavaBean for propertyKey
 */
class PropertyKeyBean {
    String name = null
    String dataType = null
    String cardinality = null

    /**
     * Create the propertyKey
     * @param mgmt
     */
    public void make(JanusGraphManagement mgmt) {
        if (name == null) {
            println "need \"name\" property to define a propertyKey"
        } else if (mgmt.containsPropertyKey(name)) {
            println "property: ${name} exists"
        } else {
            try {
                mgmt.makePropertyKey(name)
                    .dataType(TypeMap.MAP.get(dataType))
                    .cardinality(Cardinality.valueOf(cardinality)).make()
                println "propertyKey:${name} creation is done"
            } catch (Exception e) {
                println "can't create property:${name}, ${e.getMessage()}"
            }
        }
    }
}

/**
 * Javabean for vertexLabel
 *
 */
class VertexLabelBean {
    String name = null
    boolean partition = false
    boolean useStatic = false

    /**
     * Create the vertex
     * @param mgmt
     */
    public void make(JanusGraphManagement mgmt) {
        if (name == null) {
            println "need \"name\" property to define a vertex"
        } else if (mgmt.containsVertexLabel(name)) {
            println "vertex: ${name} exists"
        } else {
            try {
                VertexLabelMaker maker = mgmt.makeVertexLabel(name)
                if (partition) maker.partition()
                if (useStatic) maker.setStatic()
                maker.make()
                println "vertex:${name} creation is done"
            } catch (Exception e) {
                println "can't create vertex: ${name}, ${e.getMessage()}"
            }
        }
    }
}

/**
 * JavaBean for EdgeLabel
 *
 */
class EdgeLabelBean {
    String name = null
    String multiplicity = "MULTI"
    List<String> signatures
    boolean unidirected = false

    public void make(JanusGraphManagement mgmt) {
        if (name == null) {
            println "need \"name\" property to define a label"
        } else if (mgmt.containsEdgeLabel(name)) {
            println "edge: ${name} exists"
        } else {
            try {
                EdgeLabelMaker maker = mgmt.makeEdgeLabel(name).multiplicity(Multiplicity.valueOf(multiplicity))
                if (signatures && signatures.size()) {
                    PropertyKey[] keys = new PropertyKey[signatures.size()]
                    int counter = 0;
                    for (key in signatures) {
                        keys[counter++] = mgmt.getPropertyKey(key);
                    }
                    maker.signature(keys)
                }
                if (unidirected) {
                    maker.unidirected()
                }
                maker.make()
                println "edge: ${name} creation is done"
            } catch (Exception e) {
                println "cant't create edge: ${name}, ${e.getMessage()}"
            }
        }
    }
}

/**
 * JavaBean for Index, both vertex and edge index
 *
 */
class IndexBean {
    String name = null
    List<String> propertyKeys
    boolean composite = true
    boolean unique = false
    String indexOnly = null
    String mixedIndex = null

    /**
     * Create vertex/edge index based on the {@code node}. If {@code isVertexIndex} equals true
     * a vertex index is created. Otherwise, create edge index
     *
     * @param mgmt is used to call the {@code buildIndex()}
     * @param isVertexIndex create a vertex index or edge index
     */
    public void make(JanusGraphManagement mgmt, boolean isVertexIndex) {
        if (name == null) {
            println "missing the 'name' property, not able to create an index"
            return
        }

        if (mgmt.containsGraphIndex(name)) {
            println "index: ${name} exists"
            return
        }

        if (propertyKeys == null || propertyKeys.size() == 0) {
            println "missing the 'propertyKeys property, not able to create an index"
            return
        }

        IndexBuilder ib = mgmt.buildIndex(name, isVertexIndex ? Vertex.class : Edge.class)
        for (property in propertyKeys) {
            ib.addKey(mgmt.getPropertyKey(property))
        }

        if (isVertexIndex && unique) {
            ib.unique()
        }

        //indexOnly
        if (indexOnly != null) {
            JanusGraphSchemaType key = null
            if (isVertexIndex) {
                key = mgmt.getVertexLabel(indexOnly)
            } else {
                key = mgmt.getEdgeLabel(indexOnly)
            }

            if (key == null) {
                println "${indexOnly} doesn't exist, skip only property"
            } else {
                ib.indexOnly(key)
            }
        }

        if (composite) {
            ib.buildCompositeIndex()
        }

        if (mixedIndex != null) {
            ib.buildMixedIndex(mixedIndex)
        }

        println "index: ${name} creation is done"
    }
}

/**
 * represent the individual vertex-centric index object in the "vertexCentricIndexes"
 * list
 */
class VertexCentricIndexBean {
    String name = null
    String edge = null
    List<String> propertyKeys
    String order = "incr"
    String direction = "BOTH"

    /**
     * Create vertex-centric index
     *
     * {
     *     "name": "indexName",
     *     "edge": "edgeLebel",
     *     "propertyKeys": [ "propertyKey1", "propertyKey2" ],
     *     "order": "incr|decr",
     *     "direction": "BOTH|IN|OUT"
     * }
     */
    public void make(JanusGraphManagement mgmt) {
        if (name == null) {
            println "missing 'name' property, not able to create a vertex-centric index"
            return
        }

        if (edge == null) {
            println "vertex-centric index needs 'edge' property to specify a edge label"
            return
        }

        EdgeLabel elabel = mgmt.getEdgeLabel(edge)

        if (elabel == null) {
            println "edge: ${edge} doesn't exist"
            return
        }

        if (mgmt.containsRelationIndex(elabel, name)) {
            println "vertex-centric index: ${name} exists"
            return
        }

        if (propertyKeys == null || propertyKeys.size() == 0) {
            println "missing 'propertyKeys property, not able to create an index"
            return
        }

        PropertyKey[] keys = new PropertyKey[propertyKeys.size()]
        int counter = 0
        for (property in propertyKeys) {
            PropertyKey key = mgmt.getPropertyKey(property)
            if (key == null) {
                println "propertyKey:${property} doesn't exist, can't create ${name} vertex-centric index"
                return
            }
            keys[counter++] = mgmt.getPropertyKey(property)
        }

        mgmt.buildEdgeIndex(elabel, name, Direction.valueOf(direction), Order.valueOf(order), keys)

        println "vertex-centric index: ${name} creation is done"
    }
}

/**
 * represents the whole GraphSON document. It contains:
 * - propertyKeys
 * - vertexLabels
 * - edgeLabels
 * - vertexIndexes
 * - edgeIndexes
 * - vertexCentricIndexes
 */
class GraphModel {
    List<PropertyKeyBean> propertyKeys;
    List<VertexLabelBean> vertexLabels;
    List<EdgeLabelBean> edgeLabels;
    List<IndexBean> vertexIndexes;
    List<IndexBean> edgeIndexes;
    List<VertexCentricIndexBean> vertexCentricIndexes

    /**
     * use the {@code mgmt} to create the schema
     * @param mgmt
     */
    public void make(JanusGraphManagement mgmt) {
        //create properties
        for (property in propertyKeys) {
            property.make(mgmt)
        }

        //create vertex labels
        for (vertex in vertexLabels) {
            vertex.make(mgmt)
        }

        //create edge labels

        for (edge in edgeLabels) {
            edge.make(mgmt)
        }

        //create v indexes
        for (vindex in vertexIndexes) {
            vindex.make(mgmt, true)
        }

        //create e indexes
        for (eindex in edgeIndexes) {
            eindex.make(mgmt, false)
        }

        //create vc indexes
        for (vcindex in vertexCentricIndexes) {
            vcindex.make(mgmt)
        }

        mgmt.commit()
    }
}

/**
 * A utility class to read GraphSON model document and write to JanusGraph
 */
class JanusGraphSONModel {
    StandardJanusGraph graph

    /**
     * Constructor of JanusGraphSONModel object with the {@code graph}
     * @param graph a JanusGraph and write GraphSON schema into it
     */
    public JanusGraphSONModel(JanusGraph graph) {
        if (!graph) {
            throw new Exception("JanusGraph is null")
        }
        this.graph = graph
    }

    /**
     * Read the GraphSON schema document from {@code modelFile}
     * and write to the JanusGraph
     * @param modelFile GraphSON model document and using
     *        IBM Graph GraphSON format.
     */
    public void readFile(String modelFile) {
        JanusGraphManagement mgmt = graph.openManagement()

        try {
            parse(modelFile)
                .make(mgmt)
            rollbackTxs(graph)
        } catch (Exception e) {
            print "parse GSON failed: ${e.getMessage()}"
        }
    }

    /**
     * Commit all running transactions upon the graph
     * @param graph
     * @return
     */
    public static boolean commitTxs(JanusGraph graph) {
        StandardJanusGraph sgraph = graph
        try {
            Set<JanusGraphTransaction>txs = sgraph.getOpenTransactions()
            //commit all running transactions
            Iterator<JanusGraphTransaction> iter = txs.iterator()
            while (iter.hasNext()) {
                iter.next().commit()
            }
        } catch (Exception e) {
            //ignore
            return false
        }
        return true
    }

    /**
     * Rollback back all running transaction upon the graph
     * @param graph
     * @return
     */
    public static boolean rollbackTxs(JanusGraph graph) {
        StandardJanusGraph sgraph = graph
        try {
            Set<JanusGraphTransaction>txs = sgraph.getOpenTransactions()
            //commit all running transactions
            Iterator<JanusGraphTransaction> iter = txs.iterator()
            while (iter.hasNext()) {
                iter.next().rollback()
            }
        } catch (Exception e) {
            //ignore
            return false
        }
        return true
    }

    /**
     * Parse the GraphSON document and return a GraphModel object
     * if parse successes
     * @param gsonSchemaFile
     * @return
     */
    public static GraphModel parse(String modelFile) {
        File gsonFile = new File(modelFile)

        if (!gsonFile.exists()) {
            throw new Exception("file not found:" + modelFile)
        }

        ObjectMapper mapper = new ObjectMapper()
        return mapper.readValue(gsonFile, GraphModel.class)
    }

    void make(List<ObjectNode> nodes, String name, Closure check, Closure exist, Closure create) {
        for (node in nodes) {
            String nameStr = node.get(name).asText()
            if (check.call(nameStr)) {
                exist.call(nameStr)
            } else {
                create.call(nameStr, node)
            }
        }
    }
}

/**
 * parse the GraphSON model in {@code schema} and write to
 * {@code graph}
 * @param graph a valid JanusGraph instance
 * @param schema GraphSON model document location
 * @return
 */
def writeGraphSONModel(graph, schema) {
    JanusGraphSONModel importer = new JanusGraphSONModel(graph)
    importer.readFile(schema)
}

def updateCompositeIndexState(JanusGraph graph, String name, SchemaAction newState) {
    JanusGraphManagement mgmt = graph.openManagement()
    JanusGraphIndex index = mgmt.getGraphIndex(name)
    if (index == null) {
        print "${name} index doesn't exist"
        return
    }
    mgmt.updateIndex(index, newState)
    mgmt.commit()
    JanusGraphSONModel.rollbackTxs(graph)
}
