package com.sjsu.cmpe295B.idiscoverit.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Empty extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v("","----Passing Empty activity-------");
		Intent myAudiotronIntent = new Intent(this,UD_MyAudiotronsInTabsActivity.class);
		startActivity(myAudiotronIntent);
		finish();
	}
}
