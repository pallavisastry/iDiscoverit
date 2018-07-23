package com.sjsu.cmpe295B.idiscoverit.filters;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;

/**
 * Servlet Filter implementation class UserRequestTrackFilter
 */
public class UserRequestTrackFilter implements Filter {

	private static Logger logger = Logger.getLogger(UserRequestTrackFilter.class);
	private FilterConfig _filterConfig = null;
	private HashMap<String,Integer> counterMap = new HashMap<String, Integer>();
	
    /**
     * Default constructor. 
     */
    public UserRequestTrackFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		long startTime = System.currentTimeMillis();
		
		//Getting request values
		String ipAddress=httpRequest.getRemoteAddr();
		String host = httpRequest.getRemoteHost();
		Enumeration headerNames =  httpRequest.getHeaderNames();
		String requestURI=httpRequest.getRequestURI();
		String deviceType=httpRequest.getHeader("user-agent");
		
		logger.info("*********Inside doFilter() of UserRequestTrackFilter***********");
		logger.info("(parameters in request >>>>>>>> ");
		logger.info("ipAddress ==> "+ipAddress);
		logger.info("host ==> "+host);
		logger.info("requestURI ==> "+requestURI);
		logger.info("deviceType ==> "+deviceType);
		logger.info(")");
		logger.info("header names>>>");
		
		while(headerNames.hasMoreElements()){
			String key= (String) headerNames.nextElement();
			logger.info("header = "+key);
		}

		//Incrementing number of hits to LoginAction
		if(requestURI.contains(Constants.DEVICE_LOGIN_URL)){
			if(!(counterMap.containsKey(Constants.DEVICE_LOGIN_URL))){
				counterMap.put(Constants.DEVICE_LOGIN_URL, 1);
			}else{
				counterMap.put(Constants.DEVICE_LOGIN_URL, counterMap.get(Constants.DEVICE_LOGIN_URL)+1);
			}
		}
		//Incrementing number of hits to HallOfFameAction
		if(requestURI.contains(Constants.HALL_OF_FAME_URL)){
			if(!(counterMap.containsKey(Constants.HALL_OF_FAME_URL))){
				counterMap.put(Constants.HALL_OF_FAME_URL, 1);
			}else{
				counterMap.put(Constants.HALL_OF_FAME_URL, counterMap.get(Constants.HALL_OF_FAME_URL)+1);
			}
		}
		//Incrementing number of hits to AudiotronSaveAction
		if(requestURI.contains(Constants.AUDIOTRAON_SAVE_URL)){
			if(!(counterMap.containsKey(Constants.AUDIOTRAON_SAVE_URL))){
				counterMap.put(Constants.AUDIOTRAON_SAVE_URL, 1);
			}else{
				counterMap.put(Constants.AUDIOTRAON_SAVE_URL, counterMap.get(Constants.AUDIOTRAON_SAVE_URL)+1);
			}
		}
		//Incrementing number of hits to CategoryListDisplayAction
		if(requestURI.contains(Constants.CATEGORY_LIST_URL)){
			if(!(counterMap.containsKey(Constants.CATEGORY_LIST_URL))){
				counterMap.put(Constants.CATEGORY_LIST_URL, 1);
			}else{
				counterMap.put(Constants.CATEGORY_LIST_URL, counterMap.get(Constants.CATEGORY_LIST_URL)+1);
			}
		}
		//Incrementing number of hits to AudiotronsInCategoryAction
		if(requestURI.contains(Constants.AUDIOTRONS_IN_CATEGORY_URL)){
			if(!(counterMap.containsKey(Constants.AUDIOTRONS_IN_CATEGORY_URL))){
				counterMap.put(Constants.AUDIOTRONS_IN_CATEGORY_URL, 1);
			}else{
				counterMap.put(Constants.AUDIOTRONS_IN_CATEGORY_URL, counterMap.get(Constants.AUDIOTRONS_IN_CATEGORY_URL)+1);
			}
		}
		//Incrementing number of hits to FavoritesAction
		if(requestURI.contains(Constants.FAVORITES_ACTION_URL)){
			if(!(counterMap.containsKey(Constants.FAVORITES_ACTION_URL))){
				counterMap.put(Constants.FAVORITES_ACTION_URL, 1);
			}else{
				counterMap.put(Constants.FAVORITES_ACTION_URL, counterMap.get(Constants.FAVORITES_ACTION_URL)+1);
			}
		}
		//Incrementing number of hits to FeedbackAction
		if(requestURI.contains(Constants.FEEDBACK_ACTION_URL)){
			if(!(counterMap.containsKey(Constants.FEEDBACK_ACTION_URL))){
				counterMap.put(Constants.FEEDBACK_ACTION_URL, 1);
			}else{
				counterMap.put(Constants.FEEDBACK_ACTION_URL, counterMap.get(Constants.FEEDBACK_ACTION_URL)+1);
			}
		}
		//Incrementing number of hits to MyAudiotronsAction
		if(requestURI.contains(Constants.MY_AUDIOTRONS_ACTION_URL)){
			if(!(counterMap.containsKey(Constants.MY_AUDIOTRONS_ACTION_URL))){
				counterMap.put(Constants.MY_AUDIOTRONS_ACTION_URL, 1);
			}else{
				counterMap.put(Constants.MY_AUDIOTRONS_ACTION_URL, counterMap.get(Constants.MY_AUDIOTRONS_ACTION_URL)+1);
			}
		}
		//Incrementing number of hits to MyFavoriteAudiotronAction
		if(requestURI.contains(Constants.MY_FAVORITES_URL)){
			if(!(counterMap.containsKey(Constants.MY_FAVORITES_URL))){
				counterMap.put(Constants.MY_FAVORITES_URL, 1);
			}else{
				counterMap.put(Constants.MY_FAVORITES_URL, counterMap.get(Constants.MY_FAVORITES_URL)+1);
			}
		}
		//Incrementing number of hits to RenderAudiotronsAction
		if(requestURI.contains(Constants.RENDER_AUDIOTRONS_URL)){
			if(!(counterMap.containsKey(Constants.RENDER_AUDIOTRONS_URL))){
				counterMap.put(Constants.RENDER_AUDIOTRONS_URL, 1);
			}else{
				counterMap.put(Constants.RENDER_AUDIOTRONS_URL, counterMap.get(Constants.RENDER_AUDIOTRONS_URL)+1);
			}
		}
		
		logger.info("DEvice login keys >>>>> "+counterMap.keySet());
		logger.info("DEvice login values >>>>> "+counterMap.values());
		logger.info("*********Starting next filter***********");

		_filterConfig.getServletContext().setAttribute("requestURI", requestURI);
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
		long endTime = System.currentTimeMillis();
		
		logger.info("*********Completed Filter Action***********");
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		_filterConfig = fConfig;
	}
	public FilterConfig get_filterConfig() {
		return _filterConfig;
	}

	public void set_filterConfig(FilterConfig _filterConfig) {
		this._filterConfig = _filterConfig;
	}
}
