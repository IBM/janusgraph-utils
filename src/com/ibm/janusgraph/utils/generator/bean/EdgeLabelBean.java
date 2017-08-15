package com.ibm.janusgraph.utils.generator.bean;

import java.util.List;

public class EdgeLabelBean{
    public String name = null;
    public String multiplicity = "MULTI";
    public List<String> signatures;
    public boolean unidirected = false;
    
    public EdgeLabelBean(String name){
        this.name = name;
    }
}
