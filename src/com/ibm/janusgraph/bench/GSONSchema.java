package com.ibm.janusgraph.bench;

import java.util.List;


public class GSONSchema{
    public List<PropertyKeyBean> propertyKeys;
    public List<VertexLabelBean> vertexLabels;
    public List<EdgeLabelBean> edgeLabels;
    public List<IndexBean> vertexIndexes;
    public List<IndexBean> edgeIndexes;
    public List<VertexCentricIndexBean> vertexCentricIndexes;
}
