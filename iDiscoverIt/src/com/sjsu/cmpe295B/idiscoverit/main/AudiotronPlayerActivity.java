///*Referred:http://www.androidhive.info/2012/03/android-building-audio-player-tutorial/*/
package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.MiscellaneousUtilities;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;

//TODO: IF favorites is clicked twice, then remove audio from favorites.
public class AudiotronPlayerActivity extends Activity 
implements SeekBar.OnSeekBarChangeListener{//,OnErrorListener, OnCompletionListener,

	private static String TAG = "AudiotronPlayerActivity";

	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ToggleButton favoritesButton;
	private ToggleButton flagButton;
	private ImageButton tagButton;
	private SeekBar audiotronProgressBar;
	private TextView audiotronCurrentDurationLabel;
	private TextView audiotronTotalDurationLabel;
	private TextView audiotronTitleLabel;
	private RatingBar rating;

	private MediaPlayer mPlayer;
	private Handler mHandler = new Handler();

	private int seekForwardTime = 5000;
	private int seekBackwardTime = 5000;

	private String audiotronSelected;
	private String audiotronTitle;
	private String ratingValue,tagInputValue;
	SessionManager session;
	private String URL_STRING,favorites_URL_STRING,addTag_URL_STRING;
	private String username;
	//private String isFavoredStr;
	
	SharedPreferences prefs ;
	SharedPreferences.Editor prefsEditor;
	
	private BroadcastReceiver playerActivityReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audiotron_player);
		Log.v(TAG, "*************PLAYER ACTIVITY STARTED*******************");

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
	
		//#Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		playerActivityReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG,"||||| Logout in progress ||||||");
				//Starting login activity
				Intent i = new Intent(AudiotronPlayerActivity.this,LoginActivity.class);
				startActivity(i);
				finish();				
			}
		};
		
		// #This will be available only after session.checkLogin() is called
		prefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = prefs.getString("feedback_URL", Constants.feedback_URL);
		favorites_URL_STRING=prefs.getString("favorites_URL", Constants.favorites_URL);
		addTag_URL_STRING = prefs.getString("addTag_URL_STRING",Constants.addTag_URL_STRING);
		
		username =session.getUserDetails().get("username");//prefs.getString("username", "temp");
		prefsEditor = prefs.edit();
		
		setupViews();
	}

	private void setupViews() {
		Log.v(TAG, "--------Inside setupViews()--------");

		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		favoritesButton = (ToggleButton) findViewById(R.id.favorites);
		flagButton = (ToggleButton) findViewById(R.id.flag);
		tagButton = (ImageButton) findViewById(R.id.tag);
		
		audiotronProgressBar = (SeekBar) findViewById(R.id.audiotronProgressBar);
		audiotronTitleLabel = (TextView) findViewById(R.id.audiotronTitle);
		audiotronCurrentDurationLabel = (TextView) findViewById(R.id.audiotronCurrentDurationLabel);
		audiotronTotalDurationLabel = (TextView) findViewById(R.id.audiotronTotalDurationLabel);

		mPlayer = new MediaPlayer();
		
		audiotronTitle=getIntent().getStringExtra("audiotronName").trim();
		audiotronSelected = getIntent().getStringExtra("downloadedName").trim();
		Log.v(TAG, "--------tron selected ----->>> " + audiotronSelected);
		
	//	isFavoredStr = getIntent().getStringExtra("isFavorite");//#########new-fav
	//	Log.v(TAG,"------is favored------>>>>"+isFavoredStr);
		
		// Listeners
		audiotronProgressBar.setOnSeekBarChangeListener(this);
	
//		mPlayer.setOnCompletionListener(this);
//		mPlayer.setOnErrorListener(this);
		
		audiotronTitleLabel.setText(audiotronTitle);
		
		btnPlay.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (mPlayer.isPlaying()) {
					if (mPlayer != null) {
						mPlayer.pause();
					}
				} else {
					if (mPlayer != null) {
						// mPlayer.start();

						btnPlay.setImageResource(R.drawable.img_btn_pause);
						playAudiotron(audiotronSelected);
					}
				}
			}
		});
		btnForward.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				int curPosition = mPlayer.getCurrentPosition();
				if (curPosition + seekForwardTime <= mPlayer.getDuration()) {
					mPlayer.seekTo(curPosition + seekForwardTime);
				} else {
					mPlayer.seekTo(mPlayer.getDuration());
				}
			}
		});
		btnBackward.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// get current song position
				int currentPosition = mPlayer.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward song
					mPlayer.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mPlayer.seekTo(0);
				}

			}
		});
		///////// #new: Favorites toggle button--------//		
//		if(isFavoredStr.equals("true")){
//			favoritesButton.setEnabled(false);
//			favoritesButton.setVisibility(View.INVISIBLE);
//			favoritesButton.setBackgroundResource(R.drawable.add_to_favorites_false);
//		}else
		{
			//favoritesButton.setEnabled(true);
			favoritesButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.v(TAG,"-----Inside isChecked of +toFavs-----"+isChecked);
				if(isChecked){
					prefsEditor.putString("favorite","yes");
					boolean isPrefsCommitted = prefsEditor.commit();
					Log.v(TAG,"----Are preferences committed in AudiotronPlayer-Favbutoon activity >> "+isPrefsCommitted);

					AddToFavoriteTask task = new AddToFavoriteTask();
					task.execute();
				}//else->IMPLEMENT REMOVING FROM FAVORITES
			}
		});
		}
		
		flagButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.v(TAG,"-----Inside isChecked of Flag-----" + isChecked);
				if(isChecked) {
					prefsEditor.putString("flag", "yes");
					boolean isPrefsCommitted = prefsEditor.commit();
					Log.v(TAG, "-----Are preferences committed in Audiotron Player-FlagButton activity >>" + isPrefsCommitted);
					
					AddFeedbackTask task = new AddFeedbackTask();
					task.execute();
				}
			}
			
		});
		
		tagButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				final AlertDialog.Builder popTagDialog = new AlertDialog.Builder(AudiotronPlayerActivity.this);
				popTagDialog.setTitle(R.string.custom_tag_name);
				final EditText tagInput = new EditText(AudiotronPlayerActivity.this);
				popTagDialog.setView(tagInput);
				popTagDialog.setPositiveButton("Save", new DialogInterface.OnClickListener(){

					public void onClick(DialogInterface dialog, int which) {
						tagInputValue = tagInput.getText().toString();
						Log.v(TAG,">>>>>Information: Input: custom tag name entered :>>>>> "+tagInputValue+" by user: "+username);
						Toast.makeText(getApplicationContext(), tagInputValue+":Tag saved", Toast.LENGTH_LONG).show();
						dialog.cancel();
						
						AddTagTask task = new AddTagTask();
						task.execute();
					}
					
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				popTagDialog.create();popTagDialog.show();
			}
		});
	}

	private void playAudiotron(String name) {
		Log.v(TAG,"--------Inside playAudiotron----->> file name="+name);
		try {
			
////			mPlayer.reset();
////			mPlayer.setDataSource(Environment.getExternalStorageDirectory()	+ "/iDiscoverit/Listen/" + name + ".3gp");// name
//			mPlayer.setDataSource(Environment.getExternalStorageDirectory()+"/iDiscoverit/Listen/"+"playingMedia"+".3gp");
//			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mPlayer.prepareAsync();
////			mPlayer.prepare();
//			mPlayer.start();

			
//			mediaPlayer = new MediaPlayer();

			btnPlay.setImageResource(R.drawable.img_btn_pause);
			MediaPlayer.OnCompletionListener listener = new MediaPlayer.OnCompletionListener() {

				public void onCompletion(MediaPlayer mPlayer) {
					Log.v(TAG, "---------inside onComplListner---------");
					mPlayer.release();
					mHandler.removeCallbacks(mUpdateTimeTask);
					Log.i(TAG,"#######Information: Mediaplayer.OnCompletionListiner:: Player released");
					
					showRatingDialog();
				}
			};
			mPlayer.setOnCompletionListener(listener);

			mPlayer.setDataSource(Environment.getExternalStorageDirectory()+"/iDiscoverit/Listen/"+name);//"playingMedia"+".3gp");
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

			MediaPlayer.OnErrorListener errListener = new MediaPlayer.OnErrorListener() {

				public boolean onError(MediaPlayer mPlayer, int what,
						int extra) {
					Log.v(TAG, "---------inside onErrListner---------");
					Log.d(TAG, "Error:" + what + "," + extra);
					mPlayer.release();mHandler.removeCallbacks(mUpdateTimeTask);
					return false;
				}
			};
			mPlayer.setOnErrorListener(errListener);

			mPlayer.prepareAsync();
			Log.i(TAG, "~~~~Starting MEdia Player~~~~~~");

			MediaPlayer.OnPreparedListener prepListener = new MediaPlayer.OnPreparedListener() {

				public void onPrepared(MediaPlayer mPlayer) {
					Log.v(TAG, "---------inside onPrepListner---------");
					
					updateProgressBar();
					mPlayer.start();
				}
			};
			mPlayer.setOnPreparedListener(prepListener);
			
//			String tronTitle = name;
//			audiotronTitleLabel.setText(tronTitle);
//			btnPlay.setImageResource(R.drawable.img_btn_pause);
			
			audiotronProgressBar.setProgress(0);
			audiotronProgressBar.setMax(100);

//			updateProgressBar();
			
		} catch (IllegalArgumentException e) {
			Log.v(TAG,"$$$$$$Illlegal Arg Exception in playAudiotron()$$$$"+e);
		} catch (IllegalStateException e) {
			Log.v(TAG,"$$$$$$Illlegal state Exception in playAudiotron()$$$$"+e);
		} catch (IOException e) {
			Log.v(TAG,"$$$$$$IO-Exception in playAudiotron()$$$$"+e);
		}
	}

	public void updateProgressBar() {
		Log.v(TAG,"--------Inside updateProgressBar---------");

		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {

		public void run() {
			Log.v(TAG,"--------Inside Runnable-mUpdateTimeTask----------");
			
			long totalDuration = mPlayer.getDuration();
			long curDuration = mPlayer.getCurrentPosition();
			audiotronTotalDurationLabel.setText(""+ MiscellaneousUtilities.milliSecondsToTimer(totalDuration));
			audiotronCurrentDurationLabel.setText(""+ MiscellaneousUtilities.milliSecondsToTimer(curDuration));

			int progress = MiscellaneousUtilities.getProgressPercentage(curDuration, totalDuration);
			audiotronProgressBar.setProgress(progress);
			mHandler.postDelayed(this, 100);
		}
	};

//	public void onCompletion(MediaPlayer mp) {
//		Log.v(TAG, "-------Inside onCompletion()---------");
//		if (mp != null) {
//			btnPlay.setImageResource(R.drawable.stop_btn);
//			// mp.stop();
//			Log.v(TAG, "----Releasing m player--------");
//			mHandler.removeCallbacks(mUpdateTimeTask);
//			mp.release();
//		}
//
//		showRatingDialog();
//
//	}

	private void showRatingDialog() {
		Log.v(TAG, "-------Inside showRatingDialog()---------");

		Context context = getApplicationContext();
		LayoutInflater inflater =(LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_ratingbar_layout, (ViewGroup) findViewById(R.id.layout_rating_bar_root));
		
		rating = (RatingBar) layout.findViewById(R.id.custom_audio_rating_bar);
		rating.setStepSize(1.0F);
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
		
		popDialog.setIcon(android.R.drawable.btn_star_big_on);
		popDialog.setTitle("Rate!");
		popDialog.setView(layout);//rating

		// #### CACHE The rating value is a hash map and once the user exits the application,update the db!!!!!
		popDialog.setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						float rateVal= rating.getRating();
						Log.v(TAG,"====>ORiginal rating value  >> "+rateVal);
						ratingValue = Float.toString(rateVal);//String.valueOf(rateVal);
						Log.v(TAG, " ======>>RAaaaaaaaaating is >>>>>> "+ ratingValue);
						dialog.dismiss();

						if (!ratingValue.equals(null) && (!ratingValue.equals("0"))) {
						
							Log.v(TAG,"----Adding rating to prefs ------");
							prefsEditor.putString("rating", ratingValue); //#new putting this value in cache:>audioName:rating;example= palla1.3gp:3
							prefsEditor.commit();
							AddFeedbackTask task = new AddFeedbackTask();
							task.execute();
						}
					}
				}).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		popDialog.create();
		popDialog.show();
	}

	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

	}
	public void onStartTrackingTouch(SeekBar arg0) {
		Log.v(TAG,"--------Inside onStartTrackingTouch---------");
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	public void onStopTrackingTouch(SeekBar arg0) {
		Log.v(TAG,"--------Inside onStopTrackingTouch---------");
		int totalDuration = mPlayer.getDuration();
		Log.v(TAG,"~~~~totalDuration in  onStopTrackingTouch()~~~~~ "+totalDuration);
		int currentPosition = MiscellaneousUtilities.progressToTimer(
				arg0.getProgress(), totalDuration);

		Log.v(TAG,"~~~~currentPosition in  onStopTrackingTouch()~~~~~ "+currentPosition);
		
		// forward or backward to certain seconds
		mPlayer.seekTo(currentPosition);
		
		// update timer progress again
		updateProgressBar();
	}

	@SuppressWarnings("unchecked")
	private boolean addFeedback() {
		Log.v(TAG,"--------Inside addFeedback---------");
		ArrayList postParameters = new ArrayList();
		JSONObject jsonObjFromInputStream;
		String jsonResponseMsg;
		String ratingStr,flagStr;
		try {
			Log.v(TAG,"Float value of 0 :"+Float.toString(0));
			ratingStr = prefs.getString("rating", "0.0");//Float.toString(0);
			flagStr=prefs.getString("flag", "no");
			Log.v(TAG,"Rating value is "+ratingStr+" flag is set to: "+flagStr);
			
			postParameters.add(new BasicNameValuePair("rating",ratingStr));//ratingValue
			postParameters.add(new BasicNameValuePair("audiotronName",audiotronTitle));
			postParameters.add(new BasicNameValuePair("userName", username));
			postParameters.add(new BasicNameValuePair("flag", flagStr));

			//////////#new......
//			Log.v(TAG,"####Information: fav-prefs >> "+prefs.getString("favorites", "no"));
//			postParameters.add(new BasicNameValuePair("favorites", prefs.getString("favorites", "no")));

			jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");

			Log.v(TAG, "~~~~~json OBJ>>> " + jsonObjFromInputStream.keys());
			Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());

			jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");

			if (jsonResponseMsg.equals("feedbackSaveSuccess")) {
				Log.v(TAG, "####Information: Feedback was saved successfully!");
				return true;
			} else if (jsonResponseMsg.equals("feedbackSaveError")) {
				Log.v(TAG,
						"####Information: Feedback already given added!");
				return false;
			}

		} catch (IOException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
		} catch (JSONException e) {
			Log.e(TAG, "$$$$$$ JSON-EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "$$$$$$ UNKOWN EXCEPTION >> msg = " + e.getMessage()	+ " $$$$$$$$", e);
		}finally{
			Log.v(TAG,"~~~~~~~~~~~~~~~Inside finally~~~~~~~~~~~~");
			Log.v(TAG,"------Nullyfying objects in finally------");
			jsonObjFromInputStream = null;
			jsonResponseMsg=null;
		}
		return false;
	}

	class AddFeedbackTask extends AsyncTask<Void, Toast, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.v(TAG, "-------Inside doInBackground in AddFeedbackTask");
			Boolean result = addFeedback();
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(getApplicationContext(),
						"Ooppss...Feedback error!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Thank You For Your Feedback!",Toast.LENGTH_LONG).show();
			}
		}
	}
	private boolean addToFavorites(){
		Log.v(TAG,"--------Inside addToFavorites---------");
		ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
		JSONObject jsonObjFromInputStream;
		String jsonResponseMsg;
		String favoriteValue;
		try {
			favoriteValue = prefs.getString("favorite", "no");
			Log.v(TAG,"#####Info: Is favorited ??? >>> "+favoriteValue);
			
			postParameters.add(new BasicNameValuePair("favorite", favoriteValue));
			postParameters.add(new BasicNameValuePair("audiotronName",audiotronTitle));
			postParameters.add(new BasicNameValuePair("userName", username));

			jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(favorites_URL_STRING, postParameters, "json");

			Log.v(TAG, "~~~~~json OBJ>>> " + jsonObjFromInputStream.keys());
			Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());

			jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");

			if (jsonResponseMsg.equals("addSuccess")) {
				Log.v(TAG, "####Information: Saved to favorites successfully!");
				return true;
			} else if (jsonResponseMsg.equals("addError")) {
				Log.v(TAG,
						"####Information: Already in favorites!!");
				return false;
			}

		} catch (IOException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
		} catch (JSONException e) {
			Log.e(TAG, "$$$$$$ JSON-EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "$$$$$$ UNKOWN EXCEPTION >> msg = " + e.getMessage()	+ " $$$$$$$$", e);
		}finally{
			Log.v(TAG,"~~~~~~~~~~~~~~~Inside finally~~~~~~~~~~~~");
			Log.v(TAG,"------Nullyfying objects in finally------");
			jsonObjFromInputStream = null;
			jsonResponseMsg=null;
		}
		return false;

	}
	class AddToFavoriteTask extends AsyncTask<Void, Toast, Boolean>{

		@Override
		protected Boolean doInBackground(Void... params) {
			Log.v(TAG, "-------Inside doInBackground in AddToFavoriteTask");
			Boolean isFavorited = addToFavorites();
			return isFavorited;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(getApplicationContext(),
						"Ooppss...Unable to add to favorites at this time.Please do so later!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Added to favorites successfully!",Toast.LENGTH_LONG).show();
			}
		}
	}
	private boolean addTag(){
		Log.v(TAG,"--------Inside addTag---------");
		ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
		JSONObject jsonObjFromInputStream;
		String jsonResponseMsg;
		String tagValue;
		try {
			tagValue = prefs.getString("tag", "no");
			Log.v(TAG,"#####Info: tag  >>> "+tagInputValue+" username=>"+username);
			
			postParameters.add(new BasicNameValuePair("tag", tagInputValue));
			postParameters.add(new BasicNameValuePair("audiotronName",audiotronTitle));
			postParameters.add(new BasicNameValuePair("userName",username));//username

			jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(addTag_URL_STRING, postParameters, "json");

			Log.v(TAG, "~~~~~json OBJ>>> " + jsonObjFromInputStream.keys());
			Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());

			jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");

			if (jsonResponseMsg.equals("addSuccess")) {
				Log.v(TAG, "####Information: Saved Tag successfully!");
				return true;
			} else if (jsonResponseMsg.equals("addError")) {
				Log.v(TAG,
						"####Information: Already Tagged!!");
				return false;
			}

		} catch (IOException e) {
			Log.e(TAG, "$$$$$$ EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
		} catch (JSONException e) {
			Log.e(TAG, "$$$$$$ JSON-EXCEPTION IO/E >> msg = " + e.getMessage()+ " $$$$$$$$", e);
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(TAG, "$$$$$$ UNKOWN EXCEPTION >> msg = " + e.getMessage()	+ " $$$$$$$$", e);
		}finally{
			Log.v(TAG,"~~~~~~~~~~~~~~~Inside finally~~~~~~~~~~~~");
			Log.v(TAG,"------Nullyfying objects in finally------");
			jsonObjFromInputStream = null;
			jsonResponseMsg=null;
		}
		return false;

	}
	class AddTagTask extends AsyncTask<Void, Toast, Boolean>{
		
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.v(TAG, "-------Inside doInBackground in AddTagTask");
			Boolean isTagged = addTag();
			return isTagged;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				Toast.makeText(getApplicationContext(),
						"No Tagged.Error.!", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), "Tagged successfully!",Toast.LENGTH_LONG).show();
			}
		}
	}
	public boolean onError(MediaPlayer mp, int what, int extra) {
		Log.v(TAG,"$$$$---- Inside onError()-----$$$$");
		StringBuilder sb = new StringBuilder();
		  sb.append("Media Player Error: ");
		  switch (what) {
		  case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
		   sb.append("Not Valid for Progressive Playback");
		   break;
		  case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
		   sb.append("Server Died");
		   break;
		  case MediaPlayer.MEDIA_INFO_BUFFERING_END:
			  sb.append("Buffering ended!!");
		   break;
		  case MediaPlayer.MEDIA_INFO_BUFFERING_START:
		     sb.append("Buffering started!!");
			break;
		  case MediaPlayer.MEDIA_ERROR_UNKNOWN:
		   sb.append("Unknown");
		   break;
		  default:
		   sb.append(" Non standard (");
		   sb.append(what);
		   sb.append(")");
		  }
		  sb.append(" (" + what + ") ");
		  sb.append(extra);
		  Log.e(TAG, sb.toString());
		  return true;
	}
//	private void shareItem(){
//		Log.v(TAG,"--------Inside shareItem()-------");
//	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_audiotron_player, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(AudiotronPlayerActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				//add help xml process
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(playerActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(playerActivityReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mPlayer!=null){
			mPlayer.release();
			mPlayer=null;
		}
	}
}
