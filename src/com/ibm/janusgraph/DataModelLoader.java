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
		// TODO Auto-generated method stub
		JanusGraph g = JanusGraphFactory.open("samples/janusgraph-cql-es.properties");
		
		try {
			loadSchema(g, "samples/schema.json");
		}
		catch (Exception e) {
			System.out.println("Failed to import data model due to " + e.getMessage());
		}
		 
	}

}
