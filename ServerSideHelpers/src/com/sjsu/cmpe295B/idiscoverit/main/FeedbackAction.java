package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class FeedbackAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = -4236100809860581953L;
	private static final Logger logger = Logger.getLogger(FeedbackAction.class);
	private static final String TAG = "FeedbackAction";
	
	private String rating;
	private String audiotronName;
	boolean isFeedbackAdded;
	private String flag;
	private boolean isFlag = false;
	private String userName;
	private String customActionMsg;
	private Map<String, Object> session;
	private String FEEDBACK_SAVE_SUCCESS = "feedbackSaveSuccess";
	private String FEEDBACK_SAVE_ERROR = "feedbackSaveError";
	
	public String execute() {
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE FeedbackAction STARTS----------------------------");
		
		isFeedbackAdded = addRatingInDB();
		logger.info(TAG + "#####Information: was feedback added :: "+isFeedbackAdded);
		
		if(isFeedbackAdded){
			logger.info(TAG + "--------------Feedback successfully added----------");
			setCustomActionMsg(FEEDBACK_SAVE_SUCCESS);
			return SUCCESS;
		}
		else { 
			logger.info(TAG + "--------------Feedback NOT added.Already in the DB----------");
			setCustomActionMsg(FEEDBACK_SAVE_ERROR);
			return SUCCESS; //#return error msg!
		}
		
	}

	private boolean addRatingInDB(){
		logger.info(TAG + "-----------------------Inside addRatingInDB() of FeedbackAction----------------------------");
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		logger.info(TAG + "###Info flag is >>>>"+flag);
		logger.info(TAG + "###Info RAting is >>>>"+rating);
		
		if(flag.equals("yes")){
			isFlag=true;
		}
		
		boolean isAdded = daoHandler.addFeedbackToMedia(rating, isFlag,audiotronName, userName);
		
		logger.info(TAG + "###Info: result of adding feedback "+isAdded);
		
		return isAdded;
		
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public void setAudiotronName(String audiotronName) {
		this.audiotronName = audiotronName;
	}

//	public void setFlag(boolean isFlag) {
//		this.isFlag = isFlag;
//	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
	
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session=arg0;
	}
	public Map<String, Object> getSession(){
		return this.session;
	}

}
