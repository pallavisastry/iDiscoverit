package com.sjsu.cmpe295B.idiscoverit.main;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sjsu.cmpe295B.idiscoverit.utilities.Constants;
import com.sjsu.cmpe295B.idiscoverit.utilities.SessionManager;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.HttpClientHelper;
import com.sjsu.cmpe295B.idiscoverit.webHelpers.StreamAudiotronsActivity;

public class SearchActivity extends Activity {
	
	private static final String TAG="SearchActivity";
	private String query;
	private ArrayList<String> searchResultArrayList;
	private String[] resultArray;
	private String URL_STRING;
	SessionManager session;
	
	private ListView searchListView;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.v(TAG, "**********************************************************");
		Log.v(TAG,"***********SearchActivity Activity Started***************");
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();

		searchResultArrayList=new ArrayList<String>();
		searchListView = (ListView) findViewById(R.id.search_list);
		
		progressDialog=ProgressDialog.show(SearchActivity.this, "Searching....", "Searching for audiotrons with name: "+query);

		// #This preference is available only after session is appropriately set by checking session.checkLogin()
		SharedPreferences urlPrefs = this.getSharedPreferences("iDiscoveritPreferences", MODE_PRIVATE);
		URL_STRING = urlPrefs.getString("search_URL",Constants.search_URL);
		Log.d(TAG, "---------Preferred URL is ----->" + URL_STRING);

		Intent intent = getIntent();
		if(intent.ACTION_SEARCH.equals(intent.getAction())){
			query = intent.getStringExtra(SearchManager.QUERY);
			Log.v(TAG,"####Info: Search string is >>> "+query);
			SearchTask task = new SearchTask();
			task.execute();
		}
	}
	private ArrayList<String> doSearch(String query){
		JSONObject jsonObjFromInputStream;
		JSONArray jsonArraryFromIS;
		ArrayList postParameters = new ArrayList();
		
		try {
			Log.v(TAG, "-------Inside getCategoryListToDisplay---------- ");
			postParameters.add(new BasicNameValuePair("query",query));
			jsonObjFromInputStream = (JSONObject) HttpClientHelper.executePost(URL_STRING, postParameters, "json");
			Log.v(TAG, "~~~~~json OBJ>>>  vals>> "+jsonObjFromInputStream.names());
			
			String jsonResponseMsg = jsonObjFromInputStream.getString("customActionMsg");
			if(jsonResponseMsg.equals("searchRetrievalSuccess")){
				jsonArraryFromIS = (JSONArray) jsonObjFromInputStream.get("searchResultsList");

				for(int i=0;i<jsonArraryFromIS.length();i++)
					searchResultArrayList.add(jsonArraryFromIS.getString(i));
				Log.v(TAG,"#####Information: search result => "+searchResultArrayList);
				
			}else if(jsonResponseMsg.equals("searchRetrievalNone")){
				Log.v(TAG,"#######Information: There are no cateogories in the list#########");
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"$$$$$$ JSON-Exception in doSearch in CatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (IOException e) {
			Log.e(TAG,
					"$$$$$$ IO-Exception in 'else-try' block doSearch in CatActivity...... "
							+ e.getMessage() + "$$$$$$$$");
		} catch (Exception e) {
			Log.e(TAG,"$$$$$$ EXCEPTION in doSearch $$$$$$$"+e.getMessage());
		}catch(OutOfMemoryError e){
			Log.e(TAG,"$$$$@@@@%%%%%%% outta mem error!!!!! $$$$@@@@%%%%%%% "+e);
		}
		finally{
			Log.v(TAG,"----Nullyfying all objects in doSearch-------");
			jsonObjFromInputStream = null;
			jsonArraryFromIS = null;
			postParameters = null;
		}
		return searchResultArrayList;
	}
	private void displaySearchList(ArrayList<String> searchResultList){
		searchListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,resultArray));
		
		searchListView.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				String text = ((TextView)view).getText().toString();
				Toast.makeText(getApplicationContext(), "Selected file is: "+(((TextView)view).getText().toString()), Toast.LENGTH_LONG).show();
				Intent i = new Intent(SearchActivity.this,StreamAudiotronsActivity.class).putExtra("audiotronName", text);
				startActivity(i);
			}
		});
		
	}
	private class SearchTask extends AsyncTask<Void, ProgressDialog, ArrayList<String>>{

		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			
			ArrayList<String> searchResult = doSearch(query);
			resultArray = searchResult.toArray(new String[searchResult.size()]);
			
			runOnUiThread(new Runnable() {
				
				public void run() {
					if(progressDialog.isShowing())
						progressDialog.dismiss();
				}
			});
			return searchResult;
		}
		@Override
		protected void onPostExecute(ArrayList<String> result){
			if(result.size()>0)
				displaySearchList(result);
			else{
				Toast.makeText(getApplicationContext(), "No Results.Please enter some other search criteria!", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}

}
