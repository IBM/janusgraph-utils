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
package com.ibm.janusgraph.utils.importer.vertex;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.JanusGraphVertex;

import com.ibm.janusgraph.utils.importer.util.BatchHelper;
import com.ibm.janusgraph.utils.importer.util.Config;
import com.ibm.janusgraph.utils.importer.util.Constants;
import com.ibm.janusgraph.utils.importer.util.Worker;
import com.ibm.janusgraph.utils.importer.util.WorkerListener;

public class VertexLoaderWorker extends Worker {
    private final UUID myID = UUID.randomUUID();

    private final int COMMIT_COUNT;

    private final String defaultVertexLabel;
    private String vertexLabelFieldName;
    private JanusGraphTransaction graphTransaction;
    private long currentRecord;

    private static final Logger log = LoggerFactory.getLogger(VertexLoaderWorker.class);

    public VertexLoaderWorker(final Iterator<Map<String, String>> records, final Map<String, Object> propertiesMap,
            final JanusGraph graph) {
        super(records, propertiesMap, graph);

        this.currentRecord = 0;
        this.defaultVertexLabel = (String) propertiesMap.get(Constants.VERTEX_LABEL_MAPPING);
        this.vertexLabelFieldName = null;

        COMMIT_COUNT = Config.getConfig().getVertexRecordCommitCount();

        if (propertiesMap.values().contains(Constants.VERTEX_LABEL_MAPPING)) {
            // find the vertex
            for (String propName : propertiesMap.keySet()) {
                if (Constants.VERTEX_LABEL_MAPPING.equals(propertiesMap.get(propName))) {
                    this.vertexLabelFieldName = propName;
                    break;
                }
            }
        }
    }

    private void acceptRecord(Map<String, String> record) throws Exception {
        String vertexLabel = defaultVertexLabel;
        if (vertexLabelFieldName != null) {
            vertexLabel = record.get(vertexLabelFieldName);
        }
        JanusGraphVertex v = graphTransaction.addVertex(vertexLabel);
		// Removes the vertex in catch block if uniqueness constraint fails for a key and value 
        try {
        	// set the properties of the vertex
        	for (String column : record.keySet()) {
        		String value = record.get(column);
        		// If value="" or it is a vertex label then skip it
        		if (value == null || value.length() == 0 || column.equals(vertexLabelFieldName))
        			continue;

        		String propName = (String) getPropertiesMap().get(column);
        		if (propName == null) {
        			// log.info("Thread " + myID + ".Cannot find property name for
        			// column " + column
        			// + " in the properties map. Using the column name as
        			// default.");
        			continue;
        			// propName = column;
        		}

        		// Update property only if it does not exist already
        		if (!v.properties(propName).hasNext()) {
        			// TODO Convert properties between data types. e.g. Date
        			Object convertedValue = BatchHelper.convertPropertyValue(value,    							
        					graphTransaction.getPropertyKey(propName).dataType());
        			v.property(propName, convertedValue);
        		}
        	} 
        } catch (Exception error) {
        	log.error("Error in acceptRecord:: Vertex:: "+vertexLabel+" :: "+error);
        	v.remove();
        	log.info("acceptRecord::Vertex Removed");
        	return;
        }

        if (currentRecord % COMMIT_COUNT == 0) {
            graphTransaction.commit();
            graphTransaction.close();
            graphTransaction = getGraph().newTransaction();
        }
        currentRecord++;
    }

    public UUID getMyID() {
        return myID;
    }

    @Override
    public void run() {
        log.info("Starting new thread " + myID);

        // Start new graph transaction
        graphTransaction = getGraph().newTransaction();
        getRecords().forEachRemaining(new Consumer<Map<String, String>>() {
            @Override
            public void accept(Map<String, String> record) {
                try {
                    acceptRecord(record);
                } catch (Exception e) {
                    log.error("Thread " + myID + ". Exception during record import.", e);
                }
            }

        });
        graphTransaction.commit();
        graphTransaction.close();

        log.info("Finished thread " + myID);

        notifyListener(WorkerListener.State.Done);
    }

}
