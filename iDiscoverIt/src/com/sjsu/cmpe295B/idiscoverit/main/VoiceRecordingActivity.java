package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.NetworkAuthenticator;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.utilities.TimeCounter;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.UploadAudiotronsService;


public class VoiceRecordingActivity extends Activity {

	MediaRecorder mediaRecorder;
	File audioFile = null;
	private static String TAG = "VoiceRecordingActivity";
	private Button uploadButton;
	private ToggleButton recButton;
	private TextView timerTextView;
	private TimeCounterTask task;
	SessionManager session;

	String initValue;
	String[] catArray;
	String selectedCategory;

	String tempFileName;
	File customNamedFile;
	String titleInputValue;
	long recordingSeconds;

	SharedPreferences prefs;
	SharedPreferences.Editor prefsEditor;

	private BroadcastReceiver voiceRecordingActivityReceiver;

	/*
	 * Called when Activity is first called. Setting all the parameters/buttons
	 * here
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice_recording);

		Log.v(TAG,
				"************* iDiscoverit APPLICATION STARTED >> (VoiceREcording Activity) **************");

		session = new SessionManager(getApplicationContext());
		session.checkLogin();

		prefs = this.getSharedPreferences("iDiscoveritPreferences",
				MODE_PRIVATE);
		prefsEditor = prefs.edit();

		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		voiceRecordingActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(VoiceRecordingActivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		String isSkipMessageChecked = prefs.getString("skipMessage_voice", "unchecked");
		Log.v(TAG, "#####is skip message checked??? :" + isSkipMessageChecked);
		if (!(isSkipMessageChecked.equals("checked"))) {
			Intent in = new Intent(VoiceRecordingActivity.this, VR_HelpActivity.class);
			startActivity(in);
		} else if (isSkipMessageChecked.equals("unchecked")) {
			Intent in = new Intent(VoiceRecordingActivity.this, VR_HelpActivity.class);
			startActivity(in);
		}

		setupViews();
	}

	private void setupViews() {

		catArray = getResources().getStringArray(R.array.categoryArray);

		// #new
		timerTextView = (TextView) findViewById(R.id.record_timer);
		int min=0;int sec=0;
		initValue=String.format("%d min, %d sec", min, sec);
		timerTextView.setText(initValue);
		
		recButton = (ToggleButton) findViewById(R.id.rec);
		uploadButton = (Button) findViewById(R.id.uploadBtn);
		uploadButton.setEnabled(false);
		
		recButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					try {
						preRecorderSettings();
					} catch (IOException e) {
						Log.v(TAG,
								"$$$$$IO Exception calling preRecorderSettings()$$$$$$");
						e.printStackTrace();
					}
				} else {
					stopRecording();
				}
			}
		});
		/*
		 * On click on the uploadButton in the view, another activity is called.
		 * This Activity is the UploadAudiotronActivity activity.
		 */
		uploadButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v(TAG, "-------Clicked uploadButton-----");

				if (NetworkAuthenticator
						.checkConnection(VoiceRecordingActivity.this)) {
					Log.v(TAG,
							" ####Information: Launching new upload - Child Thread in VoiceRecording ########");
					// #new...
					chooseCategory();
				} else {
					NetworkAuthenticator.noConnectionToast();
				}
			}
		});
	}

	/*
	 * startRecording method is called when start button is clicked! This info
	 * is provided is activity_voice_recording.xml
	 */
	private void preRecorderSettings() throws IOException {

		Log.v(TAG, "-------Inside preRecorderSettings()-----");

		// -----Setting external path for storing the audio file if external
		// storage is available--------//
		if (!isExternalStorageState()) {
			Toast.makeText(this, "Please make sdcard available for this app!",
					Toast.LENGTH_LONG).show();
			Log.v(TAG, "$$$$%%%% SDCARD unavailable.......");
		}
/*----------------------------------For Real Phone------------------------------*/
		// #Creating directory to hold media files-on real phone
		File f = new File(Environment.getExternalStorageDirectory(),
				"iDiscoverit/OwnAtrons");// #modified
		if (f.exists())
			f.delete();

		Log.v(TAG, ">>>>> is new directory created >>> " + f.mkdir());
		File externalStorageDir = new File(
				Environment.getExternalStorageDirectory()
						+ "/iDiscoverit/OwnAtrons");// #modified

		try {

			Log.v(TAG, "####### Information >> sample Directory is ======> "
					+ externalStorageDir + " ####"); // # mnt/sdcard path

			uploadButton.setEnabled(false);

			audioFile = File.createTempFile("audiotron", ".3gp",
					externalStorageDir); // temporary name for the
											// audiofile//titleValue

			Log.v(TAG, "####### Information >> audio file path ======> "
					+ audioFile.getAbsolutePath() + " and"
					+ " audio_name is =====>" + audioFile.getName() + "######");

		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$$$$$ EXCEPTION >> sdcard access error $$$$$$$$$$$$ && message==>"
							+ e.getMessage());
		}
		/*----------------------------------------------------------------*/
		
		/*--------------------For Emulator--------------------------------------------*/
//		File f = new File(Environment.getExternalStorageDirectory(),"");// #modified
//		if (f.exists())
//			f.delete();
//
//		Log.v(TAG, ">>>>> is new directory created >>> " + f.mkdir());
//		File externalStorageDir = new File(Environment.getExternalStorageDirectory()+ "");// #modified
//
//		try {
//
//			Log.v(TAG, "####### Information >> sample Directory is ======> "+ externalStorageDir + " ####"); // # mnt/sdcard path
//
//			uploadButton.setEnabled(false);
//
//			audioFile = File.createTempFile("audiotron", ".3gp",
//					externalStorageDir); // temporary name for the
//											// audiofile//titleValue
//
//			Log.v(TAG, "####### Information >> audio file path ======> "
//					+ audioFile.getAbsolutePath() + " and"
//					+ " audio_name is =====>" + audioFile.getName() + "######");
//
//		} catch (IOException e) {
//			Log.e(TAG,
//					"$$$$$$$$$$ EXCEPTION >> sdcard access error $$$$$$$$$$$$ && message==>"
//							+ e.getMessage());
//		}
		/*--------------------For Emulator--------------------------------------------*/
		
		PreparingMediaTask preparingMediaTask = new PreparingMediaTask();
		preparingMediaTask.execute();
	}

	private class PreparingMediaTask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... par) {
			String res = prepareMediaRecorder();
			if (res.equals(Constants.MEDIA_PREP_SUCCESS)) {
				return Constants.MEDIA_PREP_SUCCESS;
			} else {
				return Constants.MEDIA_PREP_FAIL;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			Log.v(TAG, "------Inside onPostExecute(..)---------");
			if (result.equals(Constants.MEDIA_PREP_SUCCESS)) {
				Toast.makeText(VoiceRecordingActivity.this, "Please record",
						Toast.LENGTH_SHORT); // this is just for debug!!!
				startRecorder();
			} else {
				Toast.makeText(VoiceRecordingActivity.this,
						"Media Recorder prepartion error.please try aagin!!",
						Toast.LENGTH_LONG).show();
			}

		}
	}

	private String prepareMediaRecorder() {

		// -----Preparing Media Recorder to record audio --------//
		Log.v(TAG, "-------Inside prepareMediaRecorder()-----");
		try {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			// mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// mediaRecorder.setAudioSamplingRate(8000);
			mediaRecorder.setMaxDuration(120000);// 120000=2minutes,60000=0.3
													// min
			mediaRecorder.setOutputFile(audioFile.getAbsolutePath());

			mediaRecorder.prepare();

			return Constants.MEDIA_PREP_SUCCESS;

		} catch (IllegalStateException e) {
			Log.e(TAG,
					"$$$$$$$$$$ EXCEPTION ISE in prepareMediaRecorder() $$$$$$$$$$");
			e.printStackTrace();
			return Constants.MEDIA_PREP_FAIL;
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$$$$$ EXCEPTION ISE in prepareMediaRecorder() $$$$$$$$$$");
			e.printStackTrace();
			return Constants.MEDIA_PREP_FAIL;
		}
	}

	// #new: Starts recorder on the main thread
	private void startRecorder() {
		task = new TimeCounterTask();
		task.execute(new TimeCounter());
		mediaRecorder.start();
	}

	/*
	 * stopRecording method's functionality is similar to start recording.After
	 * recording is completed,the audiotron is stored in media library using
	 * addRecordingToMediaLibrary method
	 */
	private void stopRecording() {
		Log.v(TAG, "-------Inside stopRecording()-----");

		uploadButton.setEnabled(false);
		try {
			mediaRecorder.stop();
			// #new
			if (task != null) {
				if (!task.isCancelled())
					task.cancel(true);
			}
		} catch (IllegalStateException e) {
			Log.e(TAG,"----Recorder was stopped irresponsibly!-----"+ e.getMessage());
			Toast.makeText(this,"Please do not click stop button without recording for a sec",Toast.LENGTH_SHORT).show();
			audioFile.delete();
		}
		showDialogBox();
	}

	private void showDialogBox() {
		Log.v(TAG, "------------Inside showDialogBox()--------------");
		boolean cancel = false;
		// #new:Adding custom title to the recorded audiotron
		final AlertDialog.Builder popAudioTitleDialog = new AlertDialog.Builder(
				VoiceRecordingActivity.this);
		popAudioTitleDialog.setTitle(R.string.custom_audiotron_title);
		final EditText titleInput = new EditText(this);
		popAudioTitleDialog.setView(titleInput);
		titleInput.setFocusable(true);

		popAudioTitleDialog.setPositiveButton("Save",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						titleInputValue = titleInput.getText().toString();

						if (!TextUtils.isEmpty(titleInputValue)) {
							Log.v(TAG,">>>>>Information: Input: custom title entered by user :>>>>> "+ titleInputValue);
							Toast.makeText(getApplicationContext(),titleInputValue + ":Audiotron saved",Toast.LENGTH_LONG).show();
							uploadButton.setEnabled(true);
							addRecordingToMediaLibrary();
							
						} else {
							uploadButton.setEnabled(false);
							Toast.makeText(getApplicationContext(),"File name should not be empty when uploading to cloud",Toast.LENGTH_LONG).show();
							showDialogBox();
						}
					}

				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(
								getApplicationContext(),
								"Audiotron not uploaded! Please upload it later:",
								Toast.LENGTH_LONG).show();
						dialog.cancel();

						final AlertDialog.Builder popup = new AlertDialog.Builder(
								VoiceRecordingActivity.this);
						popup.setTitle("Audiotron temporary storage path");
						popup.setMessage("The recorded audiotron is saved in path: "
								+ audioFile.getAbsolutePath()
								+ " with the temporary"
								+ " name: "
								+ audioFile.getName());
						popup.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
									}
								});
						popup.create();
						popup.show();
					}
				});
		popAudioTitleDialog.create();
		popAudioTitleDialog.show();

	}

	private void chooseCategory() {
		Log.v(TAG, "----------Inside chooseCategory()----------");
		// #Picking a category for the recorded audiotron
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(
				VoiceRecordingActivity.this);
		popDialog.setTitle(R.string.pick_category);
		popDialog.setItems(R.array.categoryArray,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(),
								"chosen category is >>> " + catArray[which],
								Toast.LENGTH_LONG).show();
						selectedCategory = catArray[which];
						uploadButton.setEnabled(false);
						dialog.cancel();
						startUpload();
					}
				});
		popDialog.create();
		popDialog.show();
	}

	/*
	 * addRecordingToMediaLibrary method builds proper name to the recorded
	 * audiotron and saves it in the SD card path
	 */
	protected void addRecordingToMediaLibrary() {
		Log.v(TAG, "-------Inside addRecordingToMediaLibrary()-----");
		ContentValues values = new ContentValues(3);
		long current = System.currentTimeMillis();

		/*--------------------For Real Phone--------------------------------------------*/		
		// #new code to change file name 
		customNamedFile = new File(Environment.getExternalStorageDirectory()
				+ "/iDiscoverit/OwnAtrons", titleInputValue + ".3gp");// #modified

		try {
			FileUtils.copyFile(audioFile, customNamedFile);
			Log.v(TAG, "+++++temp file props >> " + audioFile.isFile()
					+ " length>> " + audioFile.length());
			Log.v(TAG, "+++++new file created >> " + customNamedFile.isFile()
					+ " length>> " + customNamedFile.length());

			if (FileUtils.contentEquals(audioFile, customNamedFile) == true) {
				audioFile.delete();
				Log.v(TAG,
						"--------<Temp audio file deleted succesfully after contents copy-------!!!");
			}
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$ IOException in addRecordingToMediaLibrary() while copying file contents$$$$$");
			e.printStackTrace();
		}
		values.put(MediaStore.Audio.Media.TITLE, customNamedFile.getName());
		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current)); 
		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
		values.put(MediaStore.Audio.Media.DATA,customNamedFile.getAbsolutePath());

		ContentResolver contentResolver = getContentResolver();

		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

		Uri newUri = contentResolver.insert(base, values);

		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
		Toast.makeText(this, "Added File" + newUri, Toast.LENGTH_LONG).show();

		// #new
		if (customNamedFile != null || customNamedFile.getName().equals("")) {
			tempFileName = customNamedFile.getName();
		} else {
			tempFileName = audioFile.getName();
		}
		/*--------------------For Phone--------------------------------------------*/
		/*--------------------For Emulator--------------------------------------------*/
//		customNamedFile = new File(Environment.getExternalStorageDirectory()
//				+ "", titleInputValue + ".3gp");
//
//		try {
//			FileUtils.copyFile(audioFile, customNamedFile);
//			Log.v(TAG, "+++++temp file props >> " + audioFile.isFile()
//					+ " length>> " + audioFile.length());
//			Log.v(TAG, "+++++new file created >> " + customNamedFile.isFile()
//					+ " length>> " + customNamedFile.length());
//
//			if (FileUtils.contentEquals(audioFile, customNamedFile) == true) {
//				audioFile.delete();
//				Log.v(TAG,
//						"--------<Temp audio file deleted succesfully after contents copy-------!!!");
//			}
//		} catch (IOException e) {
//			Log.e(TAG,
//					"$$$$$ IOException in addRecordingToMediaLibrary() while copying file contents$$$$$");
//			e.printStackTrace();
//		}
//		
//		values.put(MediaStore.Audio.Media.TITLE, customNamedFile.getName());
//		values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current)); 
//		values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
//		values.put(MediaStore.Audio.Media.DATA,customNamedFile.getAbsolutePath());
//
//		ContentResolver contentResolver = getContentResolver();
//
//		Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//		Uri newUri = contentResolver.insert(base, values);
//
//		sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
//		Toast.makeText(this, "Added File" + newUri, Toast.LENGTH_LONG).show();
//
//		// #new
//		if (customNamedFile != null || customNamedFile.getName().equals("")) {
//			tempFileName = customNamedFile.getName();
//		} else {
//			tempFileName = audioFile.getName();
//		}
		/*--------------------For Emulator--------------------------------------------*/
		
		prefsEditor.putLong(tempFileName.trim(), recordingSeconds);
		boolean prefsEditSucces = prefsEditor.commit();

		Log.v(TAG, "prefs sucess edit > " + prefsEditSucces);
		Log.v(TAG, "seconds is ---> " + prefs.getLong(tempFileName.trim(), 0));

	}

	private void startUpload() {
		Log.v(TAG, "-------Inside startUpload()-----");
		ArrayList<String> myAudiotronsArrList = new ArrayList<String>();
		myAudiotronsArrList.add(customNamedFile.getName());
		
		/*
		 * # on click of upload button, the name of the newly added file is sent
		 * to the file uploader service
		 */
		Intent intent = new Intent(VoiceRecordingActivity.this,
				UploadAudiotronsService.class);
		Bundle extras = new Bundle();
		intent.putExtra("extras", extras);
		extras.putStringArrayList("SDtronsArray", myAudiotronsArrList);
		extras.putString("selectedCategory", selectedCategory);
		extras.putString("username", session.getUserDetails().get("username"));
		startService(intent);
	}

	/**
	 * 
	 * Time counter for recorder
	 * 
	 */
	private class TimeCounterTask extends AsyncTask<TimeCounter, Long, Void> {

		@Override
		protected Void doInBackground(TimeCounter... params) {
			TimeCounter tc = params[0];
			while (true) {
				if (isCancelled())
					break;

				publishProgress(tc.countTime());
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.e(TAG,"$$$$$$$$ Interrupted exception in TimeCounterTask$$$$$$"	+ e);
					break;
				}
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			super.onProgressUpdate(values);
			long millis = values[0].longValue();
			int minutes = (int) ((millis / 1000) / 60);
			int seconds = (int) ((millis / 1000) % 60);

			Log.v(TAG, ".......Total duration of recording in seconds = "+ millis / 1000);
			long fullTime = millis / 1000;
			setRecordingTime(fullTime);

			String s = String.format("%d min, %d sec", minutes, seconds);
			timerTextView.setText(" \n");
			timerTextView.setText(s);
		}
	}

	private void setRecordingTime(long rtime) {
		recordingSeconds = rtime;

	}

//	private boolean isFileNameExists() {
//		Log.v(TAG, "----Inside isFileNameExists()--------");
//		String files;
//		File f = new File(Environment.getExternalStorageDirectory()
//				+ "/iDiscoverit/OwnAtrons");
//		File[] listOfFiles = f.listFiles();
//
//		for (int i = 0; i < listOfFiles.length; i++) {
//			if (listOfFiles[i].isFile()) {
//				files = listOfFiles[i].getName();
//				Log.v(TAG, "File names in folder >> " + files);
//				Log.v(TAG, "Title value input >> " + titleInputValue);
//				String prunedFileName = FilenameUtils.removeExtension(files);
//				Log.v(TAG, "Pruned file name >> " + prunedFileName);
//				if (prunedFileName.equals(titleInputValue)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	/**
	 * Checking only for 2 states: if external storage is available and
	 * writeable, returns true else false;
	 * 
	 * @return
	 */
	private boolean isExternalStorageState() {
		String mExternalStorageState = Environment.getExternalStorageState();
		Log.v(TAG, "------External storage of the device state :---->"
				+ mExternalStorageState);

		if (Environment.MEDIA_MOUNTED.equals(mExternalStorageState)) {
			// mExternalStorageAvailble = mExternalStorageWriteable = true;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_voice_recording, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_home:
			startActivity(new Intent(VoiceRecordingActivity.this,
					HomeActivity.class));
			return true;
		case R.id.menu_help:
			displayHelpAlert();
        	return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(VoiceRecordingActivity.this,VR_HelpActivity.class);
    	startActivity(in);
	}

	// private void shareItem(){
	// byte[] fBytes = new byte[(int) customNamedFile.length()];
	// try {
	// fBytes = IOUtils.toByteArray(new FileReader(customNamedFile));
	// } catch (FileNotFoundException e) {
	// Log.v(TAG,"$$$$$FFE while converting custom file to byte Array in shareItem()"+e);
	// } catch (IOException e) {
	// Log.v(TAG,"$$$$$IOE while converting custom file to byte Array in shareItem()"+e);
	// }
	//
	// Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	// sharingIntent.setType("audio/3gp");
	// sharingIntent.putExtra("audio",customNamedFile);
	// startActivity(Intent.createChooser(sharingIntent,"Share via"));
	// }
	// ------Activity's lifecycle methods s----------//
	// #new
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	// #new
	@Override
	public void onResume() {
		super.onResume();

		if (mediaRecorder == null)
			setupViews();

		Log.d(TAG, "---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(voiceRecordingActivityReceiver, iFilter);

	}

	@Override
	public void onPause() {
		super.onPause();

		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
		Log.d(TAG, "---onPause->UN-Registering receiver-----");
		unregisterReceiver(voiceRecordingActivityReceiver);
	}

	// #new
	@Override
	public void onStop() {
		super.onStop();

		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}

	}

	// #new
	@Override
	public void onStart() {
		super.onStart();
		if (mediaRecorder == null)
			setupViews();
	}

	// #new
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mediaRecorder != null) {
			mediaRecorder.release();
			mediaRecorder = null;
		}
		android.os.Debug.stopMethodTracing();
	}

}
