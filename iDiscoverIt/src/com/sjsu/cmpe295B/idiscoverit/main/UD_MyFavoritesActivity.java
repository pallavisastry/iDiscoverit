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

public class UD_MyFavoritesActivity extends Activity {

	private static String TAG = "UD_MyFavoritesActivity";
	
	private SessionManager session;
	private String URL_STRING;
	private String username;
	
	private ListView myAudiotronsListView;
	private MyFavoriteAudiotronsListadapter myAudiotronsListadapter;
	private ArrayList<String> myAudiotronsArrList;
	
	private BroadcastReceiver myFavoritesActivityReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ud__my_favorites);
		
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********UD_MyFavoritesActivity Activity Started***************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		

		// #Setting a dynamic broadcast receiver to logout completely.
		IntentFilter intentFilter = new IntentFilter();
		intentFilter
				.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		myFavoritesActivityReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "||||| Logout in progress ||||||");
				// Starting login activity
				Intent i = new Intent(UD_MyFavoritesActivity.this,
						LoginActivity.class);
				startActivity(i);
				finish();
			}
		};
		
		SharedPreferences prefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = prefs.getString("myFavoriteAudiotrons_URL", Constants.myFavoriteAudiotrons_URL);
		Log.v(TAG,"---------Preferred URL is ----->"+URL_STRING);
		
		username = prefs.getString("username", "");
		Log.v(TAG,"###Information: User name is >>> "+username);
		
		setupViews();
	}
	private void setupViews(){
		myAudiotronsListView = (ListView) findViewById(R.id.my_favorite_audiotrons_list);
		myAudiotronsArrList = new ArrayList<String>();
		
		DownloadMyFavoriteAudiotronsTask task = new DownloadMyFavoriteAudiotronsTask();
		task.execute(myAudiotronsArrList);
	}
	private ArrayList<String> getMyFavoriteAudiotronsToDisplay(){
		
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
				Log.v(TAG,"####### Information: value of audiotrons >>>"+ jsonObjFromInputStream.get("myFavoriteAudiotronListFromDB"));
				jsonArraryFromIS=(JSONArray) jsonObjFromInputStream.get("myFavoriteAudiotronListFromDB");
				
				for (int i = 0; i < jsonArraryFromIS.length(); i++) {
					myAudiotronsArrList.add(jsonArraryFromIS.getString(i));
				}
			}else{
				Log.v(TAG,"#######Information: There are no audiotrons in the list;Please add some to favorites!!");
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in getMyFavAudiotronsToDisplay in UD_MyFavAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'try' block getMyFavAudiotronsToDisplay in UD_MyFavAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			Log.e(TAG,
					"$$$$$$ Exception in try block getMyFAvAudiotronsToDisplay in UD_MyFavAudActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		}

		return myAudiotronsArrList;

	}
	private void display(ArrayList<String> result) {
		Log.v(TAG, "---------Inside display() of UD_MyFavAudsAct-----------");
		Log.v(TAG,"####Information>>>>>> resulting length in array of display() >> '"	+ result.size());
		
		if (result.equals(null)) {
			Toast.makeText(getApplicationContext(),	"Display Error.Please check later!", Toast.LENGTH_LONG).show();
		}
		else{
			myAudiotronsListadapter = new MyFavoriteAudiotronsListadapter(this,result);
			myAudiotronsListView.setAdapter(myAudiotronsListadapter);
			
			myAudiotronsListView.setOnItemClickListener(new OnItemClickListener() {
	
				public void onItemClick(AdapterView parent, View view,int position, long id) {
					TextView tv = (TextView) view.findViewById(R.id.my_favorite_audiotron_text);
					final String text = tv.getText().toString();
	
					Toast.makeText(getApplicationContext(),"Clicked list item " + position + " text is " + text,Toast.LENGTH_SHORT).show();
	
					//#This call will play the chosen-self recorded audiotron from over network.
					Intent i = new Intent(UD_MyFavoritesActivity.this,StreamAudiotronsActivity.class).putExtra("audiotronName", text);
							//.putExtra("isFavorite", "true");
					startActivity(i);
				}
	
			});
		}
	}
	class DownloadMyFavoriteAudiotronsTask extends AsyncTask<ArrayList<String>, Toast, ArrayList<String>>{
		
		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... tempAudiotronsHolderArr) {
			tempAudiotronsHolderArr[0] = getMyFavoriteAudiotronsToDisplay();

			Log.v(TAG,"####Information>>>>>> resulting length in array of doInBgnd() >> '"+ tempAudiotronsHolderArr[0].size());
			return tempAudiotronsHolderArr[0];
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {

			Log.v(TAG, "------Inside onPostExecute(..)---------");
			Log.v(TAG, "-----Completed Task--------");
			
			if(result.size()>0)
				UD_MyFavoritesActivity.this.display(result);
			else{
				//Put this is a dialog pop.
				Toast.makeText(getApplicationContext(), "You have no favorites.Please mark favorites!", Toast.LENGTH_LONG).show();
			}

		}
	}
	class MyFavoriteAudiotronsListadapter extends BaseAdapter{

		LayoutInflater vi;
		
		public MyFavoriteAudiotronsListadapter(Activity context,ArrayList<String> arr) {
			super();
			Log.v(TAG, "------Inside MyAudiotronsListadapter constructor-------"+context+" Arr "+arr);
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.v(TAG, "------Inside getView()-------");

			ViewHolder holder;
			View v = convertView;

			if (v == null) {

				v = vi.inflate(R.layout.activity_ud__my_favorites, null);

				holder = new ViewHolder();
				
				holder.myAudiotronListView = (ListView) v.findViewById(R.id.my_favorite_audiotrons_list);
				holder.txv = (TextView) v.findViewById(R.id.my_favorite_audiotron_text);
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
		getMenuInflater().inflate(R.menu.activity_ud__my_favorites, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_home:
				startActivity(new Intent(UD_MyFavoritesActivity.this, HomeActivity.class));
				return true;
			case R.id.menu_help:
				displayHelpAlert();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displayHelpAlert() {
		Intent in = new Intent(UD_MyFavoritesActivity.this,UD_Profile_HelpActivity.class);
    	startActivity(in);
	}

	/**-----Acitivty Lifecycle methods--------**/
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG,"---onResume->Registering receiver----");
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction("com.sjsu.cmpe295B.idiscoverit.utilities.ACTION_LOGOUT");
		registerReceiver(myFavoritesActivityReceiver,iFilter);
	}
	@Override
	public void onPause(){
		super.onPause();
		Log.d(TAG,"---onPause->UN-Registering receiver-----");
		unregisterReceiver(myFavoritesActivityReceiver);
	}

}
