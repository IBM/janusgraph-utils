package com.ibm.janusgraph.utils.generator.bean;

import java.util.Map;

public class ColumnBean {
    public String dataType;
    public boolean composit=true;
    public String mixedIndex;
    public String indexOnly;
    public String dataSubType; //to support sub categories of certain dataTypes
    public Map<String, String> dateRange = null;
    public String dateFormat = null;
}