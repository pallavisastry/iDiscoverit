package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.Media;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * Get the main top rated audiotrons
 * @author pallavi
 *
 */
public class HallOfFameAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = 1698877591219779169L;
	private static final String TAG = "HallOfFameAction";
	private static final Logger logger = Logger.getLogger(HallOfFameAction.class);
	
	private Map<String, Object> session;
	private String customActionMsg;
	private HashMap<String, Long> ratingMap = new HashMap<String, Long>();
	private HashMap<String, Integer> totalHitsMap = new HashMap<String, Integer>();
	private HashMap<String, String> userMap = new HashMap<String, String>();
	private String AUDIOTRONS_RETRIEVAL_SUCCESS = "famedtronsRetrievalSuccess";
	private String AUDIOTRONS_RETRIEVAL_ERROR = "famedtronsRetrievalError";
	
	public String execute() {
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE HallOfFameAction STARTS----------------------------");
		
		session = this.getSession();
		
		getTopRatedAudiotronsFromDB();
		
		if(ratingMap.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE HallOfFameAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_SUCCESS);
			return SUCCESS; //# Returns audio-name list in json.
		}
		else {
			logger.info(TAG + "-----------------------SERVERSIDE HallOfFameAction----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(AUDIOTRONS_RETRIEVAL_ERROR);
			return SUCCESS;//# Return Success but with error msg.
		}
	}
	
	private void getTopRatedAudiotronsFromDB() {
		logger.info(TAG + "--------Inside getTopRatedAudiotronsFromDB(....)---------");
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		ArrayList<Media> mediaList = daoHandler.getHallOfFameAudiotronNames();

		for(Iterator<Media> it = mediaList.iterator();it.hasNext();){
			Media media = it.next();

			ratingMap.put(media.getAudiotronName(),media.getAverageRating());
			totalHitsMap.put(media.getAudiotronName(),media.getTotalHitCount());
			userMap.put(media.getAudiotronName(), media.getUser().getUserName());
		}
		
		logger.info(TAG + " Rating map >>" +ratingMap);
		logger.info(TAG + " Total map >>" +totalHitsMap);
		
		this.setRatingMap(ratingMap);
		this.setTotalHitsMap(totalHitsMap);
	}

	public HashMap<String, Integer> getTotalHitsMap() {
		return totalHitsMap;
	}

	public void setTotalHitsMap(HashMap<String, Integer> totalHitsMap) {
		this.totalHitsMap = totalHitsMap;
	}

	public HashMap<String, Long> getRatingMap() {
		return ratingMap;
	}

	public void setRatingMap(HashMap<String, Long> ratingMap) {
		this.ratingMap = ratingMap;
	}
	public HashMap<String, String> getUserMap() {
		return userMap;
	}

	public void setUserMap(HashMap<String, String> userMap) {
		this.userMap = userMap;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
}
