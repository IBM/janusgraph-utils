package com.ibm.janusgraph.bench.beans;

import java.util.List;

public class IndexBean{
    public String name = null;
    public List<String> propertyKeys;
    public boolean composite = true;
    public boolean unique = false;
    //public String indexOnly = null;
    //public String mixedIndex = null;
    public IndexBean(String name, List<String> propertyKeys, boolean composite, boolean unique) {
        this.name = name;
        this.propertyKeys = propertyKeys;
        this.composite = composite;
        this.unique = unique;
    }
    
    
}
