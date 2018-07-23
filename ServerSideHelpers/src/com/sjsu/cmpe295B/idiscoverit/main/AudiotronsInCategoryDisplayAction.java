package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.Category;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class AudiotronsInCategoryDisplayAction extends ActionSupport {

	private static final long serialVersionUID = -8687294849306667234L;

	private static final Logger logger = Logger.getLogger(AudiotronsInCategoryDisplayAction.class);
	private static final String TAG = "AudiotronsInCategoryDisplayAction";
	
	private List<String> audiotronListForCategoryFromDB;
	private String categoryInRequest;
	private String customActionMsg;
	private String username;
	
	public String execute() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE AudiotronsInCategoryDisplayAction STARTS----------------------------");
		logger.info(TAG + " User name-Serverside >>>> "+username);
		
		if (logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		logger.info(TAG + "####Information: Category in request is >>>>>> "+categoryInRequest);
		Category categoryObjFromRequest = new Category(categoryInRequest);
		audiotronListForCategoryFromDB = getAllAudiotronsForCategoryFromDB(categoryObjFromRequest);
		
		//#Remove the 2 lines of code below for production
		for(Iterator<String> iter= audiotronListForCategoryFromDB.iterator();iter.hasNext();)
			logger.info(TAG + "##### Information: AUDIOTRONS in categor >>>>>>>> "+"categoryInRequest: "+iter.next());
		
		if(audiotronListForCategoryFromDB.size()>0){
			logger.info(TAG + "-----------------------SERVERSIDE AudiotronsInCategoryDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg("audiotronsRetrievalSuccess");
			return SUCCESS; //# Returns audio list in json.
		}else{
			logger.info(TAG + "-----------------------SERVERSIDE AudiotronsInCategoryDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg("audiotronsRetrievalError");
			setAudiotronListForCategoryFromDB(null);
			return SUCCESS;//# Return an empty json list and handle it!!!
		}
	}

	private List<String> getAllAudiotronsForCategoryFromDB(Category category) {
		logger.info(TAG + "--------Inside getAllAudiotronsForCategoryFromDB(....)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		
		List<String> audiotronList = daoHandler.distributeAudiotronsInCategory(username, category);
		
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
