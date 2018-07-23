package com.sjsu.cmpe295B.idiscoverit.main;

import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class TagAudiotronAction extends ActionSupport {

	private static final long serialVersionUID = -8036714984397885764L;
	private static final Logger logger = Logger.getLogger(TagAudiotronAction.class);
	private static final String TAG = "TagAudiotronAction";
	
	private String tag;
	private String userName;
	private String audiotronName;
	private String customActionMsg;
	private String ADD_SUCCESS = "addSuccess";
	private String ADD_ERROR = "addError";
	
	public String execute() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE TagAudiotronAction STARTS----------------------------");
		logger.info(TAG + "##### Info: Values in TagAudiotronAction request: tag= "+tag+",username="+userName+", audioname="+audiotronName);
		boolean isInserted = insertTag();
		
		if(isInserted) {
			logger.info(TAG + "####Information: -----> Audiotron successfully tagged---------");
			setCustomActionMsg(ADD_SUCCESS);
			return SUCCESS;
		}
		else {
			logger.info(TAG + "####Information: -----> Audiotron Not tagged---------");
			setCustomActionMsg(ADD_ERROR);
			return SUCCESS;
		}
	}
	
	private boolean insertTag() {
		logger.info(TAG + "--------Inside insertTag()-------------");
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		boolean isTagged = daoHandler.addTagToAudiotron(userName, audiotronName, tag);
		
		logger.info(TAG + "###Info: was the media tagged >> "+isTagged);
		return isTagged;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public void setUserName(String username) {
		this.userName = username;
	}
	
	public void setAudiotronName(String audiotronName) {
		this.audiotronName = audiotronName;
	}
	public String getCustomActionMsg() {
		return customActionMsg;
	}
	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
}
