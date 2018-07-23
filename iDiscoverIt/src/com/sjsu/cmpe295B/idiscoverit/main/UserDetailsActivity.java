package com.sjsu.cmpe295B.idiscoverit.main;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class UserDetailsActivity extends TabActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);
		
		setupViews();
	}
	private void setupViews(){
		TabHost tabHost = getTabHost();
		
		//Tab for profile
		TabSpec profileSpec = tabHost.newTabSpec("P");
		profileSpec.setIndicator("",getResources().getDrawable(R.drawable.my_profile_edit_selector));
		Intent profileIntent = new Intent(this,UD_ProfileActivity.class);
		profileSpec.setContent(profileIntent);
		tabHost.addTab(profileSpec);
		
		//Tab for My_Audiotrons
		TabSpec myAudiotronSpec =tabHost.newTabSpec("A");//My Audiotrons
		myAudiotronSpec.setIndicator("",getResources().getDrawable(R.drawable.my_audiotrons_folder_selector));
	//	Intent myAudiotronIntent = new Intent(this,UD_MyAuditronsAcitivity.class);
	//	Intent myAudiotronIntent = new Intent(this,UD_MyAudiotronsInTabsActivity.class);
		Intent myAudiotronIntent = new Intent(this,Empty.class);
		myAudiotronSpec.setContent(myAudiotronIntent);
		tabHost.addTab(myAudiotronSpec);
		
		//Tab for My_favorites
		TabSpec myFavoriteSpec = tabHost.newTabSpec("F");//My Favorites
		myFavoriteSpec.setIndicator("",getResources().getDrawable(R.drawable.my_favorites_selector));
		Intent myFavoritesIntent = new Intent(this,UD_MyFavoritesActivity.class);
		myFavoriteSpec.setContent(myFavoritesIntent);
		tabHost.addTab(myFavoriteSpec);
		//Adding all specs to the host
	
	}
	//#new
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_user_details, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(UserDetailsActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				startActivity(new Intent(UserDetailsActivity.this, UD_Profile_HelpActivity.class));
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
