package com.ibm.janusgraph.utils.importer.dataloader;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.janusgraph.core.JanusGraph;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.janusgraph.utils.importer.edge.EdgeLoaderWorker;
import com.ibm.janusgraph.utils.importer.util.Config;
import com.ibm.janusgraph.utils.importer.util.Worker;
import com.ibm.janusgraph.utils.importer.util.WorkerPool;
import com.ibm.janusgraph.utils.importer.vertex.VertexLoaderWorker;

public class DataLoader {
	private JanusGraph graph;

	private Logger log = Logger.getLogger(DataLoader.class);

	public DataLoader(JanusGraph graph) {
		this.graph = graph;
	}

	public void loadVertex(String filesDirectory, String mappingFile) throws Exception {
		loadData(filesDirectory, mappingFile, "vertexMap", (Class) VertexLoaderWorker.class);
	}

	public void loadEdges(String filesDirectory, String mappingFile) throws Exception {
		loadData(filesDirectory, mappingFile, "edgeMap", (Class) EdgeLoaderWorker.class);
	}

	public void loadData(String filesDirectory, String mappingFile, String mapToLoad, Class<Worker> workerClass)
			throws Exception {
		long startTime = System.nanoTime();
		log.info("Start loading data for " + mapToLoad);

		// Read the mapping json
		String mappingJson = new String(Files.readAllBytes(Paths.get(mappingFile)));
		JSONObject mapping = new JSONObject(mappingJson);

		JSONObject vertexMap = mapping.getJSONObject(mapToLoad);
		Iterator<String> keysIter = vertexMap.keys();

		int availProcessors = Config.getConfig().getWorkers();
		try (WorkerPool workers = new WorkerPool(availProcessors, availProcessors * 2)) {
			while (keysIter.hasNext()) {
				String fileName = keysIter.next();
				Map<String, Object> propMapping = new Gson().fromJson(vertexMap.getJSONObject(fileName).toString(),
						new TypeToken<HashMap<String, Object>>() {
						}.getType());
				new DataFileLoader(graph, workerClass).loadFile(filesDirectory + "/" + fileName, propMapping, workers);
			}
		}

		// log elapsed time in seconds
		long totalTime = (System.nanoTime() - startTime) / 1000000000;
		log.info("Loaded " + mapToLoad + " in " + totalTime + " seconds!");
	}
}
