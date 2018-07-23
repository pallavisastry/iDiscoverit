//Referred:http://stackoverflow.com/questions/4977110/androidvoice-recording-with-timer
package com.sjsu.cmpe295B.idiscoverit.utilities;

import java.util.Date;
/**
 * 
 * This class provides a timer
 * object.
 *
 */
public class TimeCounter {
	private long startTime;
	
	public TimeCounter(){
		startTime = new Date().getTime();
	}
	public long countTime(){
		return new Date().getTime() - startTime;
	}
}
