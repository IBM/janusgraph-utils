package com.ibm.janusgraph.utils.generator.bean;

public class VertexLabelBean{
    public String name = null;
    public boolean partition = false;
    public boolean useStatic = false;
    public VertexLabelBean(String name) {
        this.name = name;
    }
}
