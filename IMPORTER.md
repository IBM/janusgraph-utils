# JanusGraphBatchImporter

## Description

JanusGraphBatchImporter is a tool that imports bulk data into JanusGraph using multiple CSV files for the data and json config files to define the schema and mapping between schema and data. It uses multiple workers in order to achieve full speed of import of the data

## Dependencies and how to use

JanusGraphBatchImporter depends on JanusGraph libraries and also commons-csv-1.4.jar

In order to use under Linux:

Edit batch_import.sh and set JANUSGRAPH_PATH to the path where JanusGraph binaries are located.
Edit conf/batch_import.properties file to configure the importer 

Usage of batch_import.sh:
      batch_import.sh <janusgraph-config-file> <data-files-directory> <schema.json> <data-mapping.json>
      
      <janusgraph-config-file>: Properties file with the JanuGraph configuration
      <data-files-directory>: Relative directory where the data files are located
      <schema.json>: JSON file defining the schema of the graph
      <data-mapping.json>: mapping file defining the relationship between the CSV/s fields and the graph

## Schema JSON

## Data mapping JSON
