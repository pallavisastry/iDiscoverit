package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.FavoriteBean;
import com.sjsu.cmpe295B.idiscoverit.model.FeedbackBean;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class WebUserTrackAction extends ActionSupport implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3850181874343379622L;

	private static final Logger logger = Logger
			.getLogger(WebUserTrackAction.class);
	private transient static final String TAG = "UserTrackAction";

	private List<Media> audiotronListForUserFromDB = new ArrayList<Media>();
	private List<FeedbackBean> audiotronsRatedByUser = new ArrayList<FeedbackBean>();
	private List<FavoriteBean> favoriteAudiotronsListByUser = new ArrayList<FavoriteBean>();

	private String userName;
	private String audiotronName;
	private static String globalUserName = null;
	private long mediaId;
	private long favoritesId;
	Media media;
	private Map session;

	public String execute() {

		logger.info("==============================================================================================");
		logger.info("-----------------------SERVERSIDE UserTrackAction STARTS----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug("......DEBUG LOG.......");
		}
		
		if(session.containsKey("username"))
			globalUserName = (String) session.get("username");
		
		logger.info("User logged in is: " + globalUserName);
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName); 
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter
				.hasNext();)
			logger.info("##### Information: AUDIOTRONS for User >>>>>>>>"
					+ iter.next());
		
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName); 
		
		for (Iterator<FeedbackBean> iter = audiotronsRatedByUser.iterator(); iter
				.hasNext();)
			logger.info("##### Information: Audiotrons rated by User >>>>>>>>"
					+ iter.next());
		
		favoriteAudiotronsListByUser = getAudiotronsAsFavoritesByUserFromDB(globalUserName);
		
		for(Iterator<FavoriteBean> it = favoriteAudiotronsListByUser.iterator(); it.hasNext();)
			logger.info("##### Information: Audiotrons favorited by User >>>>>>>>" + it.next());
		
		if (audiotronListForUserFromDB.size() > 0 || audiotronsRatedByUser.size() > 0 || favoriteAudiotronsListByUser.size() > 0) {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction ENDS WITH SUCCESS----------------------------");
			logger.info("==============================================================================================");

			return SUCCESS; 
		} else {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction ENDS WITH ERROR----------------------------");
			logger.info("==============================================================================================");

			return ERROR;
		}
	}

	private List<Media> getAllAudiotronsForUserFromDB(String userName) {
		logger.info("--------Inside getAllAudiotronsForUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<Media> audiotronList = daoHandler.getAllAudiotronsForUser(userName);
		
		return audiotronList;
	}
	
	private List<FeedbackBean> getAudiotronsRatedByUserFromDB(String userName) {
		logger.info("--------Inside getAudiotronsRatedByUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<FeedbackBean> feedbackList = daoHandler.getAudiotronsRatedByUser(userName);
		
		return feedbackList;
	}
	
	private List<FavoriteBean> getAudiotronsAsFavoritesByUserFromDB(String userName) {
		logger.info("--------Inside getAudiotronsAsFavoritesByUserFromDB(String userName)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		List<FavoriteBean> mediaList = daoHandler.getAudiotronsAsFavoritesByUser(userName);
		
		return mediaList;
	}
	
	public String deleteAudiotron() {
		logger.info("==============================================================================================");
		logger.info("-----------------------SERVERSIDE UserTrackAction inside deleteAudiotron() ----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug("......DEBUG LOG.......");
		}
		
		logger.info("Media id selected is>>>>" + getMediaId());
		
		logger.info("Logged in user for deleting Media()>>>>" + globalUserName);
		
		deleteAudiotronForUserFromDB(globalUserName, getMediaId()); 
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName);
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName);
		favoriteAudiotronsListByUser = getAudiotronsAsFavoritesByUserFromDB(globalUserName);
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter
				.hasNext();)
			logger.info("##### Information: AUDIOTRONS for User after deleting >>>>>>>>"
					+ iter.next());
		
		if (audiotronListForUserFromDB.size() > 0) {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for deleteAudiotron() ENDS WITH SUCCESS----------------------------");
			logger.info("==============================================================================================");

			return SUCCESS; // # Returns audio list in json.
		} else {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for deleteAudiotron() ENDS WITH ERROR----------------------------");
			logger.info("==============================================================================================");

			return ERROR;// # Return an empty json list and handle it!!!
		}
	}
	
	public String deleteAfavoriteAudiotron() {
		logger.info("==============================================================================================");
		logger.info("-----------------------SERVERSIDE UserTrackAction inside deleteAfavoriteAudiotron() ----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug("......DEBUG LOG.......");
		}
		
		logger.info("Favorites id selected is>>>>" + getFavoritesId());
		
		logger.info("Logged in user for deleting Favorite()>>>>>" + globalUserName);
		
		unmarkAnAudiotronAsUnfavoriteForUser(globalUserName, getFavoritesId());
		
		favoriteAudiotronsListByUser = getAudiotronsAsFavoritesByUserFromDB(globalUserName);
		
		for (Iterator<FavoriteBean> iter = favoriteAudiotronsListByUser.iterator(); iter
				.hasNext();)
			logger.info("##### Information: Favorite AUDIOTRONS for User after deleting >>>>>>>>"
					+ iter.next());
		
		if (favoriteAudiotronsListByUser.size() > 0) {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for deleteAfavoriteAudiotron() ENDS WITH SUCCESS----------------------------");
			logger.info("==============================================================================================");

			return SUCCESS; // # Returns audio list in json.
		} else {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for deleteAfavoriteAudiotron() ENDS WITH ERROR----------------------------");
			logger.info("==============================================================================================");

			return ERROR;// # Return an empty json list and handle it!!!
		}
	}
	
	public String edit() {
		logger.info("==============================================================================================");
		logger.info("-----------------------SERVERSIDE UserTrackAction inside edit() ----------------------------");

		if (logger.isInfoEnabled()) {
			logger.info(".....INFORMATION LOG......");
		} else if (logger.isDebugEnabled()) {
			logger.debug("......DEBUG LOG.......");
		}
		
		editAudiotronNameForUser(globalUserName, getMediaId(), audiotronName); //change to non-hardcoded
		
		audiotronListForUserFromDB = getAllAudiotronsForUserFromDB(globalUserName);
		audiotronsRatedByUser = getAudiotronsRatedByUserFromDB(globalUserName);
		//latestAudiotronRecordedByUser = getlatestAudiotronRecordedByUserFromDB(globalUserName);
		
		for (Iterator<Media> iter = audiotronListForUserFromDB.iterator(); iter
				.hasNext();)
			logger.info("##### Information: AUDIOTRONS for User after editing >>>>>>>>"
					+ iter.next());
		
		if (audiotronListForUserFromDB.size() > 0) {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for edit() ENDS WITH SUCCESS----------------------------");
			logger.info("==============================================================================================");

			return SUCCESS; // # Returns audio list in json.
		} else {
			logger.info(TAG
					+ "-----------------------SERVERSIDE UserTrackAction for edit() ENDS WITH ERROR----------------------------");
			logger.info("==============================================================================================");

			return ERROR;// # Return an empty json list and handle it!!!
		}
	}
	
	private void deleteAudiotronForUserFromDB(String userName, long mediaId) {
		logger.info("--------Inside deleteAudiotronForUserFromDB(String userName, Long mediaId)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		daoHandler.deleteAudiotronForUser(userName, mediaId);
	}
	
	private void unmarkAnAudiotronAsUnfavoriteForUser(String userName, long favoritesId) {
		logger.info("--------Inside unmarkAnAudiotronAsUnfavoriteForUser(String userName, Long favoritesId)---------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		daoHandler.unmarkAnAudiotronAsFavoriteForUser(userName, favoritesId);
	}
	
	private void editAudiotronNameForUser(String userName, long mediaId, String audiotronName) {
		logger.info("--------Inside editAudiotronNameForUser(String userName, Long mediaId, String audiotronName)---------");

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

	public List<FavoriteBean> getFavoriteAudiotronsListByUser() {
		return favoriteAudiotronsListByUser;
	}

	public void setFavoriteAudiotronsListByUser(
			List<FavoriteBean> favoriteAudiotronsListByUser) {
		this.favoriteAudiotronsListByUser = favoriteAudiotronsListByUser;
	}

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

	public long getFavoritesId() {
		return favoritesId;
	}

	public void setFavoritesId(long favoritesId) {
		this.favoritesId = favoritesId;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public Map getSession() {
		return session;
	}
}
