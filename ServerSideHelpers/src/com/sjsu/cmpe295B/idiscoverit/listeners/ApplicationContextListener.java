package com.sjsu.cmpe295B.idiscoverit.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

/**
 * Application Lifecycle Listener implementation class ApplicationContextListener
 *
 */
public class ApplicationContextListener implements ServletContextListener {
	
	private static Logger logger =Logger.getLogger(ApplicationContextListener.class);
    private static final String TAG = "ApplicationContextListener";
    private static final String REQUEST_URI = "requestURI";
	
	/**
     * Default constructor. 
     */
    public ApplicationContextListener() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent event) {
    	logger.info(TAG + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    	logger.info(TAG + "*********Inside contextInitialized of ApplicationContextListener********");
    	ServletContext context = event.getServletContext();
    	
    	logger.info(TAG + "Context attrs >>> "+context.getAttributeNames());
    	logger.info(TAG + "Request uri >> "+context.getAttribute(REQUEST_URI));
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
    	logger.info(TAG + "~~~~~~Inside contextDestroyed~~~~~~");
        logger.info(TAG + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        ServletContext context = event.getServletContext();
        logger.info(TAG + "Context attrs >>> "+context.getAttributeNames());
    	logger.info(TAG + "Request uri >> "+context.getAttribute(REQUEST_URI));
    }
	
}
