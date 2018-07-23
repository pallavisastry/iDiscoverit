package com.sjsu.cmpe295B.idiscoverit.main;

import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;
import com.sjsu.cmpe295B.idiscoverit.model.User;
import com.sjsu.cmpe295B.idiscoverit.persistence.IdiscoveritDAOHandler;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MD5HashGenerator;

//TODO:1) Change result type to json for all and if the request comes from web then display web page!!!
public class RegistrationAction extends ActionSupport {

	private static final long serialVersionUID = -3380158248813497266L;
	private static final Logger logger = Logger.getLogger(RegistrationAction.class);
	private static final String TAG = "RegistrationAction";
	
	private static int counter;
	private String username;
	private transient String password;
	
	private User userBean;
	private String hashedPassword;

	public String execute() {
		counter++;
		
		logger.info(TAG + "==============================================================================================");
		logger.info(TAG + "---------SERVERSIDE-RegistrationAction Starts---------------");
		logger.info(TAG + "---------------No. of hits so far ------------>>> "+counter);
		
		hashedPassword = MD5HashGenerator.getMD5(password);//this.getPassword());
		
		setPassword(hashedPassword);
		String userAdditionResult = addUser();
		
		if(userAdditionResult.equals(Constants.REGISTRATION_SUCCESSFUL))
			return SUCCESS;
		else
			return ERROR;
	}
	
	private String addUser() {
		logger.info(TAG + "--------Inside addUser() of RegistrationAction-------");
		
		IdiscoveritDAOHandler daoHandler = new IdiscoveritDAOHandler();
		String result = daoHandler.addUser(username, password);
		logger.info(TAG + "Result of registration is >>>> "+result);
		
		if(result.equals(Constants.REGISTRATION_SUCCESSFUL)) {
			return Constants.REGISTRATION_SUCCESSFUL;
		}
		else if(result.equals(Constants.REGISTRATION_FAIL+Constants.USERNAME_EXISTS)) {
			return Constants.USERNAME_EXISTS;
		}
		else {
			return Constants.REGISTRATION_FAIL;
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}
}
