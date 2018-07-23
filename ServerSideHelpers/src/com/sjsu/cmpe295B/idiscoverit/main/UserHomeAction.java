package com.sjsu.cmpe295B.idiscoverit.main;

import org.apache.log4j.Logger;
import com.opensymphony.xwork2.ActionSupport;

public class UserHomeAction extends ActionSupport {

	private static final long serialVersionUID = -7839290042851069167L;
	private static final Logger logger = Logger.getLogger(UserHomeAction.class);
	private String userName;
	
	public String execute(){
		//Do user stuff and get user's details here !!!
		
		return SUCCESS;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
