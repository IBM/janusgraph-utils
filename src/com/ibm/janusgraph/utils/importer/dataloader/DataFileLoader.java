package com.ibm.janusgraph.utils.importer.dataloader;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.janusgraph.core.JanusGraph;

import com.ibm.janusgraph.utils.importer.util.BatchHelper;
import com.ibm.janusgraph.utils.importer.util.Config;
import com.ibm.janusgraph.utils.importer.util.Constants;
import com.ibm.janusgraph.utils.importer.util.Worker;
import com.ibm.janusgraph.utils.importer.util.WorkerPool;

public class DataFileLoader {
	private JanusGraph graph;
	private Map<String, Object> propertiesMap;
	private Class<Worker> workerClass;

	private Logger log = Logger.getLogger(DataFileLoader.class);

	public DataFileLoader(JanusGraph graph, Class<Worker> workerClass) {
		this.graph = graph;
		this.workerClass = workerClass;
	}

	private void startWorkers(Iterator<CSVRecord> iter, long targetRecordCount, WorkerPool workers)
			throws Exception {
		while (iter.hasNext()) {
			long currentRecord = 0;
			List<Map<String,String>> sub = new ArrayList<Map<String,String>>(); 
			while (iter.hasNext() && currentRecord < targetRecordCount) {
				sub.add(iter.next().toMap());
				currentRecord++;
			}
			Constructor<Worker> constructor = workerClass.getConstructor(Iterator.class, Map.class, JanusGraph.class);
			Worker worker = constructor.newInstance(sub.iterator(), propertiesMap, graph);
			workers.submit(worker);
		}
	}

	public void loadFile(String fileName, Map<String, Object> propertiesMap, WorkerPool workers) throws Exception {
		log.info("Loading " + fileName);
//		long linesCount = BatchHelper.countLines(fileName);

		this.propertiesMap = propertiesMap;

		Reader in = new FileReader(fileName);
		Iterator<CSVRecord> iter = CSVFormat.EXCEL.withHeader().parse(in).iterator();
//		long freeMemory = Runtime.getRuntime().freeMemory()/1024/1024;
		// TODO Calculate targetThreadCount using the free memory and number of threads to execute
		//Max record count per thread
		startWorkers(iter, Config.getConfig().getWorkersTargetRecordCount(), workers);
	}
}
