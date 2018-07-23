package com.sjsu.cmpe295B.idiscoverit.utilities;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
//import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener{

	
	
	
	//Fires when user changes a pref.
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	//	NetworkActivity.refreshDisplay=true;
	}
	
	
}
