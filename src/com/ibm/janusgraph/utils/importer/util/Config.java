package com.ibm.janusgraph.utils.importer.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	private Properties properties = null;
	private String availProcessors = null;

	private static Config config = null;
	private static String propFile = "batch_import.properties";

	public static void setConfigFile(String propFile) {
		Config.propFile = propFile;
	}

	public static Config getConfig() {
		if (config == null) {
			config = new Config();
			try {
				config.loadProperties(propFile);
			} catch (Exception e) {
				// Error reading properties file
			}
		}

		return config;
	}

	public Config() {
		properties = new Properties();

		availProcessors = new Integer(Runtime.getRuntime().availableProcessors()).toString();
	}

	public void loadProperties(String propFile) throws FileNotFoundException, IOException {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFile);
		if (inputStream != null) {
			properties.load(inputStream);
		} else {
			properties = null;
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public int getWorkersTargetRecordCount() {
		return properties == null ? null
				: new Integer(properties.getProperty("workers.target_record_count",
						Constants.DEFAULT_WORKERS_TARGET_RECORD_COUNT.toString()));
	}

	public int getWorkers() {
		return properties == null ? null : new Integer(properties.getProperty("workers", availProcessors));
	}

	public int getVertexRecordCommitCount() {
		return properties == null ? null
				: new Integer(properties.getProperty("vertex.record_commit_count",
						Constants.DEFAULT_VERTEX_COMMIT_COUNT.toString()));
	}

	public int getEdgeRecordCommitCount() {
		return properties == null ? null
				: new Integer(properties.getProperty("edge.record_commit_count",
						Constants.DEFAULT_EDGE_COMMIT_COUNT.toString()));
	}
}