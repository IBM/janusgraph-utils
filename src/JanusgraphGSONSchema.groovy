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
import org.janusgraph.core.PropertyKey
import org.janusgraph.core.attribute.Geoshape
import org.janusgraph.core.schema.JanusGraphManagement
import org.janusgraph.core.schema.JanusGraphSchemaType
import org.janusgraph.core.schema.JanusGraphManagement.IndexBuilder

/**
 * A utility class to read GraphSON schema document and write to JanusGraph
 */
class JanusgraphGSONSchema {
    JanusGraph graph
    File gsonFile
    static HashMap<String, Class> sTypeMap = new HashMap()

    /**
     * Create the data type mapping table here
     * note: can't find the corresponding classes for:
     *       Decimal and Precision
     */
    static {
        sTypeMap.put("String", String.class)
        sTypeMap.put("Character", Character.class)
        sTypeMap.put("Boolean", Boolean.class)
        sTypeMap.put("Byte", Byte.class)
        sTypeMap.put("Short", Short.class)
        sTypeMap.put("Integer", Integer.class)
        sTypeMap.put("Long", Long.class)
        sTypeMap.put("Float", Float.class)
        sTypeMap.put("Geoshape", Geoshape.class)
        sTypeMap.put("UUID", UUID.class)
        sTypeMap.put("Date", Date.class)

    }

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
        gsonFile = new File(gsonSchemaFile)
        ObjectNode root = null

        if (!gsonFile.exists()) {
            throw new Exception("file not found:" + gsonSchemaFile)
        }

        try {
            ObjectMapper mapper = new ObjectMapper()
            root = mapper.readTree(gsonFile)
        } catch (Exception e) {
            throw new Exception("can't parse GSON file:" + e.getMessage())
        }

        JanusGraphManagement mgmt = graph.openManagement()

        if (root.has("propertyKeys")) {
            for (node in root.get("propertyKeys").asList()) {
                String name = node.get("name").asText()
                if (mgmt.containsPropertyKey(name)) {
                    println "property: ${name} exists"
                } else {
                    try {
                        mgmt.makePropertyKey(node.get("name").asText())
                            .dataType(sTypeMap.get(node.get("dataType").asText()))
                            .cardinality(Cardinality.valueOf(node.get("cardinality").asText())).make()
                        println "propertyKey:${name} creation is done"
                    } catch (Exception e) {
                        println "can't create property:${name}, ${e.getMessage()}"
                    }
                }
            }
        }

        if (root.has("vertexLabels")) {
            for (vertex in root.get("vertexLabels").asList()) {
                String name = vertex.get("name").asText()
                if (mgmt.containsVertexLabel(name)) {
                    println "vertex: ${name} exists"
                } else {
                    try {
                        mgmt.makeVertexLabel(name).make()
                        println "vertex:${name} creation is done"
                    } catch (Exception e) {
                        println "can't create vertex: ${name}, ${e.getMessage()}"
                    }
                }
            }
        }

        if (root.has("edgeLabels")) {
            for (edge in root.get("edgeLabels").asList()) {
                String name = edge.get("name").asText()
                if (mgmt.containsEdgeLabel(name)) {
                    println "edge: ${name} exists"
                } else {
                    try {
                        mgmt.makeEdgeLabel(name).make()
                        println "edge:${name} creation is done"
                    } catch (Exception e) {
                        println "cant't create edge: ${name}, ${e.getMessage()}"
                    }
                }
            }
        }

        if (root.has("vertexIndexes")) {
            for (vindex in root.get("vertexIndexes").asList()) {
                makeIndex(mgmt, vindex, true)
            }
        }

        if (root.has("edgeIndexes")) {
            for (eindex in root.get("edgeIndexes").asList()) {
                makeIndex(mgmt, eindex, false)
            }
        }

        if (root.has("vertexCentricIndexes")) {
            for (ecindex in root.get("vertexCentricIndexes").asList()) {
                makeVCIndex(mgmt, ecindex)
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

    /**
     * Create vertex/edge index based on the {@code node}. If {@code isVertexIndex} equeals true
     * a vertex index is created. Otherwise, create edge index
     *
     * @param mgmt is used to call the {@code buildIndex()}
     * @param node contains "name", "propertyKeys", "unique", "composite" and "mixedIndex" properties
     * @param isVertexIndex create a vertex index or edge index
     */
    void makeIndex(JanusGraphManagement mgmt, ObjectNode node, boolean isVertexIndex) {
        if (!node.has("name")) {
            println "missing the 'name' property, not able to create an index"
            return
        }

        String name = node.get("name").asText()
        if (mgmt.containsGraphIndex(name)) {
            println "index: ${name} exists"
            return
        }

        if (!node.has("propertyKeys")) {
            println "missing the 'propertyKeys property, not able to create an index"
            return
        }

        IndexBuilder ib = mgmt.buildIndex(node.get("name").asText(), isVertexIndex ? Vertex.class : Edge.class)
        List<TextNode> properties = node.get("propertyKeys").asList()
        for (property in properties) {
            ib.addKey(mgmt.getPropertyKey(property.asText()))
        }

        if (node.has("unique") && node.get("unique").asBoolean()) {
            ib.unique()
        }

        //indexOnly
        if (node.has("only")) {
            JanusGraphSchemaType key = null
            String onlyName = node.get("only").asText()
            if (isVertexIndex) {
                key = mgmt.getVertexLabel(onlyName)
            } else {
                key = mgmt.getEdgeLabel(onlyName)
            }

            if (key == null) {
                println "${onlyName} doesn't exist, skip only property"
            } else {
                ib.indexOnly(key)
            }
        }

        if (node.has("composite") && node.get("composite").asBoolean()) {
            ib.buildCompositeIndex()
        }

        if (node.has("mixedIndex")) {
            ib.buildMixedIndex(node.get("mixedIndex").asText())
        }

        println "index: ${name} creation is done"
    }

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
    void makeVCIndex(JanusGraphManagement mgmt, ObjectNode node) {
        if (!node.has("name")) {
            println "missing 'name' property, not able to create an index"
            return
        }

        if (!node.has("edge")) {
            println "vertex-centric index needs 'edge' property to specify a edge label"
            return
        }

        String edgeName = node.get("edge").asText()
        EdgeLabel elabel = mgmt.getEdgeLabel(edgeName)

        if (elabel == null) {
            println "edge: ${edgeName} doesn't exist"
            return
        }

        String name = node.get("name").asText()
        if (mgmt.containsRelationIndex(elabel, name)) {
            println "index: ${name} exists"
            return
        }

        if (!node.has("propertyKeys")) {
            println "missing 'propertyKeys property, not able to create an index"
            return
        }

        List<TextNode> properties = node.get("propertyKeys").asList()
        if (properties == null || properties.size() == 0) {
            println "index: ${name} needs 'propertyKeys' properties"
            return
        }

        Direction dir = node.has("direction") ? Direction.valueOf(node.get("direction").asText()) : Direction.BOTH
        Order order = node.has("order") ? Order.valueOf(node.get("order").asText()) : Order.incr

        PropertyKey[] keys = new PropertyKey[properties.size()]
        int counter = 0
        for (property in properties) {
            PropertyKey key = mgmt.getPropertyKey(property.asText())
            if (key == null) {
                println "propertyKey:${property.asText()} doesn't exist, can't create ${name} vertex-centric index"
                return
            }
            keys[counter++] = mgmt.getPropertyKey(property.asText())
        }

        mgmt.buildEdgeIndex(elabel, name, dir, order, keys)

        println "vertex-centric index: ${name} creation is done"
    }
}
