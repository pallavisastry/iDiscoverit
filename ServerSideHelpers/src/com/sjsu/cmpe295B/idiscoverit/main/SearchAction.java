package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * Search and retrieve audios
 * @author pallavi
 *
 */
public class SearchAction extends ActionSupport {

	private static final long serialVersionUID = -8809852978247103509L;
	private static final String TAG = "SearchAction";
	private static final Logger logger = Logger.getLogger(SearchAction.class);

	private String query;
	private String username;
	private String customActionMsg;
	private ArrayList<String> searchResultsList;
	private String SEARCH_RETRIEVAL_SUCCESS = "searchRetrievalSuccess";
	private String SEARCH_RETRIEVAL_NONE = "searchRetrievalNone";
	
	public String execute() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE SearchAction STARTS----------------------------");
		
		if(logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} 
		else if(logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		logger.info(TAG + "#####query is >>>>> "+query);
		searchResultsList = getSearchResultsListFromDB();
		logger.info(TAG + "#####Information: Size of search result list>>>> "+searchResultsList.size());
		
		if(searchResultsList.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE RenderAudiotronsAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(SEARCH_RETRIEVAL_SUCCESS);
			return SUCCESS;
		}
		else {
			logger.info(TAG + "-----------------------SERVERSIDE RenderAudiotronsAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(SEARCH_RETRIEVAL_NONE);
			return SUCCESS; //!!!MAP error to a list with string array i/p for errors and check it in android!!!
		}
	}
	
	private ArrayList<String> getSearchResultsListFromDB() {
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		ArrayList<String> aTronsList = daoHandler.getSearchResults(query);
		
		System.out.println("Searched results >>> "+aTronsList);
		
		return aTronsList;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getCustomActionMsg() {
		return customActionMsg;
	}
	
	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
	
	public ArrayList<String> getSearchResultsList() {
		return searchResultsList;
	}
	
	public void setSearchResultsList(ArrayList<String> searchResultsList) {
		this.searchResultsList = searchResultsList;
	}
}