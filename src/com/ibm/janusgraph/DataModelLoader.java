package com.ibm.janusgraph;


import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;

import groovy.lang.GroovyClassLoader;

public class DataModelLoader {
	
	private static void loadSchema(JanusGraph g, String schemaFile) throws Exception {
		GroovyClassLoader gcl = new GroovyClassLoader();
		Class<?> groovyclass = gcl.parseClass(new File ("src/JanusGraphModelImporter.groovy"));
		
		Object scriptInstance = groovyclass.newInstance();
		
		Class[] args = new Class[2];
		args[0] = JanusGraph.class;
		args[1] = String.class;
		
		Method m = scriptInstance.getClass().getMethod("writeGraphSONModel", args);
		m.invoke(scriptInstance, g, schemaFile);
		
	}
	
	public static void main(String[] args) {
		String schemaFile = "samples/schema.json";
		String configFile = "samples/janusgraph-cql-es.properties";
		
		if (null != args)
			if (args.length >= 1) 
				schemaFile = args[0];
			if (args.length >= 2) 
				configFile = args[1];
		
		// use custom or default config file to get JanusGraph
		JanusGraph g = JanusGraphFactory.open(configFile);
		
		try {
			loadSchema(g, schemaFile);
		}
		catch (Exception e) {
			System.out.println("Failed to import data model due to " + e.getMessage());
		}
		finally {
			g.close();
		}
		 
	}

}
