package com.sjsu.cmpe295B.idiscoverit.utilities;

/*Reffered: http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/*/
import java.io.BufferedInputStream;
//import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/*This class is a utility class of common utility methods to be used across the application*/
public class MiscellaneousUtilities {

	private static final String TAG = "MiscellaneousUtilities";
	//private int bufferSize;

	/**
	 * inputStreamAsString(..) converts stream to string and returs it to the
	 * calling method.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static String inputStreamAsString(InputStream stream)
			throws IOException {
		Log.v(TAG, "-----------Inside inputStreamAsString------------");

		BufferedInputStream bis = new BufferedInputStream(stream);
		StringBuilder sb = new StringBuilder();
		
		int ch;
		
		while((ch = bis.read())!=-1){
			sb.append((char)ch);
		}
		bis.close();
		System.out.println("sb.tostring in inputStreamAsJSONObj>>> "+sb.toString() );
		
		return sb.toString();
	}	
	/***
	 * inputStreamAsJSONObj(..) converts stream to JSONObject and returs it to the
	 * calling method.
	 * 
	 * @param stream
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public static JSONObject inputStreamAsJSONObj(InputStream stream)
			throws JSONException,IOException {
		Log.v(TAG, "-----------Inside inputStreamAsJSONObj------------");
		StringBuilder sb;
		BufferedInputStream bis = new BufferedInputStream(stream);

//		ObjectInputStream ois = new ObjectInputStream(bis);
//		int avail= ois.available();
//		Log.v(TAG,">>>>> Available size of response is >>> "+bis.available());
//		int size = bis.available();
//	
//		if(size>0)
//			sb = new StringBuilder(size);
//		else
		
		sb= new StringBuilder();
		Log.v(TAG,">>>>String builder init >>---->>>--->> "+sb.capacity());
		
		JSONObject jsonObj=null;
		int ch;
		
		while((ch = bis.read())!=-1){
			sb.append((char)ch);
		}
		bis.close();

		System.out.println("sb.tostring in inputStreamAsJSONObj>>> "+sb.toString() );
		
		jsonObj=new JSONObject(sb.toString());
		
		sb = null;
		return jsonObj;
		
	}
	public static JSONObject getInputStreamAsJsonObj(InputStream stream)throws JSONException,IOException {
		Log.v(TAG, "-----------Inside inputStreamAsJSONObj------------");
		BufferedInputStream bis = new BufferedInputStream(stream);
		StringBuilder sb=new StringBuilder(stream.available());
		JSONObject jsonObj=new JSONObject();
		int ch;
		
		while((ch = bis.read())!=-1){
			sb.append((char)ch);
		}
		stream.close();
		bis.close();

		System.out.println("sb.tostring in inputStreamAsJSONObj>>> "+sb.toString() );
		
		jsonObj=new JSONObject(sb.toString());
		
//		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
//		String line;
//		JSONObject jsonObj=new JSONObject();
//		StringBuilder sb=new StringBuilder(stream.available());
//		System.out.println("####Information: String builder capacity = "+sb.capacity()+" & length = "+sb.length());
//		while((line=in.readLine())!=null){
//			sb.append(line+"\n");
//		}
//		stream.close();
//		in.close();
		
		System.out.println(sb);
		jsonObj=new JSONObject(sb.toString());
		return jsonObj;
		
	}
	/***
	 * Function to convert milliseconds to time in 
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * @param milliseconds
	 * @return
	 */
	public static String milliSecondsToTimer(long milliseconds){
		String finalTimerString ="";
		String secondString="";
		
		//Convert total duration into time
		int hours = (int) (milliseconds / (1000*60*60));
		int minutes = (int) (milliseconds % (1000*60*60))/ (1000*60);
		int seconds =(int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		
		//Add hours if there
		if(hours>0){
			finalTimerString =hours + ":";
		}
		//Prepending 0 to seconds if it is 1 digit
		if(seconds < 10){
			secondString = "0" + seconds;
		}else{
			secondString = "" + seconds;
		}
		finalTimerString = finalTimerString + minutes + ":" + secondString;
		
		return finalTimerString;	
	}
	/**
	 * Function to get percentage of progress
	 * @param currentDuration
	 * @param totalDuration
	 * @return
	 */
	public static int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double)0;
		
		long currentSeconds = (int) currentDuration / 1000;
		long totalSeconds = (int) totalDuration / 1000;
		
		percentage = (((double)currentSeconds) / totalSeconds) * 100;
		
		return percentage.intValue();
	}
	/**
	 * Function to change progress to timer
	 * @param progress
	 * @param totalDuration
	 * @return
	 */
	public static int progressToTimer(int progress, int totalDuration){
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int)(((double)progress / 100) * totalDuration);
		
		//Return current duration in milliseconds
		return currentDuration * 1000;
	}
	
}
