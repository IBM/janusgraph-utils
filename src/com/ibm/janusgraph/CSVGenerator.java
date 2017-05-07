package com.ibm.janusgraph;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CSVGenerator {
    private CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
    private CSVConfig csvConf = null;
    private CSVIdBean idFactory = null;
    private int[] RANDDOM_INT_RANGE = {100000,999999};

    public CSVGenerator(String csvConfPath){
        this.csvConf = loadConfig(csvConfPath);
        this.idFactory = new CSVIdBean(csvConf.VertexTypes);
        
    }
    
    private ArrayList<Object> generateOneRecord(Map<String, String> columns){
        ArrayList<Object> rec = new ArrayList<Object>();
        
        columns.forEach( (name, value) -> {
            if (value.toLowerCase().equals("integer")){
                //rec.add(randomInteger(RANGE[0],RANGE[1],randomInt));
                rec.add(RandomUtils.nextInt(RANDDOM_INT_RANGE[0],RANDDOM_INT_RANGE[1]));
            }
            else{
                rec.add(RandomStringUtils.randomAlphabetic(10));
            }
        });
        return rec;
    }
    
    public void writeEdgeCSV(EdgeTypeBean type, String outputDirectory ){
        String csvFile = outputDirectory + "/" + type.name + "_edges.csv";
        ArrayList<String> header = new ArrayList<String>();
        header.add("Left");
        header.add("Right");
        header.addAll(type.columns.keySet());
        try {
            CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(csvFile), csvFileFormat);
            csvFilePrinter.printRecord(header);
            for(int i = 0; i < type.row; i++){
                ArrayList<Object> record = new ArrayList<Object>();
                record.add(idFactory.getRandomIdForVertexType(type.Left));
                record.add(idFactory.getRandomIdForVertexType(type.Right));
                record.addAll(generateOneRecord(type.columns));
                csvFilePrinter.printRecord(record);
            }
            csvFilePrinter.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e.toString());
        }
    }
    
    void writeVertexCSV(VertexTypeBean type, String outputDirectory ){
        String csvFile = outputDirectory + "/" + type.name + ".csv";
        ArrayList<String> header = new ArrayList<String>();
        header.add("node_id");
        header.addAll(type.columns.keySet());
        int botId = idFactory.getMinId(type.name);
        int topId = idFactory.getMaxId(type.name);
        try {
            CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(csvFile), csvFileFormat);
            csvFilePrinter.printRecord(header);
            for (int i = botId; i<=topId; i++){
                ArrayList<Object> record = new ArrayList<Object>();
                record.add(i);
                record.addAll(generateOneRecord(type.columns));
                csvFilePrinter.printRecord(record);
            }
            csvFilePrinter.close();
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    public void writeAllCSVs(String outputDirectory){
        for (VertexTypeBean csv : csvConf.VertexTypes){
            Runnable task = () -> { writeVertexCSV(csv, "/tmp");};
            new Thread(task).start();
        }
        
        for (EdgeTypeBean csv: csvConf.EdgeTypes){
            Runnable task = () -> { writeEdgeCSV(csv, "/tmp");};
            new Thread(task).start();
            
        }
    }
    
    static CSVConfig loadConfig(String jsonConfFile){
        ObjectMapper confMapper = new ObjectMapper();
        try {
            return confMapper.readValue(new File(jsonConfFile), CSVConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse, read, or evaluate the config JSON. " + e.toString());
        }
    }
    
    public void validateCSVConf(CSVConfig conf){
        // TODO validate the csvconfig here:
        // 1. Left and Right needs to be one of the VertexTypes
    }
}
