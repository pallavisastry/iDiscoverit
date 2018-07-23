package com.sjsu.cmpe295B.idiscoverit.webHelpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;

import com.sjsu.cmpe295B.idiscoverit.main.AudiotronPlayerActivity;
import com.sjsu.cmpe295B.idiscoverit.main.LoginActivity;
import com.sjsu.cmpe295B.idiscoverit.main.R;
import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

//TODO:CHECK FOR audiotrons favorred/feedbacked-----if so DONT SHOW buttons respectively!!!!
//****MAKE THIS AS A SERVICE!!!
public class StreamAudiotronsActivity extends Activity {// implements
														// OnClickListener,OnTouchListener,OnCompletionListener,OnBufferingUpdateListener{

	private static String TAG = "StreamAudiotronsActivity";

//	private Button streamBtn;
	private BroadcastReceiver streamAcivityReceiver;
	
//	private final Handler handler = new Handler();
//	private MediaPlayer mediaPlayer;
	
	private File downloadedMediaFile;
	private URLConnection con;
	private InputStream inputStream;
	FileOutputStream outStream;
	File bufferedFile;
	
	private SessionManager session;
	private String URL_STRING ;

	private String audiotronNameClicked;
	String completeUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_stream_audiotrons);

		Log.v(TAG, "**********************************************************");
		Log.v(TAG, "***********STreamAudiotron Activity Started***************");
		setupView();
	}

	private void setupView() {

		Log.v(TAG, "---------Inside setupView() of StreamAudiotronActivity");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		// #Setting a dynamic broadcast receiver to logout completely.
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
				streamAcivityReceiver = new BroadcastReceiver() {

					@Override
					public void onReceive(Context context, Intent intent) {
						Log.d(TAG, "||||| Logout in progress ||||||");
						// Starting login activity
						Intent i = new Intent(StreamAudiotronsActivity.this,
								LoginActivity.class);
						startActivity(i);
						finish();
					}
				};
		
		SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = urlPrefs.getString("streamAudiotronsActivity_URL", Constants.streamAudiotronsActivity_URL);
		
		Log.d(TAG,"---------Preferred URL is ----->"+URL_STRING);
		
		//streamBtn = (Button) findViewById(R.id.btnStream);

		// Get the name of the clicked audiotron
		audiotronNameClicked = getIntent().getStringExtra("audiotronName").trim();
		Log.v(TAG,"##### Information: Audiotron to request>>>>> "+audiotronNameClicked);
		
		//streamBtn.setOnClickListener(new View.OnClickListener() 
		//{
		//	public void onClick(View v) 
		//	{

				new Thread(new Runnable() {
					public void run() {
						try {
							Uri.Builder uriBuilder = Uri.parse(URL_STRING).buildUpon();
							uriBuilder.path("ServerSideHelpers/renderAudiotronsAction");
							uriBuilder.appendQueryParameter("audiotronNameToRequest",audiotronNameClicked);
							completeUrl = uriBuilder.build().toString();

							Log.v(TAG,"###### Information: Constructed url using Uri Builder>>>> "+ completeUrl + " ######");

							con = new URL(completeUrl).openConnection();
							con.connect();
							
							inputStream = con.getInputStream(); //#check for null returns/errors!!!
							downloadedMediaFile = File.createTempFile("downloadingMedia", ".3gp");
							
							if (inputStream == null) {
								Log.e(TAG,"$$$$$$$$Unable to Create input stream to server$$$$$$"+ completeUrl);
							} else {
								
								outStream = new FileOutputStream(downloadedMediaFile);
								try{
								IOUtils.copy(inputStream, outStream);outStream.flush();outStream.close();
								Log.v(TAG,"####### file path =>>>>"+ downloadedMediaFile.getPath()+ 
										", length =>>>"+ downloadedMediaFile.length());

								startAudiotronPlayerActivity();

								} catch (IOException e) {
									e.printStackTrace();
								}
							}

						}
						catch (MalformedURLException e) {
						Log.v(TAG,"$$$$$$Mal-FE in setUpView......$$$$$$$$");
							e.printStackTrace();
						} catch (FileNotFoundException e) {
							Log.v(TAG,"$$$$$$FFE in setUpView on creation of outStream......$$$$$$$$");
							e.printStackTrace();
						} catch (IOException e) {
							Log.v(TAG, "$$$$$$IOE in setUpView......$$$$$$$$");
							e.printStackTrace();
						}
					}
				}).start();
			//}
		//});
	}
	private void startAudiotronPlayerActivity() { 

		Log.v(TAG, "-----Inside startMEdiaPlayer() --------");

		try {
			Log.v(TAG,"#######Information: Context path is >>>"	+ Environment.getExternalStorageDirectory());
			
			File f = new File(Environment.getExternalStorageDirectory()+"/iDiscoverit/Listen");
			if(!(f.exists()))f.mkdir();
			
			bufferedFile = new File(Environment.getExternalStorageDirectory()+"/iDiscoverit/Listen", "playingMedia"+ ".3gp");// getCacheDir()

			Log.v(TAG, "Created new buffered file--------->"+bufferedFile.exists());
			Log.v(TAG, "Buffered File length:" + bufferedFile.length() + "");
			Log.v(TAG, "Buffered File path:" + bufferedFile.getPath());
			Log.v(TAG, "------------------------->");

			moveFile(downloadedMediaFile, bufferedFile);
			bufferedFile.deleteOnExit(); //DELETE APPROPRIATELY LATER!!!

			Log.v(TAG, "File Name: "+ bufferedFile.getName());
			Log.v(TAG, "File length:" + bufferedFile.length() + "");
			Log.v(TAG, "bufferedFile File abs path:"+ bufferedFile.getAbsolutePath());
	
			Intent i = new Intent(StreamAudiotronsActivity.this,AudiotronPlayerActivity.class).putExtra("audiotronName", audiotronNameClicked)
					.putExtra("downloadedName",bufferedFile.getName());
			startActivity(i);
			
			///////////WORKING CODE BELOW/////////////////

//			mediaPlayer = new MediaPlayer();
//
//			MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {
//
//				public void onCompletion(MediaPlayer mediaPlayer) {
//					Log.v(TAG, "---------inside onComplListner---------");
//					mediaPlayer.release();
//					Log.i(TAG,
//							"#######Information: Mediaplayer.OnCompletionListiner:: Player released");
//				}
//			};
//			mediaPlayer.setOnCompletionListener(listener);
//
//			mediaPlayer.setDataSource(Environment.getExternalStorageDirectory()+"/iDiscoverit/Listen/"+"playingMedia"+".3gp");
//			//(bufferedFile.getAbsolutePath()));// ;"/audioTron-1014634039.3gp"
//			
//			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//			MediaPlayer.OnErrorListener errListener = new MediaPlayer.OnErrorListener() {
//
//				public boolean onError(MediaPlayer mediaPlayer, int what,
//						int extra) {
//					Log.v(TAG, "---------inside onErrListner---------");
//					Log.d(TAG, "Error:" + what + "," + extra);
//					mediaPlayer.release();
//					return false;
//				}
//			};
//			mediaPlayer.setOnErrorListener(errListener);
//
//			mediaPlayer.prepareAsync();
//			Log.i(TAG, "~~~~Starting MEdia Player~~~~~~");
//
//			MediaPlayer.OnPreparedListener prepListener = new MediaPlayer.OnPreparedListener() {
//
//				public void onPrepared(MediaPlayer mp) {
//					Log.v(TAG, "---------inside onPrepListner---------");
//					mediaPlayer.start();
//				}
//			};
//			mediaPlayer.setOnPreparedListener(prepListener);
			///////////WORKING CODE /////////////////
			
		} catch (IOException e) {
			Log.e(TAG, "$$$$$$ IOE in startMediaPlayer() " + e.getMessage()
					+ " $$$$$$$$");
		}

	}

	public void moveFile(File oldLocation, File newLocation) throws IOException {

		Log.v(TAG, "-----Inside moveFile() --------");

		if (oldLocation.exists()) {
			BufferedInputStream reader = new BufferedInputStream(new FileInputStream(oldLocation));
			BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(newLocation, false));
			try {
				byte[] buff = new byte[16384];//8192
				int numChars;
				while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
					writer.write(buff, 0, numChars);
				}
			} catch (IOException ex) {
				throw new IOException("IOException when transferring "+ oldLocation.getPath() + " to "
						+ newLocation.getPath());
			} finally {
				try {
					if (reader != null) {
						writer.close();
						reader.close();
					}
				} catch (IOException ex) {
					Log.e(getClass().getName(),"Error closing files when transferring "+ oldLocation.getPath() + " to "
									+ newLocation.getPath());
				}
			}
		} else {
			throw new IOException("Old location does not exist when transferring "+ oldLocation.getPath() + " to "
							+ newLocation.getPath());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_stream_audiotrons, menu);
		return true;
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(streamAcivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(streamAcivityReceiver);
	}
}