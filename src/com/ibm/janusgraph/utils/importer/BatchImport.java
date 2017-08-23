/*******************************************************************************
 *   Copyright 2017 IBM Corp. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ibm.janusgraph.utils.importer;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import com.ibm.janusgraph.utils.importer.dataloader.DataLoader;
import com.ibm.janusgraph.utils.importer.schema.SchemaLoader;

public class BatchImport {

    public static void main(String args[]) throws Exception {

        if (null == args || args.length < 4) {
            System.err.println(
                    "Usage: BatchImport <janusgraph-config-file> <data-files-directory> <schema.json> <data-mapping.json>");
            System.exit(1);
        }

        JanusGraph graph = JanusGraphFactory.open(args[0]);
        new SchemaLoader().loadSchema(graph, args[2]);
        new DataLoader(graph).loadVertex(args[1], args[3]);
        new DataLoader(graph).loadEdges(args[1], args[3]);
        graph.close();
    }
}
