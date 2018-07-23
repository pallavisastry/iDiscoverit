package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.io.InputStream;
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

public class AudiotronsInCategoryDisplayActivity extends Activity {

	private static final String TAG = "AudiotronsInCategoryDisplayActivity";
	private static String URL_STRING;

	private InputStream inputStream;
	private ListView audiotronsListView;
	
	private AudiotronsListadapter audiotronsListadapter;
	private ArrayList<String> audiotronArrList;
//	private ArrayList<String> randomAudiotronArrList;

	private String categoryName;
	private SessionManager session;
	private String username;
	
	private BroadcastReceiver audiotronsListActivityReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audiotrons_in_category_display);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********AudiotronsInCategoryDisplayActivity Activity Started***************");

		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		
		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		audiotronsListActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(AudiotronsInCategoryDisplayActivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = urlPrefs.getString("audiotronsInCategoryDisplayActivity_URL", Constants.audiotronsInCategoryDisplayActivity_URL);
		Log.d(TAG,"---------Preferred URL is ----->"+URL_STRING);
		
		categoryName = getIntent().getStringExtra("categoryName");
		Log.v(TAG, "#####-category name inside next activity------------>"+ categoryName);
	
		username = session.getUserDetails().get("username");//#new
		Log.v(TAG,"#####Infomration- User name is >>>> "+username);//#new
		
		audiotronsListView = (ListView) this.findViewById(R.id.list);
		audiotronArrList = new ArrayList<String>();
		
		String isSkipMessageChecked = urlPrefs.getString("skipMessage_category", "unchecked");
		Log.v(TAG, "#####is skip message checked??? :" + isSkipMessageChecked);
		if (!(isSkipMessageChecked.equals("checked"))) {
			Intent in = new Intent(AudiotronsInCategoryDisplayActivity.this, ACT_HelpActivity.class);
			startActivity(in);
		} else if (isSkipMessageChecked.equals("unchecked")) {
			Intent in = new Intent(AudiotronsInCategoryDisplayActivity.this, ACT_HelpActivity.class);
			startActivity(in);
		}
		
		DownloadAudiotronsInCategoryTask task = new DownloadAudiotronsInCategoryTask();
		task.execute(audiotronArrList);
	}

	private void display(ArrayList<String> result) {
		Log.v(TAG, "---------Inside display() of AudiotronCatDispl-----------");
		Log.v(TAG,"####Information>>>>>> resulting length in array of display() >> '"	+ result.size());
		
		if (result.equals(null)) {
			Toast.makeText(getApplicationContext(),	"Oopps.Sorrrryy..Nothing to display now.", Toast.LENGTH_LONG).show();
		}
		else{
			audiotronsListadapter = new AudiotronsListadapter(this,result);
			audiotronsListView.setAdapter(audiotronsListadapter);
			
			//#start download of the file
			audiotronsListView.setOnItemClickListener(new OnItemClickListener() {
	
				public void onItemClick(AdapterView parent, View view,int position, long id) {
					TextView tv = (TextView) view.findViewById(R.id.audiotron_text);
					final String text = tv.getText().toString();
	
					Toast.makeText(getApplicationContext(),"Clicked list item " + position + " text is " + text,Toast.LENGTH_SHORT).show();
	
					Intent i = new Intent(AudiotronsInCategoryDisplayActivity.this,StreamAudiotronsActivity.class).putExtra("audiotronName", text);
					startActivity(i);
				}
	
			});
		}
	}

	private ArrayList<String> getAudiotronsInCategoryToDisplay() {

		ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
		JSONArray jsonArraryFromIS = null;
		String jsonResponseMsg; 
		
		Log.v(TAG, "-------Inside getAudiotronsInCategoryToDisplay----------");

		try {
			postParameters.add(new BasicNameValuePair("categoryInRequest",categoryName));
			postParameters.add(new BasicNameValuePair("username", username));//#new
			JSONObject jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");

			Log.v(TAG, "~~~~~json OBJ>>> " + jsonObjFromInputStream.keys());
			Log.v(TAG, "####### Information: Audiotrons:: key value pair>>>"+ jsonObjFromInputStream.names());
			
			jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");
			if(jsonResponseMsg.equals("audiotronsRetrievalSuccess"))
			{
				Log.v(TAG,"####### Information: value of audiotrons >>>"+ jsonObjFromInputStream.get("audiotronListForCategoryFromDB"));
				jsonArraryFromIS=(JSONArray) jsonObjFromInputStream.get("audiotronListForCategoryFromDB");
				
				for (int i = 0; i < jsonArraryFromIS.length(); i++) {
					audiotronArrList.add(jsonArraryFromIS.getString(i));
				}
			}else{
				Log.v(TAG,"#######Information: There are no audiotrons in the list; empty list!!");
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in getAudiotronsInCategoryToDisplay in AudsInCatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'try' block getAudiotronsInCategoryToDisplay in AudsInCatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			Log.e(TAG,
					"$$$$$$ Exception in try block getAudiotronsInCategoryToDisplay in AudsInCatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		}

		return audiotronArrList;
	}
	/**
	 * DownloadAudiotronsInCategoryTask is an inner class that
	 * gets all the audio files requested under a 'category'
	 * over the internet asynchronously.
	 * @author pallavi
	 *
	 */
	private class DownloadAudiotronsInCategoryTask extends AsyncTask<ArrayList<String>, Toast, ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... tempAudiotronsHolderArr) {

			tempAudiotronsHolderArr[0] = getAudiotronsInCategoryToDisplay();// category[0]);

			Log.v(TAG,"####Information>>>>>> resulting length in array of doInBgnd() >> '"+ tempAudiotronsHolderArr[0].size());
			return tempAudiotronsHolderArr[0];
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			Log.v(TAG, "------Inside onPostExecute(..)---------");
			Log.v(TAG, "-----Completed Task--------");
			
			if(result.size()>0)
				AudiotronsInCategoryDisplayActivity.this.display(result);
			else{
				//Put this is a dialog pop.
				Toast.makeText(getApplicationContext(), "There are no audiotrons in this category.Please choose another", Toast.LENGTH_LONG).show();
			}

		}

	}
	/**
	 * 
	 * @author pallavi
	 * An adapter inner class for display list of 
	 * audiotrons in clickable ListView, obtained from the database.
	 *
	 */
	private class AudiotronsListadapter extends BaseAdapter {
		
		LayoutInflater vi;
		
		public AudiotronsListadapter(Activity context,ArrayList<String> arr) {
			super();
			Log.v(TAG, "------Inside AudiotronsListadapter constructor-------"+context+" Arr "+arr);
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "------Inside getView()-------");

			ViewHolder holder;
			View v = convertView;

			if (v == null) {

				v = vi.inflate(R.layout.activity_audiotrons_in_category_display, null);

				holder = new ViewHolder();
				
				holder.audiotronListView = (ListView) v.findViewById(R.id.list);
				holder.txv = (TextView) v.findViewById(R.id.audiotron_text);
				holder.txv.setText(audiotronArrList.get(position));
			
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}
			return v;
		}

		public int getCount() {
			return audiotronArrList.size();
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

	}

	class ViewHolder {
		ListView audiotronListView;
		TextView txv;
	//	RatingBar rtb;
	}
	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(audiotronsListActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause()->UN-Registering receiver-----");
		unregisterReceiver(audiotronsListActivityReceiver);
	}
	// #new
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	// #new
	public void onDestroy() {
		super.onDestroy();
		try {
			if (inputStream != null)
				inputStream.close();
		} catch (IOException e) {
			Log.e(TAG,"$$$$$$ IOE-instream closing exception in onDestroy() : "	+ e.getMessage() + " $$$$$$$$$$$");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_audiotrons_in_category_display, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(AudiotronsInCategoryDisplayActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(AudiotronsInCategoryDisplayActivity.this,ACT_HelpActivity.class);
    	startActivity(in);
	}
}
