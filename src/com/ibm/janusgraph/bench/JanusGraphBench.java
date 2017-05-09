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

    

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        
        //GSONSchema json = loadSchema("/home/ubuntu/workspaces/JanusGraphBench/schema.json");
        prepareCSV("/home/ubuntu/workspaces/JanusGraphBench/config_style_v3.json");
        System.out.println("Finished");
    }
}
