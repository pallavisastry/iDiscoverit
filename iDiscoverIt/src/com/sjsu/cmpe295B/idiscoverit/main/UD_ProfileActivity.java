package com.sjsu.cmpe295B.idiscoverit.main;

import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UD_ProfileActivity extends Activity {
	private static final String TAG = "UD_ProfileActivity";

	private SessionManager session;
	private String username;
	private Button logoutButton;

	private TextView usernameTxtView;

	private BroadcastReceiver profileActivityReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ud_profile);
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,
				"***********UD_ProfileActivity Activity Started***************");

		session = new SessionManager(getApplicationContext());
		session.checkLogin();

		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		profileActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(UD_ProfileActivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		SharedPreferences prefs = this.getSharedPreferences("iDiscoveritPreferences",
				MODE_PRIVATE);

		String isSkipMessageChecked = prefs.getString("skipMessage_prof", "unchecked");
		Log.v(TAG, "#####is skip message checked??? :" + isSkipMessageChecked);
		if (!(isSkipMessageChecked.equals("checked"))) {
			Intent in = new Intent(UD_ProfileActivity.this, UD_Profile_HelpActivity.class);
			startActivity(in);
		} else if (isSkipMessageChecked.equals("unchecked")) {
			Intent in = new Intent(UD_ProfileActivity.this, UD_Profile_HelpActivity.class);
			startActivity(in);
		}

		setupViews();
	}

	private void setupViews() {
		Log.v(TAG, "---------Inside setupViews()---------");
		usernameTxtView = (TextView) findViewById(R.id.profile_txt_view);
		logoutButton = (Button) findViewById(R.id.logout);

		username = session.getUserDetails().get("username");
		Log.v(TAG, "####Info: username from session is >>> " + username);
		usernameTxtView.setText((CharSequence) "Hello: " + username);

		logoutButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Log.v(TAG, "------Inside onclick of logout button");
				session.logoutUser();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ud_profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG,"---------Inside onOptionsItemSelected-------");
		switch (item.getItemId()) {
		case R.id.menu_home:
			startActivity(new Intent(UD_ProfileActivity.this,HomeActivity.class));
			return true;
		case R.id.menu_help:
			startActivity(new Intent(UD_ProfileActivity.this, UD_Profile_HelpActivity.class));
	    	return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	private void displayHelpAlert() {
//		Log.v(TAG,"-----Inside displayHelpAlert()----------");
//		Intent in = new Intent(UD_ProfileActivity.this, UD_Profile_HelpActivity.class);
//    	startActivity(in);
//	}

	/** -----Acitivty Lifecycle methods-------- **/
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(profileActivityReceiver, iFilter);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "---onPause()->UN-Registering receiver-----");
		unregisterReceiver(profileActivityReceiver);
	}
}
