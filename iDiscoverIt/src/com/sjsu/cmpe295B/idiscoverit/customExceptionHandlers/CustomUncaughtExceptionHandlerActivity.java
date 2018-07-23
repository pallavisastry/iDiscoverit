package com.sjsu.cmpe295B.idiscoverit.customExceptionHandlers;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;

public class CustomUncaughtExceptionHandlerActivity extends Activity implements UncaughtExceptionHandler {

	public void uncaughtException(Thread thread, Throwable ex) {
		
		System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$   ERROR   $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
		ex.printStackTrace();
		// TODO : handle web problems/http different status using a helper class
	}

}
