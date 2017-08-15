package com.ibm.janusgraph.utils.importer.schema;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.Order;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.databind.JsonNode;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.EdgeLabel;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.Multiplicity;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.JanusGraphManagement.IndexBuilder;
import org.janusgraph.core.schema.JanusGraphSchemaType;

import com.google.common.collect.ImmutableMap;

public class SchemaLoader {

	private JanusGraph graph;

	/**
	 * Create the data type mapping table here note: can't find the
	 * corresponding classes for: Decimal and Precision
	 */
	public static final Map<String, Class> sTypeMap = 
			ImmutableMap.<String, Class>builder()
			.put("String", String.class)
			.put("Character", Character.class)
			.put("Boolean", Boolean.class)
			.put("Byte", Byte.class)
			.put("Short", Short.class)
			.put("Integer", Integer.class)
			.put("Long", Long.class)
			.put("Float", Float.class)
			.put("Geoshape", Geoshape.class)
			.put("UUID", UUID.class)
			.put("Date", Date.class)
			.build();

	private Logger log = Logger.getLogger(SchemaLoader.class);

	/**
	 * Constructor of JansugraphGSONSchema object with the {@code graph}
	 * 
	 * @param graph
	 *            a JanusGraph and write GraphSON schema into it
	 */

	public SchemaLoader(JanusGraph graph) {
		if (graph == null) {
			// throw new Exception("JanusGraph is null");
		}
		this.graph = graph;
	}

	/**
     * Read the GraphSON schema document from {@code gsonSchemaFile}
     * and write to the JanusGraph
     * @param gsonSchemaFile GraphSON schema document and using
     *        IBM Graph GraphSON schema format.
     */
    public void loadFile(String gsonSchemaFile) throws Exception {
        File gsonFile = new File(gsonSchemaFile);
        JsonNode root = null;

        if (!gsonFile.exists()) {
            throw new Exception("file not found:" + gsonSchemaFile);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            root = mapper.readTree(gsonFile);
        } catch (Exception e) {
            throw new Exception("can't parse GSON file:" + e.getMessage());
        }

        JanusGraphManagement mgmt = graph.openManagement();

        if (root.has("propertyKeys")) {
            for (JsonNode node: root.findValue("propertyKeys")) {
            		//get("propertyKeys").asList()) {
                String name = node.get("name").asText();
                if (mgmt.containsPropertyKey(name)) {
                    log.info("property: "+ name + " exists");
                } else {
                    try {
                        mgmt.makePropertyKey(node.get("name").asText())
                            .dataType(sTypeMap.get(node.get("dataType").asText()))
                            .cardinality(Cardinality.valueOf(node.get("cardinality").asText())).make();
                        log.info("propertyKey:" + name + " creation is done");
                    } catch (Exception e) {
                        log.error("can't create property:" + name + ", " + e.getMessage());
                    }
                }
            }
        }

        if (root.has("vertexLabels")) {
            for (JsonNode vertex: root.findValue("vertexLabels")) {
                String name = vertex.get("name").asText();
                if (mgmt.containsVertexLabel(name)) {
                    log.info("vertex: " + name + " exists");
                } else {
                    try {
                        mgmt.makeVertexLabel(name).make();
                        log.info("vertex: " + name + " creation is done");
                    } catch (Exception e) {
                        log.error("can't create vertex: " + name + ", " + e.getMessage());
                    }
                }
            }
        }

        if (root.has("edgeLabels")) {
            for (JsonNode edge : root.findValue("edgeLabels")) {
                String name = edge.get("name").asText();
                if (mgmt.containsEdgeLabel(name)) {
                    log.info("edge: " + name + " exists");
                } else {
                    try {
                        mgmt.makeEdgeLabel(name).multiplicity(Multiplicity.valueOf(edge.get("multiplicity").asText())).make();
                        log.info("edge: " + name + " creation is done");
                    } catch (Exception e) {
                        log.error("cant't create edge: " + name + ", "  + e.getMessage());
                    }
                }
            }
        }

        if (root.has("vertexIndexes")) {
            for (JsonNode vindex : root.findValue("vertexIndexes")) {
                makeIndex(mgmt, vindex, true);
            }
        }

        if (root.has("edgeIndexes")) {
            for (JsonNode eindex : root.findValue("edgeIndexes")) {
                makeIndex(mgmt, eindex, false);
            }
        }

        if (root.has("vertexCentricIndexes")) {
            for (JsonNode ecindex : root.findValue("vertexCentricIndexes")) {
                makeVCIndex(mgmt, ecindex);
            }
        }

        mgmt.commit();
    }

	/**
     * Create vertex/edge index based on the {@code node}. If {@code isVertexIndex} equeals true
     * a vertex index is created. Otherwise, create edge index
     *
     * @param mgmt is used to call the {@code buildIndex()}
     * @param node contains "name", "propertyKeys", "unique", "composite" and "mixedIndex" properties
     * @param isVertexIndex create a vertex index or edge index
     */
    void makeIndex(JanusGraphManagement mgmt, JsonNode node, boolean isVertexIndex) {
        if (!node.has("name")) {
            log.info("missing the 'name' property, not able to create an index");
            return;
        }

        String name = node.get("name").asText();
        if (mgmt.containsGraphIndex(name)) {
            log.info("index: " + name + " exists");
            return;
        }

        if (!node.has("propertyKeys")) {
            log.info("missing the 'propertyKeys property, not able to create an index");
            return;
        }

        IndexBuilder ib = mgmt.buildIndex(node.get("name").asText(), isVertexIndex ? Vertex.class : Edge.class);
        for (JsonNode property : node.findValue("propertyKeys")) {
            ib.addKey(mgmt.getPropertyKey(property.asText()));
        }

        if (node.has("unique") && node.get("unique").asBoolean()) {
            ib.unique();
        }

        //indexOnly
        if (node.has("only")) {
            JanusGraphSchemaType key = null;
            String onlyName = node.get("only").asText();
            if (isVertexIndex) {
                key = mgmt.getVertexLabel(onlyName);
            } else {
                key = mgmt.getEdgeLabel(onlyName);
            }

            if (key == null) {
                log.info(onlyName + " doesn't exist, skip only property");
            } else {
                ib.indexOnly(key);
            }
        }

        if (node.has("composite") && node.get("composite").asBoolean()) {
            ib.buildCompositeIndex();
        }

        if (node.has("mixedIndex")) {
            ib.buildMixedIndex(node.get("mixedIndex").asText());
        }

        log.info("index: " + name + " creation is done");
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
    void makeVCIndex(JanusGraphManagement mgmt, JsonNode node) {
        if (!node.has("name")) {
            log.info("missing 'name' property, not able to create an index");
            return;
        }

        if (!node.has("edge")) {
            log.info("vertex-centric index needs 'edge' property to specify a edge label");
            return;
        }

        String edgeName = node.get("edge").asText();
        EdgeLabel elabel = mgmt.getEdgeLabel(edgeName);

        if (elabel == null) {
            log.info("edge: " + edgeName + " doesn't exist");
            return;
        }

        String name = node.get("name").asText();
        if (mgmt.containsRelationIndex(elabel, name)) {
            log.info("index: " + name + " exists");
            return;
        }

        if (!node.has("propertyKeys")) {
            log.info("missing 'propertyKeys property, not able to create an index");
            return;
        }

        JsonNode properties = node.findValue("propertyKeys");
        if (properties == null || properties.size() == 0) {
            log.info("index: " + name + " needs 'propertyKeys' properties");
            return;
        }

        Direction dir = node.has("direction") ? Direction.valueOf(node.get("direction").asText()) : Direction.BOTH;
        Order order = node.has("order") ? Order.valueOf(node.get("order").asText()) : Order.incr;

        PropertyKey[] keys = new PropertyKey[properties.size()];
        int counter = 0;
        for (JsonNode property : properties) {
            PropertyKey key = mgmt.getPropertyKey(property.asText());
            if (key == null) {
                log.info("propertyKey:${property.asText()} doesn't exist, can't create ${name} vertex-centric index");
                return;
            }
            keys[counter++] = mgmt.getPropertyKey(property.asText());
        }

        mgmt.buildEdgeIndex(elabel, name, dir, order, keys);

        log.info("vertex-centric index: ${name} creation is done");
    }
}
