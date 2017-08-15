package com.ibm.janusgraph.utils.importer.util;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.janusgraph.core.JanusGraph;

public abstract class Worker implements Runnable {
	private final Iterator<Map<String,String>> records;
	private final JanusGraph graph;
	private final Map<String, Object> propertiesMap;

	public Worker(final Iterator<Map<String,String>> records, final Map<String, Object> propertiesMap, final JanusGraph graph) {
		this.records = records;
		this.graph = graph;
		this.propertiesMap = propertiesMap;
	}

	public Iterator<Map<String,String>> getRecords() {
		return records;
	}

	public JanusGraph getGraph() {
		return graph;
	}

	public Map<String, Object> getPropertiesMap() {
		return propertiesMap;
	}
	
	
}
