package com.sjsu.cmpe295B.idiscoverit.main;

import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

public class VR_HelpActivity extends Activity {
	private static String TAG = "VR_HelpActivity";
	SessionManager session;
	
	private TextView cloud_icon_TxtView;
	private TextView record_btn_pressed_TxtView;
	private TextView record_btn_new_TxtView;
	private Button doneBtn;
	private Dialog dialog;
	private CheckBox skipChkBox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"************* iDiscoverit APPLICATION STARTED >> (VR_HelpActivity Activity) **************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		dialog=new Dialog(VR_HelpActivity.this);
		dialog.setContentView(R.layout.activity_help);
		dialog.setTitle("Help and Usage");
		dialog.setCancelable(false);
		
		
		ImageSpan is = new ImageSpan(VR_HelpActivity.this,R.drawable.rec_btn_pressed_help);
		ImageSpan is1 = new ImageSpan(VR_HelpActivity.this,R.drawable.rec_btn_new_help);
		ImageSpan is2 = new ImageSpan(VR_HelpActivity.this,R.drawable.ic_action_cloud_btn);
		
		cloud_icon_TxtView =(TextView) dialog.findViewById(R.id.textview1);
		record_btn_pressed_TxtView = (TextView) dialog.findViewById(R.id.textview2);
		record_btn_new_TxtView = (TextView) dialog.findViewById(R.id.textview3);
		doneBtn=(Button)dialog.findViewById(R.id.done_btn);
		skipChkBox =(CheckBox) dialog.findViewById(R.id.skip); 
		
		SpannableString text = new SpannableString("1.Click on          icon to start recording");
		SpannableString text1 = new SpannableString("2.Click on          icon to stop recording");
		SpannableString text2 = new SpannableString("3.Click on          icon to send to cloud");
		
		text.setSpan(is, 8,7+10, 0);
		text1.setSpan(is1, 8,7+10, 0);
		text2.setSpan(is2, 8,7+10, 0);

		record_btn_pressed_TxtView.setText(text);
		record_btn_new_TxtView.setText(text1);
		cloud_icon_TxtView.setText(text2);
		skipChkBox.setClickable(true);
		
		doneBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Log.v(TAG,"------VAlue of is checked >>>> "+skipChkBox.isChecked());
				if(skipChkBox.isChecked()){
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_voice", "checked");
			        prefsEditor.commit();
				}else{
					SharedPreferences prefs = getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
			        SharedPreferences.Editor prefsEditor = prefs.edit();
			        prefsEditor.putString("skipMessage_voice", "unchecked");
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
