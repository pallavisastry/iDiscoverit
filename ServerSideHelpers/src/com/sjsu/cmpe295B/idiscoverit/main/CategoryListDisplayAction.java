package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.List;
import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;

public class CategoryListDisplayAction extends ActionSupport{

	private static final long serialVersionUID = -4226768929660943920L;

	private static final Logger logger = Logger.getLogger(CategoryListDisplayAction.class);
	private static final String TAG = "CategoryListDisplayAction";
	
	private List<String> categoryListFromDB;
	private String customActionMsg;
	private static String CATEGORY_RETRIEVAL_SUCCESS = "categoryRetrievalSuccess";
	private static String CATEGORY_RETRIEVAL_ERROR = "categoryRetrievalError";
	
	public String execute(){
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "-----------------------SERVERSIDE CategoryListDisplayAction STARTS----------------------------");
		
		if(logger.isInfoEnabled()){
			logger.info(TAG + ".....INFORMATION LOG......");
		}else if(logger.isDebugEnabled()){
			logger.debug(TAG + "......DEBUG LOG.......");
		}
		
		categoryListFromDB = getAllCategoryListFromDB();
		logger.info(TAG + " List from DB >>> "+categoryListFromDB.size());
		
		if(categoryListFromDB.size() > 0) {
			logger.info(TAG + "-----------------------SERVERSIDE CategoryListDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(CATEGORY_RETRIEVAL_SUCCESS);
			return SUCCESS; //# Returns category list in json.
		} else {
			logger.info(TAG + "-----------------------SERVERSIDE CategoryListDisplayAction ENDS----------------------------");
			logger.info(TAG + "==============================================================================================");
			setCustomActionMsg(CATEGORY_RETRIEVAL_ERROR);
			setCategoryListFromDB(null);
			return SUCCESS;//# Return an empty json list and handle it!!!
		}
	}

	private List<String> getAllCategoryListFromDB(){
		logger.info(TAG + "--------Inside getCategoryListFromDB()---------");
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		
		List<String> categoryList = daoHandler.getAllCategoriesFromDB();
		return categoryList;
		
	}
	
	public void setCategoryListFromDB(List<String> categoryListFromDB) {
		this.categoryListFromDB = categoryListFromDB;
	}
	
	public List<String> getCategoryListFromDB(){
		return categoryListFromDB;
	}

	public String getCustomActionMsg() {
		return customActionMsg;
	}

	public void setCustomActionMsg(String customActionMsg) {
		this.customActionMsg = customActionMsg;
	}
}
