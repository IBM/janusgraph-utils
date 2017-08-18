package com.ibm.janusgraph.utils.generator.bean;

import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class ColumnBean {
    public String dataType;
    public boolean composit = false;
    @JsonDeserialize(as=String.class)
    public String mixedIndex = null;
    public String indexOnly;
    public String dataSubType; //to support sub categories of certain dataTypes
    public Map<String, String> dateRange = null;
    public String dateFormat = null;
}