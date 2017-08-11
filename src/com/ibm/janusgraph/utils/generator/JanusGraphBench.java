package com.ibm.janusgraph.utils.generator;

public class JanusGraphBench {

    public JanusGraphBench(String csvConfPath){

    }
    
    static void prepareCSV(String csvConfPath, String outputDirectory){
        CSVGenerator csv = new CSVGenerator(csvConfPath);
        System.out.println("Loaded csv config file: "+ csvConfPath);
        csv.writeAllCSVs(outputDirectory);  
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        if (null == args || args.length < 2) {
            System.err.println("Usage: JanusGraphBench <benchmark-config-file> <mapper-schema-output-directory>");
            System.exit(1);
        }
       String csvConfPath = args[0];
       prepareCSV(csvConfPath, args[1]);
       GSONUtil.writeToFile(args[1] + "/schema.json",GSONUtil.configToSchema(csvConfPath));
       GSONUtil.writeToFile(args[1] + "/datamapper.json", GSONUtil.toDataMap(csvConfPath));
       
    }
}
