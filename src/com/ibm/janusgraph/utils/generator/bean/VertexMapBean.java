package com.ibm.janusgraph.utils.generator.bean;

import java.util.HashMap;
import java.util.Map;

public class VertexMapBean {
    public Map<String, String> maps = new HashMap<>();
    
    public VertexMapBean(String labelName){
        this.maps.put("[VertexLabel]", labelName);
        this.maps.put("node_id", "node_id");
    }
}
