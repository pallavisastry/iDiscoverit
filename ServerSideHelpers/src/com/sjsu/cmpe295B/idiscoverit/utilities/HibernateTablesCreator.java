package com.sjsu.cmpe295B.idiscoverit.utilities;

import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * 
 * @author pallavi
 *This class creates tables using hibernate configuration class
 */
public class HibernateTablesCreator 
{
	@SuppressWarnings("rawtypes")
	public final static void createTables(Class className){

		AnnotationConfiguration config= new AnnotationConfiguration();
		((AnnotationConfiguration) config).addAnnotatedClass(className.getClass());
		config.configure();		
		new SchemaExport(config).create(true,true);
	}
}
