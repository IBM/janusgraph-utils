package com.ibm.janusgraph.bench;

import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.janusgraph.bench.beans.CSVConfig;
import com.ibm.janusgraph.bench.beans.GSONSchema;

public class GSONUtil {

    public static GSONSchema loadSchema(String gsonSchemaFile){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(gsonSchemaFile), GSONSchema.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse, read, or evaluate the GSON schema. " + e.toString());
        }
    }
    
    public static String dumpSchema(GSONSchema gson){
        
        return null;
    }
    
    public GSONSchema configToSchemaConverter(CSVConfig conf){
        
        return null;
    }
}
