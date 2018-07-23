package com.sjsu.cmpe295B.idiscoverit.interceptors;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.sjsu.cmpe295B.idiscoverit.utilities.HibernateUtil;

/**
 * 
 * @author pallavi
 * 
 * This class manages hibernate sessions from request-response using
 * OpenSessionInView pattern
 *
 */
public class HibernateInterceptor extends AbstractInterceptor{

	private static final long serialVersionUID = 6630914291036718071L;
	private static final String TAG = "HibernateInterceptor";
	private static Logger logger=Logger.getLogger(HibernateInterceptor.class);
	private SessionFactory sf=HibernateUtil.getSessionFactory();
	
	@Override
	public String intercept(ActionInvocation invocation) {
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE HIBERNATE SESSION INTERCEPTOR STARTS----------------------------");
		logger.info(TAG + "............HIBERNATE SESSION INFORMATION LOG...............");

		try{
			logger.info(TAG + "-----Starting a DB Transaction------");
			sf.getCurrentSession().beginTransaction();
			
			// Invoke the next action
			String invocationResult = invocation.invoke();
			System.out.println(TAG + "####Information: Invocation Result is>>>>> "+invocationResult+" #######");
			
			// Commit and cleanup
            logger.info(TAG + "............Committing the database transaction.............");
            sf.getCurrentSession().getTransaction().commit();
            System.out.println(TAG + " IS current transaction active: "+sf.getCurrentSession().getTransaction().isActive());
            System.out.println(TAG + " IS current session open: "+sf.getCurrentSession().isOpen());
            
            return invocationResult;
		}
		catch(HibernateException ex){
			logger.error("$$$$$$$ Hibernate Exception in 1st block of HibernateInterceptor"+ex.getMessage()+"$$$$$$$$$$");
			return Action.ERROR;
		}
		catch(Exception e){
			try{
				if(sf.getCurrentSession().getTransaction()!=null && sf.getCurrentSession().getTransaction().isActive()){
					logger.debug(":-((((Information: Rolling back the session!)))))-:");
					sf.getCurrentSession().getTransaction().rollback();
				}
			}catch(HibernateException ex){
				logger.error("$$$$$$$Hibernate Exception in 2nd try/catch block of HibernateInterceptor"+ex.getMessage()+" $$$$$$$$$");
				return Action.ERROR;
			}
			return Action.ERROR;
		}
		finally{
			System.out.println("*****Inside finally block *********");
			try{
				if(sf.getCurrentSession().isOpen()){
					logger.info("******* Closing Current Hibernate Session *******");
					sf.getCurrentSession().close();
				}
			}catch(HibernateException ex){
				logger.error("$$$$$$$Hibernate Exception in finally block of HibernateInterceptor"+ex.getMessage()+" $$$$$$$$$");
				return Action.ERROR;
			}
			logger.info("***** Finished SERVERSIDE 'finally' block *********");
			logger.info("==============================================================================================");
		}
	}
}
