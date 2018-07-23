package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.List;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * Class to list a user's favorite audios
 * 
 * @author pallavi
 *
 */
public class MyFavoritesListAction extends ActionSupport {

	private static final long serialVersionUID = 3525758812417877870L;

	private static final Logger logger = Logger.getLogger(MyFavoritesListAction.class);
	private static final String TAG = "MyFavoritesListAction";
	
	private List<String> myFavoriteAudiotronListFromDB;
	private String username;
	private String customActionMsg;
	private String AUDIOTRONS_RETRIEVAL_SUCCESS = "audiotronsRetrievalSuccess";
	private String AUDIOTRONS_RETRIEVAL_ERROR = "audiotronsRetrievalError";
	
	public String execute(){
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE MyFavoritesListAction STARTS----------------------------");
		
		myFavoriteAudiotronListFromDB=getMyFavoriteAudiotrons();
	
		if(myFavoriteAudiotronListFromDB.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE MyFavoritesListAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_SUCCESS);
			return SUCCESS; //# Returns audio list in json.
		}
		else {
			logger.info(TAG + "-----------------------SERVERSIDE MyFavoritesListAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_ERROR);
			return SUCCESS;//# Return an empty json list and handle it!!!
		}
		
	}
	
	private List<String> getMyFavoriteAudiotrons(){
		logger.info(TAG + "--------Inside getMyFavoriteAudiotrons(....)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<String> audiotronList = daoHandler.getMyFavoriteAudiotrons(username);
		
		return audiotronList;
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
	public List<String> getMyFavoriteAudiotronListFromDB() {
		return myFavoriteAudiotronListFromDB;
	}
	public void setMyFavoriteAudiotronListFromDB(
			List<String> myFavoriteAudiotronListFromDB) {
		this.myFavoriteAudiotronListFromDB = myFavoriteAudiotronListFromDB;
	}
}
