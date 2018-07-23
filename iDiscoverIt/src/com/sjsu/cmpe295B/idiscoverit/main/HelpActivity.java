package com.sjsu.cmpe295B.idiscoverit.main;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

public class HelpActivity extends Activity {
	private static String TAG = "HelpActivity";
	SessionManager session;
	
	private TextView audio_icon_TxtView;
	private TextView audio_recorder_TxtView;
	private TextView profile_TxtView;
	private TextView home_TxtView;
	private Button doneBtn;
	private Dialog dialog;
	private CheckBox skipChkBox;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"************* iDiscoverit APPLICATION STARTED >> (HelpActivity Activity) **************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		dialog=new Dialog(HelpActivity.this);
		dialog.setContentView(R.layout.activity_help);
		dialog.setTitle("Help and Usage");
		dialog.setCancelable(false);
		
		
		ImageSpan is = new ImageSpan(HelpActivity.this,R.drawable.audio_icon);
		ImageSpan is1 = new ImageSpan(HelpActivity.this,R.drawable.audio_recorder_btn);
		ImageSpan is2 = new ImageSpan(HelpActivity.this,R.drawable.profile);
		ImageSpan is3 = new ImageSpan(HelpActivity.this,R.drawable.home);
		
		audio_icon_TxtView =(TextView) dialog.findViewById(R.id.textview1);
		audio_recorder_TxtView = (TextView) dialog.findViewById(R.id.textview2);
		profile_TxtView = (TextView) dialog.findViewById(R.id.textview3);
		home_TxtView=(TextView) dialog.findViewById(R.id.textview4);
		doneBtn=(Button)dialog.findViewById(R.id.done_btn);
		skipChkBox =(CheckBox) dialog.findViewById(R.id.skip); 
		
		SpannableString text = new SpannableString("1.Click on          icon to listen to audios");
		SpannableString text1 = new SpannableString("2.Click on          icon to start recorder");
		SpannableString text2 = new SpannableString("3.Click on          icon to check your profile");
		SpannableString text3 = new SpannableString("4.Click on          icon to navigate to 'Home'");
		
		text.setSpan(is, 8,7+10, 0);
		text1.setSpan(is1, 8,7+10, 0);
		text2.setSpan(is2, 8,7+10, 0);
		text3.setSpan(is3, 8,7+10, 0);

		audio_icon_TxtView.setText(text);
		audio_recorder_TxtView.setText(text1);
		profile_TxtView.setText(text2);
		home_TxtView.setText(text3);
		skipChkBox.setClickable(true);
		
		doneBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v(TAG,"------VAlue of is checked >>>> "+skipChkBox.isChecked());
				if(skipChkBox.isChecked()){
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage", "checked");
			        prefsEditor.commit();
				}else{
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage", "unchecked");
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
		getMenuInflater().inflate(R.menu.activity_help, menu);
		return true;
	}

}
