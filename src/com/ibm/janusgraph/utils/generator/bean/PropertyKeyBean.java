package com.ibm.janusgraph.utils.generator.bean;

public class PropertyKeyBean {
    public String name;
    public String dataType;
    public String cardinality = "SINGLE";
    
    public PropertyKeyBean(String name, String dataType){
        this.name = name;
        this.dataType = dataType;
    }
}
