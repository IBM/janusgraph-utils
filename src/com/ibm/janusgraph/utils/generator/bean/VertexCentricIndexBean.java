package com.ibm.janusgraph.utils.generator.bean;

import java.util.List;

public class VertexCentricIndexBean{
    String name = null;
    String edge = null;
    List<String> propertyKeys;
    String order = "incr";
    String direction = "BOTH";
}
