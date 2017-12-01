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
        if (null == args || args.length < 2) {
            System.err.println("Usage: JanusGraphBench <csv-config-file> <mapper-schema-output-directory>");
            System.exit(1);
        }
       try {
        String csvConfPath = args[0];
           prepareCSV(csvConfPath, args[1]);
           GSONUtil.writeToFile(args[1] + "/schema.json",GSONUtil.configToSchema(csvConfPath));
           GSONUtil.writeToFile(args[1] + "/datamapper.json", GSONUtil.toDataMap(csvConfPath));
    } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }

    }
}
