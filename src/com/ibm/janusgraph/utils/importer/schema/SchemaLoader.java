package com.ibm.janusgraph.utils.importer.schema;


import java.io.File;
import java.lang.reflect.Method;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import groovy.lang.GroovyClassLoader;

public class SchemaLoader {

	public SchemaLoader() {
	}
	
	public void loadSchema(JanusGraph g, String schemaFile) throws Exception {
		GroovyClassLoader gcl = new GroovyClassLoader();
		Class<?> groovyclass = gcl.parseClass(new File ("src/JanusGraphModelImporter.groovy"));
		
		Object scriptInstance = groovyclass.newInstance();
		
		Class<?>[] args = new Class[2];
		args[0] = JanusGraph.class;
		args[1] = String.class;
		
		Method m = scriptInstance.getClass().getMethod("writeGraphSONModel", args);
		m.invoke(scriptInstance, g, schemaFile);
		gcl.close();
		
	}
	
	public static void main(String[] args) {
       
		if (null == args || args.length < 2) {
            System.err.println("Usage: SchemaLoader <janusgraph-config-file> <schema-file>");
            System.exit(1);
        }		

		String configFile = args[0];
		String schemaFile = args[1];
		
		// use custom or default config file to get JanusGraph
		JanusGraph g = JanusGraphFactory.open(configFile);
		
		try {
			new SchemaLoader().loadSchema(g, schemaFile);
		}
		catch (Exception e) {
			System.out.println("Failed to import schema due to " + e.getMessage());
		}
		finally {
			g.close();
		}
		 
	}

}
