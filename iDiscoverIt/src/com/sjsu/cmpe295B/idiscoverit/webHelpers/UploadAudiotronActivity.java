package com.sjsu.cmpe295B.idiscoverit.webHelpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.main.LoginActivity;
import com.sjsu.cmpe295B.idiscoverit.main.R;
import com.sjsu.cmpe295B.idiscoverit.main.UD_MyAuditronsAcitivity;
import com.sjsu.cmpe295B.idiscoverit.main.VoiceRecordingActivity;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MiscellaneousUtilities;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

//TODO:1) MAKE THIS CLASS AS A SERVICE AND USE ProgressBar to indicate upload !!!!
//TODO:2) MAKE this class re-usable to upload multiple audiotrons from SD card !!!
//TODO:3) UPDATE 'urlString' to use production url later and it should be final/uneditable field !!!!
//TODO:4) Put inputStreamAsString(..) in a utility class to be used by all!!
//TODO:5) response can be verified against http error codes instead of jsp page ???????

/*This class will look into SD card, where an Audiotron(audio-file) is stored and when there is 
 * internet connection uploads to the backing cloud web application through http-client*/
public class UploadAudiotronActivity extends Activity {

	private static String TAG = "UploadAudiotronActivity";
	private static final String UPLOAD_SUCCESS = "UPLOAD_SUCCESS";
	private static final String UPLOAD_FAIL = "UPLOAD_FAIL";
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILURE = "FAILURE";
	private static final String UPLOAD_EXCEPTION="UPLOAD_EXCEPTION";
	private static final String UNKNOWN_ERROR="UNKNOWN_ERROR";
	private static final String UNREGISTERED_USER = "UNREGISTERED_USER";
	
	private String selectedCategory;
	private String audioFileName;
	
	//HttpURLConnection connection = null;
	InputStream inStream = null;
	HttpClient httpClient;
	HttpPost postRequest;
	MultipartEntity reqEntity;
	ResponseHandler<String> responseHandler;
	
	File file;
	FileBody fileBody;
	
	String strResponse = null;
	String filePath = null;
	SessionManager session;
	private static String urlString;
	private BroadcastReceiver uploadActivityReceiver;
			
	ProgressDialog progressDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_recording);
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		progressDialog = ProgressDialog.show(this, "File Upload", "File upload In progress.."); 
		
		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		uploadActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(UploadAudiotronActivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		
		SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		urlString = urlPrefs.getString("uploadAudiotronActivity_URL", Constants.uploadAudiotronActivity_URL);
		
		Log.v(TAG,"************* iDiscoverit APPLICATION STARTED >> (UploadAudiotronActivity Activity) **************");

		audioFileName = getIntent().getStringExtra("audioFileName");
		filePath = getIntent().getStringExtra("filePath");
		selectedCategory = getIntent().getStringExtra("selectedCategory");
		
		Log.v(TAG, " ####Information: File name to be uploaded is >> "+ audioFileName + " #######");
		Log.v(TAG, " ####Information: File Path to check >> "+ filePath + " #######");
		Log.v(TAG, " ####Information: SelectedCategory >> "+ selectedCategory + " #######");

		AudiotronUploadTask audiotronUploadTask = new AudiotronUploadTask();
		Log.v(TAG, "````````````````````STARTING ASYNC TASK````````````````````");
		audiotronUploadTask.execute(urlString);
	}

	/*
	 * This is a private method which will run in the background uploading the
	 * audio file created by the user to the backing web application
	 */
	private String saveAudiotronToServer() {
		Log.v(TAG, "---Inside saveAudiotronToServer------");
		
		HashMap<String, String> user = session.getUserDetails();
		String userName=user.get(SessionManager.KEY_NAME);
		
		try {

			System.setProperty("http.keepAlive", "false");

			httpClient = new DefaultHttpClient();
			responseHandler = new BasicResponseHandler();

			postRequest = new HttpPost(urlString);

			reqEntity = new MultipartEntity(
					HttpMultipartMode.BROWSER_COMPATIBLE);
			Log.v(TAG,"####### Information >>> Set reqEntity to multipart mode!#######");

			file = new File(filePath);
			
			Log.v(TAG,"####### Information >>> Created new file with filebody with file name ==>"
							+ file.getName());

			reqEntity.addPart("audiotron",new org.apache.http.entity.mime.content.FileBody(file));
			reqEntity.addPart("audiotronFileName",new StringBody(file.getName()));
			
			reqEntity.addPart("userName",new StringBody(userName));
			reqEntity.addPart("category",new StringBody(selectedCategory));
			
			postRequest.setEntity(reqEntity);
			
			Log.v(TAG,
					"####### Information >>> Executing httpReponse!!! #########");
			HttpResponse httpResponse = httpClient.execute(postRequest);
			Log.v(TAG, "####### Information >>> Execute called");

			StatusLine sl = httpResponse.getStatusLine();
			int sc = sl.getStatusCode();
			Log.v(TAG, "####### Information >>> status line == " + sl
					+ " and status code == " + sc + "#####");

			Log.v(TAG, "~~~~~~~~~~~~~~ Information:SERVER RESPONSE >>>");
			inStream = httpResponse.getEntity().getContent();
			

			//Converting instream to json object
			JSONObject jsonObj = MiscellaneousUtilities.inputStreamAsJSONObj(inStream);
			
			String jsonResponseString = jsonObj.getString("audiotronSaveMsg");
			Log.v(TAG," json >>>>> "+jsonObj.getString("audiotronSaveMsg"));

			if(jsonResponseString.equals("saveSuccess")){
				Log.v(TAG,"#####Information: Successful audiotron save!! #########");
				return UPLOAD_SUCCESS;
			}
			else if(jsonResponseString.equals("saveError")){
				Log.v(TAG,"#####Information: Saving audiotron error on DB side #########");
				return UPLOAD_FAIL;
			}else if(jsonResponseString.equals("noUser")){
				Log.v(TAG,"#####Information:Unrgistered User cannot upload audiotrons! #########");
				return UNREGISTERED_USER;
			}
			else if(jsonResponseString.equals("mediaNameExists")){
				Log.v(TAG,"#####Information:Media name exists.Please choose another! #########");
				return Constants.MEDIA_NAME_EXISTS;
			}
			
			return UPLOAD_FAIL;

		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION UEE >> msg = " + e.getMessage()
					+ " $$$$$$$$", e);
			return UPLOAD_EXCEPTION;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION IAE >> msg = " + e.getMessage()
					+ " $$$$$$$$", e);
			return UPLOAD_EXCEPTION;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION CPE >> msg = " + e.getMessage()
					+ " $$$$$$$$", e);
			return UPLOAD_EXCEPTION;
		} catch (IOException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION IO/E >> msg = " + e.getMessage()
					+ " $$$$$$$$", e);
			return UPLOAD_EXCEPTION;
		} catch (JSONException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION JSON >> msg = " + e.getMessage()
					+ " $$$$$$$$", e);
			return UPLOAD_EXCEPTION; //conver to json exception
		}
	}
	/**
	 * AudiotronUploadTask is an inner-class that starts a new thread of execution 
	 * to execute network-active code
	 * */
	private class AudiotronUploadTask extends AsyncTask<String, Toast, String> {

		@Override
		protected String doInBackground(String... params) {
			Log.v(TAG,
					"------Inside doInBgnd(..) of UploadAudiotronActivity---------");
			// provide a cancel button
			if (isCancelled())
				return UPLOAD_FAIL;
			else {
				String interimResult = saveAudiotronToServer();
				runOnUiThread(new Runnable() {
					
					public void run() {
						if(progressDialog.isShowing())
							progressDialog.dismiss();
					}
				});
				Log.v(TAG, "####Information>>>>>> result of doInBgnd() >> '"+ interimResult + "' #########");
				return interimResult;
//				if (interimResult.equals(UPLOAD_SUCCESS))
//					return SUCCESS;
//				else if(interimResult.equals(UPLOAD_FAIL))
//					return FAILURE;
//				else if(interimResult.equals(UPLOAD_EXCEPTION))
//					return UPLOAD_EXCEPTION;
//				else
//					return UNKNOWN_ERROR;
				
			}
		}
		@Override
		protected void onPostExecute(String result){
			Log.v(TAG, "------Inside onPostExecute(..)---------");
			
			if (result.equals(UPLOAD_SUCCESS)){
				Log.i(TAG,"result of Upload=============> "+result);
				Toast.makeText(UploadAudiotronActivity.this, "Upload Successful!", Toast.LENGTH_LONG).show();
			}
			else if(result.equals(UPLOAD_FAIL)){
				Log.i(TAG,"result of Upload=============> "+result);
				Toast.makeText(UploadAudiotronActivity.this, "Upload UnSuccessful.Please try again later!", Toast.LENGTH_LONG).show();
			}else if(result.equals(UNREGISTERED_USER)){
				Log.i(TAG,"result of Upload=============> "+result);
				Toast.makeText(UploadAudiotronActivity.this, "Unregistered user.Please register and upload!", Toast.LENGTH_LONG).show();
			}else if(result.equals(Constants.MEDIA_NAME_EXISTS)){
				Log.i(TAG,"result of Upload=============> "+result);
				Toast.makeText(UploadAudiotronActivity.this, "Media name exists!Please choose another name for '"+audioFileName+"' and upload again!!", Toast.LENGTH_LONG).show();
			}
			else {
				Log.i(TAG,"result of Upload=============> "+result);
				Toast.makeText(UploadAudiotronActivity.this, "Internal Error.Please try again later!", Toast.LENGTH_LONG).show();
			}
			//#use handler!!!!!!
			
			/**Going back to previous=Voice recording activity..CHECK IF its better to be a singleTask or Standard!!!!*/
			Intent i = new Intent(
					UploadAudiotronActivity.this,
					VoiceRecordingActivity.class);
			startActivity(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_file_upload_to_cloud, menu);
		return true;
	}
	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(uploadActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(uploadActivityReceiver);
	}
	// #new
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
