package com.ibm.janusgraph.utils.importer.vertex;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.JanusGraphVertex;

import com.ibm.janusgraph.utils.importer.util.BatchHelper;
import com.ibm.janusgraph.utils.importer.util.Config;
import com.ibm.janusgraph.utils.importer.util.Constants;
import com.ibm.janusgraph.utils.importer.util.Worker;

public class VertexLoaderWorker extends Worker {
	private final UUID myID = UUID.randomUUID();
	
	private final int COMMIT_COUNT;

	private final String defaultVertexLabel;
	private String vertexLabelFieldName;
	private JanusGraphTransaction graphTransaction;
	private long currentRecord;

	private Logger log = Logger.getLogger(VertexLoaderWorker.class);

	public VertexLoaderWorker(final Iterator<Map<String,String>> records, final Map<String, Object> propertiesMap,
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

	private void acceptRecord(Map<String,String> record) throws Exception {
		String vertexLabel = defaultVertexLabel;
		if (vertexLabelFieldName != null) {
			vertexLabel = record.get(vertexLabelFieldName);
		}
		JanusGraphVertex v = graphTransaction.addVertex(vertexLabel);

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
				Object convertedValue = BatchHelper.convertPropertyValue(value, graphTransaction.getPropertyKey(propName).dataType());
				v.property(propName, convertedValue);
			}
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
		getRecords().forEachRemaining(new Consumer<Map<String,String>>() {
			@Override
			public void accept(Map<String,String> record) {
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
	}

}
