package com.ibm.janusgraph.utils.generator.bean;

import java.util.HashMap;
import java.util.Map;

public class BatchImporterDataMap {
    public Map<String, Map<String, String>> vertexMap = new HashMap<>();
    public Map<String, Map<String, Object>> edgeMap = new HashMap<>();
}
