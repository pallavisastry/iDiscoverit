package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

/**
 * This class is used to add/mark media as favorites
 * @author pallavi
 *
 */
public class FavoritesAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = 848782587646636374L;
	private static final Logger logger = Logger.getLogger(FavoritesAction.class);
	private static final String TAG = "FavoritesAction";
	
	private String userName;
	private String audiotronName;
	private String favorite;
	private String customActionMsg;
	private boolean isFavoriteAdded;
	private Map<String, Object> session;
	private String ADD_SUCCESS_MSG = "addSuccess";
	private String ADD_ERROR_MSG = "addError";
	
	public String execute() {
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE FavoritesAction STARTS----------------------------");

		session = this.getSession();
		logger.info(TAG + "~~~~ Favorites Session obj ~~~~~~~>>> "+session.keySet()+" values~~~~~~"+session.values());
		
		isFavoriteAdded = addToFavorites();
		logger.info(TAG + "#####Information: favorites added :: "+isFavoriteAdded);
		
		if(isFavoriteAdded){
			logger.info(TAG + "--------------Favorites added successfully----------");
			setCustomActionMsg(ADD_SUCCESS_MSG);
			return SUCCESS;
		}else{
			logger.info(TAG + "--------------Favorites NOT added.Already in the DB----------");
			setCustomActionMsg(ADD_ERROR_MSG);
			return SUCCESS; //#Already in favorites
		}
	}
	
	private boolean addToFavorites(){
		logger.info(TAG + "-----------------------Inside addToFavorites() of FavoritesAction----------------------------");

		boolean isFavorite;
		boolean isAdded = false;
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		
		if(favorite.equals("yes")){
			isFavorite=true;
			isAdded = daoHandler.addToFavorites(userName, audiotronName, isFavorite);
		}
		
		return isAdded;
	}
	
	@Override
	public void setSession(Map<String, Object> arg0) {
		this.session=arg0;
	}
	
	public Map<String, Object> getSession(){
		return this.session;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}

	public String getAudiotronName() {
		return audiotronName;
	}

	public void setAudiotronName(String audiotronName) {
		this.audiotronName = audiotronName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFavorite() {
		return favorite;
	}
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
}	
