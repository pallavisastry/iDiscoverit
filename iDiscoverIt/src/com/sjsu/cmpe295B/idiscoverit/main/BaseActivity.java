package com.sjsu.cmpe295B.idiscoverit.main;

import android.app.Activity;

import com.sjsu.cmpe295B.idiscoverit.customExceptionHandlers.CustomUncaughtExceptionHandlerActivity;

public class BaseActivity extends Activity{

	CustomUncaughtExceptionHandlerActivity customUncaughtExceptionHandlerActivity = new CustomUncaughtExceptionHandlerActivity();
	public BaseActivity(){
		Thread.currentThread().setUncaughtExceptionHandler(customUncaughtExceptionHandlerActivity);
	}    
}
