package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.FeedbackBean;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * 
 * @author pallavi
 *
 */
public class UserTrackAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = 3850181874343379622L;
	private static final Logger logger = Logger.getLogger(UserTrackAction.class);
	private transient static final String TAG = "UserTrackAction";

	private List<Media> audiotronListForUserFromDB = new ArrayList<Media>();
	private List<FeedbackBean> audiotronsRatedByUser = new ArrayList<FeedbackBean>();
	private String userName;
	private String audiotronName;
	private static String globalUserName = null;
	private long mediaId;
	Media media;
	private Map<String, Object> session;

	public String execute() {

		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction STARTS----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		if(session.containsKey("username"))
			globalUserName = (String) session.get("username");
		
		logger.info(TAG + "User logged in is: " + globalUserName);
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName); //change to non-hardcoded
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter.hasNext();)
			logger.info(TAG + "##### Information: AUDIOTRONS for User >>>>>>>>" + iter.next());
		
		/*latestAudiotronRecordedByUser = getlatestAudiotronRecordedByUserFromDB(globalUserName); //change to non-hardcoded
		
		for (Iterator<Media> iter = latestAudiotronRecordedByUser.iterator(); iter
				.hasNext();)
			logger.info("##### Information: Latest Recorded Audiotron for User >>>>>>>>"
					+ iter.next());*/
		
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName); //change to non-hardcoded
		
		for (Iterator<FeedbackBean> iter = audiotronsRatedByUser.iterator(); iter.hasNext();)
			logger.info(TAG + "##### Information: Audiotrons rated by User >>>>>>>>" + iter.next());
		
		if (audiotronListForUserFromDB.size() > 0 || audiotronsRatedByUser.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction ENDS WITH SUCCESS----------------------------");
			logger.info(TAG + "==============================================================================================");
			return SUCCESS; 
		} 
		else {
			logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction ENDS WITH ERROR----------------------------");
			logger.info(TAG + "==============================================================================================");
			return ERROR;
		}
	}

	private List<Media> getAllAudiotronsForUserFromDB(String userName) {
		logger.info(TAG + "--------Inside getAllAudiotronsForUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<Media> audiotronList = daoHandler.getAllAudiotronsForUser(userName);
		
		return audiotronList;
	}
	
	/*private List<Media> getlatestAudiotronRecordedByUserFromDB(String userName) {
		logger.info("--------Inside getlatestAudiotronRecordedByUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<Media> audiotronList = daoHandler.getlatestAudiotronRecordedByUser(userName);
		
		return audiotronList;
	}*/
	
	private List<FeedbackBean> getAudiotronsRatedByUserFromDB(String userName) {
		logger.info(TAG + "--------Inside getAudiotronsRatedByUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<FeedbackBean> feedbackList = daoHandler.getAudiotronsRatedByUser(userName);
		
		return feedbackList;
	}
	
	public String delete() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction inside delete() ----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		logger.info(TAG + " Media id selected is>>>>" + getMediaId());
		
		logger.info(TAG + " Logged in user for delete()>>>>" + globalUserName);
		
		deleteAudiotronForUserFromDB(globalUserName, getMediaId()); //change to non-hardcoded
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName);
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName);
		//latestAudiotronRecordedByUser = getlatestAudiotronRecordedByUserFromDB(globalUserName);
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter.hasNext();)
			logger.info(TAG + "##### Information: AUDIOTRONS for User after deleting >>>>>>>>"+ iter.next());
		
		if (audiotronListForUserFromDB.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction for delete() ENDS WITH SUCCESS----------------------------");
			logger.info(TAG + "==============================================================================================");
			return SUCCESS; // # Returns audio list in json.
		} 
		else {
			logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction for delete() ENDS WITH ERROR----------------------------");
			logger.info(TAG + "==============================================================================================");
			return ERROR;// # Return an empty json list and handle it!!!
		}
	}
	
	public String edit() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE UserTrackAction inside edit() ----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(TAG + ".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		editAudiotronNameForUser(globalUserName, getMediaId(), audiotronName); //change to non-hardcoded
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName);
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName);
		//latestAudiotronRecordedByUser = getlatestAudiotronRecordedByUserFromDB(globalUserName);
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter.hasNext();)
			logger.info(TAG + "##### Information: AUDIOTRONS for User after editing >>>>>>>>" + iter.next());
		
		if (audiotronListForUserFromDB.size() > 0) {
			logger.info(TAG	+ "-----------------------SERVERSIDE UserTrackAction for edit() ENDS WITH SUCCESS----------------------------");
			logger.info(TAG + "==============================================================================================");
			return SUCCESS; // # Returns audio list in json.
		} 
		else {
			logger.info(TAG	+ "-----------------------SERVERSIDE UserTrackAction for edit() ENDS WITH ERROR----------------------------");
			logger.info(TAG + "==============================================================================================");
			return ERROR;// # Return an empty json list and handle it!!!
		}
	}
	
	private void deleteAudiotronForUserFromDB(String userName, long mediaId) {
		logger.info(TAG + "--------Inside deleteAudiotronForUserFromDB(String userName, Long mediaId)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		daoHandler.deleteAudiotronForUser(userName, mediaId);
	}
	
	private void editAudiotronNameForUser(String userName, long mediaId, String audiotronName) {
		logger.info(TAG + "--------Inside editAudiotronNameForUser(String userName, Long mediaId, String audiotronName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		daoHandler.updateAudiotronNameForUser(mediaId, userName, audiotronName);
	}

	public List<Media> getAudiotronListForUserFromDB() {
		return audiotronListForUserFromDB;
	}

	public void setAudiotronListForUserFromDB(
			List<Media> audiotronListForUserFromDB) {
		this.audiotronListForUserFromDB = audiotronListForUserFromDB;
	}

	public String getAudiotronName() {
		return audiotronName;
	}

	public void setAudiotronName(String audiotronName) {
		this.audiotronName = audiotronName;
	}

	public long getMediaId() {
		return mediaId;
	}

	public void setMediaId(long mediaId) {
		this.mediaId = mediaId;
	}

	/*public List<Media> getLatestAudiotronRecordedByUser() {
		return latestAudiotronRecordedByUser;
	}

	public void setLatestAudiotronRecordedByUser(
			List<Media> latestAudiotronRecordedByUser) {
		this.latestAudiotronRecordedByUser = latestAudiotronRecordedByUser;
	}*/

	public List<FeedbackBean> getAudiotronsRatedByUser() {
		return audiotronsRatedByUser;
	}

	public void setAudiotronsRatedByUser(List<FeedbackBean> audiotronsRatedByUser) {
		this.audiotronsRatedByUser = audiotronsRatedByUser;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
}
