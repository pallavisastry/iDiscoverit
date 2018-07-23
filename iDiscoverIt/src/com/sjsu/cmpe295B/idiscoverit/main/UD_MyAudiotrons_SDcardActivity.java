package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.UploadAudiotronsService;

/**
 * 
 * UD_MyAudiotrons_SDcardActivity class is responsible
 * for displaying all the audiotrons in the SD card
 * created by the user.
 *
 */
public class UD_MyAudiotrons_SDcardActivity extends Activity {

	private static String TAG = "UD_MyAudiotrons_SDcardActivity";
	private SessionManager session;
	
	private String username;
	private ListView myAudiotronsListView;
	private ArrayList<String> myAudiotronsArrList,uncheckedTronsList;
	private BroadcastReceiver myAudiotronsActivityReceiver;
	private ImageButton uploadButton;
	private String[] fileNames;
	
	private String files;
	private File folder;
	private File[] listOfFiles;
	
	String[] catArray;
	String selectedCategory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ud_my_audiotrons_sdcard);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********UD_MyAudiotrons_SDcardActivity Activity Started***************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		// #Setting a dynamic broadcast receiver to logout completely.
				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
				myAudiotronsActivityReceiver = new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						Log.d(TAG, "||||| Logout in progress ||||||");
						// Starting login activity
						Intent i = new Intent(UD_MyAudiotrons_SDcardActivity.this,LoginActivity.class);
						startActivity(i);
						finish();
					}
				};
				
				SharedPreferences prefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
				String isSkipMessageChecked = prefs.getString("skipMessage_sdCard", "unchecked");
				Log.v(TAG, "#####is skip message checked??? :" + isSkipMessageChecked);
				if (!(isSkipMessageChecked.equals("checked"))) {
					Intent in = new Intent(UD_MyAudiotrons_SDcardActivity.this, MyAudiotrons_SDcard_HelpActivity.class);
					startActivity(in);
				} else if (isSkipMessageChecked.equals("unchecked")) {
					Intent in = new Intent(UD_MyAudiotrons_SDcardActivity.this, MyAudiotrons_SDcard_HelpActivity.class);
					startActivity(in);
				}
		
		setupViews();
	}
	private void setupViews(){
		Log.v(TAG,"-------- Inside setupViews()----------");
		myAudiotronsListView= (ListView) findViewById(R.id.my_audiotrons_list);
		
		uncheckedTronsList=new ArrayList<String>();
		myAudiotronsArrList = new ArrayList<String>();
		username = session.getUserDetails().get("username");
		
		uploadButton=(ImageButton) findViewById(R.id.upload_sd_files);
		uploadButton.setEnabled(false);
		
		/*Calling getAudiotronsFromSDCard()*/
		myAudiotronsArrList=getAudiotronsFromSDcard();
		fileNames = myAudiotronsArrList.toArray(new String[myAudiotronsArrList.size()]);
		catArray = getResources().getStringArray(R.array.categoryArray);
		
		myAudiotronsArrList.clear();
		System.out.println("array list cleared >> "+myAudiotronsArrList.isEmpty());
		
		myAudiotronsListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_multiple_choice,fileNames));
		myAudiotronsListView.setItemsCanFocus(false);
		myAudiotronsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		myAudiotronsListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView parent, View view,int position, long id) {
				uploadButton.setEnabled(true);
				
				SparseBooleanArray spBooleanArray = myAudiotronsListView.getCheckedItemPositions();
				//String results ="";
				int count=myAudiotronsListView.getAdapter().getCount();
				for(int i=0;i<count;i++){
					if(spBooleanArray.get(i)){
						//results += i + ",";
						if(!myAudiotronsArrList.contains((String) parent.getItemAtPosition(i)))
							myAudiotronsArrList.add((String) parent.getItemAtPosition(i));
						if(uncheckedTronsList.size()>0 && uncheckedTronsList.contains((String) parent.getItemAtPosition(i)))
							uncheckedTronsList.remove((String) parent.getItemAtPosition(i));
					}
					else{
						if(!uncheckedTronsList.contains((String) parent.getItemAtPosition(i))){
							uncheckedTronsList.add((String) parent.getItemAtPosition(i));
						}
						myAudiotronsArrList.remove((String) parent.getItemAtPosition(i));
					}
				}//Toast.makeText(getApplicationContext(), "The checked items are :" + results,Toast.LENGTH_SHORT).show();
			
				System.out.println("clicked arr list >> "+myAudiotronsArrList+" unchecked >> "+uncheckedTronsList);
				
				
			}
		});
		
		uploadButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(myAudiotronsArrList!=null && myAudiotronsArrList.size()>0){
					Log.v(TAG,"-------Inside uploadButton click----------");
					Log.v(TAG,"Array list value from uploadButton view is >>>> "+myAudiotronsArrList);
					chooseCategory();
				}
			}
		});
	}
	private void chooseCategory() {
		Log.v(TAG,"----------Inside chooseCategory()----------");
		
		// #Picking a category for the recorded audiotron
		final AlertDialog.Builder popDialog = new AlertDialog.Builder(UD_MyAudiotrons_SDcardActivity.this);
		popDialog.setTitle(R.string.pick_category);
		popDialog.setItems(R.array.categoryArray,new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getApplicationContext(),"chosen category is >>> " + catArray[which],	Toast.LENGTH_LONG).show();
						selectedCategory = catArray[which];
						dialog.cancel();
						startUploadService();
					}
				});
		popDialog.create();
		popDialog.show();
	}
	private void startUploadService(){
		Log.v(TAG,"-----------Inside startUploadService()------------");
		Log.v(TAG,"####Selected category >>> "+selectedCategory);
		if(selectedCategory!=null){
			Intent intent = new Intent(UD_MyAudiotrons_SDcardActivity.this,UploadAudiotronsService.class);
			Bundle extras = new Bundle();
			intent.putExtra("extras", extras);
			extras.putStringArrayList("SDtronsArray", myAudiotronsArrList);
			extras.putString("selectedCategory", selectedCategory); 
			extras.putString("username",username);
			startService(intent);
		}else{
			Toast.makeText(getApplicationContext(),"You have to choose a category! ",	Toast.LENGTH_LONG).show();	
		}
	}
	private ArrayList<String> getAudiotronsFromSDcard(){
		Log.v(TAG,"-------- Inside getAudiotronsFromSDcard()----------");
		String fPath =Environment.getExternalStorageDirectory()+"/iDiscoverit/OwnAtrons"; 
		
		folder = new File(fPath);
		listOfFiles=folder.listFiles();
		
		for(int i=0;i<listOfFiles.length;i++){
			if(listOfFiles[i].isFile()){
				files = listOfFiles[i].getName();
				fileNames=new String[listOfFiles.length];
				
				if(files.endsWith(".3gp")){
					myAudiotronsArrList.add(files);
				}
			}
		}
		System.out.println("Full array list >> "+myAudiotronsArrList);
		return myAudiotronsArrList;
	}		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ud_my_audiotrons_sdcard,
				menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(UD_MyAudiotrons_SDcardActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
			Intent in = new Intent(UD_MyAudiotrons_SDcardActivity.this,MyAudiotrons_SDcard_HelpActivity.class);
	    	startActivity(in);
		}

	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this,UserDetailsActivity.class));
		finish();
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(myAudiotronsActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(myAudiotronsActivityReceiver);
	}
}
