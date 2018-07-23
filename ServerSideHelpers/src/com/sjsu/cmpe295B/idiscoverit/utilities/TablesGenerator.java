package com.sjsu.cmpe295B.idiscoverit.utilities;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sjsu.cmpe295B.idiscoverit.model.Media;

public class TablesGenerator {
	private static Logger logger = Logger.getLogger(TablesGenerator.class);

	public void createTables() {
		HibernateTablesCreator.createTables(Media.class);
		
		Session session=HibernateUtil.getSessionFactory().openSession();		
		Transaction transaction=session.beginTransaction();
		
		//Create Media objects and save it in session!
	}
}
