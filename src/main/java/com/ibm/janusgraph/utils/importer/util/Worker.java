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
package com.ibm.janusgraph.utils.importer.util;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;
import java.util.List;

import org.janusgraph.core.JanusGraph;


public abstract class Worker implements Runnable {
    private final Iterator<Map<String, String>> records;
    private List<WorkerListener> listeners = new LinkedList<WorkerListener>();
    private final JanusGraph graph;
    private final Map<String, Object> propertiesMap;

    public Worker(final Iterator<Map<String, String>> records, final Map<String, Object> propertiesMap,
            final JanusGraph graph) {
        this.records = records;
        this.graph = graph;
        this.propertiesMap = propertiesMap;
    }

    public Iterator<Map<String, String>> getRecords() {
        return records;
    }

    public JanusGraph getGraph() {
        return graph;
    }

    public Map<String, Object> getPropertiesMap() {
        return propertiesMap;
    }

    public void addListener(WorkerListener listener) {
        listeners.add(listener);
    }

    /**
     * Notify the state change to all listeners
     * @param state new state
     */
    public void notifyListener(WorkerListener.State state) {
        listeners.forEach(listener -> {
            listener.notify(this, state);
        });
    }
}
