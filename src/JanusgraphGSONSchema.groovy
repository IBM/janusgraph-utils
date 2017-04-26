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
import org.janusgraph.core.Multiplicity
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.EdgeLabelMaker
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.JanusGraphSchemaType
import org.janusgraph.core.schema.VertexLabelMaker
import org.janusgraph.core.schema.JanusGraphManagement.IndexBuilder

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
 * A utility class to read GraphSON schema document and write to JanusGraph
 */
class JanusgraphGSONSchema {
    JanusGraph graph

    /**
     * Constructor of JansugraphGSONSchema object with the {@code graph}
     * @param graph a JanusGraph and write GraphSON schema into it
     */
    public JanusgraphGSONSchema(JanusGraph graph) {
        if (!graph) {
            throw new Exception("JanusGraph is null")
        }
        this.graph = graph
    }

    /**
     * Read the GraphSON schema document from {@code gsonSchemaFile}
     * and write to the JanusGraph
     * @param gsonSchemaFile GraphSON schema document and using
     *        IBM Graph GraphSON schema format.
     */
    public void readFile(String gsonSchemaFile) {
        File gsonFile = new File(gsonSchemaFile)
        ObjectNode root = null

        if (!gsonFile.exists()) {
            throw new Exception("file not found:" + gsonSchemaFile)
        }

        ObjectMapper mapper = new ObjectMapper()

        try {
            root = mapper.readTree(gsonFile)
        } catch (Exception e) {
            print "parse GSON failed: ${e.getMessage()}"
            return
        }

        JanusGraphManagement mgmt = graph.openManagement()

        if (root.has("propertyKeys")) {
            for (node in root.get("propertyKeys").asList()) {
                //use JSON ==> POJO in jackson-databind
                try {
                    mapper.convertValue(node, PropertyKeyBean.class)
                        .make(mgmt)
                } catch (Exception e) {
                    println "incorrect propertyKey format: ${e.getMessage()}"
                }
            }
        }

        if (root.has("vertexLabels")) {
            for (vertex in root.get("vertexLabels").asList()) {
                try {
                    mapper.convertValue(vertex, VertexLabelBean.class)
                        .make(mgmt)
                } catch (Exception e) {
                    println "incorrect vertex label format: ${e.getMessage()}"
                }
            }
        }

        if (root.has("edgeLabels")) {
            for (edge in root.get("edgeLabels").asList()) {
                try {
                    mapper.convertValue(edge, EdgeLabelBean.class)
                        .make(mgmt)
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }
        }

        if (root.has("vertexIndexes")) {
            for (vindex in root.get("vertexIndexes").asList()) {
                try {
                    mapper.convertValue(vindex, IndexBean.class)
                        .make(mgmt, true)
                } catch (Exception e) {
                    println "incorrect vetex index format ${e.getMessage()}"
                }
            }
        }

        if (root.has("edgeIndexes")) {
            for (eindex in root.get("edgeIndexes").asList()) {
                try {
                    mapper.convertValue(eindex, IndexBean.class)
                        .make(mgmt, false)
                } catch (Exception e) {
                    println "incorrect edge index format: ${e.getMessage()}"
                }
            }
        }

        if (root.has("vertexCentricIndexes")) {
            for (ecindex in root.get("vertexCentricIndexes").asList()) {
                try {
                    mapper.convertValue(ecindex, VertexCentricIndexBean.class)
                        .make(mgmt)
                } catch (Exception e) {
                    println "incorrect vertex-centric index format: ${e.getMessage()}"
                }
            }
        }

        mgmt.commit()
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
