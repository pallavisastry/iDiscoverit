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

public class ACT_HelpActivity extends Activity {
	private static String TAG = "ACT_HelpActivity";
	SessionManager session;
	
	private TextView toprated_audio_folder_TxtView;
	private TextView new_audio_TxtView;
	private Button doneBtn;
	private Dialog dialog;
	private CheckBox skipChkBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"************* iDiscoverit APPLICATION STARTED >> (ACT_HelpActivity Activity) **************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		dialog=new Dialog(ACT_HelpActivity.this);
		dialog.setContentView(R.layout.activity_help);
		dialog.setTitle("Help and Usage");
		dialog.setCancelable(false);
		
		
		ImageSpan is = new ImageSpan(ACT_HelpActivity.this,R.drawable.top_audio_folder_help);
		ImageSpan is1 = new ImageSpan(ACT_HelpActivity.this,R.drawable.icon_new);
		
		toprated_audio_folder_TxtView =(TextView) dialog.findViewById(R.id.textview1);
		new_audio_TxtView = (TextView) dialog.findViewById(R.id.textview2);
		doneBtn=(Button)dialog.findViewById(R.id.done_btn);
		skipChkBox =(CheckBox) dialog.findViewById(R.id.skip); 
		
		SpannableString text = new SpannableString("1.Click on          icon to view top rated audios");
		SpannableString text1 = new SpannableString("2.Click on          icon to view new audios");
		
		text.setSpan(is, 8,7+10, 0);
		text1.setSpan(is1, 8,7+10, 0);

		toprated_audio_folder_TxtView.setText(text);
		new_audio_TxtView.setText(text1);
		skipChkBox.setClickable(true);
		
		doneBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v(TAG,"------VAlue of is checked >>>> "+skipChkBox.isChecked());
				if(skipChkBox.isChecked()){
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_category", "checked");
			        prefsEditor.commit();
				}else{
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_category", "unchecked");
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
