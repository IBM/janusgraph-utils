/*******************************************************************************
 *   Copyright 2017 IBM Corp. All Rights Reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package com.ibm.janusgraph.utils.generator.bean;

import java.util.ArrayList;
import java.util.List;


public class GSONSchema{
    public List<PropertyKeyBean> propertyKeys = new ArrayList<PropertyKeyBean>();
    public List<VertexLabelBean> vertexLabels = new ArrayList<VertexLabelBean>();
    public List<EdgeLabelBean> edgeLabels= new ArrayList<EdgeLabelBean>();
    public List<IndexBean> vertexIndexes= new ArrayList<IndexBean>();
    public List<IndexBean> edgeIndexes= new ArrayList<IndexBean>();
    public List<VertexCentricIndexBean> vertexCentricIndexes= new ArrayList<VertexCentricIndexBean>();
    
}
