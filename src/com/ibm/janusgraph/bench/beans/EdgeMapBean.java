package com.ibm.janusgraph.bench.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeMapBean {
    public Map<String, Object> maps = new HashMap<>();
    public EdgeMapBean(String labelName){
        this.maps.put("[EdgeLabel]", labelName);
//        Map<String, String> subMap = new HashMap<>();
//        Map<String, String> subMap2 = new HashMap<>();
//
//        subMap.put("Left", labelName + ".node_id");
//        this.maps.put("[edge_left]", subMap);
//        subMap2.put("Right", labelName + ".node_id");
//        this.maps.put("[edge_right]", subMap2);
    }
}
