package com.ibm.janusgraph.utils.importer;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import com.ibm.janusgraph.utils.importer.dataloader.DataLoader;
import com.ibm.janusgraph.utils.importer.schema.SchemaLoader;

public class BatchImport {

    public static void main(String args[]) throws Exception {

        if (null == args || args.length < 4) {
            System.err.println("Usage: BatchImport <janusgraph-config-file> <data-files-directory> <schema.json> <data-mapping.json>");
            System.exit(1);
        }

        JanusGraph graph = JanusGraphFactory.open(args[0]);
        new SchemaLoader(graph).loadFile(args[2]);
        new DataLoader(graph).loadVertex(args[1], args[3]);
        new DataLoader(graph).loadEdges(args[1], args[3]);
        graph.close();
    }
}
