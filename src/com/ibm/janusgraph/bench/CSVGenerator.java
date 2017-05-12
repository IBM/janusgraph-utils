package com.ibm.janusgraph.bench;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.janusgraph.bench.beans.CSVConfig;
import com.ibm.janusgraph.bench.beans.CSVIdBean;
import com.ibm.janusgraph.bench.beans.ColumnBean;
import com.ibm.janusgraph.bench.beans.EdgeTypeBean;
import com.ibm.janusgraph.bench.beans.RelationBean;
import com.ibm.janusgraph.bench.beans.VertexTypeBean;

public class CSVGenerator {
    private CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator("\n");
    private CSVConfig csvConf = null;
    private CSVIdBean idFactory = null;
    private int[] RANDDOM_INT_RANGE = {100000,99999999};

    public CSVGenerator(String csvConfPath){
        this.csvConf = loadConfig(csvConfPath);
        this.idFactory = new CSVIdBean(csvConf.VertexTypes);
        
    }
    
    private ArrayList<Object> generateOneRecord(Map<String,ColumnBean> columns){
        ArrayList<Object> rec = new ArrayList<Object>();
        
        columns.forEach( (name, value) -> {
            if (value.dataType.toLowerCase().equals("integer") 
                    || value.dataType.toLowerCase().equals("long")){
                //rec.add(randomInteger(RANGE[0],RANGE[1],randomInt));
                rec.add(RandomUtils.nextInt(RANDDOM_INT_RANGE[0],RANDDOM_INT_RANGE[1]));
            }
            else{
                rec.add(RandomStringUtils.randomAlphabetic(10));
            }
        });
        return rec;
    }
    
    public void writeEdgeCSVs(EdgeTypeBean type, String outputDirectory ){
        ArrayList<String> header = new ArrayList<String>();
        header.add("Left");
        header.add("Right");
        header.addAll(type.columns.keySet());
        try {
            for (RelationBean relation: type.relations) {
                /*Ex: /tmp/left-right_E1_edges.csv    */
                String csvFile = outputDirectory + "/" + relation.left +"-" + relation.right
                        + "_" + type.name + "_edges.csv";
                CSVPrinter csvFilePrinter = new CSVPrinter(new FileWriter(csvFile), csvFileFormat);
                csvFilePrinter.printRecord(header);

                for (int i = 0; i < relation.row; i++) {
                    ArrayList<Object> record = new ArrayList<Object>();
                    record.add(idFactory.getRandomIdForVertexType(relation.left));
                    record.add(idFactory.getRandomIdForVertexType(relation.right));
                    record.addAll(generateOneRecord(type.columns));
                    csvFilePrinter.printRecord(record);
                }
                csvFilePrinter.close();
                System.out.println("Generated edge file: "+ csvFile);
            }
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
            System.out.println("Generated vertex file: "+ csvFile);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
    public void writeAllCSVs(String outputDirectory){
        for (VertexTypeBean vertex : csvConf.VertexTypes){
            Runnable task = () -> { writeVertexCSV(vertex, "/tmp");};
            new Thread(task).start();
        }
        
        for (EdgeTypeBean edge: csvConf.EdgeTypes){
            Runnable task = () -> { writeEdgeCSVs(edge, "/tmp");};
            new Thread(task).start();
            
        }
    }
    
    static CSVConfig loadConfig(String jsonConfFile){
        ObjectMapper confMapper = new ObjectMapper();
        try {
            
            CSVConfig conf = confMapper.readValue(new File(jsonConfFile), CSVConfig.class);
            isValidConfig(conf);
            return conf;
        } catch (Exception e) {
            throw new RuntimeException("Fail to parse, read, or evaluate the config JSON. " + e.toString());
        }
    }
    
    public static void isValidConfig(CSVConfig config){
        List<String> typeArray = new ArrayList<String>();
        config.VertexTypes.forEach(vertextype -> typeArray.add(vertextype.name));
        for (EdgeTypeBean edgeType: config.EdgeTypes){
            for (RelationBean relation: edgeType.relations) {
                    if(!typeArray.contains(relation.left)){
                        throw new RuntimeException("relationships: "
                                + relation.left + " is not of vertex types: " + typeArray.toString());}
                    if(!typeArray.contains(relation.right))
                        throw new RuntimeException("relationships: "
                                + relation.right + " is not of vertex types: " + typeArray.toString());        
            }
        }
        //return true;
    }

}
