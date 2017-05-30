package com.ibm.janusgraph.bench;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.janusgraph.bench.beans.BatchImporterDataMap;
import com.ibm.janusgraph.bench.beans.CSVConfig;
import com.ibm.janusgraph.bench.beans.ColumnBean;
import com.ibm.janusgraph.bench.beans.EdgeLabelBean;
import com.ibm.janusgraph.bench.beans.EdgeMapBean;
import com.ibm.janusgraph.bench.beans.EdgeTypeBean;
import com.ibm.janusgraph.bench.beans.GSONSchema;
import com.ibm.janusgraph.bench.beans.IndexBean;
import com.ibm.janusgraph.bench.beans.PropertyKeyBean;
import com.ibm.janusgraph.bench.beans.RelationBean;
import com.ibm.janusgraph.bench.beans.VertexLabelBean;
import com.ibm.janusgraph.bench.beans.VertexMapBean;
import com.ibm.janusgraph.bench.beans.VertexTypeBean;
public class GSONUtil {

    public static GSONSchema loadSchema(String gsonSchemaFile){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(gsonSchemaFile), GSONSchema.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse, read, or evaluate the GSON schema. " + e.toString());
        }
    }
    
    public static void writeToFile(String jsonOutputFile,Object gson){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(jsonOutputFile), gson);
            System.out.println("Generated: "+ jsonOutputFile);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e.toString());
        } 

    }
    public static BatchImporterDataMap toDataMap(String csvConfPath){
        BatchImporterDataMap bmDataMap = new BatchImporterDataMap();
        CSVConfig csvConf = CSVGenerator.loadConfig(csvConfPath);
        for(VertexTypeBean type: csvConf.VertexTypes){
            String vertexFileName = type.name + ".csv";
            VertexMapBean vertex = new VertexMapBean(type.name);
            for (String key: type.columns.keySet()){
                vertex.maps.put(key, key);
            }
            bmDataMap.vertexMap.put(vertexFileName,vertex.maps);
        }
        
        for(EdgeTypeBean type: csvConf.EdgeTypes){
            for (RelationBean relation: type.relations) {
                /*Ex: /tmp/<left-label>-<right-label>_E1_edges.csv    */
                String edgeFileName = relation.left +"-" + relation.right + "_" + type.name + "_edges.csv";
                EdgeMapBean vertex = new EdgeMapBean(type.name);
                Map<String, String> subMap = new HashMap<>();
                Map<String, String> subMap2 = new HashMap<>();
                subMap.put("Left", relation.left + ".node_id");
                vertex.maps.put("[edge_left]", subMap);
                subMap2.put("Right", relation.right + ".node_id");
                vertex.maps.put("[edge_right]", subMap2);
                for (String key: type.columns.keySet()){
                    vertex.maps.put(key, key);
                }
            bmDataMap.edgeMap.put(edgeFileName,vertex.maps);
            }   
        }
        return bmDataMap;
    }
    public static GSONSchema configToSchema(String csvConfPath){
        GSONSchema gson = new GSONSchema();
        CSVConfig csvConf = CSVGenerator.loadConfig(csvConfPath);
        //manually add node_id as a unique propertyKey and index
        PropertyKeyBean nodeIdKey = new PropertyKeyBean("node_id", "Integer");
        gson.propertyKeys.add(nodeIdKey);
        IndexBean nodeIdIndex = new IndexBean("node_id", Arrays.asList("node_id"), true, true, null, null);
        gson.vertexIndexes.add(nodeIdIndex);
        
        for (VertexTypeBean type : csvConf.VertexTypes){
            //add vertexLabels
            VertexLabelBean vertexLabel = new VertexLabelBean(type.name);
            gson.vertexLabels.add(vertexLabel);
            
            //add propertyKeys
            for (Entry<String,ColumnBean> col : type.columns.entrySet()){
                String propertyKeyName = col.getKey();
                String propertyKeyType = col.getValue().dataType;
                boolean keyIndexType = col.getValue().composit;
                String indexOnly = col.getValue().indexOnly;
                String mixedIndex = col.getValue().mixedIndex;
                
                //TODO test duplicated keys
                if (!gson.vertexIndexes.contains(col.getKey()))
                        gson.propertyKeys.add(new PropertyKeyBean(propertyKeyName,propertyKeyType));
                
                //add vertexIndexes
                IndexBean index = new IndexBean(propertyKeyName,
                                                Arrays.asList(propertyKeyName),
                                                keyIndexType, 
                                                false,indexOnly,mixedIndex);
              //TODO test duplicated keys
                if (!gson.vertexIndexes.contains(index.name))
                    gson.vertexIndexes.add(index);
            }
            
        }
        
        for (EdgeTypeBean type : csvConf.EdgeTypes){
            //add edgeLabels
            EdgeLabelBean edgeLabel = new EdgeLabelBean(type.name);
            gson.edgeLabels.add(edgeLabel);
            
            //propertKeys
            for (Entry<String,ColumnBean> col : type.columns.entrySet()){
                String propertyKeyName = col.getKey();
                String propertyKeyType = col.getValue().dataType;
                boolean keyIndexType = col.getValue().composit;
                String indexOnly = col.getValue().indexOnly;
                String mixedIndex = col.getValue().mixedIndex;
                gson.propertyKeys.add(new PropertyKeyBean(propertyKeyName,propertyKeyType));
                
                //add edgeIndexes
                IndexBean index = new IndexBean(propertyKeyName,
                                                Arrays.asList(propertyKeyName),
                                                keyIndexType, 
                                                false, indexOnly, mixedIndex);
                gson.edgeIndexes.add(index);
            }
        }
        return gson;
    }
}
