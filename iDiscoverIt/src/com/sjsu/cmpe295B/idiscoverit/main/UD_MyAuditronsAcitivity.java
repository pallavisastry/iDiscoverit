package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.StreamAudiotronsActivity;

/**
 * 
 * This class gets and displays all the user-created audiotrons
 * in audiotron-folder tab from the cloud.
 * 
 *
 */
public class UD_MyAuditronsAcitivity extends Activity {

	private static String TAG = "UD_MyAuditronsAcitivity";
	private SessionManager session;
	private String URL_STRING;
	private String username;
	private ListView myAudiotronsListView;
	private MyAudiotronsListadapter myAudiotronsListadapter;
	private ArrayList<String> myAudiotronsArrList;
	private BroadcastReceiver myAudiotronsActivityReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ud__my_auditrons_acitivity);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********UD_MyAuditronsAcitivity Activity Started***************");
		
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
				Intent i = new Intent(UD_MyAuditronsAcitivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		SharedPreferences prefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = prefs.getString("myAudiotrons_URL",Constants.myAudiotrons_URL);
		Log.v(TAG,"---------Preferred URL is ----->"+URL_STRING);
		
		username = prefs.getString("username", "");
		Log.v(TAG,"###Information: User name is >>> "+username);
		
		setupViews();
		
	}
	private void setupViews(){
		myAudiotronsListView= (ListView) findViewById(R.id.my_audiotrons_list);
		myAudiotronsArrList = new ArrayList<String>();
		
		DownloadMyAudiotronsTask task = new DownloadMyAudiotronsTask();
		task.execute(myAudiotronsArrList);
		
	}
	/**
	 * getMyAudiotronsToDisplay() gets all the audiotrons
	 * from the cloud, uploaded by the current user
	 * @return
	 */
	private ArrayList<String> getMyAudiotronsToDisplay() {
		ArrayList postParameters = new ArrayList();
		JSONArray jsonArraryFromIS = null;
		String jsonResponseMsg; 
		
		Log.v(TAG, "-------Inside getAudiotronsInCategoryToDisplay----------");

		try {
			postParameters.add(new BasicNameValuePair("username",username));
			JSONObject jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");

			Log.v(TAG, "~~~~~json OBJ>>> " + jsonObjFromInputStream.keys());
			Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());
			
			jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");
			
			if(jsonResponseMsg.equals("audiotronsRetrievalSuccess"))
			{
				Log.v(TAG,"####### Information: value of audiotrons >>>"+ jsonObjFromInputStream.get("audiotronListForUserFromDB"));
				jsonArraryFromIS=(JSONArray) jsonObjFromInputStream.get("audiotronListForUserFromDB");
				
				for (int i = 0; i < jsonArraryFromIS.length(); i++) {
					myAudiotronsArrList.add(jsonArraryFromIS.getString(i));
				}
			}else{
				Log.v(TAG,"#######Information: There are no audiotrons in the list;Please record and upload some audiotrons!!");
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in getMyAudiotronsToDisplay in UD_MyAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'try' block getMyAudiotronsToDisplay in UD_MyAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			Log.e(TAG,
					"$$$$$$ Exception in try block getMyAudiotronsToDisplay in UD_MyAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		}

		return myAudiotronsArrList;

	}
	/**
	 * DownloadMyAudiotronsTask is an async class
	 * that downloads the names of the audiotrons
	 * uploaded by the current user
	 */
	private class DownloadMyAudiotronsTask extends AsyncTask<ArrayList<String>, Toast, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... tempAudiotronsHolderArr) {
			tempAudiotronsHolderArr[0] = getMyAudiotronsToDisplay();

			Log.v(TAG,"####Information>>>>>> resulting length in array of doInBgnd() >> '"+ tempAudiotronsHolderArr[0].size());
			return tempAudiotronsHolderArr[0];
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			Log.v(TAG, "------Inside onPostExecute(..)---------");
			Log.v(TAG, "-----Completed Task--------");
			
			if(result.size()>0)
				UD_MyAuditronsAcitivity.this.display(result);
			else{
				//Put this is a dialog pop.
				Toast.makeText(getApplicationContext(), "You have not created any audiotrons.Please record some!", Toast.LENGTH_LONG).show();
			}
		}
	}
	/**
	 * display() displays the list of audiotrons 
	 * obtained from the getMyAudiotronsToDisplay()
	 * @param result
	 */
	private void display(ArrayList<String> result) {
		Log.v(TAG, "---------Inside display() of UD_MyAudsAct-----------");
		Log.v(TAG,"####Information>>>>>> resulting length in array of display() >> '"	+ result.size());
		
		if (result.equals(null)) {
			Toast.makeText(getApplicationContext(),	"Display Error.Please check later!", Toast.LENGTH_LONG).show();
		}
		else{
			myAudiotronsListadapter = new MyAudiotronsListadapter(this,result);
			myAudiotronsListView.setAdapter(myAudiotronsListadapter);
			
			myAudiotronsListView.setOnItemClickListener(new OnItemClickListener() {
	
				public void onItemClick(AdapterView parent, View view,int position, long id) {
					TextView tv = (TextView) view.findViewById(R.id.my_audiotron_text);
					final String text = tv.getText().toString();
	
					Toast.makeText(getApplicationContext(),"Clicked list item " + position + " text is " + text,Toast.LENGTH_SHORT).show();
	
					//#This call will play the chosen-self recorded audiotron from over network.
					Intent i = new Intent(UD_MyAuditronsAcitivity.this,StreamAudiotronsActivity.class).putExtra("audiotronName", text);
					startActivity(i);
				}
			});
		}
	}
	/**
	 * MyAudiotronsListadapter is adapter class
	 * that displays the audiotrons in a list
	 */
	private class MyAudiotronsListadapter extends BaseAdapter{
		LayoutInflater vi;
		public MyAudiotronsListadapter(Activity context,ArrayList<String> arr) {
			super();
			Log.v(TAG, "------Inside MyAudiotronsListadapter constructor-------"+context+" Arr "+arr);
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "------Inside getView()-------");

			ViewHolder holder;
			View v = convertView;
			if (v == null) {
				v = vi.inflate(R.layout.activity_ud__my_auditrons_acitivity, null);
				holder = new ViewHolder();
				holder.myAudiotronListView = (ListView) v.findViewById(R.id.my_audiotrons_list);
				holder.txv = (TextView) v.findViewById(R.id.my_audiotron_text);
				holder.txv.setText(myAudiotronsArrList.get(position));	
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			return v;
		}
		public int getCount() {
			return myAudiotronsArrList.size();
		}
		public Object getItem(int arg0) {
			return arg0;
		}
		public long getItemId(int arg0) {
			return arg0;
		}
	}
	class ViewHolder {
		ListView myAudiotronListView;
		TextView txv;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_ud__my_auditrons_acitivity,
				menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(UD_MyAuditronsAcitivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(UD_MyAuditronsAcitivity.this,MyAudiotrons_SDcard_HelpActivity.class);
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
