package com.sjsu.cmpe295B.idiscoverit.utilities;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static Logger logger = Logger.getLogger(HibernateUtil.class);
	
	private static SessionFactory buildSessionFactory() {
		try {

			return new AnnotationConfiguration().configure()
					.buildSessionFactory();
		} 
		catch (HibernateException ex) {
			logger.error("Initial SessionFactory creation failed." + ex);
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static void shutdown(){
		getSessionFactory().close();
	}
}