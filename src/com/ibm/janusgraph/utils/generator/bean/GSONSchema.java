package com.ibm.janusgraph.utils.generator.bean;

import java.util.ArrayList;
import java.util.List;


public class GSONSchema{
    public List<PropertyKeyBean> propertyKeys = new ArrayList();
    public List<VertexLabelBean> vertexLabels = new ArrayList();
    public List<EdgeLabelBean> edgeLabels= new ArrayList();
    public List<IndexBean> vertexIndexes= new ArrayList();
    public List<IndexBean> edgeIndexes= new ArrayList();
    public List<VertexCentricIndexBean> vertexCentricIndexes= new ArrayList();
    
}
