package com.ibm.janusgraph.bench;

import java.util.List;

public class IndexBean{
    public String name = null;
    public List<String> propertyKeys;
    public boolean composite = true;
    public boolean unique = false;
    public String indexOnly = null;
    public String mixedIndex = null;
}
