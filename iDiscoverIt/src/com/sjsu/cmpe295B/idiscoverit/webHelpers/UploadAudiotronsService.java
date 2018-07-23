package com.sjsu.cmpe295B.idiscoverit.webHelpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.main.HomeActivity;
import com.sjsu.cmpe295B.idiscoverit.main.R;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MiscellaneousUtilities;

public class UploadAudiotronsService extends IntentService {
	
	private static String TAG = "UploadAudiotronsService";
	private static String URL_STRING;
	
	//HttpURLConnection connection = null;
	InputStream inStream = null;
	HttpClient httpClient;
	HttpPost postRequest;
	MultipartEntity reqEntity;

	File file;
	private String selectedCategory;
	private String username;
	private ArrayList<String> filesListToUpload = new ArrayList<String>();
	private String filePath;
	private Long recordTime;
	
	private int NOTIFICATION;
	private NotificationManager mNM;
	Notification.Builder mBuilder;
	private final IBinder mBinder = new LocalBinder();
	SharedPreferences urlPrefs;
	
	public UploadAudiotronsService() {
		super("UploadAudiotronsService");
	}
	
	public class LocalBinder extends Binder{
		UploadAudiotronsService getService(){
			return UploadAudiotronsService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public void onCreate(){
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********UploadAudiotronsService Started***************");
		
		NOTIFICATION=R.string.upload_service_started;
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		/*Pending Intent stack to take the user to Home 
		 * screen on click of notification icon*/
		Intent i = new Intent();
		i.setClass(this, HomeActivity.class);
		TaskStackBuilder sBuilder = TaskStackBuilder.create(this);
		sBuilder.addParentStack(HomeActivity.class);
		sBuilder.addNextIntent(i);
		PendingIntent pin = sBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		
		mBuilder = new Notification.Builder(this);
		mBuilder.setSmallIcon(R.drawable.notification_icon)
	    .setContentTitle("Upload Audiotrons")
	    .setContentIntent(pin);
		
		urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = urlPrefs.getString("uploadAudiotronActivity_URL", Constants.uploadAudiotronActivity_URL);
		
		Map<String,?> keys= urlPrefs.getAll();
		for(Map.Entry<String, ?> entry:keys.entrySet()){
			Log.v(TAG,"<<<<<<<map values>>>> @@@@@@ >>> "+entry.getKey()+" : "+entry.getValue().toString());
		}
	}
	/**
	 * uploadAudiotrons() connects to the server
	 * and uploads files one by one.
	 * It also maintains a notification bar to 
	 * show the progress of the upload.
	 */
	private void uploadAudiotrons(){
		Log.v(TAG,"-------Inside uploadAudiotrons()-----------");
		filePath=Environment.getExternalStorageDirectory()+"/iDiscoverit/OwnAtrons/";
		
		for(int i=0;i<filesListToUpload.size();i++){
			file=new File(filePath+filesListToUpload.get(i).trim());
			Log.v(TAG,"Files to pick up with file path >>> "+file.getName()+" in : "+file.getAbsolutePath());
			
			//#new
			recordTime = urlPrefs.getLong(file.getName().trim(), 0);
			Log.v(TAG,"Recorded time of audiotron of file: "+file.getName()+" is = "+recordTime);
			
			try{
				Log.v(TAG,"----Starting progress notification bar indicator-----");
				mBuilder.setContentText("Upload in progress");
				mBuilder.setProgress(0, 0, false);
				mNM.notify(NOTIFICATION, mBuilder.build());
				
				System.setProperty("http.keepAlive", "false");
	
				httpClient = new DefaultHttpClient();
	
				postRequest = new HttpPost(URL_STRING);
	
				reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
				Log.v(TAG,"####### Information >>> Set reqEntity to multipart mode!#######");
	
				Log.v(TAG,"####### Information >>> Created new file with filebody with file name ==>"+ file.getName());
	
				reqEntity.addPart("audiotron",new org.apache.http.entity.mime.content.FileBody(file));
				reqEntity.addPart("audiotronFileName",new StringBody(file.getName()));
				reqEntity.addPart("recordingTime",new StringBody(String.valueOf(recordTime)));
				reqEntity.addPart("userName",new StringBody(username));
				reqEntity.addPart("category",new StringBody(selectedCategory));
				
				postRequest.setEntity(reqEntity);
				
				Log.v(TAG,"####### Information >>> Executing httpReponse!!! #########");
				HttpResponse httpResponse = httpClient.execute(postRequest);
				Log.v(TAG, "####### Information >>> Execute called");
	
				StatusLine sl = httpResponse.getStatusLine();
				int sc = sl.getStatusCode();
				Log.v(TAG, "####### Information >>> status line == " + sl+ " and status code == " + sc + "#####");
				
				Log.v(TAG, "~~~~~~~~~~~~~~ Information:SERVER RESPONSE >>>");
				inStream = httpResponse.getEntity().getContent();
	
				//Converting instream to json object
				JSONObject jsonObj = MiscellaneousUtilities.inputStreamAsJSONObj(inStream);
				
				String jsonResponseString = jsonObj.getString("audiotronSaveMsg");
				Log.v(TAG," json >>>>> "+jsonObj.getString("audiotronSaveMsg"));
	
				if(jsonResponseString.equals("saveSuccess")){
					Log.v(TAG,"#####Information: Successful audiotron save!! #########");
				}
				else if(jsonResponseString.equals("saveError")){
					Log.v(TAG,"#####Information: Saving audiotron error on DB side #########");
				}else if(jsonResponseString.equals("noUser")){
					Log.v(TAG,"#####Information:Unrgistered User cannot upload audiotrons! #########");
				}

			} catch (UnsupportedEncodingException e) {
				Log.e(TAG, "$$$$$$ EXCEPTION UEE >> msg = " + e.getMessage()
						+ " $$$$$$$$", e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, "$$$$$$ EXCEPTION IAE >> msg = " + e.getMessage()
						+ " $$$$$$$$", e);
			} catch (ClientProtocolException e) {
				Log.e(TAG, "$$$$$$ EXCEPTION CPE >> msg = " + e.getMessage()
						+ " $$$$$$$$", e);
			} catch (IOException e) {
				Log.e(TAG, "$$$$$$ EXCEPTION IO/E >> msg = " + e.getMessage()
						+ " $$$$$$$$", e);
			} catch (JSONException e) {
				Log.e(TAG, "$$$$$$ EXCEPTION JSON >> msg = " + e.getMessage()
						+ " $$$$$$$$", e);
			}
		}mBuilder.setContentText("Upload Complete").setProgress(0,0,false);
		mNM.notify(NOTIFICATION, mBuilder.build());
		
	}
	/**
	 * onStartCommand() will be called by the system
	 * after onCreate(). This method gets information:
	 * file names,category and username and calls the 
	 * upload() on a new thread.
	 *
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG,"-------Inside onStartCommand()-----------");
		
		Bundle extras = intent.getBundleExtra("extras");
		if(extras!=null){
			Log.v(TAG,"###Extras not null####");
			filesListToUpload = extras.getStringArrayList("SDtronsArray");
			selectedCategory = extras.getString("selectedCategory");
			username=extras.getString("username");
			Log.v(TAG,"Arraylist extras >>> "+filesListToUpload+" in category >> "+selectedCategory+" for user = "+username);
		}
		
		new Thread(new Runnable() {
			
			public void run() {
				uploadAudiotrons();				
			}
		}).start();
		Toast.makeText(getApplicationContext(), "Upload successful!", Toast.LENGTH_SHORT).show();
		return START_STICKY;
	}
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG,"-------Inside onHandleIntent of service-------");
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		Log.v(TAG,"-------Inside onDestroy of service-------");
		mNM.cancel(NOTIFICATION);
		Toast.makeText(this,R.string.upload_service_stopped,Toast.LENGTH_LONG).show();
	}
	
}
