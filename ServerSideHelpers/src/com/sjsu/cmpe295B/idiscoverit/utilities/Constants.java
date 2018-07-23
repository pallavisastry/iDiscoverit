package com.sjsu.cmpe295B.idiscoverit.utilities;

/**
 * 
 * @author pallavi
 *This is an interface of constants
 */
public interface Constants {

	String MEDIA_SAVE_TO_DB_SUCCESS = "media_save_successful";
	String MEDIA_SAVE_TO_DB_FAIL    = "media_save_fail";
	
	String USERNAME_EXISTS 		   = "This username is already taken. Please enter a different name!";
	String REGISTRATION_SUCCESSFUL = "Congratulations. Registration Successful";
	String REGISTRATION_FAIL 	   = "Registration unsuccessful. Sorry, please register again!";
	boolean USER_EXISTS 		   = true;
	String MEDIA_EXISTS 		   = "This audiotron name is already taken. Please enter a differnt name!";
	
	String REGISTERED_USER 		   = "Ooops. You are already a registered user; please use your login!";
	String UNREGISTERED_USER 	   = "Username/Password does not exist.Please register";
	
	String LOGIN_SUCCESS 	 ="Login Successful";
	String LOGIN_FAIL 	 	 = "Login fail. Please check username and password";
	String AUDIORENDER_ERROR = "Sorry.No Audiotron";
	
	String FAIL = "FAIL";
	
	String DEVICE_LOGIN_URL			  = "/ServerSideHelpers/deviceLoginAction";
	String HALL_OF_FAME_URL			  = "/ServerSideHelpers/hallOfFameAction";
	String AUDIOTRAON_SAVE_URL 		  = "/ServerSideHelpers/audiotronSaveAction";
	String CATEGORY_LIST_URL 		  = "/ServerSideHelpers/categoryListDisplayAction";
	String AUDIOTRONS_IN_CATEGORY_URL = "/ServerSideHelpers/audiotronsInCategoryDisplayAction";
	String FAVORITES_ACTION_URL 	  = "/ServerSideHelpers/addToFavoritesAction";
	String FEEDBACK_ACTION_URL 		  = "/ServerSideHelpers/feedbackAction";
	String MY_AUDIOTRONS_ACTION_URL   = "/ServerSideHelpers/myAudiotronsAction";
	String MY_FAVORITES_URL 		  = "/ServerSideHelpers/myFavoritesListAction";
	String RENDER_AUDIOTRONS_URL 	  = "/ServerSideHelpers/renderAudiotronsAction";
}
