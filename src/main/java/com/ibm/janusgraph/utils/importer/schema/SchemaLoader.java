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
package com.ibm.janusgraph.utils.importer.schema;

import com.ibm.janusgraph.utils.schema.JanusGraphSONSchema;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

public class SchemaLoader {

    public SchemaLoader() {
    }

    public void loadSchema(JanusGraph g, String schemaFile) throws Exception {
        JanusGraphSONSchema importer = new JanusGraphSONSchema(g);
        importer.readFile(schemaFile);
    }

    public static void main(String[] args) {

        if (null == args || args.length < 2) {
            System.err.println("Usage: SchemaLoader <janusgraph-config-file> <schema-file>");
            System.exit(1);
        }

        String configFile = args[0];
        String schemaFile = args[1];

        // use custom or default config file to get JanusGraph
        JanusGraph g = JanusGraphFactory.open(configFile);

        try {
            new SchemaLoader().loadSchema(g, schemaFile);
        } catch (Exception e) {
            System.out.println("Failed to import schema due to " + e.getMessage());
        } finally {
            g.close();
        }

    }

}
