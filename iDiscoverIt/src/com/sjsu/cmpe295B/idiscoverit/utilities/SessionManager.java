//http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/
package com.sjsu.cmpe295B.idiscoverit.utilities;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.sjsu.cmpe295B.idiscoverit.main.LoginActivity;

//TODO:Implement broadcast reciever to log out!!!!!!!!
public class SessionManager {

	private String TAG = "SessionManager";
	
	SharedPreferences preferences;
	Editor editor;
	Context _context;
	int PRIVATE_MODE = 0;
	
	private static final String PREF_NAME = "iDiscoveritPreferences";
	private static final String IS_LOGIN = "isLoggedIn";
	public static final String KEY_NAME = "username";
	
	/**
	 * Constructor
	 * @param context
	 */
	public SessionManager(Context context){
		Log.v(TAG,"********SESSION MANAGER*****************");
		this._context = context;
		preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = preferences.edit();
	}
	/**
	 * create login session
	 * @param uName
	 */
	public void createLoginSession(String uName){
		editor.putBoolean(IS_LOGIN, true);
		editor.putString(KEY_NAME, uName);
		editor.commit();
	}
	/**
	 * Checks user login status
	 * If false, redirects user to login activity
	 * else nothing.
	 */
	public void checkLogin(){
		if(!this.isLoggedIn()){
			//user not logged.Redirect to Login Activity
			Intent i = new Intent(_context,LoginActivity.class);
			//closing all activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			//Add new flag to start new activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//Start login activity again
			_context.startActivity(i);
		}
	}
	
	/**
	 * Storing/mapping session data
	 * @return
	 */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> userSessionMap = new HashMap<String, String>();
		//user name
		userSessionMap.put(KEY_NAME,preferences.getString(KEY_NAME, null));
		
		return userSessionMap;
	}
	
	/**
	 * clearing session data
	 */
	public void logoutUser(){
		Log.v(TAG, "----------LOG OUT---------------");
		
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		_context.sendBroadcast(broadcastIntent);
		
		//clearing all data from shared preferences
		editor.clear();
		editor.commit();
		
		Intent i = new Intent(_context,LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_context.startActivity(i);
	}
	public boolean isLoggedIn(){
		return preferences.getBoolean(IS_LOGIN, false);
	}
	
}
