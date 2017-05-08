package com.ibm.janusgraph.bench;
import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.janusgraph.bench.beans.GSONSchema;

public class JanusGraphBench {

    public JanusGraphBench(String csvConfPath){

    }
    
    static void prepareCSV(String csvConfPath){
        CSVGenerator csv = new CSVGenerator(csvConfPath);
        csv.writeAllCSVs("/tmp");
        
    }

    
    static GSONSchema loadSchema(String gsonSchemaFile){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File(gsonSchemaFile), GSONSchema.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse, read, or evaluate the GSON schema. " + e.toString());
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        //GSONSchema json = loadSchema("/home/ubuntu/workspaces/JanusGraphBench/schema.json");
        prepareCSV("/home/ubuntu/workspaces/JanusGraphBench/config.json");
        System.out.println("Finished");
    }
}
