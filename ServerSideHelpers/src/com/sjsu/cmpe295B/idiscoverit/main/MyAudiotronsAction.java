package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * This class is used to show all the audios recorded by a user.
 * @author pallavi
 *
 */
public class MyAudiotronsAction extends ActionSupport {

	private static final long serialVersionUID = -7819162265351871255L;
	private static final Logger logger = Logger.getLogger(MyAudiotronsAction.class);
	private static final String TAG = "MyAudiotronsAction";
	private String username;
	private String customActionMsg;
	private ArrayList<String> audiotronListForUserFromDB;
	private String AUDIOTRON_RETRIEVAL_SUCCESS = "audiotronsRetrievalSuccess";
	private String AUDIOTRON_RETRIEVAL_ERROR = "audiotronsRetrievalError";
	
	public String execute(){
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE MyAudiotronsAction STARTS----------------------------");

		audiotronListForUserFromDB = getMyAudiotrons();
		
		for(Iterator<String>it = audiotronListForUserFromDB.iterator();it.hasNext();)
			System.out.println(TAG + ">>>>> "+it.next());
		
		if(audiotronListForUserFromDB.size()>0){
			logger.info(TAG + "-----------------------SERVERSIDE MyAudiotronsAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRON_RETRIEVAL_SUCCESS);
			return SUCCESS;
		}else{
			logger.info(TAG + "-----------------------SERVERSIDE MyAudiotronsAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRON_RETRIEVAL_ERROR);
			return SUCCESS;
		}
	}
	
	private ArrayList<String> getMyAudiotrons() {
		
		logger.info(TAG + "--------Inside getMyAudiotrons()---------");
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<Media> myAudiotronList = daoHandler.getAllAudiotronsForUser(username);
		ArrayList<String>myAudiotronNamesList=new ArrayList<String>();
		
		for(Media m:myAudiotronList)
			myAudiotronNamesList.add(m.getAudiotronName());
		
		return myAudiotronNamesList;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customMsg) {
		this.customActionMsg = customMsg;
	}

	public ArrayList<String> getAudiotronListForUserFromDB() {
		return audiotronListForUserFromDB;
	}

	public void setAudiotronListForUserFromDB(
			ArrayList<String> audiotronListForUserFromDB) {
		this.audiotronListForUserFromDB = audiotronListForUserFromDB;
	}
}
