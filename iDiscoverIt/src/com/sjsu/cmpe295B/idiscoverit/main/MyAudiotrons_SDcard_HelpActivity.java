package com.sjsu.cmpe295B.idiscoverit.main;

import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MyAudiotrons_SDcard_HelpActivity extends Activity {
	private static String TAG = "MyAudiotrons_SDcard_HelpActivity";
	SessionManager session;
	
	private TextView upload_to_cloud_TxtView;
	private TextView audio_folder_sd_card_TxtView;
	private TextView audio_folder_cloud_TxtView;
	private Button doneBtn;
	private Dialog dialog;
	private CheckBox skipChkBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"************* iDiscoverit APPLICATION STARTED >> (MyAudiotrons_SDcard_HelpActivity) **************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		dialog=new Dialog(MyAudiotrons_SDcard_HelpActivity.this);
		dialog.setContentView(R.layout.activity_help);
		dialog.setTitle("Help and Usage");
		dialog.setCancelable(false);
		
		
		ImageSpan is = new ImageSpan(MyAudiotrons_SDcard_HelpActivity.this,R.drawable.audio_folder_sd_card);
		ImageSpan is1 = new ImageSpan(MyAudiotrons_SDcard_HelpActivity.this,R.drawable.audio_folder_cloud);
		ImageSpan is2 = new ImageSpan(MyAudiotrons_SDcard_HelpActivity.this,R.drawable.ic_action_cloud_btn);
		
		audio_folder_sd_card_TxtView =(TextView) dialog.findViewById(R.id.textview1);
		audio_folder_cloud_TxtView = (TextView) dialog.findViewById(R.id.textview2);
		upload_to_cloud_TxtView = (TextView) dialog.findViewById(R.id.textview3);
		
		doneBtn=(Button)dialog.findViewById(R.id.done_btn);
		skipChkBox =(CheckBox) dialog.findViewById(R.id.skip); 
		
		SpannableString text = new SpannableString("1.Click on          icon to view audios in SD card");
		SpannableString text1 = new SpannableString("2.Click on          icon to view audios saved on cloud");
		SpannableString text2 = new SpannableString("3.Click on          icon to send audios to cloud");
		
		text.setSpan(is, 8,7+10, 0);
		text1.setSpan(is1, 8,7+10, 0);
		text2.setSpan(is2, 8,7+10, 0);

		audio_folder_sd_card_TxtView.setText(text);
		audio_folder_cloud_TxtView.setText(text1);
		upload_to_cloud_TxtView.setText(text2);
		skipChkBox.setClickable(true);
		
		doneBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v(TAG,"------VAlue of is checked >>>> "+skipChkBox.isChecked());
				if(skipChkBox.isChecked()){
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_sdCard", "checked");
			        prefsEditor.commit();
				}else{
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_sdCard", "unchecked");
			        prefsEditor.commit();
				}
				dialog.dismiss();
				finish();				
			}
		});
		
		dialog.show();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		finish();
		android.os.Debug.stopMethodTracing();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_help,
				menu);
		return true;
	}

}
