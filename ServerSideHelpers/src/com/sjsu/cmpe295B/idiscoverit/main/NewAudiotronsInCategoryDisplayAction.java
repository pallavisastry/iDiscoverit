package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class NewAudiotronsInCategoryDisplayAction extends ActionSupport {

	private static final long serialVersionUID = -747116642293948996L;
	
	private static final Logger logger = Logger.getLogger(NewAudiotronsInCategoryDisplayAction.class);
	private static final String TAG = "NewAudiotronsInCategoryDisplayAction";
	
	private List<String> audiotronListForCategoryFromDB;//,randomAudiotronListFromDB;
	private String categoryInRequest;
	private String customActionMsg;
	private String username;
	private String AUDIOTRONS_RETRIEVAL_SUCCESS = "audiotronsRetrievalSuccess";
	private String AUDIOTRONS_RETRIEVAL_ERROR = "audiotronsRetrievalError";
	
	public String execute() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE AudiotronsInCategoryDisplayAction STARTS----------------------------");
		logger.info(TAG + " User name-Serverside >>>> " + username);
		if (logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		logger.info(TAG + "####Information: Category in request is >>>>>> " + categoryInRequest);

		audiotronListForCategoryFromDB = getUnheardAudiotronsForCategoryFromDB();
		
		//#Remove the 2 lines of code below for production
		for(Iterator<String> iter= audiotronListForCategoryFromDB.iterator();iter.hasNext();)
			logger.info(TAG + "##### Information: AUDIOTRONS in categor >>>>>>>> " + "categoryInRequest: " + iter.next());
		
		if(audiotronListForCategoryFromDB.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE NewAudiotronsInCategoryDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_SUCCESS);
			return SUCCESS; //# Returns audio list in json.
		}
		else {
			logger.info(TAG + "-----------------------SERVERSIDE NewAudiotronsInCategoryDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_ERROR);
			setAudiotronListForCategoryFromDB(null);
			return SUCCESS;//# Return an empty json list and handle it!!!
		}
	}
	
	private List<String> getUnheardAudiotronsForCategoryFromDB() {
		logger.info(TAG + "--------Inside getUnheardAudiotronsForCategoryFromDB(....)---------");
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<String> audiotronList = daoHandler.getRookieTrons(categoryInRequest);
		
		for(Iterator<String> it = audiotronList.iterator();it.hasNext();)
			System.out.println("Next name >>>> "+it.next());
		
		return audiotronList;
	}
	
	public void setAudiotronListForCategoryFromDB(
			List<String> audiotronListForCategoryFromDB) {
		this.audiotronListForCategoryFromDB = audiotronListForCategoryFromDB;
	}

	public List<String> getAudiotronListForCategoryFromDB() {
		return audiotronListForCategoryFromDB;
	}

	public void setCategoryInRequest(String categoryInRequest) {
		this.categoryInRequest = categoryInRequest;
	}
	public void setUsername(String uname){
		this.username=uname;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
}
