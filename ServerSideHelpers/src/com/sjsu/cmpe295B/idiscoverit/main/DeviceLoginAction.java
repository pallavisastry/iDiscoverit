package com.sjsu.cmpe295B.idiscoverit.main;

import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.SessionAware;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MD5HashGenerator;

/**
 * This class authenticates a user on login
 * 
 * @author pallavi
 *
 */
public class DeviceLoginAction extends ActionSupport implements SessionAware{

	private static final long serialVersionUID = 8170391717887197767L;
	private static final Logger logger = Logger.getLogger(DeviceLoginAction.class);
	private static final String TAG = "DeviceLoginAction";

	private String userName;
	private String password;
	
	private boolean isRegSuccess;
	private boolean isLoginSuccess;
	
	private Map<String, Object> session;
	
	public String execute() {
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "---------SERVERSIDE-DeviceLoginAction Starts---------------");

		session = this.getSession();
		
		String hashedPassword;
		hashedPassword = MD5HashGenerator.getMD5(password);
		this.setPassword(hashedPassword);
		String result = authenticateUser();
		
		logger.info(TAG + "result of authenticateUser >>>>>>" + result);

		if (result.equals(Constants.LOGIN_SUCCESS)) {
			session.put("loggedin", true);
			session.put("username", userName);
			session.put("userExists", ""); // #This value is only to check if user name already exists and pass it to caller.
			this.setLoginSuccess(true);
			this.setRegSuccess(false);
			return SUCCESS;
		} else if (result.equals(Constants.REGISTRATION_SUCCESSFUL)) {
			session.put("loggedin", true);
			session.put("username", userName);
			session.put("userExists", "");
			this.setRegSuccess(true);
			this.setLoginSuccess(true);
			return SUCCESS;
		} else if (result.equals(Constants.USERNAME_EXISTS)) {
			session.put("loggedin", false);
			session.put("username", "");
			session.put("userExists", "userExists");
			this.setRegSuccess(false);
			this.setLoginSuccess(false);
			return SUCCESS;
		} else if (result.equals(Constants.REGISTRATION_FAIL
				+ Constants.LOGIN_FAIL)) {
			session.put("loggedin", false);
			session.put("username", "");
			session.put("userExists", "");
			this.setRegSuccess(false);
			this.setLoginSuccess(false);
			return SUCCESS;
		} else if (result.equals(Constants.REGISTRATION_FAIL)) {
			session.put("loggedin", false);
			session.put("username", "");
			session.put("userExists", "");
			this.setRegSuccess(false);
			this.setLoginSuccess(false);
			return SUCCESS;
		}
		 else
			return ERROR;
	}
	
	private String authenticateUser(){
		logger.info(TAG + "-------------Inside authenticateUser()----------");
		
		String result;
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		boolean isAuthentic = daoHandler.isAuthenticated(userName, password);
		
		logger.info(TAG + "###### Result of isAuthenticated: user authenticated : "+isAuthentic);
		
		if(!isAuthentic) { //#not authenticated
			//Signing user up;Change this to if the user requests for it!
			result = daoHandler.addUser(userName, password);
			
			if(result.equals(Constants.REGISTRATION_SUCCESSFUL)) {
				return Constants.REGISTRATION_SUCCESSFUL;
			}
			else if(result.equals(Constants.USERNAME_EXISTS)) {
				return Constants.USERNAME_EXISTS;
			}
			else if(result.equals(Constants.REGISTRATION_FAIL+Constants.LOGIN_FAIL)) {
				return Constants.REGISTRATION_FAIL+Constants.LOGIN_FAIL;
			}
			else {
				return Constants.REGISTRATION_FAIL;
			}
		}
		else {
			return Constants.LOGIN_SUCCESS;
		}
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRegSuccess() {
		return isRegSuccess;
	}

	public void setRegSuccess(boolean isRegSuccess) {
		this.isRegSuccess = isRegSuccess;
	}

	public boolean isLoginSuccess() {
		return isLoginSuccess;
	}

	public void setLoginSuccess(boolean isLoginSuccess) {
		this.isLoginSuccess = isLoginSuccess;
	}
	
	public Map<String, Object> getSession() {
		return session;
	}
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
}
