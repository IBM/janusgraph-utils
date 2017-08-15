package com.ibm.janusgraph.utils.generator.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
@JsonInclude(Include.NON_EMPTY)
public class IndexBean{
    public String name = null;
    public List<String> propertyKeys;
    public boolean composite = true;
    public boolean unique = false;
    public String indexOnly = null;
    public String mixedIndex = null;
    public IndexBean(
            String          name,
            List<String>    propertyKeys,
            boolean         composite, 
            boolean         unique,
            String          indexOnly,
            String          mixedIndex){
        this.name = name;
        this.propertyKeys = propertyKeys;
        this.composite = composite;
        this.unique = unique;
        this.indexOnly = indexOnly;
        this.mixedIndex = mixedIndex;
    }
    
    
}
