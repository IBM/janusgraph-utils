package com.ibm.janusgraph.utils.importer.util;

public class Constants {
	public static final String INTERMEDIARIES_KEY_NAME = "Intermediaries";
	public static final String INTERMEDIARIES_LABEL = "Intermediaries";
	public static final String INTERMEDIARIES_FILE_NAME = "Intermediaries.csv";
	public static final String OFFICERS_KEY_NAME = "Officers";
	public static final String OFFICERS_LABEL = "Officers";
	public static final String OFFICERS_FILE_NAME = "Officers.csv";
	public static final String INTERMEDIARIES_OFFICERS_LABEL = "Intermediaries_Officers";
	
	public static final String VERTEX_MAP = "vertexMap";
	public static final String EDGE_MAP = "edgeMap";
	
	public static final String EDGE_LEFT_MAPPING = "[edge_left]";
	public static final String EDGE_RIGHT_MAPPING = "[edge_right]";
	public static final String VERTEX_LABEL_MAPPING = "[VertexLabel]";
	public static final String EDGE_LABEL_MAPPING = "[EdgeLabel]";
	
	public static final Integer DEFAULT_WORKERS_TARGET_RECORD_COUNT = 50000;
	public static final Integer DEFAULT_VERTEX_COMMIT_COUNT = 10000;
	public static final Integer DEFAULT_EDGE_COMMIT_COUNT = 1000;
}
