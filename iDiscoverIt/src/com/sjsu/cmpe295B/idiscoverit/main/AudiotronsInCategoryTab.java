package com.sjsu.cmpe295B.idiscoverit.main;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class AudiotronsInCategoryTab extends TabActivity {

	private static String TAG = "AudiotronsInCategoryTab";
	String categoryName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audiotrons_in_category_tab);
		categoryName = getIntent().getStringExtra("categoryName");
		Log.v(TAG, "#####-category name inside next activity------------>"
				+ categoryName);

		setupViews();
	}

	private void setupViews() {
		TabHost tabHost = getTabHost();

		//#New unrated/unfavored rookie audiotrons
		TabSpec myAudiotronSpec = tabHost.newTabSpec("New");// My Audiotrons
		myAudiotronSpec.setIndicator("",getResources().getDrawable(R.drawable.new_audiotrons_in_category_selector));
		Intent myAudiotronIntent = new Intent(this,NewAudiotronsInCategoryDisplay.class)
									.putExtra("categoryName",categoryName);
		myAudiotronSpec.setContent(myAudiotronIntent);
		tabHost.addTab(myAudiotronSpec);
		
		//# distributed/favored/rated audiotrons
		TabSpec profileSpec = tabHost.newTabSpec("topRated");
		profileSpec.setIndicator("",getResources().getDrawable(R.drawable.top_rated_audio_folder_selector));
		Intent profileIntent = new Intent(this,AudiotronsInCategoryDisplayActivity.class)
							.putExtra("categoryName", categoryName);
		profileSpec.setContent(profileIntent);
		tabHost.addTab(profileSpec);

		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_audiotrons_in_category_tab,menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(AudiotronsInCategoryTab.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(AudiotronsInCategoryTab.this,ACT_HelpActivity.class);
    	startActivity(in);
	}
}
