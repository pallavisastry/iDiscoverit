package com.sjsu.cmpe295B.idiscoverit.main;

import android.os.Bundle;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class UD_MyAudiotronsInTabsActivity extends TabActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ud__my_audiotrons_in_tabs);
		
		setupViews();
	}
	private void setupViews(){
		TabHost tabHost = getTabHost();
		//Tab for my_audiotrons in sd card
		TabSpec myAudiotronSpec = tabHost.newTabSpec("SD");// My Audiotrons
		myAudiotronSpec.setIndicator("",getResources().getDrawable(R.drawable.my_tabbed_audio_folder_sdcard_selector));
		Intent myAudiotronIntent = new Intent(this,UD_MyAudiotrons_SDcardActivity.class);
		myAudiotronSpec.setContent(myAudiotronIntent);
		tabHost.addTab(myAudiotronSpec);
		
		//Tab for my_aduiotrons in cloud
		TabSpec profileSpec = tabHost.newTabSpec("CL");
		profileSpec.setIndicator("Cloud");
		profileSpec.setIndicator("",getResources().getDrawable(R.drawable.my_tabbed_audio_folder_selector));
		Intent profileIntent = new Intent(this,UD_MyAuditronsAcitivity.class);
		profileSpec.setContent(profileIntent);
		tabHost.addTab(profileSpec);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ud__my_audiotrons_in_tabs,
				menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(UD_MyAudiotronsInTabsActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(UD_MyAudiotronsInTabsActivity.this,MyAudiotrons_SDcard_HelpActivity.class);
    	startActivity(in);
	}
	//#new
//		@Override
//		public void onBackPressed() {
//			
//		}
}
