package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MD5HashGenerator;


/**
 * This action class is used only when a user logs in from a web page.
 * @author pallavi
 *
 */
//TODO:1) Change result type to json for all and if the request comes from web then display web page!!!
public class LoginAction extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 3921703242124204473L;
	private static final Logger logger = Logger.getLogger(LoginAction.class);
	private static final String TAG = "LoginAction";
	private static int counter;
	
	private String userName;
	private String password;
	private String errorMessage;
	private String hashedPassword;
	private Map<String, Object> session;

	public String execute() {
		counter++;

		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "---------SERVERSIDE-LoginAction Starts---------------");
		logger.info(TAG + "---------------No. of hits so far ------------>>> "+ counter);

		session = this.getSession();
		hashedPassword = MD5HashGenerator.getMD5(password);
		setPassword(hashedPassword);
		this.setPassword(hashedPassword);

		boolean result = authenticateUser();
		logger.info(TAG + "Result of authenticateUser >>>>>>" + result);

		if (result) {
			session.put("loggedin", true);
			session.put("username", userName);
			return SUCCESS;
		} 
		else {
			session.put("loggedin", false);
			setErrorMessage(Constants.LOGIN_FAIL);
			return ERROR;
		}
	}

	private boolean authenticateUser() {
		logger.info("-------------Inside authenticateUser()----------");

		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		boolean isAuthentic = daoHandler.isAuthenticated(userName, password);

		logger.info(TAG + "###### Result is ###### " + isAuthentic);

		if (isAuthentic)
			return true;
		else
			return false;
	}

	/* .....Getters/Setters..... */

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;

	}

	public Map<String, Object> getSession() {
		return session;
	}
}