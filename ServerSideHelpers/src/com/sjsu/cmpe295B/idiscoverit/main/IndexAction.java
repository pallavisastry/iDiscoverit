package com.sjsu.cmpe295B.idiscoverit.main;

import com.opensymphony.xwork2.ActionSupport;

/**
 * A general welcome class when the url is for index page.
 * The index jsp page will redirect action to login action and
 * jsp page!
 * @author pallavi
 *
 */
public class IndexAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5658821771406546205L;
	
	public String execute(){
		return SUCCESS;
	}

}
